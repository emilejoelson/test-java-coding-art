package ma.codingart.testjava.mapper;

import ma.codingart.testjava.dto.request.CategoryDtoRequest;
import ma.codingart.testjava.dto.request.CategoryPatchDtoRequest;
import ma.codingart.testjava.dto.response.BaseDto;
import ma.codingart.testjava.dto.response.CategoryDtoResponse;
import ma.codingart.testjava.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    @Mappings
            ({
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "uuid", ignore = true)
            })
    Category categoryDtoRequestToCategory(CategoryDtoRequest categoryDtoRequest);

    @Mappings
            ({
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "uuid", ignore = true)
            })
    Category categoryPatchDtoRequestToCategory(CategoryPatchDtoRequest categoryPatchDtoRequest);

    @Mapping(target = "baseDto", expression = "java(toBaseDto(category))")
    CategoryDtoResponse categoryToCategoryDtoResponse(Category category);

    default BaseDto toBaseDto(Category category) {
        return new BaseDto(category.getCreatedAt(), category.getUpdatedAt());
    }
}
