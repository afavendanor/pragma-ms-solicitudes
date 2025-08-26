package co.com.pragma.api.handler;

import co.com.pragma.api.dto.CreateSolicitudeDTO;
import co.com.pragma.api.mapper.SolicitudeApiRestMapper;
import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.error.CustomException;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.usecase.solicitude.RegisterSolicitudeUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitudeHandlerTest {

    @Mock
    private RegisterSolicitudeUseCase registerSolicitudeUseCase;

    @Mock
    private SolicitudeApiRestMapper SolicitudeApiRestMapper;

    @InjectMocks
    private SolicitudeHandler SolicitudeHandler;

    @Test
    void guardarUsuario_debeRetornarRespuestaExitosa() {
        // Arrange
        CreateSolicitudeDTO createSolicitudeDTO = new CreateSolicitudeDTO();

        Solicitude solicitude = new Solicitude();

        when(SolicitudeApiRestMapper.createSolicitudeDTOToSolicitude(any(CreateSolicitudeDTO.class)))
                .thenReturn(solicitude);
        when(registerSolicitudeUseCase.execute(any(Solicitude.class)))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(SolicitudeHandler.createSolicitude(createSolicitudeDTO))
                .assertNext(respuesta -> {
                    assertNotNull(respuesta);
                    assertEquals(ResponseCode.MSSO001, ResponseCode.valueOf(respuesta.getResponseCode()));
                    assertNull(respuesta.getData());
                })
                .verifyComplete();

        verify(SolicitudeApiRestMapper, times(1)).createSolicitudeDTOToSolicitude(any(CreateSolicitudeDTO.class));
        verify(registerSolicitudeUseCase, times(1)).execute(any(Solicitude.class));
    }

    @Test
    void guardarUsuario_deberiaRetornarError_cuandoFalla() {
        // Arrange
        CreateSolicitudeDTO createSolicitudeDTO = new CreateSolicitudeDTO();

        Solicitude Solicitude = new Solicitude();

        when(SolicitudeApiRestMapper.createSolicitudeDTOToSolicitude(any(CreateSolicitudeDTO.class)))
                .thenReturn(Solicitude);
        when(registerSolicitudeUseCase.execute(any(Solicitude.class)))
                .thenReturn(Mono.error(new CustomException(ResponseCode.MSSO000, "Fallo de prueba")));

        // Act & Assert
        StepVerifier.create(SolicitudeHandler.createSolicitude(createSolicitudeDTO))
                .assertNext(respuesta -> {
                    assertNotNull(respuesta);
                    assertEquals(ResponseCode.MSSO000, ResponseCode.valueOf(respuesta.getResponseCode()));
                    assertNull(respuesta.getData());
                })
                .verifyComplete();

        verify(SolicitudeApiRestMapper, times(1)).createSolicitudeDTOToSolicitude(any(CreateSolicitudeDTO.class));
        verify(registerSolicitudeUseCase, times(1)).execute(any(Solicitude.class));
    }

    @Test
    void guardarUsuario_deberiaRetornarError_cuandoFallaMapper() {
        // Arrange
        CreateSolicitudeDTO createSolicitudeDTO = new CreateSolicitudeDTO();

        Solicitude Solicitude = new Solicitude();

        when(SolicitudeApiRestMapper.createSolicitudeDTOToSolicitude(any(CreateSolicitudeDTO.class)))
                .thenThrow(new CustomException(ResponseCode.MSSO000, "Fallo de prueba"));

        // Act & Assert
        StepVerifier.create(SolicitudeHandler.createSolicitude(createSolicitudeDTO))
                .assertNext(respuesta -> {
                    assertNotNull(respuesta);
                    assertEquals(ResponseCode.MSSO000, ResponseCode.valueOf(respuesta.getResponseCode()));
                    assertNull(respuesta.getData());
                })
                .verifyComplete();

        verify(SolicitudeApiRestMapper, times(1)).createSolicitudeDTOToSolicitude(any(CreateSolicitudeDTO.class));
        verify(registerSolicitudeUseCase, never()).execute(any(Solicitude.class));
    }
}