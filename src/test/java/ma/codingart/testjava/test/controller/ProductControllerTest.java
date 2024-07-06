package ma.codingart.testjava.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import ma.codingart.testjava.ServiceTest;
import ma.codingart.testjava.dto.request.ProductDtoRequest;
import ma.codingart.testjava.dto.request.ProductPatchDtoRequest;
import ma.codingart.testjava.dto.response.PaginatedResponseDto;
import ma.codingart.testjava.dto.response.ProductDtoResponse;
import ma.codingart.testjava.dto.response.ProductListDtoResponse;
import ma.codingart.testjava.entity.Product;
import ma.codingart.testjava.service.EntityNameService;
import ma.codingart.testjava.service.ProductService;
import ma.codingart.testjava.utils.Constants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test.yml")
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ServiceTest serviceTest;

    @MockBean
    private ProductService productService;

    @MockBean
    private MessageSource messageSource;

    @MockBean
    private EntityNameService entityNameService;



    private static final String BASE_URL = "/api/products";
    private static final String ENTITY_NAME = "Product";
    private static final String DELETION_MESSAGE = "Your Product Has been DELETED successfully";
    private static final String PRODUCT_TITLE = "Styles Roman";
    private static final String PRODUCT_DESCRIPTION = "Description of Book";
    private static final double PRODUCT_PRICE = 29.99;
    private  static final String CATEGORY_NAME = "Books";

    private ProductDtoRequest productDtoRequest;
    private ProductPatchDtoRequest productPatchDtoRequest;
    private ProductDtoResponse productDtoResponse;
    private PaginatedResponseDto<ProductListDtoResponse> paginatedResponse;

    @BeforeEach
    void initData() {
        productDtoRequest = new ProductDtoRequest(PRODUCT_TITLE, PRODUCT_DESCRIPTION, CATEGORY_NAME, PRODUCT_PRICE, true);
        productPatchDtoRequest = new ProductPatchDtoRequest(PRODUCT_TITLE, PRODUCT_DESCRIPTION, CATEGORY_NAME, PRODUCT_PRICE, true);

        productDtoResponse = new ProductDtoResponse(
                1L,
                UUID.randomUUID(),
                PRODUCT_TITLE,
                PRODUCT_DESCRIPTION,
                CATEGORY_NAME,
                PRODUCT_PRICE,
                true
        );

        List<ProductListDtoResponse> products = Collections.singletonList(
                new ProductListDtoResponse(
                        1L,
                        UUID.randomUUID(),
                        PRODUCT_TITLE,
                        PRODUCT_DESCRIPTION,
                        PRODUCT_PRICE,
                        true
                )
        );
        paginatedResponse = new PaginatedResponseDto<>();
        paginatedResponse.setRecords(products);
        paginatedResponse.setTotalRecords(1L);
        paginatedResponse.setPages(1);
        paginatedResponse.setCurrentPage(0);
        paginatedResponse.setFirst(true);
        paginatedResponse.setLast(true);
    }

    @AfterEach
    void tearDownVirtualThreads() throws ExecutionException,InterruptedException {
        serviceTest.tearDownVirtualThreads();
    }

    @Test
    public void testCreateProduct() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(productDtoRequest);

        when(productService.createProduct(any(ProductDtoRequest.class))).thenReturn(productDtoResponse);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(PRODUCT_TITLE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(PRODUCT_DESCRIPTION));
    }

    @Test
    public void testGetAllProducts() throws Exception {
        when(productService.getAllProducts(any())).thenReturn(paginatedResponse);
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.records.size()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.records[0].title").value(PRODUCT_TITLE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.records[0].description").value(PRODUCT_DESCRIPTION));
    }

    @Test
    public void testGetProductByUuid() throws Exception {
        UUID uuid = UUID.randomUUID();

        when(productService.findProductByUuid(uuid)).thenReturn(productDtoResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(STR."\{BASE_URL}//\{uuid}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(PRODUCT_TITLE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(PRODUCT_DESCRIPTION));
    }

    @Test
    public void testUpdateProduct() throws Exception {
        UUID uuid = UUID.randomUUID();
        String content = new ObjectMapper().writeValueAsString(productPatchDtoRequest);

        ProductDtoResponse updatedProductDtoResponse = new ProductDtoResponse(
                productDtoResponse.id(),
                uuid,
                productPatchDtoRequest.title(),
                productPatchDtoRequest.description(),
                productPatchDtoRequest.categoryName(),
                productPatchDtoRequest.price(),
                productPatchDtoRequest.isEnabled()
        );

        when(productService.patchProduct(eq(uuid), any(ProductPatchDtoRequest.class))).thenReturn(updatedProductDtoResponse);

        mockMvc.perform(MockMvcRequestBuilders.patch(STR."\{BASE_URL}//\{uuid}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(PRODUCT_TITLE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(PRODUCT_DESCRIPTION));
    }

    @Test
    public void testDeleteProduct() throws Exception {
        UUID uuid = UUID.randomUUID();
        when(entityNameService.getEntityName(Product.class)).thenReturn(ENTITY_NAME);
        when(messageSource.getMessage(eq(Constants.ENTITY_DELETED), any(), any())).thenReturn(DELETION_MESSAGE);

        doNothing().when(productService).deleteProduct(uuid);

        mockMvc.perform(MockMvcRequestBuilders.delete(STR."\{BASE_URL}//\{uuid}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("OK"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(DELETION_MESSAGE));

        verify(productService, times(1)).deleteProduct(uuid);
        verify(entityNameService, times(1)).getEntityName(Product.class);
        verify(messageSource, times(1)).getMessage(eq(Constants.ENTITY_DELETED), any(), any());
    }
}
