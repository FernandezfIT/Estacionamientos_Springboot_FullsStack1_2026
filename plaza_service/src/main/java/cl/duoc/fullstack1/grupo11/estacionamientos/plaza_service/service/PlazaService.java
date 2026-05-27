package cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.service;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.dto.request.PlazaUpdateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.dto.response.PlazaResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.exception.PlazaNoDisponibleException;
import cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.exception.PlazaNoEncontradaException;
import cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.model.Plaza;
import cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.repository.PlazaRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlazaService {

    // Modificación agregada para trabajar con acceso_service:
    // se centralizan los estados usados al ocupar una plaza desde un acceso.
    private static final String ESTADO_DISPONIBLE = "Disponible";
    private static final String ESTADO_OCUPADA = "Ocupada";

    private final PlazaRepository plazaRepository;

    @PostConstruct
    @Transactional
    public void inicializarPlazas() {
        if (plazaRepository.count() > 0) {
            return;
        }

        List<Plaza> plazas = IntStream.rangeClosed(1, 50)
                .mapToObj(i -> {
                    Plaza plaza = new Plaza();
                    plaza.setCodigoPlaza(String.format("P%02d", i));

                    // Modificación menor:
                    // se usa la constante para evitar escribir el estado manualmente.
                    plaza.setEstado(ESTADO_DISPONIBLE);

                    return plaza;
                })
                .toList();

        plazaRepository.saveAll(plazas);

        log.info("Se inicializaron 50 plazas con estado Disponible");
    }

    @Transactional(readOnly = true)
    public List<PlazaResponse> listarPlazas() {
        return plazaRepository.findAll()
                .stream()
                .map(this::mapToPlazaResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PlazaResponse obtenerPlazaPorId(Long idPlaza) {
        Plaza plaza = buscarPlazaPorId(idPlaza);
        return mapToPlazaResponse(plaza);
    }

    // Modificación agregada para trabajar con acceso_service:
    // acceso_service recibirá codigoPlaza, por ejemplo "P01", no idPlaza.
    @Transactional(readOnly = true)
    public PlazaResponse obtenerPlazaPorCodigo(String codigoPlaza) {
        Plaza plaza = buscarPlazaPorCodigo(codigoPlaza);
        return mapToPlazaResponse(plaza);
    }

    @Transactional
    public PlazaResponse actualizarEstadoPlaza(Long idPlaza, PlazaUpdateRequest request) {
        Plaza plaza = buscarPlazaPorId(idPlaza);

        String estadoAnterior = plaza.getEstado();
        String estadoNuevo = request.getEstado();

        plaza.setEstado(estadoNuevo);

        Plaza plazaActualizada = plazaRepository.save(plaza);

        log.info("Plaza {} cambió de {} a {}", plaza.getCodigoPlaza(), estadoAnterior, estadoNuevo);

        return mapToPlazaResponse(plazaActualizada);
    }

    // Modificación agregada para trabajar con acceso_service:
    // permite ocupar una plaza usando su código, validando antes que esté Disponible.
    @Transactional
    public PlazaResponse ocuparPlazaPorCodigo(String codigoPlaza) {
        Plaza plaza = buscarPlazaPorCodigo(codigoPlaza);

        if (!ESTADO_DISPONIBLE.equalsIgnoreCase(plaza.getEstado())) {
            throw new PlazaNoDisponibleException(
                    "La plaza " + plaza.getCodigoPlaza() + " no está disponible. Estado actual: " + plaza.getEstado()
            );
        }

        String estadoAnterior = plaza.getEstado();

        plaza.setEstado(ESTADO_OCUPADA);

        Plaza plazaActualizada = plazaRepository.save(plaza);

        log.info("Plaza {} cambió de {} a {}", plaza.getCodigoPlaza(), estadoAnterior, ESTADO_OCUPADA);

        return mapToPlazaResponse(plazaActualizada);
    }

    private Plaza buscarPlazaPorId(Long idPlaza) {
        return plazaRepository.findById(idPlaza)
                .orElseThrow(() -> new PlazaNoEncontradaException(
                        "No existe una plaza con ID " + idPlaza
                ));
    }

    // Modificación agregada para trabajar con acceso_service:
    // encapsula la búsqueda por codigoPlaza y normaliza el código antes de consultar.
    private Plaza buscarPlazaPorCodigo(String codigoPlaza) {
        String codigoNormalizado = normalizarCodigoPlaza(codigoPlaza);

        return plazaRepository.findByCodigoPlaza(codigoNormalizado)
                .orElseThrow(() -> new PlazaNoEncontradaException(
                        "No existe una plaza con código " + codigoNormalizado
                ));
    }

    private PlazaResponse mapToPlazaResponse(Plaza plaza) {
        return new PlazaResponse(
                plaza.getIdPlaza(),
                plaza.getCodigoPlaza(),
                plaza.getEstado()
        );
    }

    // Modificación agregada para trabajar con acceso_service:
    // permite recibir códigos como "p01", " P01 " o "P01" y tratarlos igual.
    private String normalizarCodigoPlaza(String codigoPlaza) {
        return codigoPlaza.trim().toUpperCase();
    }
}