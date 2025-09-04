package co.com.pragma.model.loan_application;

import co.com.pragma.model.user.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplicationPage {
    private String identification;
    private Double amount;
    private Integer term;
    private String email;

    private LoanApplicationStatus status;
    private LoanType type;

    private User user;
}
