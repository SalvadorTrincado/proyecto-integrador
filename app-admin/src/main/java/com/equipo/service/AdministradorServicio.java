package com.equipo.service;

import com.equipo.entity.Administrador;
import com.equipo.repository.AdministradorRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service // Indica que esta clase es un servicio de Spring
public class AdministradorServicio {

    private final AdministradorRepository administradorRepositorio;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdministradorServicio(AdministradorRepository administradorRepositorio, PasswordEncoder passwordEncoder) {
        this.administradorRepositorio = administradorRepositorio;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Autentica a un administrador basado en su email y contraseña.
     */
    public Optional<Administrador> autenticarAdministrador(String email, String contraseña) {
        Optional<Administrador> administradorOptional = administradorRepositorio.findByEmail(email);

        if (administradorOptional.isPresent()) {
            Administrador administrador = administradorOptional.get();
            // Verifica si la contraseña proporcionada coincide con la contraseña hasheada almacenada
            if (passwordEncoder.matches(contraseña, administrador.getPassword())) {
                // Si la autenticación es exitosa, incrementa el número de autenticaciones
                incrementarNumeroAutenticaciones(administrador);
                return Optional.of(administrador);
            }
        }
        // Si el email no se encuentra o la contraseña no coincide
        return Optional.empty();
    }

    /**
     * Incrementa el contador de autenticaciones válidas de un administrador.
     */
    public void incrementarNumeroAutenticaciones(Administrador administrador) {
        administrador.setNumeroAutenticacion(administrador.getNumeroAutenticacion() + 1);
        administradorRepositorio.save(administrador);
    }

    public void incrementarNumeroAutenticacionesFallidas(String email) {
        administradorRepositorio.findByEmail(email)
                .ifPresent(administrador -> {
                    administrador.setNumeroAutenticacion(administrador.getNumeroAutenticacion() + 1); // También incrementamos el contador general aquí
                    administradorRepositorio.save(administrador);
                    // Aquí podrías implementar lógica adicional como bloqueo de cuenta tras varios fallos.
                });
    }

    public void cargarAdministradoresDesdeJson() {
        try (InputStream inputStream = new ClassPathResource("administradores.json").getInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            List<Administrador> admins = mapper.readValue(inputStream, new TypeReference<>() {});

            for (Administrador admin : admins) {
                if (admin.getEmail() != null && !administradorRepositorio.findByEmail(admin.getEmail()).isPresent()) {
                    admin.setPassword(passwordEncoder.encode(admin.getPassword())); // 🔐 Encode before saving
                    administradorRepositorio.save(admin);
                }
            }


        } catch (IOException e) {
            throw new RuntimeException("Error cargando administradores desde JSON", e);
        }
    }
}