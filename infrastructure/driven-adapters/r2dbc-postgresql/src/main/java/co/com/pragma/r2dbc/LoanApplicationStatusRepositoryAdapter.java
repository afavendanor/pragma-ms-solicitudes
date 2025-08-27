package co.com.pragma.r2dbc;

import co.com.pragma.model.error.CustomException;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.model.loan_application.LoanApplicationStatus;
import co.com.pragma.model.loan_application.gateways.LoanApplicationStatusRepository;
import co.com.pragma.r2dbc.entity.LoanApplicationStatusEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

@Repository
public class LoanApplicationStatusRepositoryAdapter extends ReactiveAdapterOperations<
        LoanApplicationStatus,
        LoanApplicationStatusEntity,
        Long,
        LoanApplicationStatusReactiveRepository
        > implements LoanApplicationStatusRepository {

    private static final Logger log = Loggers.getLogger(LoanApplicationStatusRepositoryAdapter.class.getName());

    public LoanApplicationStatusRepositoryAdapter(LoanApplicationStatusReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, entity -> mapper.map(entity, LoanApplicationStatus.class));
    }

    @Override
    public Mono<LoanApplicationStatus> findByName(String name) {
        LoanApplicationStatus loanApplicationStatus = new LoanApplicationStatus();
        loanApplicationStatus.setName(name);
        return super.findByExample(loanApplicationStatus)
                .next()
                .doOnError(ex -> log.error("Error obteniendo estado de crèdito: {}", ex.getMessage(), ex))
                .onErrorMap(ex -> new CustomException(ResponseCode.MSSO000));
    }
}
