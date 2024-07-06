package ma.codingart.testjava.dto.request;

import jakarta.validation.constraints.Size;
import java.io.Serializable;
import ma.codingart.testjava.entity.Category;

/**
 * A DTO for the {@link Category} entity
 */
public record CategoryDtoRequest (

        @Size(min = 3, message = "Name should have at least {min} characters")
        String name,

        @Size(min = 10, message = "Description should have at least {min} characters")
        String description
) implements Serializable {
}
