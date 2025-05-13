package com.equipo.Configuration;

import com.equipo.service.AutenticacionService; // Importa AutenticacionService
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, AutenticacionService autenticacionService) throws Exception { // Usa AutenticacionService
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        // Aquí configuramos el UserDetailsService.  IMPORTANTE: Usamos AutenticacionService
        builder.userDetailsService(autenticacionService).passwordEncoder(passwordEncoder());
        return builder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(
                                "/controller/**",
                                "/registro/**",
                                "/static/css/**",
                                "/js/**",
                                "/h2/**",
                                "/autenticacion/**"
                        ).permitAll()
                        .requestMatchers("/aplicacion_corporativa/**").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin((form) -> form
                        .loginPage("/autenticacion/paso1")
                        .loginProcessingUrl("/autenticacion/paso2-post")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/aplicacion_corporativa/area_personal", true)
                        .failureUrl("/autenticacion/paso2?error=true")
                        .permitAll()
                )
                .logout((logout) -> logout
                        .permitAll()
                        .logoutSuccessUrl("/autenticacion/paso1")
                )
                .csrf((csrf) -> csrf.disable())
                .headers((headers) -> headers
                        .frameOptions((frameOptions) -> frameOptions.sameOrigin())
                );

        return http.build();
    }
}