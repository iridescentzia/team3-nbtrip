package org.scoula.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mybatis.spring.annotation.MapperScan;
import org.scoula.security.accounting.service.CustomUserDetailsService;
import org.scoula.security.filter.AuthenticationErrorFilter;
import org.scoula.security.filter.JwtAuthenticationFilter;
import org.scoula.security.filter.JwtUsernamePasswordAuthenticationFilter;
import org.scoula.security.handler.CustomAccessDeniedHandler;
import org.scoula.security.handler.CustomAuthenticationEntryPoint;
import org.scoula.security.handler.LoginFailureHandler;
import org.scoula.security.handler.LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@Log4j2
@MapperScan(basePackages = {"org.scoula.security.accounting.mapper", "org.scoula.member.mapper", "org.scoula.mypage.mapper"})
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final CustomUserDetailsService userDetailsService;  // 사용자 인증 처리 서비스
    private final JwtAuthenticationFilter jwtAuthenticationFilter;  // JWT 토큰 인증 필터
    private final AuthenticationErrorFilter authenticationErrorFilter;  // 인증 오류 처리 필터
    private final CustomAccessDeniedHandler accessDeniedHandler;  // 접근 거부 처리 핸들러
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;  // 인증 진입점 처리 핸들러
    private final LoginSuccessHandler loginSuccessHandler;  // 로그인 성공 핸들러
    private final LoginFailureHandler loginFailureHandler;  // 로그인 실패 핸들러

    // AuthenticationManager Bean 등록
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // 비밀번호 암호화를 위한 BCrypt 인코더
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS 설정 필터
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);          // 쿠키/인증 헤더 전송 허용
        config.addAllowedOriginPattern("*");       // 모든 도메인 허용 (개발용)
        config.addAllowedHeader("*");              // 모든 헤더 허용
        config.addAllowedMethod("*");              // 모든 HTTP 메서드 허용
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    // 보안 검사를 제외할 리소스 설정
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                // 정적 리소스
                "/assets/**",
                "/favicon.ico",
                "/robots.txt",

                // 시스템 모니터링
                "/api/health",
                "/api/status",

                // Swagger/OpenAPI 문서
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/webjars/**"
        );
    }

    // HTTP 보안 설정 및 필터 체인 구성
    @Override
    public void configure(HttpSecurity http) throws Exception {
        JwtUsernamePasswordAuthenticationFilter jwtLoginFilter =
                new JwtUsernamePasswordAuthenticationFilter(
                        authenticationManagerBean(),
                        loginSuccessHandler,
                        loginFailureHandler
                );

        http
                .addFilterBefore(corsFilter(), CsrfFilter.class)
                .addFilterBefore(authenticationErrorFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // 기본 인증 방식 비활성화
        http
                .httpBasic().disable()  // HTTP Basic 인증 비활성화
                .csrf().disable()  // CSRF 보호 비활성화 (JWT 사용으로 불필요)
                .formLogin().disable()  // 폼 로그인 비활성화
                .sessionManagement()  // 세션 정책 설정
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Stateless (세션 사용 안함)

        // 예외 처리 핸들러 설정
        http
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)  // 인증 실패 시
                .accessDeniedHandler(accessDeniedHandler);  // 권한 부족 시

        // URL별 접근 권한 설정
        http
                .authorizeRequests()

                // OPTIONS 요청 (CORS Preflight) 항상 허용
                .antMatchers(HttpMethod.OPTIONS).permitAll()

                // 인증 불필요(공개 API)
                .antMatchers(HttpMethod.POST, "/api/auth/login").permitAll()  // 로그인
                .antMatchers(HttpMethod.POST, "/api/auth/register").permitAll()  // 회원가입
                .antMatchers(HttpMethod.GET, "/api/merchants/**").permitAll()  // 가맹점 조회

                // 인증 필요(보호된 API)
                .antMatchers("/api/auth/logout").authenticated()  // 인증 관련
                .antMatchers("/api/users/**").authenticated()  // 사용자 관리
                .antMatchers("/api/groups/**").authenticated()  // 그룹 관리
                .antMatchers(HttpMethod.POST, "/api/merchants").authenticated()  // 가맹점 등록
                .antMatchers("/api/transactions/**").authenticated()  // 거래 관리
                .antMatchers("/api/settlements/**").authenticated()  // 정산 관리
                .antMatchers("/api/accounts/**").authenticated()  // 계좌 관리
                .antMatchers("/api/notifications/**").authenticated()  // 알림 관리
                .anyRequest().authenticated();
    }

    //사용자 인증 방식 설정
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        log.info("============= AuthenticationManagerBuilder 설정 시작 =============");
        auth
                .userDetailsService(userDetailsService)  // 커스텀 사용자 정보 서비스
                .passwordEncoder(passwordEncoder());  // BCrypt 암호화
    }
}