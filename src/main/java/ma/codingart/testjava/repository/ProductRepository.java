package ma.codingart.testjava.repository;

import java.util.Optional;
import java.util.UUID;
import ma.codingart.testjava.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByUuid(UUID uuid);
    Optional<Product> findByTitle(String title);
}
