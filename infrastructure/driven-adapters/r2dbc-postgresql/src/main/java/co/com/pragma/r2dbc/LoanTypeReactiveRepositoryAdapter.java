package co.com.pragma.r2dbc;

import co.com.pragma.model.error.CustomException;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.model.loan_application.LoanType;
import co.com.pragma.model.loan_application.gateways.LoanTypeRepository;
import co.com.pragma.r2dbc.entity.LoanTypeEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

@Repository
public class LoanTypeReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        LoanType,
        LoanTypeEntity,
        Long,
        LoanTypeReactiveRepository
        > implements LoanTypeRepository {

    private static final Logger log = Loggers.getLogger(LoanTypeReactiveRepositoryAdapter.class.getName());

    public LoanTypeReactiveRepositoryAdapter(LoanTypeReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, LoanType.class));
    }

    @Override
    public Mono<LoanType> findById(Long id) {
        if (id == null) {
            return Mono.empty();
        }
        return super.findById(id)
                .doOnError(ex -> log.error("Error obteniendo tipo de crèdito: {}", ex.getMessage(), ex))
                .onErrorMap(ex -> new CustomException(ResponseCode.MSSO000));
    }

}
