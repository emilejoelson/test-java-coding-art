package ma.codingart.testjava.dto.response;

import java.io.Serializable;
import java.util.UUID;
import ma.codingart.testjava.entity.Category;

/**
 * A DTO for the {@link Category} entity
 */
public record CategoryDtoResponse (
        Long id,
        UUID uuid,
        String name,
        String description,
        BaseDto baseDto
) implements Serializable {
}
