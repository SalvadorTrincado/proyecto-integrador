package com.equipo.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(
                                new AntPathRequestMatcher("/h2/**"),
                                new AntPathRequestMatcher("/login/administrador"),
                                new AntPathRequestMatcher("/admin/area_personal"),
                                new AntPathRequestMatcher("/logout")
                        ).permitAll()
                        .anyRequest().permitAll() // Permitir todas las peticiones sin autenticación
                )
                .formLogin(Customizer.withDefaults()) // Utiliza la página de login por defecto
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login/administrador")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .csrf((csrf) -> csrf.ignoringRequestMatchers(
                        new AntPathRequestMatcher("/h2/**")
                ))
                .headers((headers) -> headers
                        .frameOptions((frameOptions) -> frameOptions.sameOrigin())
                );

        return http.build();
    }
}