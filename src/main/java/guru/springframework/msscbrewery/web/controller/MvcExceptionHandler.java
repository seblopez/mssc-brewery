package guru.springframework.msscbrewery.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
public class MvcExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List> validationErrorHandler(MethodArgumentNotValidException e) {
        List<String> errors = new ArrayList<>();
        e.getBindingResult().getAllErrors()
                .forEach(constraintViolation -> {
                    final String message = constraintViolation.toString();
                    log.error(message);
                    errors.add(message);
                });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<List> handleBindException(BindException ex) {
        return new ResponseEntity(ex.getAllErrors(), HttpStatus.BAD_REQUEST);
    }
}
