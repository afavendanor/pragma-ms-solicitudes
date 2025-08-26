package co.com.pragma.api.handler;

import co.com.pragma.api.dto.GenericResponseDTO;
import co.com.pragma.model.error.FieldError;
import co.com.pragma.model.error.ResponseCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MissingRequestValueException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class})
    public Mono<ResponseEntity<GenericResponseDTO<Map<String, String>>>> handleConstraintViolationException(ConstraintViolationException ex) {
        List<FieldError> fieldErrors = new ArrayList<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            FieldError fieldError = new FieldError(
                    violation.getPropertyPath().toString(),
                    violation.getMessage()
            );
            fieldErrors.add(fieldError);
        }

        GenericResponseDTO<Map<String, String>> respuesta = new GenericResponseDTO<>(
                ResponseCode.MSSO002,
                "Campos no son válidos",
                null,
                fieldErrors
        );

        return Mono.just(ResponseEntity.badRequest().body(respuesta));
    }

    @ExceptionHandler({WebExchangeBindException.class})
    public Mono<ResponseEntity<GenericResponseDTO<Map<String, String>>>> handleWebExchangeBindException(WebExchangeBindException ex) {
        List<FieldError> fieldErrors = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            FieldError fieldError = null;
            if (error instanceof org.springframework.validation.FieldError validatorError) {
                fieldError = new FieldError(
                        validatorError.getField(),
                        validatorError.getDefaultMessage()
                );
            }
            else if (error != null) {
                fieldError = new FieldError(
                        error.getObjectName(),
                        error.getDefaultMessage()
                );
            }
            fieldErrors.add(fieldError);
        });

        GenericResponseDTO<Map<String, String>> respuesta = new GenericResponseDTO<>(
                ResponseCode.MSSO002,
                "Campos no son válidos",
                null,
                fieldErrors
        );

        return Mono.just(ResponseEntity.badRequest().body(respuesta));
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public Mono<ResponseEntity<GenericResponseDTO<Map<String, String>>>> handleWIllegalArgumentException(
            IllegalArgumentException ex) {
        List<FieldError> fieldErrors = new ArrayList<>();

        fieldErrors.add(new FieldError(
                "error",
                ex.getMessage()
        ));

        GenericResponseDTO<Map<String, String>> respuesta = new GenericResponseDTO<>(
                ResponseCode.MSSO002,
                "Argumento inválido",
                null,
                fieldErrors
        );

        return Mono.just(ResponseEntity
                .badRequest()
                .body(respuesta));
    }

    @ExceptionHandler({MissingRequestValueException.class, ServerWebInputException.class})
    public Mono<ResponseEntity<GenericResponseDTO<Map<String, String>>>> handleMissingRequestValueException(ServerWebInputException ex) {
        List<FieldError> fieldErrors = new ArrayList<>();

        fieldErrors.add(new FieldError(
                "error",
                ex.getCause() != null ? ex.getCause().toString() : ex.getMessage()
        ));

        GenericResponseDTO<Map<String, String>> respuesta = new GenericResponseDTO<>(
                ResponseCode.MSSO002,
                "Argumento inválido",
                null,
                fieldErrors
        );

        return Mono.just(ResponseEntity
                .badRequest()
                .body(respuesta));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<GenericResponseDTO<Map<String, String>>>> handleException(Exception ex) {
        GenericResponseDTO<Map<String, String>> respuesta = new GenericResponseDTO<>(
                ResponseCode.MSSO000,
                ResponseCode.MSSO000.getHtmlMessage(),
                null,
                List.of()
        );

        return Mono.just(ResponseEntity.internalServerError().body(respuesta));
    }


}
