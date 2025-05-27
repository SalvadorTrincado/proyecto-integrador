package com.equipo.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

@Configuration
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       UserDetailsService userDetailServiceBean,
                                                       PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailServiceBean).passwordEncoder(passwordEncoder);
        return builder.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler(
            com.equipo.service.AutenticacionService autenticacionService) {
        return (request, response, authentication) -> {
            String username = authentication.getName();
            if (username != null) {
                autenticacionService.registrarIntentoExitoso(username);
            }

            SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
            handler.setDefaultTargetUrl("/aplicacion_corporativa/area_personal");
            handler.setAlwaysUseDefaultTargetUrl(true);
            handler.onAuthenticationSuccess(request, response, authentication);
        };
    }

    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler(
            com.equipo.service.AutenticacionService autenticacionService) {
        return (request, response, exception) -> {
            String email = request.getParameter("email");
            String failureUrlKey = "credenciales";

            if (email != null && !email.isEmpty()) {
                if (!(exception instanceof org.springframework.security.authentication.LockedException)) {
                    autenticacionService.registrarIntentoFallido(email);
                }
            }

            if (exception instanceof org.springframework.security.authentication.LockedException) {
                failureUrlKey = "bloqueado";
            }

            response.sendRedirect(request.getContextPath() + "/autenticacion/paso2?email=" + (email != null ? email : "") + "&error=" + failureUrlKey);
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationSuccessHandler successHandler,
                                                   AuthenticationFailureHandler failureHandler) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers( // Rutas públicas
                                "/css/**",
                                "/js/**", // Si tienes JS globales que deban ser públicos
                                "/h2-console/**",
                                "/autenticacion/paso1",
                                "/autenticacion/paso1-post",
                                "/autenticacion/paso2", // La página para introducir contraseña
                                "/registrar_usuario",
                                "/registrar_usuario_post",
                                "/recuperar-password",
                                "/api/password-recovery/**" // API para recuperación de contraseña
                        ).permitAll()
                        // Todas las demás rutas requerirán autenticación
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/autenticacion/paso1") // Página de login personalizada
                        .loginProcessingUrl("/autenticacion/paso2-post") // URL a la que se envía el formulario de login (paso 2)
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                        .permitAll() // Permite el acceso a la página de login y al procesamiento del login
                )
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/autenticacion/paso1?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .csrf((csrf) -> csrf
                        .ignoringRequestMatchers("/h2-console/**", "/api/**") // Ignorar CSRF para H2 console y tu API REST
                )
                .headers((headers) -> headers
                        .frameOptions((frameOptions) -> frameOptions.sameOrigin()) // Necesario para H2 Console
                );

        return http.build();
    }
}