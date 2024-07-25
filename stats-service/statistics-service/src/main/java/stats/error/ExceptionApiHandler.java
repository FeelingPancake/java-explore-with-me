package stats.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class ExceptionApiHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class,
        UnsupportedOperationException.class})
    public ErrorResponse handleValidationExceptions(Throwable ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getClass().getSimpleName(), ex.getMessage());
        errorResponse.log();
        return errorResponse;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class})
    public ErrorResponse handleIllegalArgumentException(Throwable ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getClass().getSimpleName(), ex.getMessage());
        errorResponse.log();
        return errorResponse;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleOthersExceptions(Throwable ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getClass().getSimpleName(), ex.getMessage());
        errorResponse.log();
        return errorResponse;
    }
}
