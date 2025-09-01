package co.com.pragma.usecase.loan_application;

import co.com.pragma.model.error.NotFoundException;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.model.loan_application.LoanApplication;
import co.com.pragma.model.loan_application.gateways.LoanApplicationRepository;
import co.com.pragma.model.loan_application.gateways.LoanApplicationStatusRepository;
import co.com.pragma.model.loan_application.gateways.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import static co.com.pragma.model.loan_application.util.LoanApplicationStatus.PENDING_REVIEW;

@RequiredArgsConstructor
public class RegisterLoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanApplicationStatusRepository loanApplicationStatusRepository;
    private final LoanTypeRepository loanTypeRepository;

    public Mono<LoanApplication> execute(LoanApplication loanApplication) {
        return loanTypeRepository.findById(loanApplication.getLoanTypeId())
                .switchIfEmpty(Mono.error(new NotFoundException(ResponseCode.MSSO004)))
                .flatMap(type -> loanApplicationStatusRepository.findByName(PENDING_REVIEW.name())
                        .switchIfEmpty(Mono.error(new NotFoundException(ResponseCode.MSSO004)))
                        .flatMap(status -> {
                            loanApplication.setLoanApplicationStatusId(status.getId());
                            return loanApplicationRepository.save(loanApplication);
                        })
                );
    }
}
