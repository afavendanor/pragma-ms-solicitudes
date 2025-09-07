package co.com.pragma.r2dbc;

import co.com.pragma.model.error.DuplicateEntryException;
import co.com.pragma.model.error.InternalErrorException;
import co.com.pragma.model.error.NotFoundException;
import co.com.pragma.model.error.ResponseCode;
import co.com.pragma.model.loan_application.*;
import co.com.pragma.model.loan_application.gateways.LoanApplicationRepository;
import co.com.pragma.r2dbc.entity.LoanApplicationEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.List;

@Repository
public class LoanApplicationRepositoryAdapter extends ReactiveAdapterOperations<
        LoanApplication,
        LoanApplicationEntity,
        Long,
        LoanApplicationReactiveRepository
        > implements LoanApplicationRepository {

    private static final Logger log = Loggers.getLogger(LoanApplicationRepositoryAdapter.class.getName());

    private final R2dbcEntityTemplate template;
    private final LoanApplicationStatusReactiveRepository loanApplicationStatusReactiveRepository;
    private final LoanTypeReactiveRepository loanTypeReactiveRepository;

    public LoanApplicationRepositoryAdapter(LoanApplicationReactiveRepository repository, ObjectMapper mapper,
                                            R2dbcEntityTemplate template, LoanApplicationStatusReactiveRepository loanApplicationStatusReactiveRepository,
                                            LoanTypeReactiveRepository loanTypeReactiveRepository) {
        super(repository, mapper, entity -> mapper.map(entity, LoanApplication.class));
        this.template = template;
        this.loanApplicationStatusReactiveRepository = loanApplicationStatusReactiveRepository;
        this.loanTypeReactiveRepository = loanTypeReactiveRepository;
    }

    @Override
    public Mono<LoanApplication> save(LoanApplication loanApplication) {
        return super.save(loanApplication)
                .doOnError(e -> log.error("Error guardando solicitud: {}", e.getMessage(), e))
                .onErrorMap(ex -> (ex instanceof DataIntegrityViolationException)
                        ? new DuplicateEntryException(ResponseCode.MSSO003)
                        : new InternalErrorException(ResponseCode.MSSO000));
    }

    @Override
    public Mono<LoanApplicationPageList> getFilterList(co.com.pragma.model.loan_application.util.LoanApplicationStatus status, int page, int size) {

        return loanApplicationStatusReactiveRepository.findByName(status.name())
                .switchIfEmpty(Mono.error(new NotFoundException(ResponseCode.MSSO010)))
                .flatMap(estado -> {

                    Criteria criteria = Criteria.where("id_loan_application_status").is(estado.getId());
                    long offset = (long) (page - 1) * size;

                    Query pageQuery = Query.query(criteria).limit(size).offset(offset);
                    Query totalQuery = Query.query(criteria);

                    Mono<List<LoanApplicationPage>> applicationsMono = template.select(LoanApplicationEntity.class)
                            .matching(pageQuery)
                            .all()
                            .map(entity -> mapper.map(entity, LoanApplication.class))
                            .flatMap(loanApplication -> {
                                LoanApplicationPage loanApplicationPage = mapper.map(loanApplication, LoanApplicationPage.class);

                                Mono<LoanApplicationStatus> statusMono = loanApplicationStatusReactiveRepository.findById(loanApplication.getLoanApplicationStatusId())
                                        .map(loanApplicationStatusEntity -> mapper.map(loanApplicationStatusEntity, LoanApplicationStatus.class));

                                Mono<LoanType> typeMono = loanTypeReactiveRepository.findById(loanApplication.getLoanTypeId())
                                        .map(loanTypeEntity -> mapper.map(loanTypeEntity, LoanType.class));

                                return Mono.zip(statusMono, typeMono)
                                        .map(tuple -> {
                                            loanApplicationPage.setStatus(tuple.getT1());
                                            loanApplicationPage.setType(tuple.getT2());
                                            return loanApplicationPage;
                                        });
                            })
                            .collectList()
                            .doOnError(ex -> log.error("Error al lista de solicitudes", ex.getMessage(), ex))
                            .onErrorMap(error -> new InternalErrorException(ResponseCode.MSSO000));

                    Mono<Long> totalMono = template.select(LoanApplicationEntity.class)
                            .matching(totalQuery)
                            .all()
                            .count()
                            .doOnError(ex -> log.error("Error al contar usuarios: {}", ex.getMessage(), ex))
                            .onErrorMap(error -> new InternalErrorException(ResponseCode.MSSO000));

                    Mono<Double> monoMonto = totalMonthlyDebtApprovedRequests();

                    return Mono.zip(applicationsMono, totalMono, monoMonto)
                            .flatMap(tuple -> {

                                List<LoanApplicationPage> aplications = tuple.getT1();
                                long total = tuple.getT2();
                                Double totalMonthlyDebtApprovedRequests = tuple.getT3();

                                if (aplications.isEmpty()) {
                                    return Mono.error(new NotFoundException(ResponseCode.MSSO004));
                                }

                                int totalPages = (int) Math.ceil((double) total / size);
                                boolean last = page >= totalPages;

                                return Mono.just(new LoanApplicationPageList(
                                        totalMonthlyDebtApprovedRequests, aplications, page, aplications.size(), total, totalPages, last
                                ));

                            })
                            .doOnError(e -> log.error("Error final en listar solicitudes: {}", e.getMessage(), e))
                            .onErrorMap(ex -> (ex instanceof NotFoundException) ? ex : new InternalErrorException(ResponseCode.MSSO000));

                })
                .doOnError(e -> log.error("Error final en listar solicitudes: {}", e.getMessage(), e))
                .onErrorMap(ex -> (ex instanceof NotFoundException) ? ex : new InternalErrorException(ResponseCode.MSSO000));

    }

    public Mono<Double> totalMonthlyDebtApprovedRequests() {
        return loanApplicationStatusReactiveRepository.findByName(co.com.pragma.model.loan_application.util.LoanApplicationStatus.APPROVED.name())
                .flatMapMany(status -> repository.findByloanApplicationStatusId(status.getId()))
                .map(LoanApplicationEntity::getAmount)
                .reduce(0.0, Double::sum);
    }

}
