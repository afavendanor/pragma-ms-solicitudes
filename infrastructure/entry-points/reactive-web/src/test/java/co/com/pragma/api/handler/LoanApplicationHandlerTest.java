package co.com.pragma.api.handler;

import co.com.pragma.api.dto.CreateLoanApplicationDTO;
import co.com.pragma.api.mapper.LoanApplicationApiRestMapper;
import co.com.pragma.model.loan_application.LoanApplication;
import co.com.pragma.model.error.CustomException;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.usecase.loan_application.RegisterLoanApplicationUseCase;
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
class LoanApplicationHandlerTest {

    @Mock
    private RegisterLoanApplicationUseCase registerLoanApplicationUseCase;

    @Mock
    private LoanApplicationApiRestMapper LoanApplicationApiRestMapper;

    @InjectMocks
    private LoanApplicationHandler LoanApplicationHandler;

    @Test
    void guardarUsuario_debeRetornarRespuestaExitosa() {
        // Arrange
        CreateLoanApplicationDTO createLoanApplicationDTO = new CreateLoanApplicationDTO();

        LoanApplication loanApplication = new LoanApplication();

        when(LoanApplicationApiRestMapper.createLoanApplicationDTOToLoanApplication(any(CreateLoanApplicationDTO.class)))
                .thenReturn(loanApplication);
        when(registerLoanApplicationUseCase.execute(any(LoanApplication.class)))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(LoanApplicationHandler.createLoanApplication(createLoanApplicationDTO))
                .assertNext(respuesta -> {
                    assertNotNull(respuesta);
                    assertEquals(ResponseCode.MSSO001, ResponseCode.valueOf(respuesta.getResponseCode()));
                    assertNull(respuesta.getData());
                })
                .verifyComplete();

        verify(LoanApplicationApiRestMapper, times(1)).createLoanApplicationDTOToLoanApplication(any(CreateLoanApplicationDTO.class));
        verify(registerLoanApplicationUseCase, times(1)).execute(any(LoanApplication.class));
    }

    @Test
    void guardarUsuario_deberiaRetornarError_cuandoFalla() {
        // Arrange
        CreateLoanApplicationDTO createLoanApplicationDTO = new CreateLoanApplicationDTO();

        LoanApplication LoanApplication = new LoanApplication();

        when(LoanApplicationApiRestMapper.createLoanApplicationDTOToLoanApplication(any(CreateLoanApplicationDTO.class)))
                .thenReturn(LoanApplication);
        when(registerLoanApplicationUseCase.execute(any(LoanApplication.class)))
                .thenReturn(Mono.error(new CustomException(ResponseCode.MSSO000, "Fallo de prueba")));

        // Act & Assert
        StepVerifier.create(LoanApplicationHandler.createLoanApplication(createLoanApplicationDTO))
                .assertNext(respuesta -> {
                    assertNotNull(respuesta);
                    assertEquals(ResponseCode.MSSO000, ResponseCode.valueOf(respuesta.getResponseCode()));
                    assertNull(respuesta.getData());
                })
                .verifyComplete();

        verify(LoanApplicationApiRestMapper, times(1)).createLoanApplicationDTOToLoanApplication(any(CreateLoanApplicationDTO.class));
        verify(registerLoanApplicationUseCase, times(1)).execute(any(LoanApplication.class));
    }

    @Test
    void guardarUsuario_deberiaRetornarError_cuandoFallaMapper() {
        // Arrange
        CreateLoanApplicationDTO createLoanApplicationDTO = new CreateLoanApplicationDTO();

        LoanApplication LoanApplication = new LoanApplication();

        when(LoanApplicationApiRestMapper.createLoanApplicationDTOToLoanApplication(any(CreateLoanApplicationDTO.class)))
                .thenThrow(new CustomException(ResponseCode.MSSO000, "Fallo de prueba"));

        // Act & Assert
        StepVerifier.create(LoanApplicationHandler.createLoanApplication(createLoanApplicationDTO))
                .assertNext(respuesta -> {
                    assertNotNull(respuesta);
                    assertEquals(ResponseCode.MSSO000, ResponseCode.valueOf(respuesta.getResponseCode()));
                    assertNull(respuesta.getData());
                })
                .verifyComplete();

        verify(LoanApplicationApiRestMapper, times(1)).createLoanApplicationDTOToLoanApplication(any(CreateLoanApplicationDTO.class));
        verify(registerLoanApplicationUseCase, never()).execute(any(LoanApplication.class));
    }
}