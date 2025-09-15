package co.com.pragma.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Schema(description = "Modelo de salida de la lista paginable de las aplicaciones de crèdito.")
public class LoanApplicationPageListDTO {
    @Schema(description = "Monto total mensual de los crèditos aprovados.")
    private Double totalMonthlyDebtApprovedRequests;
    @Schema(description = "Lista de solicitudes de crèdito.")
    private List<LoanApplicationPageDTO> loanApplications;
    @Schema(description = "Nùmero de pàgina de la lista.")
    private int page;
    @Schema(description = "Tamaño de pàgina de la lista.")
    private int size;
    @Schema(description = "Nùmero total de elementos que cumplen con el filtro.")
    private long totalElements;
    @Schema(description = "Nùmero total de pàginas de acuerdo a filtro.")
    private int totalPages;
    @Schema(description = "Indica si es la ùltima pàgina.")
    private boolean last;
}
