package mx.edu.utez.prestamoslibros.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.edu.utez.prestamoslibros.controller.dto.RequestPrestramoDto;
import mx.edu.utez.prestamoslibros.model.lector.Lector;
import mx.edu.utez.prestamoslibros.model.prestamo.Prestamo;
import mx.edu.utez.prestamoslibros.service.PrestamoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prestamos")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PrestamoCrontroller {

    private final PrestamoService prestamoService;

    @PostMapping("/solicitar")
    public ResponseEntity<Prestamo> solicitarPrestamo(@Valid @RequestBody RequestPrestramoDto dto) {
        Prestamo nuevoPrestamo = prestamoService.crearPrestamo(dto);
        return new ResponseEntity<>(nuevoPrestamo, HttpStatus.CREATED);
    }
    @PutMapping("/devolver/{prestamoId}")
    public ResponseEntity<Prestamo> devolverLibro(@PathVariable Long prestamoId) {
        Prestamo devuelto = prestamoService.devolverPrestamo(prestamoId);
        return ResponseEntity.ok(devuelto);
    }

    @PutMapping("/renovar/{prestamoId}")
    public ResponseEntity<Prestamo> renovarPrestamo(@PathVariable Long prestamoId) {
        Prestamo renovado = prestamoService.actualizarPrestamo(prestamoId);
        return ResponseEntity.ok(renovado);
    }

    @PutMapping("/lectores/rehabilitar/{lectorId}")
    public ResponseEntity<Lector> rehabilitarLector(@PathVariable Long lectorId) {
        Lector rehabilitado = prestamoService.rehabilitarLector(lectorId);
        return ResponseEntity.ok(rehabilitado);
    }

    @GetMapping("/lector/{lectorId}/activos")
    public ResponseEntity<List<Prestamo>> listarActivosPorLector(@PathVariable Long lectorId) {
        List<Prestamo> lista = prestamoService.listarPrestamosActivosLector(lectorId);
        return ResponseEntity.ok(lista);
    }
}
