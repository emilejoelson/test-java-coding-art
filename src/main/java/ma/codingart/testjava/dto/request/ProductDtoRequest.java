package ma.codingart.testjava.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import  ma.codingart.testjava.entity.Product;

/**
 * A DTO for the {@link Product} entity
 */
public record ProductDtoRequest(
        @NotEmpty(message = "Title is required")
        @NotBlank(message = "Title should not be blank")
        @Size(min = 3, message = "Title should have at least {min} characters")
        String title,

        @NotEmpty(message = "Description is required")
        @NotBlank(message = "Description should not be blank")
        @Size(min = 10, message = "Description should have at least {min} characters")
        String description,

        @NotEmpty(message = "Category is required")
        @NotBlank(message = "Category should not be blank")
        String categoryName,
        @Positive(message = "Price must be a positive number")
        Double price,

        Boolean isEnabled

) implements Serializable {
}
