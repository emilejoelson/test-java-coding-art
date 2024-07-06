package ma.codingart.testjava.mapper;

import ma.codingart.testjava.dto.request.ProductDtoRequest;
import ma.codingart.testjava.dto.request.ProductPatchDtoRequest;
import ma.codingart.testjava.dto.response.ProductDtoResponse;
import ma.codingart.testjava.dto.response.ProductListDtoResponse;
import ma.codingart.testjava.entity.Category;
import ma.codingart.testjava.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

   @Mappings
           ({
                   @Mapping(target = "id", ignore = true),
                   @Mapping(target = "uuid", ignore = true),
                   @Mapping(target = "category", source = "productDtoRequest.categoryName")
            })
   Product productDtoRequestToProduct(ProductDtoRequest productDtoRequest);

    @Mappings
            ({
                    @Mapping(target = "id", ignore = true),
                    @Mapping(target = "uuid", ignore = true),
                    @Mapping(target = "category", source = "productPatchDtoRequest.categoryName")
            })
    Product productPatchDtoRequestToProduct(ProductPatchDtoRequest productPatchDtoRequest);

    @Mapping(source = "product.category.name", target = "categoryName")
    ProductDtoResponse productToProductDtoResponse(Product product);


    ProductListDtoResponse productToProductListDtoResponse(Product product);

    default Category map(String categoryName){
        Category category = new Category();
        category.setName(categoryName);
        return  category;
    }

}