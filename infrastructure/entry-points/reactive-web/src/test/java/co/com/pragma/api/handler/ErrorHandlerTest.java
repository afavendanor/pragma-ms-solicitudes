package co.com.pragma.api.handler;

import co.com.pragma.api.dto.GenericResponseDTO;
import co.com.pragma.model.error.CustomException;
import co.com.pragma.model.error.FieldError;
import co.com.pragma.model.error.ResponseCode;
import org.junit.jupiter.api.Test;
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
        GenericResponseDTO<String> respuestaOk = new GenericResponseDTO<>(ResponseCode.MSSO001, "todo bien");
        Mono<GenericResponseDTO<String>> mono = Mono.just(respuestaOk);

        // Act & Assert
        StepVerifier.create(errorHandler.addErrors(mono, "testMethod"))
                .expectNextMatches(respuesta ->
                        respuesta.getResponseCode().equals(ResponseCode.MSSO001.name()) &&
                                respuesta.getResponseMessage().equals("Operación exitosa.")
                )
                .verifyComplete();
    }

    @Test
    void addErrors_whenCustomException_shouldReturnCustomErrorResponse() {
        // Arrange
        List<FieldError> fieldErrors = List.of(new FieldError("campo", "mensaje de error"));
        CustomException exception = new CustomException(ResponseCode.MSSO002, "Error personalizado", fieldErrors.toString());
        Mono<GenericResponseDTO<String>> monoError = Mono.error(exception);

        // Act & Assert
        StepVerifier.create(errorHandler.addErrors(monoError, "testMethod"))
                .expectNextMatches(respuesta ->
                        respuesta.getResponseCode().equals(ResponseCode.MSSO002.name()) &&
                                respuesta.getResponseMessage().equals("Campos no son validos.")
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
