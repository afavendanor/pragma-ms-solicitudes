package co.com.pragma.usecase.loan_application;

import co.com.pragma.model.error.NotFoundException;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.model.loan_application.LoanApplicationPage;
import co.com.pragma.model.loan_application.LoanApplicationPageList;
import co.com.pragma.model.loan_application.gateways.LoanApplicationRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListLoanApplicationUseCaseTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ListLoanApplicationUseCase listLoanApplicationUseCase;

    private LoanApplicationPage loanAppPage1;
    private LoanApplicationPage loanAppPage2;
    private User user1;
    private User user2;

    @BeforeEach
    void setup() {
        loanAppPage1 = new LoanApplicationPage();
        loanAppPage1.setEmail("user1@test.com");

        loanAppPage2 = new LoanApplicationPage();
        loanAppPage2.setEmail("user2@test.com");

        user1 = new User();
        user1.setEmail("user1@test.com");

        user2 = new User();
        user2.setEmail("user2@test.com");
    }

    @Test
    void execute_shouldReturnListWithUsers() {
        LoanApplicationPageList pageList = new LoanApplicationPageList();
        pageList.setLoanApplications(List.of(loanAppPage1, loanAppPage2));

        when(loanApplicationRepository.getFilterList(1L, 0, 10))
                .thenReturn(Mono.just(pageList));

        when(userRepository.findAllByEmails(anyList()))
                .thenReturn(Mono.just(List.of(user1, user2)));

        StepVerifier.create(listLoanApplicationUseCase.execute(1L, 0, 10))
                .assertNext(result -> {
                    assertThat(result.getLoanApplications()).hasSize(2);
                    assertThat(result.getLoanApplications().getFirst().getUser()).isNotNull();
                    assertThat(result.getLoanApplications().getFirst().getUser().getEmail())
                            .isEqualTo("user1@test.com");
                })
                .verifyComplete();
    }

    @Test
    void execute_shouldReturnEmptyWhenNoApplications() {
        LoanApplicationPageList pageList = new LoanApplicationPageList();
        pageList.setLoanApplications(Collections.emptyList());

        when(loanApplicationRepository.getFilterList(1L, 0, 10))
                .thenReturn(Mono.just(pageList));

        StepVerifier.create(listLoanApplicationUseCase.execute(1L, 0, 10))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(NotFoundException.class, error);
                    assertEquals(ResponseCode.MSSO004.getMessage(), error.getMessage());
                })
                .verify();

        verify(userRepository, never()).findAllByEmails(anyList());
    }

    @Test
    void execute_shouldReturnListWithoutUsersIfNotFound() {
        LoanApplicationPageList pageList = new LoanApplicationPageList();
        pageList.setLoanApplications(List.of(loanAppPage1));

        when(loanApplicationRepository.getFilterList(1L, 0, 10))
                .thenReturn(Mono.just(pageList));

        when(userRepository.findAllByEmails(anyList()))
                .thenReturn(Mono.just(Collections.emptyList()));

        StepVerifier.create(listLoanApplicationUseCase.execute(1L, 0, 10))
                .assertNext(result -> {
                    assertThat(result.getLoanApplications()).hasSize(1);
                    assertThat(result.getLoanApplications().getFirst().getUser()).isNull();
                })
                .verifyComplete();
    }

    @Test
    void execute_shouldErrorWhenUserRepositoryFails() {
        LoanApplicationPageList pageList = new LoanApplicationPageList();
        pageList.setLoanApplications(List.of(loanAppPage1));

        when(loanApplicationRepository.getFilterList(1L, 0, 10))
                .thenReturn(Mono.just(pageList));

        when(userRepository.findAllByEmails(anyList()))
                .thenReturn(Mono.error(new RuntimeException("User repo error")));

        StepVerifier.create(listLoanApplicationUseCase.execute(1L, 0, 10))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("User repo error"))
                .verify();
    }


}
