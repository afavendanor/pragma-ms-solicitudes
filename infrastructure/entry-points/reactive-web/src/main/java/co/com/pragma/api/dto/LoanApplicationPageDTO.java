package co.com.pragma.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Schema(description = "Modelo de salida para las solicitudes de crèdito.")
public class LoanApplicationPageDTO {
    @Schema(description = "Monto del crèdito.")
    private Double amount;
    @Schema(description = "Plazo del crèdito.")
    private Integer term;
    @Schema(description = "Email del usuario que hace el crèdito.")
    private String email;

    @Schema(description = "Estado del crèdito.")
    private LoanApplicationStatusDTO status;
    @Schema(description = "Tipo de crèdito.")
    private LoanTypeDTO type;

    @Schema(description = "Usuario dueño del crèdito.")
    private UserDTO user;
}
