package ma.codingart.testjava.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor @Builder
@Data
public class PaginatedResponseDto <T> {
    private List<T> records;
    private long totalRecords;
    private int pages;
    private int currentPage;
    private boolean first;
    private boolean last;
}

