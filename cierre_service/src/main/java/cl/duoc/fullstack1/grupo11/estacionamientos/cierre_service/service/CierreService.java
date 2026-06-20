package cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.client.PlazaClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.client.ReporteClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.client.ReservaClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.client.dto.PlazaInternaResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.client.dto.ReporteResumenResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.client.dto.ReservaInternaResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.dto.request.CierreEjecutarRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.dto.response.CierreResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.exception.CierreEjecucionException;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.exception.CierreNoEncontradoException;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.model.Cierre;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.repository.CierreRepository;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CierreService {

    private static final String ESTADO_DISPONIBLE = "Disponible";
    private static final String ESTADO_OCUPADA = "Ocupada";
    private static final String ESTADO_RESERVADA = "Reservada";

    private static final String ESTADO_CIERRE_EJECUTADO = "EJECUTADO";
    private static final String BEARER_PREFIX = "Bearer ";

    private final CierreRepository cierreRepository;
    private final PlazaClient plazaClient;
    private final ReservaClient reservaClient;
    private final ReporteClient reporteClient;
    private final JwtService jwtService;
    //private final ObjectMapper objectMapper;

    @Transactional
    public CierreResponse ejecutarCierre(
            CierreEjecutarRequest request,
            String authorizationHeader
    ) {
        String token = extraerToken(authorizationHeader);

        Long idUsuarioEjecutor = jwtService.obtenerIdUsuarioDesdeToken(token);
        String emailUsuarioEjecutor = jwtService.obtenerEmailDesdeToken(token);
        String rolEjecutor = jwtService.obtenerRolDesdeToken(token);

        log.info(
            "Iniciando cierre operativo idUsuarioEjecutor={} email={} rol={}",
            idUsuarioEjecutor,
            emailUsuarioEjecutor,
            rolEjecutor
        );

        LocalDateTime fechaHoraCierre = LocalDateTime.now();

        List<PlazaInternaResponse> plazasAntesDelCierre = plazaClient.listarPlazas(authorizationHeader);

        int totalPlazasOcupadasLiberadas = liberarPlazasOcupadas(
                plazasAntesDelCierre,
                authorizationHeader
        );

        log.info("Total de plazas ocupadas liberadas={}", totalPlazasOcupadasLiberadas);

        List<ReservaInternaResponse> reservas = reservaClient.listarReservas(authorizationHeader);

        log.info("Plazas obtenidas antes del cierre cantidad={}", plazasAntesDelCierre.size());

        ResultadoLimpiezaReservas resultadoReservas = eliminarReservasYLiberarPlazas(
                reservas,
                authorizationHeader
        );

        log.info(
            "Resultado limpieza reservas totalReservasEliminadas={} totalPlazasReservadasLiberadas={}",
            resultadoReservas.getTotalReservasEliminadas(),
            resultadoReservas.getTotalPlazasReservadasLiberadas()
        );

        //ReporteResumenResponse resumenReporte = reporteClient.obtenerResumen(authorizationHeader);

        ReporteResumenResponse resumenReporte = reporteClient.obtenerResumenPorFecha(
                fechaHoraCierre.toLocalDate(),
                authorizationHeader
        );

        if (resumenReporte == null) {
            log.warn("reporte_service devolvió resumen null para fecha={}", fechaHoraCierre.toLocalDate());
        } else {
            log.info("Resumen diario obtenido desde reporte_service fecha={}", fechaHoraCierre.toLocalDate());
        }

        List<PlazaInternaResponse> plazasDespuesDelCierre = plazaClient.listarPlazas(authorizationHeader);

        long plazasDisponiblesFinal = contarPlazasPorEstado(plazasDespuesDelCierre, ESTADO_DISPONIBLE);
        long plazasOcupadasFinal = contarPlazasPorEstado(plazasDespuesDelCierre, ESTADO_OCUPADA);
        long plazasReservadasFinal = contarPlazasPorEstado(plazasDespuesDelCierre, ESTADO_RESERVADA);

        Cierre cierre = new Cierre();

        cierre.setFechaHoraCierre(fechaHoraCierre);
        cierre.setFechaCierre(fechaHoraCierre.toLocalDate());
        cierre.setHoraCierre(fechaHoraCierre.toLocalTime());

        cierre.setIdUsuarioEjecutor(idUsuarioEjecutor);
        cierre.setEmailUsuarioEjecutor(emailUsuarioEjecutor);
        cierre.setRolEjecutor(rolEjecutor);

        cierre.setTotalPlazasOcupadasLiberadas(totalPlazasOcupadasLiberadas);
        cierre.setTotalReservasEliminadas(resultadoReservas.getTotalReservasEliminadas());
        cierre.setTotalPlazasReservadasLiberadas(resultadoReservas.getTotalPlazasReservadasLiberadas());

        cierre.setPlazasDisponiblesFinal(toInteger(plazasDisponiblesFinal));
        cierre.setPlazasOcupadasFinal(toInteger(plazasOcupadasFinal));
        cierre.setPlazasReservadasFinal(toInteger(plazasReservadasFinal));

        log.info(
                "Estado final de plazas disponibles={} ocupadas={} reservadas={}",
                plazasDisponiblesFinal,
                plazasOcupadasFinal,
                plazasReservadasFinal
        );

        cierre.setTotalMovimientos(obtenerTotalMovimientos(resumenReporte));
        cierre.setTotalAccesos(obtenerTotalAccesos(resumenReporte));
        cierre.setTotalSalidas(obtenerTotalSalidas(resumenReporte));
        cierre.setTotalReservas(obtenerTotalReservas(resumenReporte));

        cierre.setEstadoCierre(ESTADO_CIERRE_EJECUTADO);
        cierre.setResumenReporte(serializarResumenReporte(resumenReporte));
        cierre.setObservacion(construirObservacion(request, cierre));

        Cierre cierreGuardado = cierreRepository.save(cierre);

        log.info(
                "Cierre operativo guardado correctamen te idCierre={} fechaCierre={} ejecutor={}",
                cierreGuardado.getIdCierre(),
                cierreGuardado.getFechaCierre(),
                cierreGuardado.getEmailUsuarioEjecutor()
        );

        return mapToCierreResponse(cierreGuardado);
    }

    @Transactional(readOnly = true)
    public List<CierreResponse> listarCierres() {
        log.info("Iniciando listado de cierres");

        List<Cierre> cierres = cierreRepository.findAll();

        log.info("Cantidad de cierres encontrados={}", cierres.size());

        return cierres.stream()
                .map(this::mapToCierreResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CierreResponse obtenerCierrePorId(Long idCierre) {
        log.info("Iniciando búsqueda de cierre idCierre={}", idCierre);

        Cierre cierre = buscarCierrePorId(idCierre);

        log.info("Cierre encontrado idCierre={} fechaCierre={}", cierre.getIdCierre(), cierre.getFechaCierre());

        return mapToCierreResponse(cierre);
    }

    @Transactional(readOnly = true)
    public List<CierreResponse> listarCierresPorFecha(LocalDate fechaCierre) {
        log.info("Iniciando listado de cierres por fechaCierre={}", fechaCierre);

        List<Cierre> cierres = cierreRepository.findByFechaCierre(fechaCierre);

        log.info("Cantidad de cierres encontrados para fechaCierre={} cantidad={}", fechaCierre, cierres.size());

        return cierres.stream()
                .map(this::mapToCierreResponse)
                .toList();
    }

    private int liberarPlazasOcupadas(
            List<PlazaInternaResponse> plazas,
            String authorizationHeader
    ) {
        int totalLiberadas = 0;

        for (PlazaInternaResponse plaza : plazas) {
            if (plaza == null || plaza.getIdPlaza() == null) {
                continue;
            }

            if (ESTADO_OCUPADA.equalsIgnoreCase(plaza.getEstado())) {
                log.info("Liberando plaza ocupada idPlaza={} codigoPlaza={}",
                        plaza.getIdPlaza(),
                        plaza.getCodigoPlaza()
                );

                plazaClient.actualizarEstadoPlaza(
                        plaza.getIdPlaza(),
                        ESTADO_DISPONIBLE,
                        authorizationHeader
                );

                totalLiberadas++;
            }
        }

        return totalLiberadas;
    }

    private ResultadoLimpiezaReservas eliminarReservasYLiberarPlazas(
            List<ReservaInternaResponse> reservas,
            String authorizationHeader
    ) {
        int totalReservasEliminadas = 0;
        Set<Long> idsPlazasReservadasLiberadas = new HashSet<>();

        for (ReservaInternaResponse reserva : reservas) {
            if (reserva == null || reserva.getIdReserva() == null) {
                continue;
            }

            Long idPlaza = reserva.getIdPlaza();

            reservaClient.eliminarReserva(
                    reserva.getIdReserva(),
                    authorizationHeader
            );

            totalReservasEliminadas++;

            if (idPlaza != null && idsPlazasReservadasLiberadas.add(idPlaza)) {
                log.info("Liberando plaza reservada por cierre idPlaza={}", idPlaza);

                plazaClient.actualizarEstadoPlaza(
                        idPlaza,
                        ESTADO_DISPONIBLE,
                        authorizationHeader
                );
            }
        }

        return new ResultadoLimpiezaReservas(
                totalReservasEliminadas,
                idsPlazasReservadasLiberadas.size()
        );
    }

    private long contarPlazasPorEstado(
            List<PlazaInternaResponse> plazas,
            String estado
    ) {
        return plazas.stream()
                .filter(plaza -> plaza != null && plaza.getEstado() != null)
                .filter(plaza -> estado.equalsIgnoreCase(plaza.getEstado()))
                .count();
    }

    private Cierre buscarCierrePorId(Long idCierre) {
        return cierreRepository.findById(idCierre)
                .orElseThrow(() -> {
                    log.warn("Cierre no encontrado idCierre={}", idCierre);

                    return new CierreNoEncontradoException(
                            "No existe un cierre con ID " + idCierre
                    );
                });
    }

    private CierreResponse mapToCierreResponse(Cierre cierre) {
        return new CierreResponse(
                cierre.getIdCierre(),
                cierre.getFechaCierre(),
                cierre.getHoraCierre(),
                cierre.getFechaHoraCierre(),
                cierre.getIdUsuarioEjecutor(),
                cierre.getEmailUsuarioEjecutor(),
                cierre.getRolEjecutor(),
                cierre.getTotalPlazasOcupadasLiberadas(),
                cierre.getTotalReservasEliminadas(),
                cierre.getTotalPlazasReservadasLiberadas(),
                cierre.getPlazasDisponiblesFinal(),
                cierre.getPlazasOcupadasFinal(),
                cierre.getPlazasReservadasFinal(),
                cierre.getTotalMovimientos(),
                cierre.getTotalAccesos(),
                cierre.getTotalSalidas(),
                cierre.getTotalReservas(),
                cierre.getEstadoCierre(),
                cierre.getResumenReporte(),
                cierre.getObservacion()
        );
    }

    private String extraerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new CierreEjecucionException("Header Authorization inválido");
        }

        return authorizationHeader.substring(BEARER_PREFIX.length());
    }

    private Integer obtenerTotalMovimientos(ReporteResumenResponse resumenReporte) {
        return obtenerTotalAccesos(resumenReporte)
                + obtenerTotalSalidas(resumenReporte)
                + obtenerTotalReservas(resumenReporte);
    }

    private Integer obtenerTotalAccesos(ReporteResumenResponse resumenReporte) {
        if (resumenReporte == null || resumenReporte.getResumenMovimientos() == null) {
            return 0;
        }

        return toInteger(resumenReporte.getResumenMovimientos().getAccesos());
    }

    private Integer obtenerTotalSalidas(ReporteResumenResponse resumenReporte) {
        if (resumenReporte == null || resumenReporte.getResumenMovimientos() == null) {
            return 0;
        }

        return toInteger(resumenReporte.getResumenMovimientos().getSalidas());
    }

    private Integer obtenerTotalReservas(ReporteResumenResponse resumenReporte) {
        if (resumenReporte == null || resumenReporte.getResumenMovimientos() == null) {
            return 0;
        }

        return toInteger(resumenReporte.getResumenMovimientos().getReservas());
    }

    private String serializarResumenReporte(ReporteResumenResponse resumenReporte) {
        if (resumenReporte == null) {
            log.warn("No se serializa resumenReporte porque llegó null");
            return null;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            String resumenSerializado = mapper.writeValueAsString(resumenReporte);

            log.info("Resumen de reporte serializado correctamente");

            return resumenSerializado;

        } catch (JsonProcessingException ex) {
            log.warn("No fue posible serializar el resumen recibido desde reporte_service. Motivo={}", ex.getMessage());

            return "No fue posible serializar el resumen recibido desde reporte_service";
        }
    }

    private String construirObservacion(
            CierreEjecutarRequest request,
            Cierre cierre
    ) {
        if (request != null && request.getObservacion() != null && !request.getObservacion().isBlank()) {
            return request.getObservacion().trim();
        }

        return "Cierre ejecutado correctamente. Se liberaron "
                + cierre.getTotalPlazasOcupadasLiberadas()
                + " plazas ocupadas, se eliminaron "
                + cierre.getTotalReservasEliminadas()
                + " reservas y se liberaron "
                + cierre.getTotalPlazasReservadasLiberadas()
                + " plazas reservadas.";
    }

    private Integer toInteger(long valor) {
        return Math.toIntExact(valor);
    }

    private static class ResultadoLimpiezaReservas {

        private final int totalReservasEliminadas;
        private final int totalPlazasReservadasLiberadas;

        private ResultadoLimpiezaReservas(
                int totalReservasEliminadas,
                int totalPlazasReservadasLiberadas
        ) {
            this.totalReservasEliminadas = totalReservasEliminadas;
            this.totalPlazasReservadasLiberadas = totalPlazasReservadasLiberadas;
        }

        public int getTotalReservasEliminadas() {
            return totalReservasEliminadas;
        }

        public int getTotalPlazasReservadasLiberadas() {
            return totalPlazasReservadasLiberadas;
        }
    }
}