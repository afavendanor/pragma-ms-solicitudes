package co.com.pragma.api.handler;

import co.com.pragma.api.dto.CreateLoanApplicationDTO;
import co.com.pragma.api.dto.GenericResponseDTO;
import co.com.pragma.api.mapper.LoanApplicationApiRestMapper;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.usecase.loan_application.RegisterLoanApplicationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

@Component
@RequiredArgsConstructor
public class LoanApplicationHandler {

    private static final Logger log = Loggers.getLogger(LoanApplicationHandler.class.getName());

    private final RegisterLoanApplicationUseCase registerLoanApplicationUseCase;
    private final LoanApplicationApiRestMapper loanApplicationApiRestMapper;

    public Mono<GenericResponseDTO<Object>> createLoanApplication(CreateLoanApplicationDTO createLoanApplicationDTO) {

        ErrorHandler<Object> errorHandler = new ErrorHandler<>();
        return errorHandler.addErrors(
                Mono.defer(() -> {
                    log.debug("Inicializar guardar solicitud.");
                    return registerLoanApplicationUseCase.execute(
                                    loanApplicationApiRestMapper.createLoanApplicationDTOToLoanApplication(createLoanApplicationDTO)
                            )
                            .thenReturn(new GenericResponseDTO<>(HttpStatus.CREATED, ResponseCode.MSSO001, null))
                            .doOnSuccess(response ->
                                    log.debug("Finalizar guardar solicitud.")
                            );
                }),
                "createLoanApplication"
        );
    }
}