package co.com.pragma.r2dbc;

import co.com.pragma.model.error.InternalErrorException;
import co.com.pragma.model.error.NotFoundException;
import co.com.pragma.model.loan_application.*;
import co.com.pragma.r2dbc.entity.LoanApplicationEntity;
import co.com.pragma.r2dbc.entity.LoanApplicationStatusEntity;
import co.com.pragma.r2dbc.entity.LoanTypeEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.ReactiveSelectOperation;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.*;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationServiceTest {

    @InjectMocks
    private LoanApplicationRepositoryAdapter repositoryAdapter;

    @Mock
    private R2dbcEntityTemplate template;

    @Mock
    private ReactiveSelectOperation.ReactiveSelect<LoanApplicationEntity> reactiveSelectMock;

    @Mock
    private LoanApplicationStatusReactiveRepository loanApplicationStatusReactiveRepository;

    @Mock
    private LoanApplicationReactiveRepository loanApplicationReactiveRepository;

    @Mock
    private LoanTypeReactiveRepository loanTypeReactiveRepository;

    @Mock
    private ObjectMapper mapper;

    @Test
    void getFilterList_Success() {
        LoanApplicationEntity entity = new LoanApplicationEntity();
        entity.setLoanApplicationStatusId(1L);
        entity.setLoanTypeId(1L);
        entity.setAmount(100d);

        LoanApplicationStatusEntity statusEntity = new LoanApplicationStatusEntity();
        statusEntity.setId(1L);
        statusEntity.setName("Approved");

        LoanTypeEntity loanTypeEntity = new LoanTypeEntity();
        loanTypeEntity.setName("fast");

        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setLoanApplicationStatusId(1L);
        loanApplication.setLoanTypeId(1L);

        LoanApplicationStatus loanApplicationStatus = new LoanApplicationStatus();
        loanApplicationStatus.setName("Approved");

        LoanType loanType = new LoanType();
        loanType.setName("Fast");

        LoanApplicationPage loanApplicationPage = new LoanApplicationPage();
        loanApplicationPage.setStatus(loanApplicationStatus);

        when(reactiveSelectMock.all()).thenReturn(Flux.fromIterable(List.of(entity)));
        when(template.select(LoanApplicationEntity.class)).thenReturn(reactiveSelectMock);
        when(reactiveSelectMock.matching(any(Query.class))).thenReturn(reactiveSelectMock);

        when(mapper.map(any(LoanApplicationEntity.class), eq(LoanApplication.class)))
                .thenReturn(loanApplication);
        when(mapper.map(any(LoanApplication.class), eq(LoanApplicationPage.class)))
                .thenReturn(loanApplicationPage);
        when(mapper.map(any(LoanApplicationStatusEntity.class), eq(LoanApplicationStatus.class)))
                .thenReturn(loanApplicationStatus);
        when(mapper.map(any(LoanTypeEntity.class), eq(LoanType.class)))
                .thenReturn(loanType);

        when(loanApplicationStatusReactiveRepository.findByName(anyString()))
                .thenReturn(Mono.just(statusEntity));

        when(loanApplicationStatusReactiveRepository.findById(anyLong()))
                .thenReturn(Mono.just(statusEntity));
        when(loanTypeReactiveRepository.findById(anyLong()))
                .thenReturn(Mono.just(loanTypeEntity));

        when(loanApplicationReactiveRepository.findByloanApplicationStatusId(anyLong()))
                .thenReturn(Flux.just(entity));


        Mono<LoanApplicationPageList> result = repositoryAdapter.getFilterList(co.com.pragma.model.loan_application.util.LoanApplicationStatus.APPROVED, 1, 10);

        StepVerifier.create(result)
                .expectNextMatches(list -> list.getLoanApplications().size() == 1
                        && list.getTotalMonthlyDebtApprovedRequests() == 100.0)
                .verifyComplete();
    }

    @Test
    void getFilterList_Empty() {
        LoanApplicationEntity entity = new LoanApplicationEntity();
        entity.setLoanApplicationStatusId(1L);
        entity.setLoanTypeId(1L);
        entity.setAmount(100d);

        LoanApplicationStatusEntity statusEntity = new LoanApplicationStatusEntity();
        statusEntity.setId(1L);
        statusEntity.setName("Approved");

        LoanTypeEntity loanTypeEntity = new LoanTypeEntity();
        loanTypeEntity.setName("fast");

        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setLoanApplicationStatusId(1L);
        loanApplication.setLoanTypeId(1L);

        LoanApplicationStatus loanApplicationStatus = new LoanApplicationStatus();
        loanApplicationStatus.setName("Approved");

        LoanType loanType = new LoanType();
        loanType.setName("Fast");

        LoanApplicationPage loanApplicationPage = new LoanApplicationPage();
        loanApplicationPage.setStatus(loanApplicationStatus);

        when(reactiveSelectMock.all()).thenReturn(Flux.empty());
        when(template.select(LoanApplicationEntity.class)).thenReturn(reactiveSelectMock);
        when(reactiveSelectMock.matching(any(Query.class))).thenReturn(reactiveSelectMock);
        when(loanApplicationStatusReactiveRepository.findByName(anyString()))
                .thenReturn(Mono.just(statusEntity));
        when(loanApplicationReactiveRepository.findByloanApplicationStatusId(anyLong()))
                .thenReturn(Flux.just(entity));

        Mono<LoanApplicationPageList> result = repositoryAdapter.getFilterList(co.com.pragma.model.loan_application.util.LoanApplicationStatus.PENDING, 1, 10);

        StepVerifier.create(result)
                .expectErrorMatches(NotFoundException.class::isInstance)
                .verify();
    }

    @Test
    void getFilterList_ErrorInRepository() {
        LoanApplicationEntity entity = new LoanApplicationEntity();
        entity.setLoanApplicationStatusId(1L);
        entity.setLoanTypeId(1L);
        entity.setAmount(100d);

        LoanApplicationStatusEntity statusEntity = new LoanApplicationStatusEntity();
        statusEntity.setId(1L);
        statusEntity.setName("Approved");

        when(loanApplicationStatusReactiveRepository.findByName(anyString()))
                .thenReturn(Mono.just(statusEntity));
        when(loanApplicationReactiveRepository.findByloanApplicationStatusId(anyLong()))
                .thenReturn(Flux.just(entity));

        when(template.select(LoanApplicationEntity.class)).thenReturn(reactiveSelectMock);
        when(reactiveSelectMock.matching(any(Query.class))).thenReturn(reactiveSelectMock);
        when(reactiveSelectMock.all()).thenReturn(Flux.error(new RuntimeException("DB error")));

        Mono<LoanApplicationPageList> result = repositoryAdapter.getFilterList(co.com.pragma.model.loan_application.util.LoanApplicationStatus.PENDING, 1, 10);

        StepVerifier.create(result)
                .expectErrorMatches(InternalErrorException.class::isInstance)
                .verify();
    }
}
