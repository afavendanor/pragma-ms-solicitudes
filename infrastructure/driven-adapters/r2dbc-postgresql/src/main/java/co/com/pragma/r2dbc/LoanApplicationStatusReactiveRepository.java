package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.LoanApplicationStatusEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface LoanApplicationStatusReactiveRepository extends ReactiveCrudRepository<LoanApplicationStatusEntity, Long>, ReactiveQueryByExampleExecutor<LoanApplicationStatusEntity> {

    Mono<LoanApplicationStatusEntity> findByName(String name);

}
