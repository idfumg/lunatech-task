package com.example.logic.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="No such Movie")  // 404
public class MovieNotFound extends RuntimeException {

    public MovieNotFound(String message) {
        super(message);
    }
}
