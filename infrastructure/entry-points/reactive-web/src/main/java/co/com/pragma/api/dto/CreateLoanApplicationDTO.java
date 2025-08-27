package co.com.pragma.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Schema(description = "Modelo de entrada para crear un solicitud de crèdito.")
public class CreateLoanApplicationDTO {
    @NotBlank(message = "Identificaciòn es requerida.")
    @Schema(description = "Identificaciòn del usuario.")
    private String identification;
    @NotNull(message = "Monto del crèdito es requerido.")
    @Schema(description = "Monto del crèdito.")
    private Double amount;
    @NotNull(message = "Plazo del crèdito es requerido.")
    @Schema(description = "Plazo del crèdito.")
    private Integer term;
    @NotBlank(message = "Email del usuario es requerido.")
    @Schema(description = "Email del usuario.")
    private String email;
    @NotNull(message = "Tipo de crèdito es requerido.")
    @Schema(description = "Tipo de crèdito.")
    private Long loanTypeId;
}
