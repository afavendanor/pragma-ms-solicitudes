package co.com.pragma.usecase.solicitude;

import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.error.CustomException;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.model.solicitude.gateways.SolicitudeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterSolicitudeUseCaseTest {

    @Mock
    private SolicitudeRepository solicitudeRepository;

    @InjectMocks
    private RegisterSolicitudeUseCase registerSolicitudeUseCase;

    @Test
    void createSolicitude_OK() {
        // Arrange
        Solicitude solicitude = new Solicitude();
        when(solicitudeRepository.save(any(Solicitude.class)))
                .thenReturn(Mono.just(solicitude));

        // Act & Assert
        StepVerifier.create(registerSolicitudeUseCase.execute(solicitude))
                .expectNextMatches(resultado -> resultado.equals(solicitude))
                .verifyComplete();

        verify(solicitudeRepository, times(1)).save(any(Solicitude.class));
    }


    @Test
    void createSolicitude_Error() {
        // Arrange
        Solicitude solicitude = new Solicitude();
        when(solicitudeRepository.save(any(Solicitude.class)))
                .thenReturn(Mono.error(new CustomException(ResponseCode.MSSO000, "Error guardando usuario")));

        // Act & Assert
        StepVerifier.create(registerSolicitudeUseCase.execute(solicitude))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(CustomException.class, error);
                    assertEquals(ResponseCode.MSSO000, ((CustomException) error).getResponseCode());
                })
                .verify();

        verify(solicitudeRepository, times(1)).save(any(Solicitude.class));
    }

}