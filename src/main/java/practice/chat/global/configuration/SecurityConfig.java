package practice.chat.global.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import practice.chat.domain.user.application.CustomOAuth2UserService;
import practice.chat.domain.user.dao.MemberRepository;
import practice.chat.domain.user.dao.RefreshTokenDao;
import practice.chat.global.jwt.JwtAccessDeniedHandler;
import practice.chat.global.jwt.JwtAuthenticationEntryPoint;
import practice.chat.global.jwt.TokenProvider;
import practice.chat.global.oauth.handler.OAuth2AuthenticationFailureHandler;
import practice.chat.global.oauth.handler.OAuthAuthenticationSuccessHandler;
import practice.chat.global.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final TokenProvider tokenProvider;

    private final RefreshTokenDao refreshTokenDao;

    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    private final MemberRepository memberRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // h2 database 테스트가 원활하도록 관련 API 들은 전부 무시
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .antMatchers("/h2-console/**", "/favicon.ico");
    }



    @Bean
    public OAuthAuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return new OAuthAuthenticationSuccessHandler(
                tokenProvider,
                refreshTokenDao,
                oAuth2AuthorizationRequestBasedOnCookieRepository()
        );
    }

    /*
     * 쿠키 기반 인가 Repository
     * 인가 응답을 연계 하고 검증할 때 사용.
     * */
    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CSRF 설정 Disable
        //스프링 사용 가이드 에 따르면 session 을 저장하지 않는 REST 서버는 csrf 와같은 공격에 대해 영향을 받지 않는다 라고 나와있다. 서버에 탈취할 수 있는 session 이 없기때문이다.
        http.csrf().disable()
                // exception handling 할 때 우리가 만든 클래스를 추가
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // 시큐리티는 기본적으로 세션을 사용
                // 여기서는 세션을 사용하지 않기 때문에 세션 설정을 Stateless 로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)


                // 로그인, 회원가입 API 는 토큰이 없는 상태에서 요청이 들어오기 때문에 permitAll 설정
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "**").permitAll()
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/oauth2/**").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE,"/member/{memberId}").access("hasRole('ADMIN') or hasRole('USER')")
                .antMatchers("/member/myInfo","/member/field"
                        ,"/member/{memberId}/change/**","/member/bookmark","/member/bookmark/**").access("hasRole('ADMIN') or hasRole('USER')")
                .antMatchers("/member/**").permitAll()
                .antMatchers("/message/**").access("hasRole('ADMIN') or hasRole('USER')")

//                .anyRequest().authenticated()   // 나머지 API 는 전부 인증 필요
                .anyRequest().permitAll()   //` 나머지 API 는 전부 허용

                // JwtFilter 를 addFilterBefore 로 등록했던 JwtSecurityConfig 클래스를 적용
                .and()
                .apply(new JwtSecurityConfig(tokenProvider))
;

        return http.build();
    }


}