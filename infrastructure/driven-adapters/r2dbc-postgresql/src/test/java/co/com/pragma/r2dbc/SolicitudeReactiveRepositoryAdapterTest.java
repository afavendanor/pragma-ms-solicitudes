package co.com.pragma.r2dbc;

import co.com.pragma.model.error.CustomException;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.r2dbc.entity.SolicitudeEntity;
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
class SolicitudeReactiveRepositoryAdapterTest {

    @InjectMocks
    SolicitudeRepositoryAdapter repositoryAdapter;

    @Mock
    SolicitudeReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    private SolicitudeEntity entity;
    private Solicitude solicitude;

    @BeforeEach
    void setup() {
        entity = new SolicitudeEntity();
        entity.setId(1L);

        solicitude = new Solicitude();
        solicitude.setId(1L);
    }

    @Test
    void mustFindValueById() {

        when(repository.findById(anyLong()))
                .thenReturn(Mono.just(entity));
        when(mapper.map(entity, Solicitude.class)).thenReturn(solicitude);

        Mono<Solicitude> result = repositoryAdapter.findById(1L);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(solicitude))
                .verifyComplete();
    }

    @Test
    void mustFindAllValues() {
        when(repository.findAll())
                .thenReturn(Flux.just(entity));
        when(mapper.map(entity, Solicitude.class)).thenReturn(solicitude);

        Flux<Solicitude> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals(solicitude))
                .verifyComplete();
    }

    @Test
    void mustFindByExample() {
        when(repository.findAll(any(Example.class)))
                .thenReturn(Flux.just(entity));
        when(mapper.map(solicitude, SolicitudeEntity.class)).thenReturn(entity);
        when(mapper.map(entity, Solicitude.class))
                .thenReturn(solicitude);

        Flux<Solicitude> result = repositoryAdapter.findByExample(solicitude);

        StepVerifier.create(result)
                .expectNext(solicitude)
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        when(repository.save(any(SolicitudeEntity.class)))
                .thenReturn(Mono.just(entity));
        when(mapper.map(solicitude, SolicitudeEntity.class)).thenReturn(entity);
        when(mapper.map(entity, Solicitude.class)).thenReturn(solicitude);

        Mono<Solicitude> result = repositoryAdapter.save(solicitude);

        StepVerifier.create(result)
                .expectNext(solicitude)
                .verifyComplete();
    }

    @Test
    void shouldSaveSolicitude_error() {
        when(mapper.map(solicitude, SolicitudeEntity.class)).thenReturn(entity);
        when(repository.save(any())).
                thenReturn(Mono.error(new RuntimeException("Error en base de datos")));

        Mono<Solicitude> result = repositoryAdapter.save(solicitude);

        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(CustomException.class, error);
                    assertEquals(ResponseCode.MSSO000, ((CustomException) error).getResponseCode());
                })
                .verify();
    }

    @Test
    void shouldSaveSolicitude_errorDuplicado() {
        when(mapper.map(solicitude, SolicitudeEntity.class)).thenReturn(entity);
        when(repository.save(any())).
                thenReturn(Mono.error(new DataIntegrityViolationException("Error, dato duplicado")));

        Mono<Solicitude> result = repositoryAdapter.save(solicitude);

        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(CustomException.class, error);
                    assertEquals(ResponseCode.MSSO003, ((CustomException) error).getResponseCode());
                })
                .verify();
    }
}
