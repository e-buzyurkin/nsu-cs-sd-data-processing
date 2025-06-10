package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightDto {
    String flight_id;
    String flight_no;
    String aircraft_code;

    String departure_datetime;
    String departure_airport;

    String arrival_datetime;
    String arrival_airport;
    String tickets_free;
}
