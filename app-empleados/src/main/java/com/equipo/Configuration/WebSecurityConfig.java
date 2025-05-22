package com.equipo.Configuration;

import com.equipo.service.UserDetailService;
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
    public AuthenticationManager authenticationManager(HttpSecurity http, UserDetailService userDetailService) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        // Aquí podrías configurar usuarios en memoria si no quieres usar un servicio de usuario personalizado
        builder.userDetailsService(userDetailService).passwordEncoder(passwordEncoder());
        return builder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(
                                "/controller/**",
                                "/templates/**",
                                "/static/css/**", // Permitimos acceso sin autenticación a todos los archivos CSS
                                "/js/**",
                                "/h2-console/**",
                                "/autenticacion/**",
                                "/aplicacion_corporativa/registro/**"
                        ).permitAll()
                        .requestMatchers("/aplicacion_corporativa/**").authenticated() // Todas las rutas bajo /aplicacion_corporativa/ requieren autenticación
                        .anyRequest().permitAll() // Permite el acceso a cualquier otra ruta (ajústalo según tus necesidades)
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