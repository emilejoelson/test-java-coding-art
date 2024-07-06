package ma.codingart.testjava.dto.response;

import java.io.Serializable;
import java.util.UUID;
import ma.codingart.testjava.entity.Product;

/**
 * A DTO for the {@link Product} entity
 */
public record ProductListDtoResponse(
        Long id ,
        UUID uuid,
        String title,
        String description,
        Double price,
        Boolean isEnabled
) implements Serializable{
}
