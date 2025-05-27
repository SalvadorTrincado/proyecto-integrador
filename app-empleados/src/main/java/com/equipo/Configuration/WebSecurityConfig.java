package com.equipo.Configuration;

// ... otras importaciones ...
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
            com.equipo.service.AutenticacionService autenticacionService) { // Asegúrate que esta es tu clase de servicio
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
            com.equipo.service.AutenticacionService autenticacionService) { // Asegúrate que esta es tu clase de servicio
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
                        .requestMatchers(
                                "/css/**", "/js/**", "/h2-console/**",
                                "/autenticacion/paso1", "/autenticacion/paso1-post",
                                "/autenticacion/paso2",
                                "/registrar_usuario", "/registrar_usuario_post",
                                "/recuperar-password", // Ruta para la vista de recuperación
                                "/api/password-recovery/**" // Rutas de la API para recuperación
                        ).permitAll()
                        .requestMatchers(
                                "/aplicacion_corporativa/registro/**",
                                "/resumen/exito", "/resumen/exito-post",
                                "/aplicacion_corporativa/area_personal",
                                "/empleado/nominas/**",
                                "/empleado/modificar-datos",
                                "/empleado/colaboraciones/**"
                        ).authenticated()
                        .anyRequest().permitAll() // Ajusta según necesidad, podría ser .authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/autenticacion/paso1")
                        .loginProcessingUrl("/autenticacion/paso2-post")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/autenticacion/paso1?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .csrf((csrf) -> csrf
                        .ignoringRequestMatchers("/h2-console/**", "/api/**") // Ignorar CSRF para H2 y API REST
                )
                .headers((headers) -> headers
                        .frameOptions((frameOptions) -> frameOptions.sameOrigin())
                );

        return http.build();
    }
}