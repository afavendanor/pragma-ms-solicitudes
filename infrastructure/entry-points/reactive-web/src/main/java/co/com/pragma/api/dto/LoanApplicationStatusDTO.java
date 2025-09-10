package co.com.pragma.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Modelo de salida para el estado del tipo de crèdito.")
public class LoanApplicationStatusDTO {
    @Schema(description = "Nombre del estado del tipo de crèdito.")
    private String name;
    @Schema(description = "Descripciòn del estado del tipo de crèdito.")
    private String description;
}
