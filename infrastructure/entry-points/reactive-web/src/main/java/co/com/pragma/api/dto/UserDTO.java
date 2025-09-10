package co.com.pragma.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo de salida para datos del usuario.")
public class UserDTO {
    @Schema(description = "Identificaciòn del usuario.")
    private String identification;
    @Schema(description = "Nombre del usuario.")
    private String name;
    @Schema(description = "Apellido del usuario.")
    private String lastName;
    @Schema(description = "Correo electrònico del usuario.")
    private String email;
    @Schema(description = "Salario base del usuario.")
    private Double baseSalary;
}
