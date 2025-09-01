package co.com.pragma.api.handler;

import co.com.pragma.api.dto.GenericResponseDTO;
import co.com.pragma.model.error.DuplicateEntryException;
import co.com.pragma.model.error.FieldErrorException;
import co.com.pragma.model.error.InternalErrorException;
import co.com.pragma.model.error.NotFoundException;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

public class ErrorHandler<T> {

    private static final Logger log = Loggers.getLogger(ErrorHandler.class.getName());

    public Mono<GenericResponseDTO<T>> addErrors(Mono<GenericResponseDTO<T>> genericResponseDTOMono, String method) {
        return genericResponseDTOMono.onErrorResume(exception -> {
            log.error("Error in {}: {} with error: {}", method, exception.getClass().getSimpleName(), exception.getMessage(), exception);

            return switch (exception) {
                case DuplicateEntryException duplicateEntryException -> Mono.just(new GenericResponseDTO<>(
                        HttpStatus.CONFLICT.value(),
                        duplicateEntryException.getMessage(),
                        null,
                        duplicateEntryException.getFieldErrors()
                ));
                case FieldErrorException fieldErrorException -> Mono.just(new GenericResponseDTO<>(
                        HttpStatus.BAD_REQUEST.value(),
                        fieldErrorException.getMessage(),
                        null,
                        fieldErrorException.getFieldErrors()
                ));
                case InternalErrorException internalErrorException -> Mono.just(new GenericResponseDTO<>(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        internalErrorException.getMessage(),
                        null,
                        internalErrorException.getFieldErrors()
                ));
                case NotFoundException notFoundException -> Mono.just(new GenericResponseDTO<>(
                        HttpStatus.NOT_FOUND.value(),
                        notFoundException.getMessage(),
                        null,
                        notFoundException.getFieldErrors()
                ));
                default -> Mono.error(exception);
            };
        });
    }
}