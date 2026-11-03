package mx.edu.utez.prestamoslibros.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestPrestramoDto {
    @NotNull(message = "El id del lector no puede estar vacio")
    private long idLector;
    @NotNull(message = "El id del libro no puede estar vacio")
    private long idLibro;

}
