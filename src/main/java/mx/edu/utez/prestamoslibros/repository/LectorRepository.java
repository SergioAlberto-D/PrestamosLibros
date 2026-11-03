package mx.edu.utez.prestamoslibros.repository;

import mx.edu.utez.prestamoslibros.model.lector.Lector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LectorRepository extends JpaRepository<Lector, Long> {
}
