package ma.codingart.testjava.service.implementation;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import ma.codingart.testjava.dto.request.CategoryDtoRequest;
import ma.codingart.testjava.dto.request.CategoryPatchDtoRequest;
import ma.codingart.testjava.dto.request.SearchRequest;
import ma.codingart.testjava.dto.response.BaseDto;
import ma.codingart.testjava.dto.response.CategoryDtoResponse;
import ma.codingart.testjava.dto.response.PaginatedResponseDto;
import ma.codingart.testjava.entity.Category;
import ma.codingart.testjava.exception.ElementAlreadyExistException;
import ma.codingart.testjava.exception.ElementIsAssociatedWithException;
import ma.codingart.testjava.exception.ElementNotFoundException;
import ma.codingart.testjava.mapper.CategoryMapper;
import ma.codingart.testjava.repository.CategoryRepository;
import ma.codingart.testjava.service.CategoryService;
import ma.codingart.testjava.utils.Constants;
import ma.codingart.testjava.utils.SearchSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private  final CategoryRepository categoryRepository;

    @Override
    public CategoryDtoResponse createCategory(CategoryDtoRequest categoryDtoRequest) throws ElementAlreadyExistException{
        categoryRepository.findByName(categoryDtoRequest.name()).ifPresent(existingCategory -> {
            throw new ElementAlreadyExistException(
                    new ElementAlreadyExistException(),
                    Constants.ALREADY_EXISTS,
                    new Object[]{categoryDtoRequest.name()}
            );
        });

        Category category = CategoryMapper.INSTANCE.categoryDtoRequestToCategory(categoryDtoRequest);
        Category savedCategory = categoryRepository.save(category);
        return CategoryMapper.INSTANCE.categoryToCategoryDtoResponse(savedCategory);
    }

    @Override
    public CategoryDtoResponse findCategoryByUuid(final UUID uuid) throws ElementNotFoundException {

        Category category = categoryRepository.findByUuid(uuid)
                                            .orElseThrow(()-> new ElementNotFoundException(new ElementNotFoundException(),Constants.NOT_FOUND,
                                                                new Object[]{uuid}));

        return CategoryMapper.INSTANCE.categoryToCategoryDtoResponse(category);
    }

    @Override
    public PaginatedResponseDto<CategoryDtoResponse> getAllCategories(SearchRequest searchRequest) {
        Specification<Category> specification = new SearchSpecification<>(searchRequest);
        return  getPaginatedResponse(specification,searchRequest);
    }

    private PaginatedResponseDto<CategoryDtoResponse> getPaginatedResponse(Specification<Category> specification, SearchRequest searchRequest) {
        Pageable pageRequest = SearchSpecification.getPageable(searchRequest.getPage(), searchRequest.getSize());
        Page<Category> categoryPage = categoryRepository.findAll(specification, pageRequest);

        List<CategoryDtoResponse> list = categoryPage.stream()
                .map(category -> new CategoryDtoResponse(
                        category.getId(),
                        category.getUuid(),
                        category.getName(),
                        category.getDescription(),
                        new BaseDto(category.getCreatedAt(), category.getUpdatedAt())
                )).toList();

        return PaginatedResponseDto.<CategoryDtoResponse>builder()
                .pages(categoryPage.getTotalPages())
                .totalRecords(categoryPage.getTotalElements())
                .first(categoryPage.isFirst())
                .last(categoryPage.isLast())
                .currentPage(categoryPage.getNumber())
                .records(list)
                .build();
    }

    @Override
    public CategoryDtoResponse patchCategory(UUID uuid, CategoryPatchDtoRequest categoryPatchDtoRequest) {
        Category existingCategory = categoryRepository.findByUuid(uuid)
                .orElseThrow(() -> new ElementNotFoundException(new ElementNotFoundException(), Constants.NOT_FOUND, new Object[]{uuid}));

        if (categoryPatchDtoRequest instanceof CategoryPatchDtoRequest(_,String description)) {
            if (description != null) {
                existingCategory.setDescription(description);
            }
        }
        Category updatedCategory = categoryRepository.save(existingCategory);
        return CategoryMapper.INSTANCE.categoryToCategoryDtoResponse(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(UUID uuid) throws ElementNotFoundException, ElementIsAssociatedWithException {
        Category category = categoryRepository.findByUuid(uuid)
                .orElseThrow(() -> new ElementNotFoundException(
                        new ElementNotFoundException(),
                        Constants.NOT_FOUND,
                        new Object[]{uuid}
                ));


        if (categoryRepository.isAssociatedWithProduct(uuid)) {
            throw new ElementIsAssociatedWithException(
                    new ElementIsAssociatedWithException(),
                    Constants.IS_ASSOCIATED_WITH,
                    new Object[]{uuid.toString()}
            );
        }

        categoryRepository.delete(category);
    }

    @Override
    public Category findByName(String name) {
        return categoryRepository.findByName(name).orElseThrow(()->new ElementNotFoundException(new ElementNotFoundException(),Constants.NOT_FOUND,
                new Object[]{name}));
    }

}
