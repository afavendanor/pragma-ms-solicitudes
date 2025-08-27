package co.com.pragma.usecase.loan_application;

import co.com.pragma.model.loan_application.LoanApplication;
import co.com.pragma.model.error.CustomException;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.model.loan_application.gateways.LoanApplicationRepository;
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

    @InjectMocks
    private RegisterLoanApplicationUseCase registerLoanApplicationUseCase;

    @Test
    void createLoanApplication_OK() {
        // Arrange
        LoanApplication loanApplication = new LoanApplication();
        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenReturn(Mono.just(loanApplication));

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
        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenReturn(Mono.error(new CustomException(ResponseCode.MSSO000, "Error guardando usuario")));

        // Act & Assert
        StepVerifier.create(registerLoanApplicationUseCase.execute(loanApplication))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(CustomException.class, error);
                    assertEquals(ResponseCode.MSSO000, ((CustomException) error).getResponseCode());
                })
                .verify();

        verify(loanApplicationRepository, times(1)).save(any(LoanApplication.class));
    }

}