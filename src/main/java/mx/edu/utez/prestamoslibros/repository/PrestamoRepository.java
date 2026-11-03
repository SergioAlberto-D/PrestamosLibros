package mx.edu.utez.prestamoslibros.repository;

import mx.edu.utez.prestamoslibros.model.enums.EstadoPrestamo;
import mx.edu.utez.prestamoslibros.model.prestamo.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    long countByLectorIdAndEstadoPrestamoIn(Long lectorId, Collection<EstadoPrestamo> estados);
    List<Prestamo> findByLectorIdAndEstadoPrestamoIn(Long lectorId, Collection<EstadoPrestamo> estados);
}
