package co.com.pragma.usecase.loan_application;

import co.com.pragma.model.error.NotFoundException;
import co.com.pragma.model.loan_application.LoanApplication;
import co.com.pragma.model.loan_application.LoanApplicationStatus;
import co.com.pragma.model.loan_application.gateways.LoanApplicationRepository;
import co.com.pragma.model.loan_application.gateways.LoanApplicationSNSSenderGateway;
import co.com.pragma.model.loan_application.gateways.LoanApplicationStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateLoanApplicationUseCaseTest {

    @Mock
    private LoanApplicationStatusRepository loanApplicationStatusRepository;

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private LoanApplicationSNSSenderGateway loanApplicationSNSSenderGateway;

    @InjectMocks
    private UpdateLoanApplicationUseCase updateLoanApplicationUseCase;

    private LoanApplication loanApplication;
    private LoanApplicationStatus status;

    @BeforeEach
    void setUp() {
        status = new LoanApplicationStatus();
        status.setId(1L);
        status.setName("APPROVED");

        loanApplication = new LoanApplication();
        loanApplication.setStatus(status);

    }

    @Test
    void shouldExecuteAndSendWhenStatusApproved() {
        when(loanApplicationStatusRepository.findByName(anyString()))
                .thenReturn(Mono.just(status));
        when(loanApplicationRepository.save(any()))
                .thenReturn(Mono.just(loanApplication));
        when(loanApplicationSNSSenderGateway.send(any(), anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(updateLoanApplicationUseCase.execute(loanApplication))
                .verifyComplete();

        verify(loanApplicationSNSSenderGateway, times(1)).send(any(LoanApplication.class), anyString());
    }

    @Test
    void shouldThrowWhenStatusNotFound() {
        when(loanApplicationStatusRepository.findByName(anyString()))
                .thenReturn(Mono.empty());

        StepVerifier.create(updateLoanApplicationUseCase.execute(loanApplication))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void shouldNotSendWhenStatusNotApprovedOrRejected() {
        status.setName(co.com.pragma.model.loan_application.util.LoanApplicationStatus.PENDING.name());
        when(loanApplicationStatusRepository.findByName(anyString()))
                .thenReturn(Mono.just(status));
        when(loanApplicationRepository.save(any()))
                .thenReturn(Mono.just(loanApplication));

        StepVerifier.create(updateLoanApplicationUseCase.execute(loanApplication))
                .verifyComplete();

        verify(loanApplicationSNSSenderGateway, never()).send(any(), anyString());
    }

    @Test
    void shouldCompleteEvenWhenSendFails() {
        when(loanApplicationStatusRepository.findByName(anyString()))
                .thenReturn(Mono.just(status));
        when(loanApplicationRepository.save(any()))
                .thenReturn(Mono.just(loanApplication));
        when(loanApplicationSNSSenderGateway.send(any(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("SNS down")));

        StepVerifier.create(updateLoanApplicationUseCase.execute(loanApplication))
                .verifyComplete();

        verify(loanApplicationSNSSenderGateway, times(1)).send(any(LoanApplication.class), anyString());
    }
}
