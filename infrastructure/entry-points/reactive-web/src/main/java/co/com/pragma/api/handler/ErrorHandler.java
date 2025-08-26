package co.com.pragma.api.handler;

import co.com.pragma.api.dto.GenericResponseDTO;
import co.com.pragma.model.error.CustomException;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

public class ErrorHandler<T> {

    private static final Logger log = Loggers.getLogger(ErrorHandler.class.getName());

    public Mono<GenericResponseDTO<T>> addErrors(Mono<GenericResponseDTO<T>> genericResponseDTOMono, String method) {
        return genericResponseDTOMono.onErrorResume(exception -> {
            log.error("Error in {}: {} with error: {}", method, exception.getClass().getSimpleName(), exception.getMessage(), exception);

            if (exception instanceof CustomException customException) {
                return Mono.just(new GenericResponseDTO<>(
                        customException.getResponseCode(),
                        customException.getMessage(),
                        null,
                        customException.getFieldErrors()
                ));
            }

            return Mono.error(exception);
        });
    }
}