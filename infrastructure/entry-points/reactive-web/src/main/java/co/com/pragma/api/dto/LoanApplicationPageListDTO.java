package co.com.pragma.api.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplicationPageListDTO {
    private Double totalMonthlyDebtApprovedRequests;
    private List<LoanApplicationPageDTO> loanApplications;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
