package co.com.pragma.r2dbc;

import co.com.pragma.model.error.CustomException;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.model.loan_application.LoanApplication;
import co.com.pragma.r2dbc.entity.LoanApplicationEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanApplicationReactiveRepositoryAdapterTest {

    @InjectMocks
    LoanApplicationRepositoryAdapter repositoryAdapter;

    @Mock
    LoanApplicationReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    private LoanApplicationEntity entity;
    private LoanApplication loanApplication;

    @BeforeEach
    void setup() {
        entity = new LoanApplicationEntity();
        entity.setId(1L);

        loanApplication = new LoanApplication();
        loanApplication.setAmount(1000d);
    }

    @Test
    void mustFindValueById() {

        when(repository.findById(anyLong()))
                .thenReturn(Mono.just(entity));
        when(mapper.map(entity, LoanApplication.class)).thenReturn(loanApplication);

        Mono<LoanApplication> result = repositoryAdapter.findById(1L);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(loanApplication))
                .verifyComplete();
    }

    @Test
    void mustFindAllValues() {
        when(repository.findAll())
                .thenReturn(Flux.just(entity));
        when(mapper.map(entity, LoanApplication.class)).thenReturn(loanApplication);

        Flux<LoanApplication> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(loanApplication))
                .verifyComplete();
    }

    @Test
    void mustFindByExample() {
        when(repository.findAll(any(Example.class)))
                .thenReturn(Flux.just(entity));
        when(mapper.map(loanApplication, LoanApplicationEntity.class)).thenReturn(entity);
        when(mapper.map(entity, LoanApplication.class))
                .thenReturn(loanApplication);

        Flux<LoanApplication> result = repositoryAdapter.findByExample(loanApplication);

        StepVerifier.create(result)
                .expectNext(loanApplication)
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        when(repository.save(any(LoanApplicationEntity.class)))
                .thenReturn(Mono.just(entity));
        when(mapper.map(loanApplication, LoanApplicationEntity.class)).thenReturn(entity);
        when(mapper.map(entity, LoanApplication.class)).thenReturn(loanApplication);

        Mono<LoanApplication> result = repositoryAdapter.save(loanApplication);

        StepVerifier.create(result)
                .expectNext(loanApplication)
                .verifyComplete();
    }

    @Test
    void shouldSaveLoanApplication_error() {
        when(mapper.map(loanApplication, LoanApplicationEntity.class)).thenReturn(entity);
        when(repository.save(any())).
                thenReturn(Mono.error(new RuntimeException("Error en base de datos")));

        Mono<LoanApplication> result = repositoryAdapter.save(loanApplication);

        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(CustomException.class, error);
                    assertEquals(ResponseCode.MSSO000, ((CustomException) error).getResponseCode());
                })
                .verify();
    }

    @Test
    void shouldSaveLoanApplication_errorDuplicado() {
        when(mapper.map(loanApplication, LoanApplicationEntity.class)).thenReturn(entity);
        when(repository.save(any())).
                thenReturn(Mono.error(new DataIntegrityViolationException("Error, dato duplicado")));

        Mono<LoanApplication> result = repositoryAdapter.save(loanApplication);

        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(CustomException.class, error);
                    assertEquals(ResponseCode.MSSO003, ((CustomException) error).getResponseCode());
                })
                .verify();
    }
}
