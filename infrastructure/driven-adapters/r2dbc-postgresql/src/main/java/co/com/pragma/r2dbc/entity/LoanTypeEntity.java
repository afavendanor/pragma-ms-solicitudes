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
@Table("loan_types")
@AllArgsConstructor
@NoArgsConstructor
public class LoanTypeEntity {
    @Id
    private Long id;
    private String name;
    @Column("minimum_amount")
    private Double minimumAmount;
    @Column("maximum_amount")
    private Double maximumAmount;
    @Column("interest_rate")
    private Double interestRate;
    @Column("automatic_validation")
    private Boolean automaticValidation;
}
