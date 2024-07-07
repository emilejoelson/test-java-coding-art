package ma.codingart.testjava.repository;

import java.util.Optional;
import java.util.UUID;
import ma.codingart.testjava.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category,Long> , JpaSpecificationExecutor<Category> {
    Optional<Category> findByUuid(UUID uuid);
    Optional<Category> findByName(String name);

    @Query("SELECT c FROM Category c WHERE c.uuid = :categoryUuid AND EXISTS (SELECT p FROM Product p WHERE p.category = c ) ")
    boolean isAssociatedWithProduct(UUID categoryUuid);
}
