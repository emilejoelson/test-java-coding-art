package ma.codingart.testjava.test.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import ma.codingart.testjava.ServiceTest;
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
import ma.codingart.testjava.repository.ProductRepository;
import ma.codingart.testjava.service.CategoryService;
import ma.codingart.testjava.service.ProductService;
import ma.codingart.testjava.utils.Constants;
import ma.codingart.testjava.utils.QueryParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.test.context.TestPropertySource;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.yml")
@Service
@RequiredArgsConstructor
public class ProductServiceTest {
    @Autowired
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ServiceTest serviceTest;

    private ProductDtoRequest productDtoRequest;
    private ProductPatchDtoRequest productPatchDtoRequest;
    private String categoryName;
    private String categoryDescription;
    private String productTitle1;
    private String productTitle2;
    private String productDescription1;
    private String productDescription2;
    private Double productPrice1;
    private  Double productPrice2;
    private  Boolean isEnabled ;
    private Product createProduct(Long id, String title, String description, Category category, Double price, Boolean isEnabled) {
        Product product = new Product();
        product.setId(id);
        product.setUuid(UUID.randomUUID());
        product.setTitle(title);
        product.setDescription(description);
        product.setCategory(category);
        product.setPrice(price);
        product.setIsEnabled(isEnabled);
        return product;
    }

    @BeforeEach
    void initData() {
        categoryName = "Books";
        categoryDescription = "Printed or digital publications containing written content, including fiction, non-fiction, and educational materials.";
        productTitle1 = "Roman";
        productTitle2= "Three Londo Woman";
        productDescription1 = "Book Description";
        productDescription2 = "Update Description";
        productPrice1 = 29.99;
        productPrice2 = 38.8;
        isEnabled = true;

        productDtoRequest = new ProductDtoRequest(
                productTitle1,
                productDescription1,
                categoryName,
                productPrice1,
                true
        );

        productPatchDtoRequest = new ProductPatchDtoRequest(
                productTitle1,
                productDescription1,
                categoryName,
                productPrice1,
                true
        );
    }

    @AfterEach
    void tearDownVirtualThreads() throws ExecutionException,InterruptedException {
        serviceTest.tearDownVirtualThreads();
    }

