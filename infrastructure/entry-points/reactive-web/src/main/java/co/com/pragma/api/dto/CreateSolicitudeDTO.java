package co.com.pragma.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Schema(description = "Modelo de entrada para crear un usuario.")
public class CreateSolicitudeDTO {
    @NotBlank(message = "Identificaciòn es requerida.")
    @Schema(description = "Identificaciòn del usuario.")
    private String identification;
}
