package co.com.pragma.r2dbc;

import co.com.pragma.model.error.CustomException;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.model.loan_application.LoanApplicationStatus;
import co.com.pragma.r2dbc.entity.LoanApplicationStatusEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationStatusReactiveRepositoryAdapterTest {

    @InjectMocks
    LoanApplicationStatusRepositoryAdapter repositoryAdapter;

    @Mock
    LoanApplicationStatusReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    private LoanApplicationStatusEntity entity;
    private LoanApplicationStatus loanApplicationStatus;

    @BeforeEach
    void setup() {
        entity = new LoanApplicationStatusEntity();
        entity.setId(1L);

        loanApplicationStatus = new LoanApplicationStatus();
        loanApplicationStatus.setName("quick_loan");
    }

    @Test
    void mustFindValueById() {

        when(repository.findById(anyLong()))
                .thenReturn(Mono.just(entity));
        when(mapper.map(entity, LoanApplicationStatus.class)).thenReturn(loanApplicationStatus);

        Mono<LoanApplicationStatus> result = repositoryAdapter.findById(1L);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(loanApplicationStatus))
                .verifyComplete();
    }

    @Test
    void mustFindAllValues() {
        when(repository.findAll())
                .thenReturn(Flux.just(entity));
        when(mapper.map(entity, LoanApplicationStatus.class)).thenReturn(loanApplicationStatus);

        Flux<LoanApplicationStatus> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(loanApplicationStatus))
                .verifyComplete();
    }

    @Test
    void mustFindByExample() {
        when(repository.findAll(any(Example.class)))
                .thenReturn(Flux.just(entity));
        when(mapper.map(loanApplicationStatus, LoanApplicationStatusEntity.class)).thenReturn(entity);
        when(mapper.map(entity, LoanApplicationStatus.class))
                .thenReturn(loanApplicationStatus);

        Flux<LoanApplicationStatus> result = repositoryAdapter.findByExample(loanApplicationStatus);

        StepVerifier.create(result)
                .expectNext(loanApplicationStatus)
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        when(repository.save(any(LoanApplicationStatusEntity.class)))
                .thenReturn(Mono.just(entity));
        when(mapper.map(loanApplicationStatus, LoanApplicationStatusEntity.class)).thenReturn(entity);
        when(mapper.map(entity, LoanApplicationStatus.class)).thenReturn(loanApplicationStatus);

        Mono<LoanApplicationStatus> result = repositoryAdapter.save(loanApplicationStatus);

        StepVerifier.create(result)
                .expectNext(loanApplicationStatus)
                .verifyComplete();
    }

    @Test
    void findByName_shouldReturnFirstElement_whenMultipleResultsExist() {
        when(repository.findAll(any(Example.class)))
                .thenReturn(Flux.just(entity));
        when(mapper.map(any(LoanApplicationStatus.class), eq(LoanApplicationStatusEntity.class)))
                .thenReturn(entity);
        when(mapper.map(any(LoanApplicationStatusEntity.class), eq(LoanApplicationStatus.class)))
                .thenReturn(loanApplicationStatus);

        Mono<LoanApplicationStatus> result = repositoryAdapter.findByName("pending");

        StepVerifier.create(result)
                .expectNext(loanApplicationStatus)
                .verifyComplete();

        verify(repository, times(1)).findAll(any());
    }

    @Test
    void findByName_shouldReturnEmpty_whenNoResultsFound() {

        when(repository.findAll(any(Example.class)))
                .thenReturn(Flux.empty());
        when(mapper.map(any(LoanApplicationStatus.class), eq(LoanApplicationStatusEntity.class)))
                .thenReturn(entity);

        Mono<LoanApplicationStatus> result = repositoryAdapter.findByName("pending");

        StepVerifier.create(result)
                .verifyComplete();

        verify(repository, times(1)).findAll(any());
    }

    @Test
    void findByName_shouldMapToCustomException_whenRepositoryThrowsException() {

        when(repository.findAll(any(Example.class)))
                .thenReturn(Flux.error(new CustomException(ResponseCode.MSSO000)));
        when(mapper.map(any(LoanApplicationStatus.class), eq(LoanApplicationStatusEntity.class)))
                .thenReturn(entity);


        StepVerifier.create(repositoryAdapter.findByName("pending"))
                .expectErrorMatches(error ->
                        error instanceof CustomException &&
                                ((CustomException) error).getResponseCode() == ResponseCode.MSSO000)
                .verify();
    }

    @Test
    void findByName_shouldReturnEmpty_whenNameEmpty() {

        Mono<LoanApplicationStatus> result = repositoryAdapter.findByName("");

        StepVerifier.create(result)
                .verifyComplete();

        verify(repository, never()).findAll(any());
    }

}
