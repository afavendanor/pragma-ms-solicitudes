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
@Schema(description = "Modelo de salida para el tipo de crèdito.")
public class LoanTypeDTO {
    @Schema(description = "Nombre del tipo de crèdito.")
    private String name;
    @Schema(description = "Monto mìnimo del tipo de crèdito.")
    private Double minimumAmount;
    @Schema(description = "Monto màximo del tipo de crèdito.")
    private Double maximumAmount;
    @Schema(description = "Tasa de interès del tipo de crèdito.")
    private Double interestRate;
}
