package com.equipo.service;

import com.equipo.dto.ColaboracionDisplayDTO;
import com.equipo.dto.ColaboracionConChatDTO; // Nuevo DTO
import com.equipo.dto.MensajeDisplayDTO; // Nuevo DTO
import com.equipo.entity.Colaboracion;
import com.equipo.entity.Empleado;
import com.equipo.entity.EstadoColaboracion;
import com.equipo.entity.MensajeChat; // Nueva entidad
import com.equipo.entity.Usuario;
import com.equipo.repository.ColaboracionRepository;
import com.equipo.repository.EmpleadoRepository;
import com.equipo.repository.MensajeChatRepository; // Nuevo repositorio
import com.equipo.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ColaboracionService {

    private static final Logger logger = LoggerFactory.getLogger(ColaboracionService.class);
    private static final DateTimeFormatter CHAT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");


    private final ColaboracionRepository colaboracionRepository;
    private final EmpleadoRepository empleadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MensajeChatRepository mensajeChatRepository; // Inyección del nuevo repositorio

    @Autowired
    public ColaboracionService(ColaboracionRepository colaboracionRepository,
                               EmpleadoRepository empleadoRepository,
                               UsuarioRepository usuarioRepository,
                               MensajeChatRepository mensajeChatRepository) { // Añadir al constructor
        this.colaboracionRepository = colaboracionRepository;
        this.empleadoRepository = empleadoRepository;
        this.usuarioRepository = usuarioRepository;
        this.mensajeChatRepository = mensajeChatRepository; // Asignar
    }

    @Transactional
    public Colaboracion invitar(UUID idEmisor, String emailReceptor) {
        Empleado emisor = empleadoRepository.findById(idEmisor)
                .orElseThrow(() -> new EntityNotFoundException("Empleado emisor no encontrado con ID: " + idEmisor));

        Usuario usuarioReceptor = usuarioRepository.findByEmail(emailReceptor)
                .orElseThrow(() -> new IllegalArgumentException("No existe un usuario registrado con el email: " + emailReceptor + "."));

        Empleado receptor = empleadoRepository.findById(usuarioReceptor.getId())
                .orElseThrow(() -> new IllegalArgumentException("El usuario con email " + emailReceptor + " no ha completado su perfil de empleado."));

        if (emisor.getId().equals(receptor.getId())) {
            throw new IllegalArgumentException("No puedes enviarte una invitación a ti mismo.");
        }

        // Se podría permitir volver a invitar si la colaboración anterior fue CANCELADA o RECHAZADA.
        // Por ahora, solo prevenimos duplicados PENDIENTES o ACEPTADAS.
        Optional<Colaboracion> existentePendienteOActiva = colaboracionRepository.findByEmisorAndReceptorAndEstadoIn(
                emisor, receptor, Arrays.asList(EstadoColaboracion.PENDIENTE, EstadoColaboracion.ACEPTADA)
        );
        if (existentePendienteOActiva.isPresent()) {
            throw new IllegalStateException("Ya existe una invitación pendiente o una colaboración activa con " + receptor.getNombre() + " " + receptor.getApellidos() + ".");
        }
        Optional<Colaboracion> existenteReceptorEmisor = colaboracionRepository.findByEmisorAndReceptorAndEstadoIn(
                receptor, emisor, Arrays.asList(EstadoColaboracion.PENDIENTE, EstadoColaboracion.ACEPTADA)
        );
        if (existenteReceptorEmisor.isPresent()) {
            throw new IllegalStateException("Ya existe una invitación pendiente o una colaboración activa donde tú eres el receptor de " + receptor.getNombre() + " " + receptor.getApellidos() + ".");
        }


        Colaboracion colaboracion = new Colaboracion();
        colaboracion.setEmisor(emisor);
        colaboracion.setReceptor(receptor);
        colaboracion.setEstado(EstadoColaboracion.PENDIENTE);
        colaboracion.setFechaInvitacion(LocalDateTime.now());

        logger.info("Invitación de colaboración creada de {} para {}", emisor.getNombre(), receptor.getNombre());
        return colaboracionRepository.save(colaboracion);
    }

    @Transactional
    public Colaboracion aceptarInvitacion(UUID idColaboracion, UUID idReceptor) {
        Colaboracion colaboracion = colaboracionRepository.findById(idColaboracion)
                .orElseThrow(() -> new EntityNotFoundException("Invitación no encontrada."));

        if (!colaboracion.getReceptor().getId().equals(idReceptor)) {
            throw new SecurityException("No tienes permiso para aceptar esta invitación.");
        }
        if (colaboracion.getEstado() != EstadoColaboracion.PENDIENTE) {
            throw new IllegalStateException("Esta invitación ya ha sido respondida (" + colaboracion.getEstado() + ").");
        }

        colaboracion.setEstado(EstadoColaboracion.ACEPTADA);
        colaboracion.setFechaRespuesta(LocalDateTime.now());
        logger.info("Invitación {} aceptada por {}", idColaboracion, idReceptor);
        return colaboracionRepository.save(colaboracion);
    }

    @Transactional
    public Colaboracion rechazarInvitacion(UUID idColaboracion, UUID idReceptor) {
        Colaboracion colaboracion = colaboracionRepository.findById(idColaboracion)
                .orElseThrow(() -> new EntityNotFoundException("Invitación no encontrada."));

        if (!colaboracion.getReceptor().getId().equals(idReceptor)) {
            throw new SecurityException("No tienes permiso para rechazar esta invitación.");
        }
        if (colaboracion.getEstado() != EstadoColaboracion.PENDIENTE) {
            throw new IllegalStateException("Esta invitación ya ha sido respondida (" + colaboracion.getEstado() + ").");
        }

        colaboracion.setEstado(EstadoColaboracion.RECHAZADA);
        colaboracion.setFechaRespuesta(LocalDateTime.now());
        logger.info("Invitación {} rechazada por {}", idColaboracion, idReceptor);
        return colaboracionRepository.save(colaboracion);
    }

    // --- NUEVOS MÉTODOS ---
    @Transactional
    public Colaboracion cancelarColaboracion(UUID idColaboracion, UUID idUsuarioCancelador) {
        Colaboracion colaboracion = colaboracionRepository.findById(idColaboracion)
                .orElseThrow(() -> new EntityNotFoundException("Colaboracion no encontrada con ID: " + idColaboracion));

        Empleado usuarioCancelador = empleadoRepository.findById(idUsuarioCancelador)
                .orElseThrow(() -> new EntityNotFoundException("Usuario cancelador no encontrado con ID: " + idUsuarioCancelador));

        if (!colaboracion.getEmisor().getId().equals(idUsuarioCancelador) &&
                !colaboracion.getReceptor().getId().equals(idUsuarioCancelador)) {
            throw new SecurityException("No tienes permiso para cancelar esta colaboración.");
        }

        if (colaboracion.getEstado() != EstadoColaboracion.ACEPTADA) {
            throw new IllegalStateException("Solo se pueden cancelar colaboraciones que están actualmente ACEPTADAS. Estado actual: " + colaboracion.getEstado());
        }

        colaboracion.setEstado(EstadoColaboracion.CANCELADA);
        colaboracion.setCanceladaPor(usuarioCancelador);
        colaboracion.setFechaCancelacion(LocalDateTime.now());
        logger.info("Colaboración {} cancelada por empleado {}", idColaboracion, idUsuarioCancelador);
        return colaboracionRepository.save(colaboracion);
    }

    @Transactional
    public MensajeChat enviarMensaje(UUID idColaboracion, UUID idEmisor, String textoMensaje) {
        if (textoMensaje == null || textoMensaje.trim().isEmpty()) {
            throw new IllegalArgumentException("El texto del mensaje no puede estar vacío.");
        }
        if (textoMensaje.length() > 1000) { // Límite de ejemplo
            throw new IllegalArgumentException("El mensaje excede la longitud máxima permitida.");
        }

        Colaboracion colaboracion = colaboracionRepository.findById(idColaboracion)
                .orElseThrow(() -> new EntityNotFoundException("Colaboracion no encontrada con ID: " + idColaboracion));

        if (colaboracion.getEstado() != EstadoColaboracion.ACEPTADA) {
            throw new IllegalStateException("No se pueden enviar mensajes a una colaboración que no está activa (ACEPTADA). Estado actual: " + colaboracion.getEstado());
        }

        Empleado emisor = empleadoRepository.findById(idEmisor)
                .orElseThrow(() -> new EntityNotFoundException("Empleado emisor no encontrado con ID: " + idEmisor));

        if (!colaboracion.getEmisor().getId().equals(idEmisor) &&
                !colaboracion.getReceptor().getId().equals(idEmisor)) {
            throw new SecurityException("No tienes permiso para enviar mensajes en esta colaboración.");
        }

        MensajeChat nuevoMensaje = new MensajeChat();
        nuevoMensaje.setColaboracion(colaboracion);
        nuevoMensaje.setEmisor(emisor);
        nuevoMensaje.setMensaje(textoMensaje);
        nuevoMensaje.setFechaHoraEnvio(LocalDateTime.now());
        nuevoMensaje.setLeido(false); // El mensaje es nuevo, no leído por el receptor aún

        logger.info("Mensaje enviado por {} en colaboración {}", idEmisor, idColaboracion);
        return mensajeChatRepository.save(nuevoMensaje);
    }

    @Transactional
    public List<MensajeChat> obtenerMensajesYMarcarComoLeidos(UUID idColaboracion, UUID idUsuarioActual) {
        Colaboracion colaboracion = colaboracionRepository.findById(idColaboracion)
                .orElseThrow(() -> new EntityNotFoundException("Colaboracion no encontrada con ID: " + idColaboracion));

        Empleado usuarioActual = empleadoRepository.findById(idUsuarioActual)
                .orElseThrow(() -> new EntityNotFoundException("Usuario actual no encontrado con ID: " + idUsuarioActual));

        if (!colaboracion.getEmisor().equals(usuarioActual) && !colaboracion.getReceptor().equals(usuarioActual)) {
            throw new SecurityException("No tienes permiso para ver los mensajes de esta colaboración.");
        }

        List<MensajeChat> mensajes = mensajeChatRepository.findByColaboracionOrderByFechaHoraEnvioAsc(colaboracion);

        // Marcar mensajes como leídos si el usuario actual es el receptor del mensaje (y no el emisor)
        for (MensajeChat mensaje : mensajes) {
            // El receptor de un mensaje es el participante de la colaboración que NO es el emisor del mensaje
            Empleado receptorDelMensaje = colaboracion.getEmisor().equals(mensaje.getEmisor()) ?
                    colaboracion.getReceptor() : colaboracion.getEmisor();

            if (receptorDelMensaje.equals(usuarioActual) && !mensaje.isLeido()) {
                mensaje.setLeido(true);
                mensajeChatRepository.save(mensaje); // Guardar cada mensaje modificado
            }
        }
        return mensajes;
    }


    @Transactional(readOnly = true)
    public ColaboracionConChatDTO obtenerColaboracionConChatParaVista(UUID idColaboracion, UUID idUsuarioActual) {
        Colaboracion colaboracion = colaboracionRepository.findById(idColaboracion)
                .orElseThrow(() -> new EntityNotFoundException("Colaboración no encontrada con ID: " + idColaboracion));

        Empleado empleadoActual = empleadoRepository.findById(idUsuarioActual)
                .orElseThrow(() -> new EntityNotFoundException("Usuario actual no encontrado con ID: " + idUsuarioActual));

        if (!colaboracion.getEmisor().getId().equals(idUsuarioActual) &&
                !colaboracion.getReceptor().getId().equals(idUsuarioActual)) {
            throw new SecurityException("No tiene permiso para acceder a esta colaboración.");
        }

        // Obtener mensajes y marcarlos como leídos (se hace dentro de este método)
        List<MensajeChat> mensajesEntidad = obtenerMensajesYMarcarComoLeidos(idColaboracion, idUsuarioActual);


        List<MensajeDisplayDTO> mensajesDisplay = mensajesEntidad.stream()
                .map(msg -> new MensajeDisplayDTO(
                        msg.getEmisor().getNombre() + " " + msg.getEmisor().getApellidos(),
                        msg.getMensaje(),
                        msg.getFechaHoraEnvio().format(CHAT_DATE_FORMATTER),
                        msg.getEmisor().getId().equals(idUsuarioActual)
                ))
                .collect(Collectors.toList());

        String nombreOtroParticipante;
        if (colaboracion.getEmisor().getId().equals(idUsuarioActual)) {
            nombreOtroParticipante = colaboracion.getReceptor().getNombre() + " " + colaboracion.getReceptor().getApellidos();
        } else {
            nombreOtroParticipante = colaboracion.getEmisor().getNombre() + " " + colaboracion.getEmisor().getApellidos();
        }

        ColaboracionConChatDTO dto = new ColaboracionConChatDTO();
        dto.setIdColaboracion(colaboracion.getId());
        dto.setNombreOtroParticipante(nombreOtroParticipante);
        dto.setEstado(colaboracion.getEstado().name());
        dto.setMensajes(mensajesDisplay);
        dto.setPuedeChatear(colaboracion.getEstado() == EstadoColaboracion.ACEPTADA);
        dto.setPuedeCancelar(colaboracion.getEstado() == EstadoColaboracion.ACEPTADA);

        return dto;
    }

    // Método para actualizar ColaboracionDisplayDTO
    private ColaboracionDisplayDTO convertToDisplayDTO(Colaboracion colaboracion, UUID usuarioActualId) {
        ColaboracionDisplayDTO dto = new ColaboracionDisplayDTO();
        dto.setId(colaboracion.getId());
        dto.setEstado(colaboracion.getEstado());
        dto.setFechaInvitacion(colaboracion.getFechaInvitacion());
        dto.setFechaRespuesta(colaboracion.getFechaRespuesta());

        // Lógica para saber si el usuario actual puede cancelar o chatear
        boolean esParticipante = colaboracion.getEmisor().getId().equals(usuarioActualId) ||
                colaboracion.getReceptor().getId().equals(usuarioActualId);

        dto.setPuedeChatear(esParticipante && colaboracion.getEstado() == EstadoColaboracion.ACEPTADA);
        dto.setPuedeCancelar(esParticipante && colaboracion.getEstado() == EstadoColaboracion.ACEPTADA);


        if (colaboracion.getEmisor() != null) {
            dto.setEmisorId(colaboracion.getEmisor().getId());
            dto.setEmisorNombreCompleto(colaboracion.getEmisor().getNombre() + " " + colaboracion.getEmisor().getApellidos());
            usuarioRepository.findById(colaboracion.getEmisor().getId())
                    .ifPresent(u -> dto.setEmisorEmail(u.getEmail()));
        }

        if (colaboracion.getReceptor() != null) {
            dto.setReceptorId(colaboracion.getReceptor().getId());
            dto.setReceptorNombreCompleto(colaboracion.getReceptor().getNombre() + " " + colaboracion.getReceptor().getApellidos());
            usuarioRepository.findById(colaboracion.getReceptor().getId())
                    .ifPresent(u -> dto.setReceptorEmail(u.getEmail()));
        }

        if (colaboracion.getEstado() == EstadoColaboracion.CANCELADA) {
            dto.setFechaCancelacion(colaboracion.getFechaCancelacion());
            if (colaboracion.getCanceladaPor() != null) {
                dto.setCanceladaPorNombre(colaboracion.getCanceladaPor().getNombre() + " " + colaboracion.getCanceladaPor().getApellidos());
            }
        }

        return dto;
    }

    // Modificar los métodos existentes para usar el nuevo convertToDisplayDTO
    @Transactional(readOnly = true)
    public List<ColaboracionDisplayDTO> findInvitacionesEnviadasDisplay(UUID idEmisor) {
        Empleado emisor = empleadoRepository.findById(idEmisor)
                .orElseThrow(() -> new EntityNotFoundException("Empleado emisor no encontrado con ID: " + idEmisor));
        return colaboracionRepository.findByEmisorOrderByFechaInvitacionDesc(emisor)
                .stream()
                .map(colab -> convertToDisplayDTO(colab, idEmisor)) // Pasar idEmisor como usuarioActualId
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ColaboracionDisplayDTO> findInvitacionesRecibidasDisplay(UUID idReceptor) {
        Empleado receptor = empleadoRepository.findById(idReceptor)
                .orElseThrow(() -> new EntityNotFoundException("Empleado receptor no encontrado con ID: " + idReceptor));
        return colaboracionRepository.findByReceptorOrderByFechaInvitacionDesc(receptor)
                .stream()
                .map(colab -> convertToDisplayDTO(colab, idReceptor)) // Pasar idReceptor como usuarioActualId
                .collect(Collectors.toList());
    }


    // Métodos originales (pueden eliminarse si ya no se usan directamente)
    @Transactional(readOnly = true)
    public List<Colaboracion> findInvitacionesEnviadas(UUID idEmisor) {
        Empleado emisor = empleadoRepository.findById(idEmisor)
                .orElseThrow(() -> new EntityNotFoundException("Empleado emisor no encontrado."));
        return colaboracionRepository.findByEmisorOrderByFechaInvitacionDesc(emisor);
    }

    @Transactional(readOnly = true)
    public List<Colaboracion> findInvitacionesRecibidas(UUID idReceptor) {
        Empleado receptor = empleadoRepository.findById(idReceptor)
                .orElseThrow(() -> new EntityNotFoundException("Empleado receptor no encontrado."));
        return colaboracionRepository.findByReceptorOrderByFechaInvitacionDesc(receptor);
    }

    @Transactional(readOnly = true)
    public List<Colaboracion> findInvitacionesRecibidasPendientes(UUID idReceptor) {
        Empleado receptor = empleadoRepository.findById(idReceptor)
                .orElseThrow(() -> new EntityNotFoundException("Empleado receptor no encontrado."));
        return colaboracionRepository.findByReceptorAndEstadoOrderByFechaInvitacionDesc(receptor, EstadoColaboracion.PENDIENTE);
    }
}