package ma.codingart.testjava.exception.handle;


import java.util.List;
import lombok.AllArgsConstructor;

import ma.codingart.testjava.dto.response.ValidationResponse;
import ma.codingart.testjava.exception.BusinessException;
import ma.codingart.testjava.exception.ElementAlreadyExistException;
import ma.codingart.testjava.exception.ElementIsAssociatedWithException;
import ma.codingart.testjava.exception.ElementNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@AllArgsConstructor
public class GenericGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<ValidationResponse> validations = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> ValidationResponse.builder()
                        .field(fieldError.getField())
                        .message(getMessage(fieldError))
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(validations);
    }



    @ExceptionHandler(value = ElementNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleException(final ElementNotFoundException e) {
        return getResponseEntity(NOT_FOUND, e);
    }

    @ExceptionHandler(value = ElementAlreadyExistException.class)
    @ResponseStatus(FOUND)
    public ResponseEntity<ErrorResponse> handleException(final ElementAlreadyExistException e) {
        return getResponseEntity(FOUND, e);
    }

    @ExceptionHandler(value = ElementIsAssociatedWithException.class)
    @ResponseStatus(FOUND)
    public ResponseEntity<ErrorResponse> handleException(final ElementIsAssociatedWithException e) {
        return getResponseEntity(FOUND, e);
    }

    private ResponseEntity<ErrorResponse> getResponseEntity(final HttpStatus status, final BusinessException e) {
        if (Objects.isNull(e.getKey())) {
            return ResponseEntity.status(status)
                    .body(ErrorResponse.builder().code(status.value()).status(status).message(e.getMessage()).build());
        }
        return ResponseEntity.status(status)
                .body(ErrorResponse.builder().code(status.value()).status(status).message(getMessage(e)).build());
    }


    private String getMessage(final BusinessException e) {
        System.out.println(LocaleContextHolder.getLocale().toString());
        return messageSource.getMessage(e.getKey(), e.getArgs(),LocaleContextHolder.getLocale());
    }

    private String getMessage(final FieldError fieldError) {
        return !( fieldError.getDefaultMessage()==null || fieldError.getDefaultMessage().isEmpty() ) ? fieldError.getDefaultMessage() :
                messageSource.getMessage(Objects.requireNonNull(fieldError.getCode()), fieldError.getArguments(), LocaleContextHolder.getLocale());
    }

}
