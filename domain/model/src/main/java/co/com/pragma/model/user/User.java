package co.com.pragma.model.user;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String identification;
    private String name;
    private String lastName;
    private LocalDate birthDay;
    private String address;
    private String phone;
    private String email;
    private Double baseSalary;
}