    @Test
    public void createProductWithSuccess() throws ElementAlreadyExistException {
        productDtoRequest = new ProductDtoRequest(productTitle1,productDescription1,categoryName,productPrice1,true);

        when(productRepository.findByTitle(productDtoRequest.title())).thenReturn(Optional.empty());
        when(categoryService.findByName(categoryName)).thenReturn(serviceTest.createCategory(1L, categoryName, categoryDescription));
        when(productRepository.save(any())).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            savedProduct.setId(1L);
            savedProduct.setUuid(UUID.randomUUID());
            return savedProduct;
        });

        ProductDtoResponse createdProduct = productService.createProduct(productDtoRequest);

        assertEquals(productDtoRequest.title(), createdProduct.title());
        assertEquals(productDtoRequest.description(), createdProduct.description());
        assertEquals(categoryName, createdProduct.categoryName());
        assertEquals(productDtoRequest.price(), createdProduct.price());
        assertTrue(createdProduct.isEnabled());
    }

    @Test
    public void createProductFailure_ElementAlreadyExists() {
        productDtoRequest = new ProductDtoRequest(productTitle1,productDescription1,categoryName,productPrice1,true);
        when(productRepository.findByTitle(productDtoRequest.title())).thenReturn(Optional.of(new Product()));

        ElementAlreadyExistException exception = assertThrows(ElementAlreadyExistException.class, () -> {
            productService.createProduct(productDtoRequest);
        });
        assertEquals(Constants.ALREADY_EXISTS, exception.getKey());
        assertEquals(productDtoRequest.title(), exception.getArgs()[0]);
    }

    @Test
    public void findProductByUuid_Success() {
        UUID uuid = UUID.randomUUID();
        Product product = createProduct(1L, productTitle1, productDescription1, serviceTest.createCategory(1L, categoryName, categoryDescription), productPrice1, true);

        when(productRepository.findByUuid(uuid)).thenReturn(Optional.of(product));

        ProductDtoResponse foundProduct = productService.findProductByUuid(uuid);

        assertEquals(product.getId(), foundProduct.id());
        assertEquals(product.getUuid(), foundProduct.uuid());
        assertEquals(product.getTitle(), foundProduct.title());
        assertEquals(product.getDescription(), foundProduct.description());
        assertEquals(product.getCategory().getName(), foundProduct.categoryName());
        assertEquals(product.getPrice(), foundProduct.price());
        assertTrue(foundProduct.isEnabled());
    }

    @Test
    public void findProductByUuid_ElementNotFound() {
        UUID uuid = UUID.randomUUID();

        when(productRepository.findByUuid(uuid)).thenReturn(Optional.empty());
        ElementNotFoundException exception = assertThrows(ElementNotFoundException.class, () -> {
            productService.findProductByUuid(uuid);
        });
        assertEquals(Constants.NOT_FOUND, exception.getKey());
        assertEquals(uuid.toString(), exception.getArgs()[0].toString());
    }

    @Test
    public void patchProduct_Success()  {
        UUID uuid = UUID.randomUUID();
        Product existingProduct = new Product();
        existingProduct.setUuid(uuid);

        ProductPatchDtoRequest patchDto = new ProductPatchDtoRequest(
                null,
                productDescription2,
                null,
                productPrice2,
                false
        );

        when(productRepository.findByUuid(uuid)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(existingProduct);

        ProductDtoResponse patchedProduct = productService.patchProduct(uuid, patchDto);

        assertEquals(uuid, patchedProduct.uuid());
        assertNull( patchedProduct.title());
        assertEquals(patchDto.description(), patchedProduct.description());
        assertNull( patchedProduct.categoryName());
        assertEquals(patchDto.price(), patchedProduct.price());
        assertFalse(patchedProduct.isEnabled());
    }

    @Test
    public void patchProduct_ElementNotFound() {
        UUID uuid = UUID.randomUUID();
        ProductPatchDtoRequest patchDto = new ProductPatchDtoRequest(
                productTitle1,
                productDescription2,
                categoryName,
                productPrice2,
                false
        );

        when(productRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        ElementNotFoundException exception = assertThrows(ElementNotFoundException.class, () -> {
            productService.patchProduct(uuid, patchDto);
        });
        assertEquals(Constants.NOT_FOUND, exception.getKey());
        assertEquals(uuid.toString(), exception.getArgs()[0].toString());
    }

    @Test
    public void patchProduct_NullDescription() {
        UUID uuid = UUID.randomUUID();
        ProductPatchDtoRequest patchDto = new ProductPatchDtoRequest(null, null, null, productPrice2, isEnabled);

        Product existingProduct = new Product();
        existingProduct.setUuid(uuid);

        String originalDescription = productDescription1;
        existingProduct.setDescription(originalDescription);

        when(productRepository.findByUuid(uuid)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(existingProduct);

        ProductDtoResponse patchedProduct = productService.patchProduct(uuid, patchDto);

        assertEquals(uuid, patchedProduct.uuid());
        assertEquals(originalDescription, patchedProduct.description());
        assertEquals(productPrice2, patchedProduct.price());
        assertEquals(isEnabled, patchedProduct.isEnabled());
    }

    @Test
    public void patchProduct_NullPrice() {
        UUID uuid = UUID.randomUUID();
        ProductPatchDtoRequest patchDto = new ProductPatchDtoRequest(null, productDescription2, null, null, isEnabled);

        Product existingProduct = new Product();
        existingProduct.setUuid(uuid);

        Double originalPrice =productPrice1 ;
        existingProduct.setPrice(originalPrice);

        when(productRepository.findByUuid(uuid)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(existingProduct);

        ProductDtoResponse patchedProduct = productService.patchProduct(uuid, patchDto);

        assertEquals(uuid, patchedProduct.uuid());
        assertEquals(productDescription2, patchedProduct.description());
        assertEquals(originalPrice, patchedProduct.price());
        assertEquals(isEnabled, patchedProduct.isEnabled());
    }

    @Test
    public void deleteProduct_Success()  {
        UUID uuid = UUID.randomUUID();
        Product product = createProduct(1L, productTitle1, productDescription1,serviceTest.createCategory(1L, categoryName, categoryDescription), productPrice1, true);
        when(productRepository.findByUuid(uuid)).thenReturn(Optional.of(product));
        assertDoesNotThrow(() -> productService.deleteProduct(uuid));
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    public void deleteProduct_ElementNotFound() {
        UUID uuid = UUID.randomUUID();

        when(productRepository.findByUuid(uuid)).thenReturn(Optional.empty());
        ElementNotFoundException exception = assertThrows(ElementNotFoundException.class, () -> {
            productService.deleteProduct(uuid);
        });

        assertEquals(Constants.NOT_FOUND, exception.getKey());
        assertEquals(uuid.toString(), exception.getArgs()[0].toString());
    }

    @Test
    public void getAllProducts_Success() {
        SearchRequest searchRequest = QueryParser.createSearchRequest("sort=id,ASC", 0, 10);
        List<Product> mockProducts = Arrays.asList(
                createProduct(1L, productTitle1, productDescription1, serviceTest.createCategory(1L, categoryName, categoryDescription), productPrice1, true),
                createProduct(2L, productTitle2, productDescription2, serviceTest.createCategory(2L, categoryName, categoryDescription), productPrice2, true)
        );

        Page<Product> mockPage = new PageImpl<>(mockProducts, PageRequest.of(0, 10), mockProducts.size());

        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(mockPage);

        PaginatedResponseDto<ProductListDtoResponse> result = productService.getAllProducts(searchRequest);

        assertNotNull(result);
        assertEquals(2, result.getRecords().size());
        assertEquals(0, result.getCurrentPage());
        assertEquals(1, result.getPages());
        assertEquals(2, result.getTotalRecords());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());
        assertEquals(productTitle1, result.getRecords().get(0).title());
        assertEquals(productDescription1, result.getRecords().get(0).description());
    }

    @Test
    public void getAllProducts_EmptyResult() {
        SearchRequest searchRequest = QueryParser.createSearchRequest("sort=id,ASC", 0, 10);

        Page<Product> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);

        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

        PaginatedResponseDto<ProductListDtoResponse> result = productService.getAllProducts(searchRequest);

        assertNotNull(result);
        assertTrue(result.getRecords().isEmpty());
        assertEquals(0, result.getCurrentPage());
        assertEquals(0, result.getPages());
        assertEquals(0, result.getTotalRecords());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());
    }
}

