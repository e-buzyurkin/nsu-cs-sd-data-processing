package com.example.demo.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BookingDto {
    String bookRef;
    String ticketNo;
    Double totalPrice;
}
