package co.com.pragma.usecase.loan_application;

import co.com.pragma.model.error.NotFoundException;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.model.loan_application.LoanApplication;
import co.com.pragma.model.loan_application.gateways.LoanApplicationRepository;
import co.com.pragma.model.loan_application.gateways.LoanApplicationSNSSenderGateway;
import co.com.pragma.model.loan_application.gateways.LoanApplicationStatusRepository;
import co.com.pragma.model.loan_application.util.LoanApplicationStatus;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RequiredArgsConstructor
public class UpdateLoanApplicationUseCase {

    private final LoanApplicationSNSSenderGateway loanApplicationSNSSenderGateway;
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanApplicationStatusRepository loanApplicationStatusRepository;

    public Mono<Void> execute(LoanApplication loanApplication) {
        return loanApplicationStatusRepository.findByName(loanApplication.getStatus().getName())
                .switchIfEmpty(Mono.error(new NotFoundException(ResponseCode.MSSO010)))
                .flatMap(status -> {
                            loanApplication.setLoanApplicationStatusId(status.getId());
                            return loanApplicationRepository.save(loanApplication)
                                    .filter(loanApp -> List.of(LoanApplicationStatus.APPROVED.name(), LoanApplicationStatus.REJECTED.name())
                                            .contains(status.getName()))
                                    .flatMap(application -> loanApplicationSNSSenderGateway.send(loanApplication, "NOTIFY")
                                            .subscribeOn(Schedulers.boundedElastic())
                                            .onErrorResume(e -> Mono.empty())
                                            .thenReturn(application)
                                    );
                        }
                )
                .then();
    }


}
