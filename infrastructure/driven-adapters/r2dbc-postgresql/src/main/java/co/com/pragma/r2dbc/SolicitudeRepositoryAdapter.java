package co.com.pragma.r2dbc;

import co.com.pragma.model.error.CustomException;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.model.solicitude.Solicitude;
import co.com.pragma.model.solicitude.gateways.SolicitudeRepository;
import co.com.pragma.r2dbc.entity.SolicitudeEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

@Repository
public class SolicitudeRepositoryAdapter extends ReactiveAdapterOperations<
        Solicitude,
        SolicitudeEntity,
        Long,
        SolicitudeReactiveRepository
        > implements SolicitudeRepository {

    private static final Logger log = Loggers.getLogger(SolicitudeRepositoryAdapter.class.getName());

    public SolicitudeRepositoryAdapter(SolicitudeReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, Solicitude.class));
    }

    @Override
    public Mono<Solicitude> save(Solicitude Solicitude) {
        return super.save(Solicitude)
                .doOnError(e -> log.error("Error guardando solicitud: {}", e.getMessage(), e))
                .onErrorMap(ex -> (ex instanceof DataIntegrityViolationException)
                        ? new CustomException(ResponseCode.MSSO003)
                        : new CustomException(ResponseCode.MSSO000));
    }

}
