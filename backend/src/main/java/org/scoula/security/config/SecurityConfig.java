package org.scoula.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mybatis.spring.annotation.MapperScan;
import org.scoula.security.accounting.service.CustomUserDetailsService;
import org.scoula.security.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
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
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@Log4j2
@MapperScan(basePackages = {"org.scoula.security.accounting.mapper"})
@ComponentScan(basePackages = {"org.scoula.security"})
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final CustomUserDetailsService userDetailsService;  // 사용자 인증 처리 서비스
    private final JwtAuthenticationFilter jwtAuthenticationFilter;  // jwt 토큰 인증 필터
    private final AuthenticationErrorFilter authenticationErrorFilter;  // 인증 오류 처리 필터
    private final CustomAccessDeniedHandler accessDeniedHandler;  // 접근 거부 처리 핸들러
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;  // 인증 처리 시점 처리 핸들러

    // 로그인 처리 필터
    @Autowired
    private JwtUsernamePasswordAuthenticationFilter jwtUsernamePasswordAuthenticationFilter;

    // 비밀번호 암호화를 위한 BCrypt 인코더
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 한글 인코딩 처리 필터
    public CharacterEncodingFilter encodingFilter() {
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);
        return encodingFilter;
    }

    // AuthenticationManager 빈 등록
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // CORS 설정 필터
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    // 보안 제외 설정
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                // 정적 리소스만 제외
                "/assets/**",
                "/favicon.ico",
                "/robots.txt",

                // 시스템 모니터링
                "/api/health",
                "/api/status",

                // 최신 Swagger/OpenAPI 3.x 경로
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/webjars/**"
        );
    }

    // HTTP 보안 설정 및 필터 체인 구성
    @Override
    public void configure(HttpSecurity http) throws Exception {
        // 필터 체인 구성
        http
                .addFilterBefore(encodingFilter(), CsrfFilter.class)
                .addFilterBefore(authenticationErrorFilter, JwtAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // 기본 인증 방식 비활성화
        http
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 예외 처리 핸들러 설정
        http
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)  // 인증 실패 시
                .accessDeniedHandler(accessDeniedHandler);  // 권한 부족 시

        // URL별 접근 권한 설정
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/users/register").permitAll()
                .antMatchers("/api/users/**").authenticated()
                .antMatchers("/api/groups/**").authenticated()
                .antMatchers("/api/payments/**").authenticated()
                .antMatchers("/api/expenses/**").authenticated()
                .antMatchers("/api/settlements/**").authenticated()
                .anyRequest().authenticated();
    }

    // 사용자 인증 방식 설정
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        log.info("AuthenticationManagerBuilder 설정 시작 ===========");
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }
}
