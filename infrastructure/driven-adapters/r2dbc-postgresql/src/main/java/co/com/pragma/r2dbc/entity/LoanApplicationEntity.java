package co.com.pragma.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("loan_applications")
@AllArgsConstructor
@NoArgsConstructor
public class LoanApplicationEntity {
    @Id
    private Long id;
    private Double amount;
    private Integer term;
    private String email;
    @Column("id_loan_application_status")
    private Long loanApplicationStatusId;
    @Column("id_loan_type")
    private Long loanTypeId;
}
