package ma.codingart.testjava.service.implementation;


import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import ma.codingart.testjava.dto.request.ProductDtoRequest;
import ma.codingart.testjava.dto.request.ProductPatchDtoRequest;
import ma.codingart.testjava.dto.request.SearchRequest;
import ma.codingart.testjava.dto.response.PaginatedResponseDto;
import ma.codingart.testjava.dto.response.ProductDtoResponse;
import ma.codingart.testjava.dto.response.ProductListDtoResponse;
import ma.codingart.testjava.entity.Category;
import ma.codingart.testjava.entity.Product;
import ma.codingart.testjava.exception.ElementAlreadyExistException;
import ma.codingart.testjava.exception.ElementNotFoundException;
import ma.codingart.testjava.mapper.ProductMapper;
import ma.codingart.testjava.repository.ProductRepository;
import ma.codingart.testjava.service.CategoryService;
import ma.codingart.testjava.service.ProductService;
import ma.codingart.testjava.utils.Constants;
import ma.codingart.testjava.utils.SearchSpecification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private  final ProductRepository productRepository;
    private  final CategoryService categoryService;
    @Override
    public ProductDtoResponse createProduct(ProductDtoRequest productDtoRequest) throws ElementAlreadyExistException {
        productRepository.findByTitle(productDtoRequest.title()).ifPresent(existingProduct -> {
            throw new ElementAlreadyExistException(
                    new ElementAlreadyExistException(),
                    Constants.ALREADY_EXISTS,
                    new Object[]{productDtoRequest.title()}
            );
        });

        Product product = ProductMapper.INSTANCE.productDtoRequestToProduct(productDtoRequest);
        Category category = categoryService.findByName(productDtoRequest.categoryName());
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);
        return ProductMapper.INSTANCE.productToProductDtoResponse(savedProduct);

    }

    @Override
    public ProductDtoResponse findProductByUuid(UUID uuid) throws ElementNotFoundException {
        Product product = productRepository.findByUuid(uuid).orElseThrow(
                ()-> new ElementNotFoundException(new ElementAlreadyExistException(),Constants.NOT_FOUND,
                        new Object[]{uuid})
        );
        return ProductMapper.INSTANCE.productToProductDtoResponse(product);
    }

    @Override
    public PaginatedResponseDto<ProductListDtoResponse> getAllProducts(SearchRequest searchRequest) {
        Specification<Product> specification = new SearchSpecification<>(searchRequest);
        specification = addEnabledProductsSpecification(specification);
        return getPaginatedResponse(specification, searchRequest);
    }

    private Specification<Product> addEnabledProductsSpecification(Specification<Product> specification) {
        return specification.and((root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("isEnabled")));
    }

    private PaginatedResponseDto<ProductListDtoResponse> getPaginatedResponse(Specification<Product> specification, SearchRequest searchRequest) {
        Pageable pageRequest = SearchSpecification.getPageable(searchRequest.getPage(), searchRequest.getSize());
        Page<Product> productPage = productRepository.findAll(specification, pageRequest);

        List<ProductListDtoResponse> list = productPage.map(product -> new ProductListDtoResponse(
                product.getId(),
                product.getUuid(),
                product.getTitle(),
                product.getDescription(),
                product.getPrice(),
                product.getIsEnabled()
        )).toList();

        return PaginatedResponseDto.<ProductListDtoResponse>builder()
                .pages(productPage.getTotalPages())
                .totalRecords(productPage.getTotalElements())
                .first(productPage.isFirst())
                .last(productPage.isLast())
                .currentPage(productPage.getNumber())
                .records(list)
                .build();
    }

    @Override
    public ProductDtoResponse patchProduct(UUID uuid, ProductPatchDtoRequest productPatchDtoRequest) {
        Product existingProduct = productRepository.findByUuid(uuid)
                .orElseThrow(() -> new ElementNotFoundException(new ElementNotFoundException(), Constants.NOT_FOUND, new Object[]{uuid}));

        if (productPatchDtoRequest instanceof ProductPatchDtoRequest(_, String description,_, Double price, Boolean isEnabled)) {

            if (description != null) {
                existingProduct.setDescription(description);
            }
            if (price != null) {
                existingProduct.setPrice(price);
            }
                existingProduct.setIsEnabled(isEnabled);
        }

        Product updatedProduct = productRepository.save(existingProduct);

        return ProductMapper.INSTANCE.productToProductDtoResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(final UUID uuid) throws ElementNotFoundException {
        Product product = productRepository.findByUuid(uuid).orElseThrow(
                () -> new ElementNotFoundException(new ElementNotFoundException(),Constants.NOT_FOUND,
                        new Object[]{uuid}));
        productRepository.delete(product);
    }
}
