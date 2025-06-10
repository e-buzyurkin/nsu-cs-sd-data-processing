package com.example.demo.dto;

public record AirportDto(
        String code,
        String name,
        String city,
        String timezone
) {}