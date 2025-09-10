package co.com.pragma.usecase.loan_application;

import co.com.pragma.model.error.NotFoundException;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.model.loan_application.LoanApplication;
import co.com.pragma.model.loan_application.gateways.LoanApplicationRepository;
import co.com.pragma.model.loan_application.gateways.LoanApplicationStatusRepository;
import co.com.pragma.model.loan_application.util.LoanApplicationStatus;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FindLoanApplicationByEmailAndStatusUseCase {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanApplicationStatusRepository loanApplicationStatusRepository;

    public Flux<LoanApplication> execute(String email, LoanApplicationStatus status) {

        return loanApplicationStatusRepository.findByName(status.name())
                .switchIfEmpty(Mono.error(new NotFoundException(ResponseCode.MSSO010)))
                .flatMapMany(statusSave -> loanApplicationRepository.getByEmailAndStatus(email, statusSave.getId())
                        .map(loanApplication -> {
                            loanApplication.setStatus(statusSave);
                            return loanApplication;
                        }));
    }

}
