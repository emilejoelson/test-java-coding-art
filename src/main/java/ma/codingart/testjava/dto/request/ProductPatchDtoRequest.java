package ma.codingart.testjava.dto.request;

import java.io.Serializable;
import ma.codingart.testjava.entity.Product;

/**
 * A DTO for the {@link Product} entity
 */

public record ProductPatchDtoRequest(
        String title,
        String description,
        String categoryName,
        Double price,
        Boolean isEnabled
) implements Serializable {
}