package co.com.pragma.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("solicitudes")
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudeEntity {
    @Id
    private Long id;
}
