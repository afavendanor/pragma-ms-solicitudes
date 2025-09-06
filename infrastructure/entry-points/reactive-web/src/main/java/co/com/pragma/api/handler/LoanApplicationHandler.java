package co.com.pragma.api.handler;

import co.com.pragma.api.dto.CreateLoanApplicationDTO;
import co.com.pragma.api.dto.GenericResponseDTO;
import co.com.pragma.api.dto.LoanApplicationPageListDTO;
import co.com.pragma.api.dto.UpdateLoanApplicationDTO;
import co.com.pragma.api.mapper.LoanApplicationApiRestMapper;
import co.com.pragma.api.security.utils.JwtUtils;
import co.com.pragma.model.error.LoginException;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.model.loan_application.util.LoanApplicationStatus;
import co.com.pragma.usecase.loan_application.ListLoanApplicationUseCase;
import co.com.pragma.usecase.loan_application.RegisterLoanApplicationUseCase;
import co.com.pragma.usecase.loan_application.UpdateLoanApplicationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

import static co.com.pragma.api.security.config.TokenJwtConfig.PREFIX_TOKEN;

@Component
@RequiredArgsConstructor
public class LoanApplicationHandler {

    private static final Logger log = Loggers.getLogger(LoanApplicationHandler.class.getName());

    private final RegisterLoanApplicationUseCase registerLoanApplicationUseCase;
    private final ListLoanApplicationUseCase listLoanApplicationUseCase;
    private final UpdateLoanApplicationUseCase updateLoanApplicationUseCase;
    private final LoanApplicationApiRestMapper loanApplicationApiRestMapper;
    private final JwtUtils jwtUtils;

    public Mono<GenericResponseDTO<Object>> createLoanApplication(CreateLoanApplicationDTO createLoanApplicationDTO,
                                                                  String authHeader) {

        ErrorHandler<Object> errorHandler = new ErrorHandler<>();
        return errorHandler.addErrors(
                Mono.defer(() -> {
                    log.debug("Inicializar guardar solicitud.");

                    return jwtUtils.getClaim(authHeader.replace(PREFIX_TOKEN, ""), "ID_")
                            .switchIfEmpty(Mono.error(new LoginException(ResponseCode.MSSO005)))
                            .flatMap(identification -> {
                                if (!identification.equals(createLoanApplicationDTO.getIdentification())) {
                                    return Mono.error(new LoginException(ResponseCode.MSSO006, "Identification mismatch"));
                                }

                                return registerLoanApplicationUseCase.execute(
                                                loanApplicationApiRestMapper
                                                        .createLoanApplicationDTOToLoanApplication(createLoanApplicationDTO)
                                        )
                                        .thenReturn(new GenericResponseDTO<>(HttpStatus.CREATED, ResponseCode.MSSO001, null))
                                        .doOnSuccess(response ->
                                                log.debug("Finalizar guardar solicitud.")
                                        );
                            });

                }),
                "createLoanApplication"
        );
    }

    public Mono<GenericResponseDTO<Void>> updateLoanApllications(UpdateLoanApplicationDTO updateLoanApplicationDTO) {
        ErrorHandler<Void> errorHandler = new ErrorHandler<>();
        return errorHandler.addErrors(
                Mono.defer(() -> {
                    log.debug("Inicializar actualizaciòn de solictudes");
                    return updateLoanApplicationUseCase.execute(
                            loanApplicationApiRestMapper.updateLoanApplicationDTOToLoanApplication(updateLoanApplicationDTO)
                            )
                            .map(dto -> new GenericResponseDTO<>(HttpStatus.OK, ResponseCode.MSSO001, dto))
                            .doOnSuccess(response -> log.debug("Finalizar consulta de solicitudes"));
                }),
                "updateLoanApllications");
    }

    public Mono<GenericResponseDTO<LoanApplicationPageListDTO>> listLoanApllications(LoanApplicationStatus status, int page, int size) {
        ErrorHandler<LoanApplicationPageListDTO> errorHandler = new ErrorHandler<>();
        return errorHandler.addErrors(
                Mono.defer(() -> {
                    log.debug("Inicializar consulta de solictudes");
                    return listLoanApplicationUseCase.execute(status, page, size)
                            .map(loanApplicationApiRestMapper::loanApplicationPageListToLoanApplicationPageListDTO)
                            .map(dto -> new GenericResponseDTO<>(HttpStatus.OK, ResponseCode.MSSO001, dto))
                            .doOnSuccess(response -> log.debug("Finalizar consulta de solicitudes"));
                }),
                "listLoanApllications");
    }

}