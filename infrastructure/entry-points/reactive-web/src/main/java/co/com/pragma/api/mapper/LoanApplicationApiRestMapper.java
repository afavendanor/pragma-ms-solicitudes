package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.CreateLoanApplicationDTO;
import co.com.pragma.model.loan_application.LoanApplication;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanApplicationApiRestMapper {

    @Mapping(target = "loanApplicationStatusId", ignore = true)
    LoanApplication createLoanApplicationDTOToLoanApplication(CreateLoanApplicationDTO createLoanApplicationDTO);

}
