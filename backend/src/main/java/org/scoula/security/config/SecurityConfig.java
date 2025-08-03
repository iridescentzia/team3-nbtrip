package org.scoula.security.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
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
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
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

import java.time.format.DateTimeFormatter;

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
        config.setAllowCredentials(true);  // 쿠키/인증 헤더 전송 허용
        config.addAllowedOriginPattern("*");  // 모든 도메인 허용 (개발용)
        config.addAllowedHeader("*");  // 모든 헤더 허용
        config.addAllowedMethod("*");  // 모든 HTTP 메서드 허용
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    // LocalDateTime → yyyy-MM-dd HH:mm:ss 포맷 직렬화 설정
    @Bean
    public Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.simpleDateFormat("yyyy-MM-dd HH:mm:ss");
        builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return builder;
    }

    // 보안 검사를 제외할 리소스 설정
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                // 프론트엔드 SPA 진입점 허용
                "/",
                "/index.html",

                // 정적 파일 확장자별 전역 허용
                "/**/*.js",
                "/**/*.css",
                "/**/*.png",
                "/**/*.jpg",
                "/**/*.svg",

                // 정적 리소스
                "/assets/**",
                "/favicon.ico",
                "/robots.txt",
                "/static/**",
                "/public/**",

                // Vue Router 페이지 경로들 - 페이지 접근 허용
                "/login",
                "/register",
                "/mypage",
                "/settlement",
                "/trips",
                "/merchants",
                "/accounts",
                "/payments",
                "/chart",
                "/notifications",
                "/paymentlist",


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
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 페이지 접근 허용 (프론트엔드 라우터)
                .antMatchers(HttpMethod.POST, "/auth/login").permitAll()  // 로그인
                .antMatchers(HttpMethod.POST, "/auth/register").permitAll()  // 회원가입
                .antMatchers(HttpMethod.POST, "/auth/logout").permitAll()  // 로그아웃
                .antMatchers(HttpMethod.POST, "/users/check-nickname").permitAll()  // 닉네임 중복 확인

                // 인증 필요(보호된 API)
                .antMatchers("/users/**").authenticated()  // 사용자 정보
                .antMatchers("/mypage/**").authenticated()  // 마이페이지
                .antMatchers("/trips/**").authenticated()  // 여행
                .antMatchers("/merchants/**").authenticated()  // 가맹점
                .antMatchers("/transactions/**").authenticated()  // 거래
                .antMatchers("/settlements/**").authenticated()  // 정산
                .antMatchers("/accounts/**").authenticated()  // 계좌
                .antMatchers("/chart/**").authenticated()  // 차트
                .antMatchers("/notifications/**").authenticated()  // 알림
                .antMatchers("/payments/**").authenticated()  // 결제
                .anyRequest().permitAll();
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