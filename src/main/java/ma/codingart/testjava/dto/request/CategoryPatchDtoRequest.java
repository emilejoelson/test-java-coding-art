package ma.codingart.testjava.dto.request;

import java.io.Serializable;
import ma.codingart.testjava.entity.Category;

/**
 * A DTO for the {@link Category} entity
 */
public record CategoryPatchDtoRequest(
        String name,
        String description
) implements Serializable {
}
