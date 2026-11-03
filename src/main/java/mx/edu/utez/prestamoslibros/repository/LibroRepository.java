package mx.edu.utez.prestamoslibros.repository;

import mx.edu.utez.prestamoslibros.model.libro.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
}
