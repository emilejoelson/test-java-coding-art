package ma.codingart.testjava.test.service;

import java.util.Arrays;
import java.util.Collections;
import ma.codingart.testjava.ServiceTest;
import ma.codingart.testjava.dto.request.CategoryDtoRequest;
import ma.codingart.testjava.dto.request.CategoryPatchDtoRequest;
import ma.codingart.testjava.dto.request.SearchRequest;
import ma.codingart.testjava.dto.response.CategoryDtoResponse;
import ma.codingart.testjava.dto.response.PaginatedResponseDto;
import ma.codingart.testjava.entity.Category;
import ma.codingart.testjava.exception.ElementAlreadyExistException;
import ma.codingart.testjava.exception.ElementIsAssociatedWithException;
import ma.codingart.testjava.exception.ElementNotFoundException;
import ma.codingart.testjava.repository.CategoryRepository;
import ma.codingart.testjava.service.CategoryService;
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
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.yml")
public class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @MockBean
    private CategoryRepository categoryRepository;

    private CategoryDtoRequest categoryDtoRequest;

    @Autowired
    private ServiceTest serviceTest;

    private String name1;
    private String description1;
    private String name2;
    private String description2;

    @BeforeEach
     void initData(){
        name1 = "Books";
        description1 = "Printed or digital publications containing written content, including fiction, non-fiction, and educational materials.";
        name2 = "Home Decorator";
        description2 = "Items used to decorate and furnish residential spaces, including furniture, lighting, and decorative pieces.";
    }

    @AfterEach
    void tearDownVirtualThreads() throws ExecutionException,InterruptedException {
        serviceTest.tearDownVirtualThreads();
    }
    @Test
    public void createCategoryWithSuccess() {
        categoryDtoRequest = new CategoryDtoRequest(name1, description1);
        when(categoryRepository.findByName(categoryDtoRequest.name())).thenReturn(Optional.empty());
        when(categoryRepository.save(any())).thenAnswer(invocation -> {
            Category savedCategory = invocation.getArgument(0);
            savedCategory.setId(1L);
            savedCategory.setUuid(UUID.randomUUID());
            return savedCategory;
        });

        CategoryDtoResponse createdCategory = categoryService.createCategory(categoryDtoRequest);

        assertEquals(categoryDtoRequest.name(), createdCategory.name());
        assertEquals(categoryDtoRequest.description(), createdCategory.description());
        assertNotNull(createdCategory.baseDto());
    }

    @Test
    public void createCategoryFailure_ElementAlreadyExists() {
        categoryDtoRequest = new CategoryDtoRequest(name1, description1);

        when(categoryRepository.findByName(name1))
                .thenReturn(Optional.of(new Category()));

        ElementAlreadyExistException exception = assertThrows(ElementAlreadyExistException.class, () -> {
            categoryService.createCategory(categoryDtoRequest);
        });

        assertEquals(Constants.ALREADY_EXISTS, exception.getKey());
        assertEquals(name1, exception.getArgs()[0]);
    }

    @Test
    public void findCategoryByUuid_Success() {
        UUID uuid = UUID.randomUUID();
        Category category = new Category();
        category.setId(1L);
        category.setUuid(uuid);
        category.setName(name1);
        category.setDescription(description1);

        when(categoryRepository.findByUuid(uuid)).thenReturn(Optional.of(category));

        CategoryDtoResponse foundCategory = categoryService.findCategoryByUuid(uuid);

        assertEquals(category.getId(), foundCategory.id());
        assertEquals(category.getUuid(), foundCategory.uuid());
        assertEquals(category.getName(), foundCategory.name());
        assertEquals(category.getDescription(), foundCategory.description());
        assertNotNull(foundCategory.baseDto());
    }

    @Test
    public void findCategoryByUuid_ElementNotFound() {
        UUID uuid = UUID.randomUUID();

        when(categoryRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        ElementNotFoundException exception = assertThrows(ElementNotFoundException.class, () -> {
            categoryService.findCategoryByUuid(uuid);
        });

        assertEquals(Constants.NOT_FOUND, exception.getKey());
        assertEquals(uuid.toString(), exception.getArgs()[0].toString());
    }

    @Test
    public void patchCategory_Success() {
        UUID uuid = UUID.randomUUID();
        CategoryPatchDtoRequest patchDto = new CategoryPatchDtoRequest(null, description1);

        Category existingCategory = new Category();
        existingCategory.setUuid(uuid);

        when(categoryRepository.findByUuid(uuid)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);

        CategoryDtoResponse patchedCategory = categoryService.patchCategory(uuid, patchDto);

        assertEquals(uuid, patchedCategory.uuid());
        assertNull(patchedCategory.name());
        assertEquals(patchDto.description(), patchedCategory.description());
        assertNotNull(patchedCategory.baseDto());
    }

    @Test
    public void patchCategory_ElementNotFound() {
        UUID uuid = UUID.randomUUID();
        CategoryPatchDtoRequest patchDto = new CategoryPatchDtoRequest(null, description1);

        when(categoryRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        ElementNotFoundException exception = assertThrows(ElementNotFoundException.class, () -> {
            categoryService.patchCategory(uuid, patchDto);
        });

        assertEquals(Constants.NOT_FOUND, exception.getKey());
        assertEquals(uuid.toString(), exception.getArgs()[0].toString());
    }

    @Test
    public void deleteCategory_Success() {
        UUID uuid = UUID.randomUUID();
        Category category = new Category();
        category.setId(1L);
        category.setUuid(uuid);
        category.setName(name1);
        category.setDescription(description1);

        when(categoryRepository.isAssociatedWithProduct(uuid)).thenReturn(Optional.empty());
        when(categoryRepository.findByUuid(uuid)).thenReturn(Optional.of(category));

        assertDoesNotThrow(() -> categoryService.deleteCategory(uuid));

        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    public void deleteCategory_AssociatedProducts() {
        UUID uuid = UUID.randomUUID();

        when(categoryRepository.isAssociatedWithProduct(uuid)).thenReturn(Optional.of(new Category()));
        ElementIsAssociatedWithException exception = assertThrows(ElementIsAssociatedWithException.class, () -> {
            categoryService.deleteCategory(uuid);
        });

        assertEquals(Constants.IS_ASSOCIATED_WITH, exception.getKey());
        assertEquals(uuid.toString(), exception.getArgs()[0].toString());
    }

    @Test
    public void getAllCategories_Success() {
        SearchRequest searchRequest = QueryParser.createSearchRequest("sort=id,ASC", 0, 10);
        List<Category> mockCategories = Arrays.asList(
                serviceTest.createCategory(1L, name1, description1),
                serviceTest.createCategory(2L, name2, description2)
        );

        Page<Category> mockPage = new PageImpl<>(mockCategories, PageRequest.of(0, 10), mockCategories.size());
        when(categoryRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(mockPage);

        PaginatedResponseDto<CategoryDtoResponse> result = categoryService.getAllCategories(searchRequest);

        assertNotNull(result);
        assertEquals(2, result.getRecords().size());

        assertEquals(0, result.getCurrentPage());
        assertEquals(1, result.getPages());
        assertEquals(2, result.getTotalRecords());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());

        assertEquals(name1, result.getRecords().get(0).name());
        assertEquals(name2, result.getRecords().get(1).name());
    }

    @Test
    public void getAllCategories_EmptyResult() {
        SearchRequest searchRequest = QueryParser.createSearchRequest("sort=id,ASC", 0, 10);

        Page<Category> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(categoryRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

        PaginatedResponseDto<CategoryDtoResponse> result = categoryService.getAllCategories(searchRequest);

        assertNotNull(result);
        assertTrue(result.getRecords().isEmpty());
        assertEquals(0, result.getCurrentPage());
        assertEquals(0, result.getPages());
        assertEquals(0, result.getTotalRecords());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());
    }
}