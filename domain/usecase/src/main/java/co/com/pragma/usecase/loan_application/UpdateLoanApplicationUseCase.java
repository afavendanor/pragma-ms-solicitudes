package co.com.pragma.usecase.loan_application;

import co.com.pragma.model.loan_application.LoanApplication;
import co.com.pragma.model.loan_application.gateways.LoanApplicationRepository;
import co.com.pragma.model.loan_application.gateways.LoanApplicationSQSSenderGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateLoanApplicationUseCase {

    private final LoanApplicationSQSSenderGateway loanApplicationSQSSenderGateway;
    private final LoanApplicationRepository loanApplicationRepository;

    public Mono<Void> execute(LoanApplication loanApplication) {
        return loanApplicationRepository.save(loanApplication)
                .flatMap(application -> loanApplicationSQSSenderGateway.send(loanApplication))
                .then();
    }


}
