package co.com.pragma.api.handler;

import co.com.pragma.api.dto.CreateLoanApplicationDTO;
import co.com.pragma.api.mapper.LoanApplicationApiRestMapper;
import co.com.pragma.model.error.InternalErrorException;
import co.com.pragma.model.loan_application.LoanApplication;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.usecase.loan_application.RegisterLoanApplicationUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
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
    private LoanApplicationApiRestMapper loanApplicationApiRestMapper;

    @InjectMocks
    private LoanApplicationHandler loanApplicationHandler;

    @Test
    void guardarLoanApplication_debeRetornarRespuestaExitosa() {
        // Arrange
        CreateLoanApplicationDTO createLoanApplicationDTO = new CreateLoanApplicationDTO();

        LoanApplication loanApplication = new LoanApplication();

        when(loanApplicationApiRestMapper.createLoanApplicationDTOToLoanApplication(any(CreateLoanApplicationDTO.class)))
                .thenReturn(loanApplication);
        when(registerLoanApplicationUseCase.execute(any(LoanApplication.class)))
                .thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(loanApplicationHandler.createLoanApplication(createLoanApplicationDTO))
                .assertNext(respuesta -> {
                    assertNotNull(respuesta);
                    assertEquals(HttpStatus.CREATED.value(), respuesta.getResponseCode());
                    assertNull(respuesta.getData());
                })
                .verifyComplete();

        verify(loanApplicationApiRestMapper, times(1)).createLoanApplicationDTOToLoanApplication(any(CreateLoanApplicationDTO.class));
        verify(registerLoanApplicationUseCase, times(1)).execute(any(LoanApplication.class));
    }

    @Test
    void guardarLoanApplication_deberiaRetornarError_cuandoFalla() {
        // Arrange
        CreateLoanApplicationDTO createLoanApplicationDTO = new CreateLoanApplicationDTO();

        LoanApplication loanApplication = new LoanApplication();

        when(loanApplicationApiRestMapper.createLoanApplicationDTOToLoanApplication(any(CreateLoanApplicationDTO.class)))
                .thenReturn(loanApplication);
        when(registerLoanApplicationUseCase.execute(any(LoanApplication.class)))
                .thenReturn(Mono.error(new InternalErrorException(ResponseCode.MSSO000, "Fallo de prueba")));

        // Act & Assert
        StepVerifier.create(loanApplicationHandler.createLoanApplication(createLoanApplicationDTO))
                .assertNext(respuesta -> {
                    assertNotNull(respuesta);
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), respuesta.getResponseCode());
                    assertNull(respuesta.getData());
                })
                .verifyComplete();

        verify(loanApplicationApiRestMapper, times(1)).createLoanApplicationDTOToLoanApplication(any(CreateLoanApplicationDTO.class));
        verify(registerLoanApplicationUseCase, times(1)).execute(any(LoanApplication.class));
    }

    @Test
    void guardarLoanApplication_deberiaRetornarError_cuandoFallaMapper() {
        // Arrange
        CreateLoanApplicationDTO createLoanApplicationDTO = new CreateLoanApplicationDTO();

        when(loanApplicationApiRestMapper.createLoanApplicationDTOToLoanApplication(any(CreateLoanApplicationDTO.class)))
                .thenThrow(new InternalErrorException(ResponseCode.MSSO000, "Fallo de prueba"));

        // Act & Assert
        StepVerifier.create(loanApplicationHandler.createLoanApplication(createLoanApplicationDTO))
                .assertNext(respuesta -> {
                    assertNotNull(respuesta);
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), respuesta.getResponseCode());
                    assertNull(respuesta.getData());
                })
                .verifyComplete();

        verify(loanApplicationApiRestMapper, times(1)).createLoanApplicationDTOToLoanApplication(any(CreateLoanApplicationDTO.class));
        verify(registerLoanApplicationUseCase, never()).execute(any(LoanApplication.class));
    }
}