package com.example.demo.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class BookingDto {
    String bookRef;
    List<String> ticketNoList;
    Double totalPrice;
}
