package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.CreateLoanApplicationDTO;
import co.com.pragma.api.dto.LoanApplicationDTO;
import co.com.pragma.api.dto.LoanApplicationPageListDTO;
import co.com.pragma.api.dto.UpdateLoanApplicationDTO;
import co.com.pragma.model.loan_application.LoanApplication;
import co.com.pragma.model.loan_application.LoanApplicationPageList;
import co.com.pragma.model.loan_application.LoanApplicationStatus;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanApplicationApiRestMapper {

    @Mapping(target = "loanApplicationStatusId", ignore = true)
    LoanApplication createLoanApplicationDTOToLoanApplication(CreateLoanApplicationDTO createLoanApplicationDTO);

    LoanApplicationPageListDTO loanApplicationPageListToLoanApplicationPageListDTO(LoanApplicationPageList loanApplicationPageList);

    @Mapping(target = "loanApplicationStatusId", ignore = true)
    LoanApplication updateLoanApplicationDTOToLoanApplication(UpdateLoanApplicationDTO updateLoanApplicationDTO);

    @AfterMapping
    default void mapStatusLoanApplication(UpdateLoanApplicationDTO updateLoanApplicationDTO, @MappingTarget LoanApplication loanApplication) {
        if (updateLoanApplicationDTO.getStatus() != null) {
            LoanApplicationStatus loanApplicationStatus = new LoanApplicationStatus();
            loanApplicationStatus.setName(updateLoanApplicationDTO.getStatus().name());
            loanApplication.setStatus(loanApplicationStatus);
        }
    }

    LoanApplicationDTO loanApplicationToLoanApplicationDTO(LoanApplication loanApplication);

}
