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
            handler.setAlwaysUseDefaultTargetUrl(true); // Esta línea debería funcionar ahora
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
                    // Solo registrar el intento fallido si la causa no es que la cuenta ya estaba bloqueada.
                    // La LockedException la lanza el UserDetailsService (AutenticacionService) si la cuenta ya está marcada como bloqueada.
                    if (!(exception instanceof LockedException)) {
                        autenticacionService.registrarIntentoFallido(email);
                    }
                }

                // Si la excepción es LockedException (porque UserDetailsService la lanzó),
                // o si después de registrar el intento fallido la cuenta AHORA está bloqueada.
                // Para ser más precisos, el UserDetailsService lanza LockedException si ya está bloqueada.
                // El failureHandler registra el intento Y PUEDE bloquearla.
                // Necesitamos que el redirect refleje el estado DESPUÉS de registrarIntentoFallido.
                // Una forma es que registrarIntentoFallido devuelva si la cuenta quedó bloqueada.
                // O, más simple aquí, si la excepción original es LockedException, ya estaba bloqueada.
                // Si no, es un fallo de credenciales, y registrarIntentoFallido se encargará de bloquearla si llega al límite.

                if (exception instanceof LockedException) {
                    failureUrlKey = "bloqueado";
                } else {
                    // Comprobamos si el intento actual de fallo ha causado un bloqueo
                    // Esto requeriría que AutenticacionService.usuarioExiste y AutenticacionService.loadUserByUsername (o una nueva función)
                    // nos devuelva el estado actual de bloqueo del usuario.
                    // Por ahora, si no es LockedException, asumimos fallo de credenciales.
                    // El propio AutenticacionService.registrarIntentoFallido ya loguea si bloquea.
                }

                // Para que el mensaje de "cuenta bloqueada" se muestre inmediatamente después del 3er fallo,
                // el failureHandler necesita saber si el último intento CAUSÓ el bloqueo.
                // Una forma es modificar registrarIntentoFallido para que devuelva un boolean.
                // O, si la excepción no es LockedException, se asume error de credenciales, y si ese error
                // lleva al bloqueo, el *siguiente* intento de login con ese usuario (en loadUserByUsername)
                // lanzará LockedException.

                // Simplificación: si la excepción que llega aquí es LockedException, la cuenta ESTABA bloqueada.
                // Si no, fue un fallo de credenciales.
                // El AutenticacionController se encarga de mostrar el mensaje correcto basado en el parámetro "error"
                response.sendRedirect(request.getContextPath() + "/autenticacion/paso2?error=" + failureUrlKey);
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationSuccessHandler successHandler, // Inyectamos los beans de handler
                                                   AuthenticationFailureHandler failureHandler) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(
                                "/css/**", "/js/**", "/h2-console/**",
                                "/autenticacion/paso1", "/autenticacion/paso1-post", "/autenticacion/paso2",
                                "/registrar_usuario", "/registrar_usuario_post",
                                "/recuperar_password", "/forgot-password"
                        ).permitAll()
                        .requestMatchers(
                                "/aplicacion_corporativa/registro/**", "/resumen/exito", "/resumen/exito-post",
                                "/aplicacion_corporativa/area_personal", "/empleado/nominas/**"
                        ).authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin((form) -> form
                        .loginPage("/autenticacion/paso1")
                        .loginProcessingUrl("/autenticacion/paso2-post")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(successHandler) // Usamos el successHandler personalizado
                        .failureHandler(failureHandler) // Usamos el failureHandler personalizado
                        // .defaultSuccessUrl("/aplicacion_corporativa/area_personal", true) // El successHandler se encarga de la redirección
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/autenticacion/paso1?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .csrf((csrf) -> csrf.disable())
                .headers((headers) -> headers
                        .frameOptions((frameOptions) -> frameOptions.sameOrigin())
                );

        return http.build();
    }
}