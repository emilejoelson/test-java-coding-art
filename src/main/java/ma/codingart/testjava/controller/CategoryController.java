package ma.codingart.testjava.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import ma.codingart.testjava.dto.request.CategoryDtoRequest;
import ma.codingart.testjava.dto.request.CategoryPatchDtoRequest;
import ma.codingart.testjava.dto.request.SearchRequest;
import ma.codingart.testjava.dto.response.CategoryDtoResponse;
import ma.codingart.testjava.dto.response.PaginatedResponseDto;
import ma.codingart.testjava.dto.response.ResponseDto;
import ma.codingart.testjava.entity.Category;
import ma.codingart.testjava.service.CategoryService;
import ma.codingart.testjava.service.EntityNameService;
import ma.codingart.testjava.utils.Constants;
import ma.codingart.testjava.utils.QueryParser;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
        private  final CategoryService categoryService;
        private  final MessageSource messageSource;
        private  final EntityNameService entityNameService;
        @PostMapping
        public ResponseEntity<CategoryDtoResponse> createCategory(@RequestBody @Valid CategoryDtoRequest categoryDtoRequest){
            CategoryDtoResponse responseCreatedCategory = categoryService.createCategory(categoryDtoRequest);
            return ResponseEntity.status(HttpStatus.OK).body(responseCreatedCategory);
        }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<CategoryDtoResponse>> getAllCategories(@RequestParam(value = "page",defaultValue = "0") int page,
                                                                                      @RequestParam(value = "size",defaultValue = "10")int size,
                                                                                      @RequestParam(value = "query",required = false)String query ){
        SearchRequest searchRequest = QueryParser.createSearchRequest(query,page,size);
        PaginatedResponseDto<CategoryDtoResponse> paginationResponse = categoryService.getAllCategories(searchRequest);

        return  ResponseEntity.status(HttpStatus.OK).body(paginationResponse);
    }

    @GetMapping("/{uuid}")
        public ResponseEntity<CategoryDtoResponse> getCategoryByUuid(@PathVariable UUID uuid){
            CategoryDtoResponse getOneCategoryResponse = categoryService.findCategoryByUuid(uuid);
            return  ResponseEntity.status(HttpStatus.OK).body(getOneCategoryResponse);

        }

        @PatchMapping("/{uuid}")
         public  ResponseEntity<CategoryDtoResponse> updateCategory(
                 @PathVariable UUID uuid,@RequestBody CategoryPatchDtoRequest categoryPatchDtoRequest
        ) {
            CategoryDtoResponse updateResponse = categoryService.patchCategory(uuid,categoryPatchDtoRequest);
            return  ResponseEntity.status(HttpStatus.OK).body(updateResponse);
        }

        @DeleteMapping("/{uuid}")
        public ResponseEntity<ResponseDto> deleteCategory(@PathVariable UUID uuid){
            categoryService.deleteCategory(uuid);
            String entityName = entityNameService.getEntityName(Category.class);
            String deletionMessage = messageSource.getMessage(Constants.ENTITY_DELETED,new Object[]{entityName},LocaleContextHolder.getLocale());
            ResponseDto responseDto = new ResponseDto(HttpStatus.OK,deletionMessage);
            return ResponseEntity.status(HttpStatus.OK).body(responseDto);
        }

}
