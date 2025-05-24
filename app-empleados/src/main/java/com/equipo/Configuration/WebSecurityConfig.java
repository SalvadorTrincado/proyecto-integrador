package com.equipo.Configuration;

import com.equipo.service.AutenticacionService;
// La siguiente importación es la interfaz de Spring Security.
import org.springframework.security.core.userdetails.UserDetailsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;

@Configuration
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       UserDetailsService userDetailServiceBean, // Spring inyectará AutenticacionService aquí
                                                       PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailServiceBean).passwordEncoder(passwordEncoder);
        return builder.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler(AutenticacionService autenticacionService) { // Inyecta AutenticacionService
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
    public AuthenticationFailureHandler customAuthenticationFailureHandler(AutenticacionService autenticacionService) { // Inyecta AutenticacionService
        return new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                                AuthenticationException exception) throws IOException, ServletException {
                String email = request.getParameter("email"); // El 'usernameParameter'
                String failureUrlKey = "credenciales";

                if (email != null && !email.isEmpty()) {
                    if (!(exception instanceof LockedException)) {
                        autenticacionService.registrarIntentoFallido(email);
                    }
                }

                if (exception instanceof LockedException) {
                    failureUrlKey = "bloqueado";
                }

                response.sendRedirect(request.getContextPath() + "/autenticacion/paso2?email=" + (email != null ? email : "") + "&error=" + failureUrlKey);
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationSuccessHandler successHandler,
                                                   AuthenticationFailureHandler failureHandler) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(
                                "/css/**", "/js/**", "/h2-console/**", // Recursos estáticos y consola H2
                                "/autenticacion/paso1", "/autenticacion/paso1-post",
                                "/autenticacion/paso2", // Permitir GET a paso2 para mostrar errores
                                "/registrar_usuario", "/registrar_usuario_post",
                                "/recuperar_password", "/forgot-password" // Funcionalidades de recuperación y registro
                        ).permitAll()
                        .requestMatchers(
                                "/aplicacion_corporativa/registro/**", // Pasos del registro de empleado
                                "/resumen/exito", "/resumen/exito-post", // Resumen y finalización del registro
                                "/aplicacion_corporativa/area_personal", // Área personal del empleado
                                "/empleado/nominas/**", // Acceso a las nóminas del empleado
                                "/empleado/modificar-datos" // NUEVA RUTA para modificar datos del empleado
                        ).authenticated() // Requieren autenticación
                        .anyRequest().permitAll() // Por defecto, permite otras rutas no especificadas (ajustar si es necesario a .authenticated())
                )
                .formLogin((form) -> form
                        .loginPage("/autenticacion/paso1") // Página de inicio de sesión (Paso 1)
                        .loginProcessingUrl("/autenticacion/paso2-post") // URL donde se procesa el login (Paso 2)
                        .usernameParameter("email") // Nombre del parámetro para el email en el form
                        .passwordParameter("password") // Nombre del parámetro para la contraseña
                        .successHandler(successHandler) // Manejador para login exitoso
                        .failureHandler(failureHandler) // Manejador para login fallido
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/autenticacion/paso1?logout") // Redirigir a paso1 con mensaje de logout
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .csrf((csrf) -> csrf
                                .ignoringRequestMatchers("/h2-console/**") // Deshabilitar CSRF para H2 console
                        // Si tienes problemas con POST en otros formularios y no manejas tokens CSRF, podrías añadir .disable() temporalmente
                        // .disable() // DESCOMENTAR SÓLO PARA PRUEBAS SI ES ESTRICTAMENTE NECESARIO
                )
                .headers((headers) -> headers
                        .frameOptions((frameOptions) -> frameOptions.sameOrigin()) // Necesario para H2 console
                );

        return http.build();
    }
}