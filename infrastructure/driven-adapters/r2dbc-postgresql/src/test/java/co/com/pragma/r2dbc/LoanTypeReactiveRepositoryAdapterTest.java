package co.com.pragma.r2dbc;

import co.com.pragma.model.error.InternalErrorException;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.model.loan_application.LoanType;
import co.com.pragma.r2dbc.entity.LoanTypeEntity;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanTypeReactiveRepositoryAdapterTest {

    @InjectMocks
    LoanTypeReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    LoanTypeReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    private LoanTypeEntity entity;
    private LoanType loanType;

    @BeforeEach
    void setup() {
        entity = new LoanTypeEntity();
        entity.setId(1L);

        loanType = new LoanType();
        loanType.setName("quick_loan");
    }

    @Test
    void mustFindValueById() {

        when(repository.findById(anyLong()))
                .thenReturn(Mono.just(entity));
        when(mapper.map(entity, LoanType.class)).thenReturn(loanType);

        Mono<LoanType> result = repositoryAdapter.findById(1L);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(loanType))
                .verifyComplete();
    }

    @Test
    void mustFindValueById_IdNull() {

        Mono<LoanType> result = repositoryAdapter.findById(null);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void mustFindValueById_Error() {

        when(repository.findById(anyLong()))
                .thenReturn(Mono.error(new InternalErrorException(ResponseCode.MSSO000)));

        Mono<LoanType> result = repositoryAdapter.findById(1L);

        StepVerifier.create(result)
                .expectErrorMatches(error ->
                        error instanceof InternalErrorException &&
                                error.getMessage().equalsIgnoreCase(ResponseCode.MSSO000.getMessage()))
                .verify();
    }

    @Test
    void mustFindAllValues() {
        when(repository.findAll())
                .thenReturn(Flux.just(entity));
        when(mapper.map(entity, LoanType.class)).thenReturn(loanType);

        Flux<LoanType> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(loanType))
                .verifyComplete();
    }

    @Test
    void mustFindByExample() {
        when(repository.findAll(any(Example.class)))
                .thenReturn(Flux.just(entity));
        when(mapper.map(loanType, LoanTypeEntity.class)).thenReturn(entity);
        when(mapper.map(entity, LoanType.class))
                .thenReturn(loanType);

        Flux<LoanType> result = repositoryAdapter.findByExample(loanType);

        StepVerifier.create(result)
                .expectNext(loanType)
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        when(repository.save(any(LoanTypeEntity.class)))
                .thenReturn(Mono.just(entity));
        when(mapper.map(loanType, LoanTypeEntity.class)).thenReturn(entity);
        when(mapper.map(entity, LoanType.class)).thenReturn(loanType);

        Mono<LoanType> result = repositoryAdapter.save(loanType);

        StepVerifier.create(result)
                .expectNext(loanType)
                .verifyComplete();
    }

}
