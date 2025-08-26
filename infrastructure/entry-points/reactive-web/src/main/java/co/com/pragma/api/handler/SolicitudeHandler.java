package co.com.pragma.api.handler;

import co.com.pragma.api.dto.CreateSolicitudeDTO;
import co.com.pragma.api.dto.GenericResponseDTO;
import co.com.pragma.api.mapper.SolicitudeApiRestMapper;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.usecase.solicitude.RegisterSolicitudeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

@Component
@RequiredArgsConstructor
public class SolicitudeHandler {

    private static final Logger log = Loggers.getLogger(SolicitudeHandler.class.getName());

    private final RegisterSolicitudeUseCase registerSolicitudeUseCase;
    private final SolicitudeApiRestMapper solicitudeApiRestMapper;

    public Mono<GenericResponseDTO<Object>> createSolicitude(CreateSolicitudeDTO createSolicitudeDTO) {

        ErrorHandler<Object> errorHandler = new ErrorHandler<>();
        return errorHandler.addErrors(
                Mono.defer(() -> {
                    log.debug("Inicializar guardar solicitud.");
                    return registerSolicitudeUseCase.execute(
                                    solicitudeApiRestMapper.createSolicitudeDTOToSolicitude(createSolicitudeDTO)
                            )
                            .thenReturn(new GenericResponseDTO<>(ResponseCode.MSSO001, null))
                            .doOnSuccess(response ->
                                    log.debug("Finalizar guardar solicitud.")
                            );
                }),
                "createSolicitude"
        );
    }
}