package co.com.pragma.model.loan_application.gateways;

import co.com.pragma.model.loan_application.LoanApplication;
import co.com.pragma.model.loan_application.LoanApplicationPageList;
import co.com.pragma.model.loan_application.util.LoanApplicationStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoanApplicationRepository {

    Mono<LoanApplication> save(LoanApplication loanApplication);

    Mono<LoanApplicationPageList> getFilterList(LoanApplicationStatus status, int page, int size);

    Flux<LoanApplication> getByEmailAndStatus(String email, Long statusId);

}
