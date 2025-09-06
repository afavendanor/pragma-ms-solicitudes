package co.com.pragma.api.handler;

import co.com.pragma.api.dto.GenericResponseDTO;
import co.com.pragma.model.error.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ErrorHandlerTest {

    private final ErrorHandler<String> errorHandler = new ErrorHandler<>();

    @Test
    void addErrors_whenNoError_shouldReturnOriginalMono() {
        // Arrange
        GenericResponseDTO<String> respuestaOk = new GenericResponseDTO<>(HttpStatus.CREATED, ResponseCode.MSSO001, "todo bien");
        Mono<GenericResponseDTO<String>> mono = Mono.just(respuestaOk);

        // Act & Assert
        StepVerifier.create(errorHandler.addErrors(mono, "testMethod"))
                .expectNextMatches(respuesta ->
                        respuesta.getResponseCode().equals(HttpStatus.CREATED.value()) &&
                                respuesta.getResponseMessage().equals("Operación exitosa.")
                )
                .verifyComplete();
    }

    @Test
    void addErrors_whenFieldErrorException_shouldReturnCustomErrorResponse() {
        // Arrange
        List<FieldError> fieldErrors = List.of(new FieldError("campo", "mensaje de error"));
        FieldErrorException exception = new FieldErrorException(ResponseCode.MSSO002, "Error personalizado", fieldErrors.toString());
        Mono<GenericResponseDTO<String>> monoError = Mono.error(exception);

        // Act & Assert
        StepVerifier.create(errorHandler.addErrors(monoError, "testMethod"))
                .expectNextMatches(respuesta ->
                        respuesta.getResponseCode().equals(HttpStatus.BAD_REQUEST.value()) &&
                                respuesta.getResponseMessage().equals(ResponseCode.MSSO002.getMessage())
                )
                .verifyComplete();
    }

    @Test
    void addErrors_whenDuplicateException_shouldReturnCustomErrorResponse() {
        // Arrange
        DuplicateEntryException exception = new DuplicateEntryException(ResponseCode.MSSO003, "Error, dato duplicado");
        Mono<GenericResponseDTO<String>> monoError = Mono.error(exception);

        // Act & Assert
        StepVerifier.create(errorHandler.addErrors(monoError, "testMethod"))
                .expectNextMatches(respuesta ->
                        respuesta.getResponseCode().equals(HttpStatus.CONFLICT.value()) &&
                                respuesta.getResponseMessage().equals(ResponseCode.MSSO003.getMessage())
                )
                .verifyComplete();
    }

    @Test
    void addErrors_whenInternalException_shouldReturnCustomErrorResponse() {
        // Arrange
        InternalErrorException exception = new InternalErrorException(ResponseCode.MSSO000, "Error inesperado");
        Mono<GenericResponseDTO<String>> monoError = Mono.error(exception);

        // Act & Assert
        StepVerifier.create(errorHandler.addErrors(monoError, "testMethod"))
                .expectNextMatches(respuesta ->
                        respuesta.getResponseCode().equals(HttpStatus.INTERNAL_SERVER_ERROR.value()) &&
                                respuesta.getResponseMessage().equals(ResponseCode.MSSO000.getMessage())
                )
                .verifyComplete();
    }

    @Test
    void addErrors_whenNotFoundException_shouldReturnCustomErrorResponse() {
        // Arrange
        NotFoundException exception = new NotFoundException(ResponseCode.MSSO004, "Error inesperado");
        Mono<GenericResponseDTO<String>> monoError = Mono.error(exception);

        // Act & Assert
        StepVerifier.create(errorHandler.addErrors(monoError, "testMethod"))
                .expectNextMatches(respuesta ->
                        respuesta.getResponseCode().equals(HttpStatus.NOT_FOUND.value()) &&
                                respuesta.getResponseMessage().equals(ResponseCode.MSSO004.getMessage())
                )
                .verifyComplete();
    }

    @Test
    void addErrors_whenLoginException_shouldReturnCustomErrorResponse() {
        // Arrange
        LoginException exception = new LoginException(ResponseCode.MSSO007, "Error inesperado");
        Mono<GenericResponseDTO<String>> monoError = Mono.error(exception);

        // Act & Assert
        StepVerifier.create(errorHandler.addErrors(monoError, "testMethod"))
                .expectNextMatches(respuesta ->
                        respuesta.getResponseCode().equals(HttpStatus.UNAUTHORIZED.value()) &&
                                respuesta.getResponseMessage().equals(ResponseCode.MSSO007.getMessage())
                )
                .verifyComplete();
    }

    @Test
    void addErrors_whenDependencyException_shouldReturnCustomErrorResponse() {
        // Arrange
        DependencyException exception = new DependencyException(ResponseCode.MSSO009, "Error inesperado");
        Mono<GenericResponseDTO<String>> monoError = Mono.error(exception);

        // Act & Assert
        StepVerifier.create(errorHandler.addErrors(monoError, "testMethod"))
                .expectNextMatches(respuesta ->
                        respuesta.getResponseCode().equals(HttpStatus.FAILED_DEPENDENCY.value()) &&
                                respuesta.getResponseMessage().equals(ResponseCode.MSSO009.getMessage())
                )
                .verifyComplete();
    }

    @Test
    void addErrors_whenGenericException_shouldReturnDefaultErrorResponse() {
        // Arrange
        RuntimeException exception = new RuntimeException("error inesperado");
        Mono<GenericResponseDTO<String>> monoError = Mono.error(exception);

        // Act & Assert
        StepVerifier.create(errorHandler.addErrors(monoError, "testMethod"))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(RuntimeException.class, error);
                    assertEquals("error inesperado", error.getMessage());
                })
                .verify();
    }
}
