package com.equipo.Configuration;

import com.equipo.service.UserDetailService; // Asegúrate que es el UserDetailService de app-empleados
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
    public AuthenticationManager authenticationManager(HttpSecurity http, UserDetailService userDetailService, PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailService).passwordEncoder(passwordEncoder);
        return builder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(
                                "/css/**", // Permitir acceso a CSS
                                "/js/**",  // Permitir acceso a JS (si tienes)
                                "/h2-console/**",
                                "/autenticacion/**",      // Login paso 1 y 2
                                "/registrar_usuario",     // Registro de nuevo usuario
                                "/registrar_usuario_post",
                                "/recuperar_password",    // Recuperación de contraseña
                                "/forgot-password"
                        ).permitAll()
                        .requestMatchers(
                                "/aplicacion_corporativa/registro/**", // Pasos del registro de empleado
                                "/resumen/exito",
                                "/resumen/exito-post",
                                "/aplicacion_corporativa/area_personal",
                                "/empleado/nominas/**" // Nueva ruta para nóminas de empleado
                        ).authenticated() // Requieren autenticación
                        .anyRequest().permitAll() // O .denyAll() o .authenticated() según política general
                )
                .formLogin((form) -> form
                        .loginPage("/autenticacion/paso1")
                        .loginProcessingUrl("/autenticacion/paso2-post") // Spring se encarga de esta URL
                        .usernameParameter("email") // Parámetro del formulario para el email
                        .passwordParameter("password") // Parámetro del formulario para la contraseña
                        .defaultSuccessUrl("/aplicacion_corporativa/area_personal", true) // Redirigir siempre aquí tras login exitoso
                        .failureUrl("/autenticacion/paso2?error=true") // Página a mostrar en caso de fallo de login
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutUrl("/logout") // Define la URL para hacer logout
                        .logoutSuccessUrl("/autenticacion/paso1?logout") // Redirige aquí tras logout exitoso
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .csrf((csrf) -> csrf.disable()) // CSRF deshabilitado como estaba
                .headers((headers) -> headers
                        .frameOptions((frameOptions) -> frameOptions.sameOrigin()) // Para H2 console
                );

        return http.build();
    }
}