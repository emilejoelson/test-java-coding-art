package ma.codingart.testjava.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="products")
public final class Product  extends  BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private UUID uuid = UUID.randomUUID();

    @Column(unique = true, nullable = false)
    private String title;

    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private Double price;

    private Boolean isEnabled = true;
    @PrePersist
    private void ensureUuid() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
        if (this.isEnabled == null) {
            this.isEnabled = true;
        }
    }

    @Override
    public String toString() {
        return STR."""
               Product{
                   id="\{id}",
                   uuid="\{uuid}",
                   title="\{title}",
                   description="\{description}",
                   price="\{price}",
                   isEnabled="\{isEnabled}"
               }
               """;
    }

}
