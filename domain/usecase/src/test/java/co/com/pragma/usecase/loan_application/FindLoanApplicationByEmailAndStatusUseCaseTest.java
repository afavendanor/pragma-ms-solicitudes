package co.com.pragma.usecase.loan_application;

import co.com.pragma.model.error.NotFoundException;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.model.loan_application.LoanApplication;
import co.com.pragma.model.loan_application.LoanType;
import co.com.pragma.model.loan_application.gateways.LoanApplicationRepository;
import co.com.pragma.model.loan_application.gateways.LoanApplicationStatusRepository;
import co.com.pragma.model.loan_application.gateways.LoanTypeRepository;
import co.com.pragma.model.loan_application.LoanApplicationStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindLoanApplicationByEmailAndStatusUseCaseTest {

    @Mock
    private LoanApplicationStatusRepository loanApplicationStatusRepository;

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @InjectMocks
    private FindLoanApplicationByEmailAndStatusUseCase useCase;

    @Test
    void execute_withValidData_returnsLoanApplications() {
        // Arrange
        String email = "andres@example.com";
        LoanApplicationStatus status = new LoanApplicationStatus();
        status.setId(2L);

        LoanApplication loanApp = new LoanApplication();
        loanApp.setLoanTypeId(99L);

        LoanType loanType = new LoanType();
        loanType.setId(99L);
        loanType.setName("PERSONAL");

        when(loanApplicationStatusRepository.findByName(anyString()))
                .thenReturn(Mono.just(status));
        when(loanApplicationRepository.getByEmailAndStatus(anyString(), anyLong()))
                .thenReturn(Flux.just(loanApp));
        when(loanTypeRepository.findById(anyLong()))
                .thenReturn(Mono.just(loanType));

        // Act
        Flux<LoanApplication> result = useCase.execute(email,
                co.com.pragma.model.loan_application.util.LoanApplicationStatus.APPROVED);

        // Assert
        StepVerifier.create(result)
                .assertNext(app -> {
                    assertEquals(status, app.getStatus());
                    assertEquals(loanType, app.getLoanType());
                })
                .verifyComplete();
    }

    @Test
    void execute_whenStatusNotFound_throwsNotFoundException() {
        // Arrange
        String email = "andres@example.com";
        LoanApplicationStatus status = new LoanApplicationStatus();
        status.setId(1L);

        when(loanApplicationStatusRepository.findByName(anyString()))
                .thenReturn(Mono.empty());

        // Act
        Flux<LoanApplication> result = useCase.execute(email, co.com.pragma.model.loan_application.util.LoanApplicationStatus.APPROVED);

        // Assert
        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(NotFoundException.class, error);
                    assertEquals(ResponseCode.MSSO010.getMessage(), error.getMessage());
                })
                .verify();
    }

    @Test
    void execute_whenLoanTypeNotFound_throwsNotFoundException() {
        // Arrange
        String email = "andres@example.com";
        LoanApplicationStatus status = new LoanApplicationStatus();
        status.setId(1L);

        LoanApplication loanApp = new LoanApplication();
        loanApp.setLoanTypeId(123L);

        when(loanApplicationStatusRepository.findByName(anyString()))
                .thenReturn(Mono.just(status));
        when(loanApplicationRepository.getByEmailAndStatus(anyString(), anyLong()))
                .thenReturn(Flux.just(loanApp));
        when(loanTypeRepository.findById(anyLong()))
                .thenReturn(Mono.empty());

        // Act
        Flux<LoanApplication> result = useCase.execute(email,
                co.com.pragma.model.loan_application.util.LoanApplicationStatus.APPROVED);

        // Assert
        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(NotFoundException.class, error);
                    assertEquals(ResponseCode.MSSO004.getMessage(), error.getMessage());
                })
                .verify();
    }

    @Test
    void execute_whenNoLoanApplications_returnsEmptyFlux() {
        // Arrange
        String email = "andres@example.com";
        LoanApplicationStatus status = new LoanApplicationStatus();
        status.setId(1L);

        when(loanApplicationStatusRepository.findByName(anyString()))
                .thenReturn(Mono.just(status));
        when(loanApplicationRepository.getByEmailAndStatus(anyString(), anyLong()))
                .thenReturn(Flux.empty());

        // Act
        Flux<LoanApplication> result = useCase.execute(email,
                co.com.pragma.model.loan_application.util.LoanApplicationStatus.APPROVED);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }
}

