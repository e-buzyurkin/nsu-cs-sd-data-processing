package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class RouteDto {
    private Integer connections;
    private List<FlightDto> flights;
}
