package co.com.pragma.model.loan_application.gateways;

import co.com.pragma.model.loan_application.LoanApplicationStatus;
import reactor.core.publisher.Mono;

public interface LoanApplicationStatusRepository {

    Mono<LoanApplicationStatus> findByName(String name);

}
