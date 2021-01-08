package com.ankat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;


@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${digest.realm.name}")
    private String realmName;

    @Value("${digest.realm.secrect}")
    private String realmSecrect;

    @Value("${digest.realm.validitySeconds}")
    private int realmValiditySeconds;

    private DigestAuthenticationEntryPoint getDigestEntryPoint() {
        DigestAuthenticationEntryPoint digestEntryPoint = new DigestAuthenticationEntryPoint();
        digestEntryPoint.setRealmName(realmName);
        digestEntryPoint.setKey(realmSecrect);
        digestEntryPoint.setNonceValiditySeconds(realmValiditySeconds);
        return digestEntryPoint;
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(User.withUsername("admin").password(getPasswordEncoder().encode("admin123")).roles("ADMIN"))
                .withUser(User.withUsername("user").password(getPasswordEncoder().encode("user123")).roles("USER"));
    }

    @Bean
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return super.userDetailsServiceBean();
    }

    private DigestAuthenticationFilter getDigestAuthFilter() throws Exception {
        DigestAuthenticationFilter digestFilter = new DigestAuthenticationFilter();
        digestFilter.setUserDetailsService(userDetailsServiceBean());
        digestFilter.setAuthenticationEntryPoint(getDigestEntryPoint());
        return digestFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .antMatcher("/**")
                .addFilter(getDigestAuthFilter())
                .exceptionHandling()
                .authenticationEntryPoint(getDigestEntryPoint())
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();
    }
}