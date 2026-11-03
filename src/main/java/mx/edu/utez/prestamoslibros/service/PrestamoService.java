package mx.edu.utez.prestamoslibros.service;

import mx.edu.utez.prestamoslibros.controller.dto.RequestPrestramoDto;
import mx.edu.utez.prestamoslibros.exception.custom.CustomException;
import mx.edu.utez.prestamoslibros.model.enums.EstadoPrestamo;
import mx.edu.utez.prestamoslibros.model.lector.Lector;
import mx.edu.utez.prestamoslibros.model.libro.Libro;
import mx.edu.utez.prestamoslibros.model.prestamo.Prestamo;
import mx.edu.utez.prestamoslibros.repository.LectorRepository;
import mx.edu.utez.prestamoslibros.repository.LibroRepository;
import mx.edu.utez.prestamoslibros.repository.PrestamoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PrestamoService {
    private final PrestamoRepository prestamoRepository;
    private final LectorRepository lectorRepository;
    private final LibroRepository libroRepository;

    public PrestamoService(PrestamoRepository prestamoRepository, LectorRepository lectorRepository, LibroRepository libroRepository) {
        this.prestamoRepository = prestamoRepository;
        this.lectorRepository = lectorRepository;
        this.libroRepository = libroRepository;
    }

    @Transactional
    public Prestamo crearPrestamo( RequestPrestramoDto dto){
        Prestamo prestamo = new Prestamo();
        Lector lector = lectorRepository.findById(dto.getIdLector()).orElseThrow(() -> new CustomException("El lector no existe"));
        if(Boolean.FALSE.equals(lector.getActivo())){
            throw new CustomException("El Lector no esta activo o no puede solicitar prestamos");
        }
        Libro libro = libroRepository.findById(dto.getIdLibro()).orElseThrow(() -> new CustomException("El libro no existe"));
        if (libro.getEjemplaresDisponibles() == null || libro.getEjemplaresDisponibles() < 1){
            throw new CustomException("El libro no tiene ejemplares disponibles");
        }
        int limiteMax = Boolean.TRUE.equals(lector.getMembreciaPremium())? 3 : 5;
        long prestamosActivos = prestamoRepository.countByLectorIdAndEstadoPrestamoIn(
                lector.getId(),
                List.of(EstadoPrestamo.PRESTADO, EstadoPrestamo.VENCIDO)
        );
        if (prestamosActivos >= limiteMax) {
            throw new CustomException("El lector alcanzó el límite de préstamos activos (" + limiteMax + ")");
        }
        libro.setEjemplaresDisponibles(libro.getEjemplaresDisponibles() - 1);
        libroRepository.save(libro);

        LocalDate hoy = LocalDate.now();
        prestamo.setLector(lector);
        prestamo.setLibro(libro);
        prestamo.setFecha(hoy);
        prestamo.setFechaDevolucionMacima(hoy.plusDays(14));
        prestamo.setEstadoPrestamo(EstadoPrestamo.PRESTADO);

        return prestamoRepository.save(prestamo);
    }
    @Transactional
    public Prestamo devolverPrestamo(Long id){
        Prestamo prestamo = prestamoRepository.findById(id).orElseThrow(() -> new CustomException("El prestamo no existe"));
        if (prestamo.getEstadoPrestamo() == EstadoPrestamo.DEVUELTO) {
            throw new CustomException("El libro ya ha sido devuelto anteriormente");
        }
        LocalDate hoy = LocalDate.now();
        if (hoy.isAfter(prestamo.getFechaDevolucionMacima())) {
            Lector lector = prestamo.getLector();
            prestamo.setEstadoPrestamo(EstadoPrestamo.VENCIDO);
            lector.setActivo(false);
            lectorRepository.save(lector);
        }
        prestamo.setEstadoPrestamo(EstadoPrestamo.DEVUELTO);
        Libro libro = prestamo.getLibro();
        libro.setEjemplaresDisponibles(libro.getEjemplaresDisponibles() + 1);
        libroRepository.save(libro);

        return prestamoRepository.save(prestamo);
    }
    @Transactional
    public Prestamo actualizarPrestamo(Long id){
        Prestamo prestamo = prestamoRepository.findById(id).orElseThrow(() -> new CustomException("El prestamo no existe"));
        LocalDate hoy = LocalDate.now();
        if (hoy.isAfter(prestamo.getFechaDevolucionMacima())) {
            prestamo.setEstadoPrestamo(EstadoPrestamo.VENCIDO);
        }
        if (prestamo.getEstadoPrestamo() == EstadoPrestamo.DEVUELTO || prestamo.getEstadoPrestamo() == EstadoPrestamo.VENCIDO) {
            throw new CustomException("El libro ya ha sido devuelto anteriormente o caduco su fecha");
        }
        prestamo.setFechaDevolucionMacima(prestamo.getFechaDevolucionMacima().plusDays(7));
        return prestamoRepository.save(prestamo);
    }
    @Transactional
    public Lector rehabilitarLector(Long lectorId) {
        Lector lector = lectorRepository.findById(lectorId)
                .orElseThrow(() -> new CustomException("El lector con ID " + lectorId + " no existe"));

        lector.setActivo(true);

        return lectorRepository.save(lector);
    }

    @Transactional(readOnly = true)
    public List<Prestamo> listarPrestamosActivosLector(Long lectorId) {
        if (!lectorRepository.existsById(lectorId)) {
            throw new CustomException("El lector con ID " + lectorId + " no existe");
        }

        return prestamoRepository.findByLectorIdAndEstadoPrestamoIn(
                lectorId,
                List.of(EstadoPrestamo.PRESTADO, EstadoPrestamo.VENCIDO)
        );
    }
}
