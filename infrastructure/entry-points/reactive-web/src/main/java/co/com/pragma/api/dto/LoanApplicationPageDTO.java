package co.com.pragma.api.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplicationPageDTO {
    private Double amount;
    private Integer term;
    private String email;

    private LoanApplicationStatusDTO status;
    private LoanTypeDTO type;

    private UserDTO user;
}
