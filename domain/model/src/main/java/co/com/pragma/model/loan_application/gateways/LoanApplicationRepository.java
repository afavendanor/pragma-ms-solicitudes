package co.com.pragma.model.loan_application.gateways;

import co.com.pragma.model.loan_application.LoanApplication;
import co.com.pragma.model.loan_application.LoanApplicationPageList;
import reactor.core.publisher.Mono;

public interface LoanApplicationRepository {

    Mono<LoanApplication> save(LoanApplication loanApplication);

    Mono<LoanApplicationPageList> getFilterList(Long status, int page, int size);

}
