package com.equipo.Configuration;
/*
import com.equipo.entity.Administrador;
import com.equipo.comun.service.AdministradorServicio;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.util.List;

@Configuration
public class CargaInicialAdministradores {

    @Bean
    public CommandLineRunner cargarAdminsDesdeJson(AdministradorServicio servicio) {
        return args -> {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<List<Administrador>> tipoLista = new TypeReference<>() {};

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("administradores.json");

            if (inputStream == null) {
                System.out.println("No se encontró el archivo administradores.json");
                return;
            }

            try {
                List<Administrador> admins = mapper.readValue(inputStream, tipoLista);
                for (Administrador admin : admins) {
                    servicio.crearAdmin(admin); // método del servicio que ya vimos
                }
                System.out.println("Administradores cargados desde JSON correctamente.");
            } catch (Exception e) {
                System.out.println("Error cargando JSON: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}*/