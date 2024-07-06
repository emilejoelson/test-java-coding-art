package ma.codingart.testjava.service;

import java.util.UUID;
import ma.codingart.testjava.dto.request.ProductDtoRequest;
import ma.codingart.testjava.dto.request.ProductPatchDtoRequest;
import ma.codingart.testjava.dto.request.SearchRequest;
import ma.codingart.testjava.dto.response.PaginatedResponseDto;
import ma.codingart.testjava.dto.response.ProductDtoResponse;
import ma.codingart.testjava.dto.response.ProductListDtoResponse;
import ma.codingart.testjava.exception.ElementAlreadyExistException;
import ma.codingart.testjava.exception.ElementNotFoundException;

public interface ProductService {
    ProductDtoResponse createProduct(ProductDtoRequest productDtoRequest) throws ElementAlreadyExistException;

    ProductDtoResponse findProductByUuid(final UUID uuid) throws ElementNotFoundException;
    PaginatedResponseDto<ProductListDtoResponse> getAllProducts(SearchRequest searchRequest);
    ProductDtoResponse patchProduct(UUID uuid, ProductPatchDtoRequest productPatchDtoRequest);
    void deleteProduct(final  UUID uuid) throws  ElementNotFoundException;
}
