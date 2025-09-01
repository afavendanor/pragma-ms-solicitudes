package co.com.pragma.model.loan_application.gateways;

import co.com.pragma.model.loan_application.LoanType;
import reactor.core.publisher.Mono;

public interface LoanTypeRepository {

    Mono<LoanType> findById(Long id);

}
