package co.com.pragma.consumer;

import co.com.pragma.model.error.FieldError;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObjectResponse<T> {
    private Integer responseCode;
    private String responseMessage;
    private T data;
    private List<FieldError> fieldErrors;

}