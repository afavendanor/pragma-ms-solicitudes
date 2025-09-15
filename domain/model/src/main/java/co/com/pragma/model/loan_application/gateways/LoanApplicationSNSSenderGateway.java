package co.com.pragma.model.loan_application.gateways;

import co.com.pragma.model.loan_application.LoanApplication;
import reactor.core.publisher.Mono;

public interface LoanApplicationSNSSenderGateway {

    Mono<String> send(LoanApplication message, String notificationType);

}
