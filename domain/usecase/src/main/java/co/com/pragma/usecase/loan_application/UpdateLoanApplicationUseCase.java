package co.com.pragma.usecase.loan_application;

import co.com.pragma.model.loan_application.LoanApplication;
import co.com.pragma.model.loan_application.gateways.LoanApplicationRepository;
import co.com.pragma.model.loan_application.gateways.LoanApplicationSQSSenderGateway;
import co.com.pragma.model.loan_application.gateways.LoanApplicationStatusRepository;
import co.com.pragma.model.loan_application.util.LoanApplicationStatus;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class UpdateLoanApplicationUseCase {

    private final LoanApplicationSQSSenderGateway loanApplicationSQSSenderGateway;
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanApplicationStatusRepository loanApplicationStatusRepository;

    public Mono<Void> execute(LoanApplication loanApplication) {
        return loanApplicationStatusRepository.findByName(loanApplication.getStatus().getName())
                        .flatMap(status ->
                            loanApplicationRepository.save(loanApplication)
                                    .filter(loanApp -> List.of(LoanApplicationStatus.APPROVED.name(), LoanApplicationStatus.REJECTED.name())
                                            .contains(status.getName()))
                                    .flatMap(application -> loanApplicationSQSSenderGateway.send(loanApplication))
                        )
                .then();
    }


}
