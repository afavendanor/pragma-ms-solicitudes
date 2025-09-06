package co.com.pragma.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoanTypeDTO {
    private String name;
    private Double minimumAmount;
    private Double maximumAmount;
    private Double interestRate;
}
