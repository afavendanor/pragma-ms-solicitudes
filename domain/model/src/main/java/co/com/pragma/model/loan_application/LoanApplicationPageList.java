package co.com.pragma.model.loan_application;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplicationPageList {
    private Double totalMonthlyDebtApprovedRequests;
    private List<LoanApplicationPage> loanApplications;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
