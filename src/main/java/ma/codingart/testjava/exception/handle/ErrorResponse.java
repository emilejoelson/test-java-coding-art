package ma.codingart.testjava.exception.handle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor @Builder
public class ErrorResponse {
    private int code ;
    private HttpStatus status;
    private String message;
}
