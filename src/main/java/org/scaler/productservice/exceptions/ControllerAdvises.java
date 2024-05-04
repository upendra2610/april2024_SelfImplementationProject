package org.scaler.productservice.exceptions;

import org.scaler.productservice.dtos.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerAdvises {

    @ExceptionHandler(NotFoundException.class)
    private ResponseEntity<ErrorDto> notFoundExceptionHandler(NotFoundException notFoundException){
        return new ResponseEntity<>(
                new ErrorDto(notFoundException.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }
}
