package ma.codingart.testjava.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.codingart.testjava.enums.SortDirection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SortRequest {
    private String key;
    private SortDirection direction;
}

