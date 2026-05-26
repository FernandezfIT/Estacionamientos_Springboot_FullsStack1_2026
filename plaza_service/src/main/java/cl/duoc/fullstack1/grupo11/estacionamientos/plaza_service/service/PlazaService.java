package cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.service;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.dto.request.PlazaUpdateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.dto.response.PlazaResponse;
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
                    plaza.setEstado("Disponible");
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

    private Plaza buscarPlazaPorId(Long idPlaza) {
        return plazaRepository.findById(idPlaza)
                .orElseThrow(() -> new PlazaNoEncontradaException(
                        "No existe una plaza con ID " + idPlaza));
    }

    private PlazaResponse mapToPlazaResponse(Plaza plaza) {
        return new PlazaResponse(
                plaza.getIdPlaza(),
                plaza.getCodigoPlaza(),
                plaza.getEstado()
        );
    }
}
