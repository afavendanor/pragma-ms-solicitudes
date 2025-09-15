package co.com.pragma.usecase.loan_application;

import co.com.pragma.model.error.InternalErrorException;
import co.com.pragma.model.error.NotFoundException;
import co.com.pragma.model.loan_application.LoanApplication;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.model.loan_application.LoanApplicationStatus;
import co.com.pragma.model.loan_application.LoanType;
import co.com.pragma.model.loan_application.gateways.LoanApplicationRepository;
import co.com.pragma.model.loan_application.gateways.LoanApplicationSNSSenderGateway;
import co.com.pragma.model.loan_application.gateways.LoanApplicationStatusRepository;
import co.com.pragma.model.loan_application.gateways.LoanTypeRepository;
import org.junit.jupiter.api.BeforeEach;
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
class RegisterLoanApplicationUseCaseTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private LoanApplicationStatusRepository loanApplicationStatusRepository;

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @Mock
    private LoanApplicationSNSSenderGateway loanApplicationSNSSenderGateway;

    @InjectMocks
    private RegisterLoanApplicationUseCase registerLoanApplicationUseCase;

    private LoanType loanType;
    private LoanApplicationStatus loanApplicationStatus;

    @BeforeEach
    void setup() {
        loanType = new LoanType();
        loanType.setId(1L);
        loanType.setName("quick_loan");

        loanApplicationStatus = new LoanApplicationStatus();
        loanApplicationStatus.setId(1L);
        loanApplicationStatus.setName("pending_review");
    }

    @Test
    void createLoanApplication_OK() {
        // Arrange
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setLoanTypeId(1L);
        loanType.setAutomaticValidation(Boolean.TRUE);

        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenReturn(Mono.just(loanApplication));
        when(loanTypeRepository.findById(anyLong()))
                .thenReturn(Mono.just(loanType));
        when(loanApplicationStatusRepository.findByName(anyString()))
                .thenReturn(Mono.just(loanApplicationStatus));
        when(loanApplicationSNSSenderGateway.send(any(), anyString()))
                .thenReturn(Mono.just("Ok"));

        // Act & Assert
        StepVerifier.create(registerLoanApplicationUseCase.execute(loanApplication))
                .expectNextMatches(resultado -> resultado.equals(loanApplication))
                .verifyComplete();

        verify(loanApplicationRepository, times(1)).save(any(LoanApplication.class));
    }

    @Test
    void createLoanApplication_errorSNS() {
        // Arrange
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setLoanTypeId(1L);
        loanType.setAutomaticValidation(Boolean.TRUE);

        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenReturn(Mono.just(loanApplication));
        when(loanTypeRepository.findById(anyLong()))
                .thenReturn(Mono.just(loanType));
        when(loanApplicationStatusRepository.findByName(anyString()))
                .thenReturn(Mono.just(loanApplicationStatus));
        when(loanApplicationSNSSenderGateway.send(any(), anyString()))
                .thenReturn(Mono.error(new InternalErrorException(ResponseCode.MSSO000)));

        // Act & Assert
        StepVerifier.create(registerLoanApplicationUseCase.execute(loanApplication))
                .expectNextMatches(resultado -> resultado.equals(loanApplication))
                .verifyComplete();

        verify(loanApplicationRepository, times(1)).save(any(LoanApplication.class));
    }


    @Test
    void createLoanApplication_Error() {
        // Arrange
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setLoanTypeId(1L);
        loanType.setAutomaticValidation(Boolean.FALSE);

        when(loanTypeRepository.findById(anyLong()))
                .thenReturn(Mono.just(loanType));
        when(loanApplicationStatusRepository.findByName(anyString()))
                .thenReturn(Mono.just(loanApplicationStatus));
        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenReturn(Mono.error(new InternalErrorException(ResponseCode.MSSO000, "Error guardando solicitud")));

        // Act & Assert
        StepVerifier.create(registerLoanApplicationUseCase.execute(loanApplication))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(InternalErrorException.class, error);
                    assertEquals(ResponseCode.MSSO000.getMessage(), error.getMessage());
                })
                .verify();

        verify(loanTypeRepository, times(1)).findById(anyLong());
        verify(loanApplicationStatusRepository, times(1)).findByName(anyString());
        verify(loanApplicationRepository, times(1)).save(any(LoanApplication.class));
    }

    @Test
    void createLoanApplication_ErrorLoanTypeGateway() {
        // Arrange
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setLoanTypeId(1L);
        loanType.setAutomaticValidation(Boolean.FALSE);

        when(loanTypeRepository.findById(anyLong()))
                .thenReturn(Mono.error(new NotFoundException(ResponseCode.MSSO004, "Error obteniendo tipo")));

        // Act & Assert
        StepVerifier.create(registerLoanApplicationUseCase.execute(loanApplication))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(NotFoundException.class, error);
                    assertEquals(ResponseCode.MSSO004.getMessage(), error.getMessage());
                })
                .verify();

        verify(loanTypeRepository, times(1)).findById(anyLong());
        verify(loanApplicationStatusRepository, never()).findByName(anyString());
        verify(loanApplicationRepository, never()).save(any(LoanApplication.class));
    }

    @Test
    void createLoanApplication_ErrorStatusGateway() {
        // Arrange
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setLoanTypeId(1L);
        loanType.setAutomaticValidation(Boolean.FALSE);

        when(loanTypeRepository.findById(anyLong()))
                .thenReturn(Mono.just(loanType));
        when(loanApplicationStatusRepository.findByName(anyString()))
                .thenReturn(Mono.error(new InternalErrorException(ResponseCode.MSSO000, "Error obteniendo estado")));

        // Act & Assert
        StepVerifier.create(registerLoanApplicationUseCase.execute(loanApplication))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(InternalErrorException.class, error);
                    assertEquals(ResponseCode.MSSO000.getMessage(), error.getMessage());
                })
                .verify();

        verify(loanTypeRepository, times(1)).findById(anyLong());
        verify(loanApplicationStatusRepository, times(1)).findByName(anyString());
        verify(loanApplicationRepository, never()).save(any(LoanApplication.class));
    }

}