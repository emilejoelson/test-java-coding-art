package ma.codingart.testjava.service;


import java.util.UUID;
import ma.codingart.testjava.dto.request.CategoryDtoRequest;
import ma.codingart.testjava.dto.request.CategoryPatchDtoRequest;
import ma.codingart.testjava.dto.request.SearchRequest;
import ma.codingart.testjava.dto.response.CategoryDtoResponse;
import ma.codingart.testjava.dto.response.PaginatedResponseDto;
import ma.codingart.testjava.entity.Category;
import ma.codingart.testjava.exception.ElementAlreadyExistException;
import ma.codingart.testjava.exception.ElementIsAssociatedWithException;
import ma.codingart.testjava.exception.ElementNotFoundException;

public interface CategoryService {
    CategoryDtoResponse createCategory(CategoryDtoRequest categoryDtoRequest) throws ElementAlreadyExistException;

    CategoryDtoResponse findCategoryByUuid(final UUID uuid) throws ElementNotFoundException;
    PaginatedResponseDto<CategoryDtoResponse> getAllCategories(SearchRequest searchRequest);
    CategoryDtoResponse patchCategory(UUID uuid, CategoryPatchDtoRequest categoryPatchDtoRequest);
    void deleteCategory(final  UUID uuid) throws  ElementNotFoundException, ElementIsAssociatedWithException;
    Category findByName(String name);
}
