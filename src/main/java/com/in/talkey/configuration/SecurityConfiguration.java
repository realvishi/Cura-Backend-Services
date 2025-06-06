package com.in.talkey.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration{

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    SecurityConfiguration(AuthenticationProvider authenticationProvider, JwtAuthenticationFilter jwtAuthenticationFilter){
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }




    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception{
       http.csrf(AbstractHttpConfigurer::disable)
               .authorizeHttpRequests(auth ->
                       auth.requestMatchers("/auth/**")

                               .permitAll()
                               .anyRequest()
                               .authenticated()
               )
               .sessionManagement(session ->
                       session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
               )
               .authenticationProvider(authenticationProvider)
               .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
