package ma.codingart.testjava.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import ma.codingart.testjava.dto.request.ProductDtoRequest;
import ma.codingart.testjava.dto.request.ProductPatchDtoRequest;
import ma.codingart.testjava.dto.request.SearchRequest;
import ma.codingart.testjava.dto.response.PaginatedResponseDto;
import ma.codingart.testjava.dto.response.ProductDtoResponse;
import ma.codingart.testjava.dto.response.ProductListDtoResponse;
import ma.codingart.testjava.dto.response.ResponseDto;
import ma.codingart.testjava.entity.Product;
import ma.codingart.testjava.service.EntityNameService;
import ma.codingart.testjava.service.ProductService;
import ma.codingart.testjava.utils.Constants;
import ma.codingart.testjava.utils.QueryParser;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
import org.springframework.http.HttpStatus;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private  final ProductService productService;
    private final MessageSource messageSource;
    private  final EntityNameService entityNameService;
    @PostMapping
    public  ResponseEntity<ProductDtoResponse> createProduct(@RequestBody @Valid ProductDtoRequest productDtoRequest){
        ProductDtoResponse createdProductResponse  = productService.createProduct(productDtoRequest);
        return ResponseEntity.status(HttpStatus.OK).body(createdProductResponse);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<ProductListDtoResponse>> getAllProducts(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size,
            @RequestParam(value = "query",required = false) String query
    ) {
        SearchRequest searchRequest = QueryParser.createSearchRequest(query,page,size);
        PaginatedResponseDto paginationResponse = productService.getAllProducts(searchRequest);
        return  ResponseEntity.status(HttpStatus.OK).body(paginationResponse);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ProductDtoResponse> getProductByUuid(@PathVariable UUID uuid){
        ProductDtoResponse getOneProductResponse = productService.findProductByUuid(uuid);
        return  ResponseEntity.status(HttpStatus.OK).body(getOneProductResponse);
    }

    @PatchMapping("/{uuid}")
    public  ResponseEntity<ProductDtoResponse> updateProduct(@PathVariable UUID uuid,@RequestBody  ProductPatchDtoRequest productPatchDtoRequest){
        ProductDtoResponse updatedProductResponse = productService.patchProduct(uuid,productPatchDtoRequest);
        return  ResponseEntity.status(HttpStatus.OK).body(updatedProductResponse);
    }

    @DeleteMapping("/{uuid}")
    public  ResponseEntity<ResponseDto> deleteProduct(@PathVariable UUID uuid){
        productService.deleteProduct(uuid);
        String entityName = entityNameService.getEntityName(Product.class);
        String deletionMessage = messageSource.getMessage(Constants.ENTITY_DELETED, new Object[]{entityName}, LocaleContextHolder.getLocale());
        ResponseDto responseDto = new ResponseDto(HttpStatus.OK,deletionMessage);
        return  ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
