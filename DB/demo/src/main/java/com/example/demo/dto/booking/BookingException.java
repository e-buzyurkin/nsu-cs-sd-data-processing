package com.example.demo.dto.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // Sets HTTP status code
public class BookingException extends RuntimeException {
    public BookingException(String message) {
        super(message);
    }
}
