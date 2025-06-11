package com.example.demo.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingDto {
    private List<Integer> flightIds;
    private FareConditionDto fareConditions;
    private String passengerName;
}
