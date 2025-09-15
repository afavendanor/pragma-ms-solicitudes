package co.com.pragma.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Schema(description = "Modelo de salida para los datos de la solicitud de crèdito.")
public class LoanApplicationDTO {
    @Schema(description = "Monto del crèdito.")
    private Double amount;
    @Schema(description = "Monto del crèdito.")
    private Integer term;
    @Schema(description = "Monto del crèdito.")
    private String email;
    @Schema(description = "Tasa de interès del crèdito.")
    private Double interestRate;
    @Schema(description = "Monto del crèdito.")
    private LoanApplicationStatusDTO status;

}
