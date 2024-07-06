package ma.codingart.testjava.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import ma.codingart.testjava.ServiceTest;
import ma.codingart.testjava.dto.request.CategoryDtoRequest;
import ma.codingart.testjava.dto.request.CategoryPatchDtoRequest;
import ma.codingart.testjava.dto.response.BaseDto;
import ma.codingart.testjava.dto.response.CategoryDtoResponse;
import ma.codingart.testjava.dto.response.PaginatedResponseDto;
import ma.codingart.testjava.entity.Category;
import ma.codingart.testjava.service.CategoryService;
import ma.codingart.testjava.service.EntityNameService;
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
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ServiceTest serviceTest;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private MessageSource messageSource;

    @MockBean
    private EntityNameService entityNameService;

    private static final String BASE_URL = "/api/categories";
    private static final String ENTITY_NAME = "Category";
    private static final String DELETION_MESSAGE = "Your Category Has been DELETED successfully";
    private static final String CATEGORY_NAME = "Books";
    private static final String CATEGORY_DESCRIPTION = "Some description";
    private static final String UPDATE_DESCRIPTION = "Updated Description";

    private CategoryDtoRequest categoryDtoRequest;
    private CategoryPatchDtoRequest categoryPatchDtoRequest;
    private CategoryDtoResponse categoryDtoResponse;
    private PaginatedResponseDto<CategoryDtoResponse> paginatedResponse;

    @BeforeEach
    void initData() {
        categoryDtoRequest = new CategoryDtoRequest(CATEGORY_NAME, CATEGORY_DESCRIPTION);
        categoryPatchDtoRequest = new CategoryPatchDtoRequest(null, UPDATE_DESCRIPTION);

        categoryDtoResponse = new CategoryDtoResponse(
                1L,
                UUID.randomUUID(),
                CATEGORY_NAME,
                CATEGORY_DESCRIPTION,
                new BaseDto(LocalDateTime.now(), null)
        );

        List<CategoryDtoResponse> categories = Collections.singletonList(categoryDtoResponse);
        paginatedResponse = new PaginatedResponseDto<>();
        paginatedResponse.setRecords(categories);
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
    public void testCreateCategory() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(categoryDtoRequest);

        when(categoryService.createCategory(any(CategoryDtoRequest.class))).thenReturn(categoryDtoResponse);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(CATEGORY_NAME))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(CATEGORY_DESCRIPTION));
    }

    @Test
    public void testGetAllCategories() throws Exception {
        when(categoryService.getAllCategories(any())).thenReturn(paginatedResponse);
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.records.size()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.records[0].name").value(CATEGORY_NAME))
                .andExpect(MockMvcResultMatchers.jsonPath("$.records[0].description").value(CATEGORY_DESCRIPTION));
    }

    @Test
    public void testGetCategoryByUuid() throws Exception {
        UUID uuid = UUID.randomUUID();

        when(categoryService.findCategoryByUuid(uuid)).thenReturn(categoryDtoResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(STR."\{BASE_URL}//\{uuid}" )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(CATEGORY_NAME))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(CATEGORY_DESCRIPTION));
    }

    @Test
    public void testUpdateCategory() throws Exception {
        UUID uuid = UUID.randomUUID();
        String content = new ObjectMapper().writeValueAsString(categoryPatchDtoRequest);

        CategoryDtoResponse updatedCategoryDtoResponse = new CategoryDtoResponse(
                categoryDtoResponse.id(),
                uuid,
                categoryDtoResponse.name(),
                categoryPatchDtoRequest.description(),
                categoryDtoResponse.baseDto()
        );

        when(categoryService.patchCategory(any(), any())).thenReturn(updatedCategoryDtoResponse);

        mockMvc.perform(MockMvcRequestBuilders.patch(STR."\{BASE_URL}//\{uuid}" )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(UPDATE_DESCRIPTION));
    }

    @Test
    public void testDeleteCategory() throws Exception {
        UUID uuid = UUID.randomUUID();
        when(entityNameService.getEntityName(Category.class)).thenReturn(ENTITY_NAME);
        when(messageSource.getMessage(eq(Constants.ENTITY_DELETED), any(), any())).thenReturn(DELETION_MESSAGE);

        doNothing().when(categoryService).deleteCategory(uuid);

        mockMvc.perform(MockMvcRequestBuilders.delete(STR."\{BASE_URL}//\{uuid}" )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("OK"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(DELETION_MESSAGE));

        verify(categoryService, times(1)).deleteCategory(uuid);
        verify(entityNameService, times(1)).getEntityName(Category.class);
        verify(messageSource, times(1)).getMessage(eq(Constants.ENTITY_DELETED), any(), any());
    }
}
