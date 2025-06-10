package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.*;
import java.util.TimeZone;

@Entity
@Table(name = "flights")
@Data
public class Flight {
    @Id
    @Column(name = "flight_id")
    private Long flightId;
    @Column(name = "flight_no")
    private String flightNo;
    @Column(name = "aircraft_code")
    private String aircraftCode;

    @Column(name = "departure_airport")
    private String departureAirport;
    @Column
    private LocalDateTime departure_date_time;
    @Column
    private TimeZone departure_timezone;
    private DayOfWeek departureDow;

    @Column(name = "arrival_airport")
    private String arrivalAirport;
    private LocalDateTime arrival_date_time;
    private TimeZone arrival_timezone;
    private DayOfWeek arrivalDow;
}
