package co.com.pragma.api.handler;

import co.com.pragma.api.dto.CreateLoanApplicationDTO;
import co.com.pragma.api.dto.LoanApplicationPageDTO;
import co.com.pragma.api.dto.LoanApplicationPageListDTO;
import co.com.pragma.api.mapper.LoanApplicationApiRestMapper;
import co.com.pragma.api.security.utils.JwtUtils;
import co.com.pragma.model.error.InternalErrorException;
import co.com.pragma.model.loan_application.LoanApplication;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.model.loan_application.LoanApplicationPage;
import co.com.pragma.model.loan_application.LoanApplicationPageList;
import co.com.pragma.model.loan_application.util.LoanApplicationStatus;
import co.com.pragma.usecase.loan_application.ListLoanApplicationUseCase;
import co.com.pragma.usecase.loan_application.RegisterLoanApplicationUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationHandlerTest {

    private static final String TOKEN = "Bearer eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9";

    @Mock
    private RegisterLoanApplicationUseCase registerLoanApplicationUseCase;

    @Mock
    private ListLoanApplicationUseCase listLoanApplicationUseCase;

    @Mock
    private LoanApplicationApiRestMapper loanApplicationApiRestMapper;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private LoanApplicationHandler loanApplicationHandler;

    @Test
    void guardarLoanApplication_debeRetornarRespuestaExitosa() {
        // Arrange
        CreateLoanApplicationDTO createLoanApplicationDTO = new CreateLoanApplicationDTO();
        createLoanApplicationDTO.setIdentification("id-123");

        LoanApplication loanApplication = new LoanApplication();

        when(loanApplicationApiRestMapper.createLoanApplicationDTOToLoanApplication(any(CreateLoanApplicationDTO.class)))
                .thenReturn(loanApplication);
        when(registerLoanApplicationUseCase.execute(any(LoanApplication.class)))
                .thenReturn(Mono.empty());
        when(jwtUtils.getClaim(anyString(), anyString()))
                .thenReturn(Mono.justOrEmpty("id-123"));

        // Act & Assert
        StepVerifier.create(loanApplicationHandler.createLoanApplication(createLoanApplicationDTO, TOKEN))
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
        createLoanApplicationDTO.setIdentification("id-123");

        LoanApplication loanApplication = new LoanApplication();

        when(loanApplicationApiRestMapper.createLoanApplicationDTOToLoanApplication(any(CreateLoanApplicationDTO.class)))
                .thenReturn(loanApplication);
        when(registerLoanApplicationUseCase.execute(any(LoanApplication.class)))
                .thenReturn(Mono.error(new InternalErrorException(ResponseCode.MSSO000, "Fallo de prueba")));
        when(jwtUtils.getClaim(anyString(), anyString()))
                .thenReturn(Mono.justOrEmpty("id-123"));


        // Act & Assert
        StepVerifier.create(loanApplicationHandler.createLoanApplication(createLoanApplicationDTO, TOKEN))
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
        createLoanApplicationDTO.setIdentification("id-123");

        when(loanApplicationApiRestMapper.createLoanApplicationDTOToLoanApplication(any(CreateLoanApplicationDTO.class)))
                .thenThrow(new InternalErrorException(ResponseCode.MSSO000, "Fallo de prueba"));
        when(jwtUtils.getClaim(anyString(), anyString()))
                .thenReturn(Mono.justOrEmpty("id-123"));

        // Act & Assert
        StepVerifier.create(loanApplicationHandler.createLoanApplication(createLoanApplicationDTO, TOKEN))
                .assertNext(respuesta -> {
                    assertNotNull(respuesta);
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), respuesta.getResponseCode());
                    assertNull(respuesta.getData());
                })
                .verifyComplete();

        verify(loanApplicationApiRestMapper, times(1)).createLoanApplicationDTOToLoanApplication(any(CreateLoanApplicationDTO.class));
        verify(registerLoanApplicationUseCase, never()).execute(any(LoanApplication.class));
    }

    @Test
    void listarLoanApplication_debeRetornarRespuestaExitosa() {
        // Arrange
        LoanApplicationPageDTO loanApplicationPageDTO = new LoanApplicationPageDTO();
        loanApplicationPageDTO.setAmount(100d);
        LoanApplicationPageListDTO loanApplicationPageListDTO = new LoanApplicationPageListDTO();
        loanApplicationPageListDTO.setLoanApplications(List.of(loanApplicationPageDTO));

        LoanApplicationPage loanApplicationPage = new LoanApplicationPage();
        LoanApplicationPageList loanApplicationPageList = new LoanApplicationPageList();
        loanApplicationPageList.setLoanApplications(List.of(loanApplicationPage));

        when(listLoanApplicationUseCase.execute(any(), anyInt(), anyInt()))
                .thenReturn(Mono.just(loanApplicationPageList));
        when(loanApplicationApiRestMapper.loanApplicationPageListToLoanApplicationPageListDTO(any(LoanApplicationPageList.class)))
                .thenReturn(loanApplicationPageListDTO);

        // Act & Assert
        StepVerifier.create(loanApplicationHandler.listLoanApllications(LoanApplicationStatus.PENDING, 1, 5))
                .assertNext(respuesta -> {
                    assertNotNull(respuesta);
                    assertEquals(HttpStatus.OK.value(), respuesta.getResponseCode());
                    assertNotNull(respuesta.getData());
                    assertEquals(1, respuesta.getData().getLoanApplications().size());
                })
                .verifyComplete();

        verify(loanApplicationApiRestMapper, times(1)).loanApplicationPageListToLoanApplicationPageListDTO(any(LoanApplicationPageList.class));
        verify(listLoanApplicationUseCase, times(1)).execute(any(), anyInt(), anyInt());
    }

    @Test
    void listarLoanApplication_deberiaRetornarError_cuandoFalla() {
        // Arrange
        when(listLoanApplicationUseCase.execute(any(), anyInt(), anyInt()))
                .thenReturn(Mono.error(new InternalErrorException(ResponseCode.MSSO000, "Fallo de prueba")));

        // Act & Assert
        StepVerifier.create(loanApplicationHandler.listLoanApllications(LoanApplicationStatus.PENDING, 1, 5))
                .assertNext(respuesta -> {
                    assertNotNull(respuesta);
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), respuesta.getResponseCode());
                    assertNull(respuesta.getData());
                })
                .verifyComplete();

        verify(loanApplicationApiRestMapper, never()).loanApplicationPageListToLoanApplicationPageListDTO(any(LoanApplicationPageList.class));
        verify(listLoanApplicationUseCase, times(1)).execute(any(), anyInt(), anyInt());
    }
}