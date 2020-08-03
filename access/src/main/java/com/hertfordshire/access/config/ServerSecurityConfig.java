package com.hertfordshire.access.config;

import com.hertfordshire.access.config.filter.basic_login.JWTAuthenticationFilter;
import com.hertfordshire.access.config.filter.basic_login.JWTAuthorizationFilter;
import com.hertfordshire.access.config.filter.basic_login.LoginFailureHandler;
import com.hertfordshire.access.config.listener.EventSendingAuthenticationFailureHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ServerSecurityConfig extends WebSecurityConfigurerAdapter {

    @Qualifier("userService")
    @Autowired
    private UserDetailsService userDetailsService;

    private static final RequestMatcher RequestMatcher_PUBLIC_URLS = new OrRequestMatcher(
            new AntPathRequestMatcher("/v1/api/public/**")
    );

    private static final RequestMatcher GRAPHQL_URL = new OrRequestMatcher(
            new AntPathRequestMatcher("/graphql")
    );

    @Override
    public void configure(final WebSecurity web) {
        web.ignoring().requestMatchers(RequestMatcher_PUBLIC_URLS, GRAPHQL_URL);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.addFilterAt(getJWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        http.addFilterBefore(corsFilter(), SessionManagementFilter.class); //adds your custom CorsFilter

        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers("/v1/api/public/**", "/graphql").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(getJWTAuthorizationFilter()).logout()
                .logoutUrl("/v1/api/protected/auth/logout")
                .permitAll()
                .logoutSuccessHandler(myLogoutHandler()).and()
                // this disables session creation on Spring Security
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }


    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public JWTAuthenticationFilter getJWTAuthenticationFilter() throws Exception {
        final JWTAuthenticationFilter filter = new JWTAuthenticationFilter();
        filter.setFilterProcessesUrl("/v1/api/public/auth/login");
        return filter;
    }


    @Bean
    public LoginFailureHandler getLoginFailureHandler() throws Exception {
        return new LoginFailureHandler();
    }

    @Bean
    public LogoutHandler myLogoutHandler(){
        return new LogoutHandler();
    }


    @Bean
    public JWTAuthorizationFilter getJWTAuthorizationFilter() throws Exception {
        return new JWTAuthorizationFilter(authenticationManager());
    }

    @Bean
    public ServletListenerRegistrationBean requestContextListener() {
        return new ServletListenerRegistrationBean(new RequestContextListener());
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder());
    }


    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Collections.singletonList("*"));
        config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    AuthenticationFailureHandler eventAuthenticationFailureHandler() {
        return new EventSendingAuthenticationFailureHandler();
    }

}