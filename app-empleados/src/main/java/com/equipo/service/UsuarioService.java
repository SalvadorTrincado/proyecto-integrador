package com.equipo.service;

import com.equipo.dto.RegistroUsuarioDTO;
import com.equipo.entity.Usuario;
import com.equipo.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Asegúrate de definir un bean para esto

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    // Nuevo método para registrar usuario desde RegistroUsuarioDTO (con nombre, sin archivo)
    public void registrarNuevoUsuarioDesdeRegistro(RegistroUsuarioDTO dto) throws Exception {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            logger.error("Las contraseñas no coinciden: {}", dto.getEmail());
            throw new Exception("Las contraseñas no coinciden");
        }

        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            logger.warn("Ya existe un usuario con este correo: {}", dto.getEmail());
            throw new Exception("Ya existe un usuario con ese correo");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre()); // Establecemos el nombre
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));

        usuarioRepository.save(usuario);
        logger.info("Usuario registrado con éxito: {}", usuario.getEmail());
    }

    public Usuario registrarNuevoUsuario(com.equipo.dto.RegistroUsuarioDTO dto) throws Exception {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            logger.error("Las contraseñas no coinciden: {}", dto.getEmail());
            throw new Exception("Las contraseñas no coinciden");
        }

        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            logger.warn("Ya existe un usuario con este correo: {}", dto.getEmail());
            throw new Exception("Ya existe un usuario con ese correo");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));

        // Bloque para guardar el archivo si existe
        /*
        if (dto.getArchivo() != null && !dto.getArchivo().isEmpty()) {
            // Guarda el archivo y asigna rutaArchivo
            String ruta = guardarArchivo(dto.getArchivo());
            usuario.setRutaArchivo(ruta);
        }
        */

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        logger.info("Usuario registrado con éxito: {}", usuarioGuardado.getEmail());

        return usuarioGuardado;
    }

    // Método para guardar archivos
    /*
    private String guardarArchivo(MultipartFile archivo) throws IOException {
        String nombreArchivo = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
        Path ruta = Paths.get("uploads").resolve(nombreArchivo);
        Files.createDirectories(ruta.getParent());
        archivo.transferTo(ruta.toFile());
        return ruta.toString();
    }
    */
}