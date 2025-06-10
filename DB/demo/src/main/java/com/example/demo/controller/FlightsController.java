package com.example.demo.controller;

import com.example.demo.dto.booking.FareConditionDto;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.example.demo.dto.FlightDto;
import com.example.demo.dto.RouteDto;
import com.example.demo.entity.route.Route;
import com.example.demo.entity.utils.SupportedLanguage;
import com.example.demo.mapper.FlightMapper;
import com.example.demo.repository.FlightRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
public class FlightsController {
    private FlightRepository flightRepository;


    /* Example query
        http://localhost:8080/arrivals/KHV?fromTime=18:00&toTime=19:00&flightNumber=PG0651&daysOfWeek=MONDAY,FRIDAY
     */

    @GetMapping("/arrivals/{airport_code}")
    public List<FlightDto> getArrivalsSchedule(
            @PathVariable String airport_code,
            @RequestParam(required = false) Set<DayOfWeek> daysOfWeek,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime fromTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime toTime,
            @RequestParam(required = false) String flightNumber) {

        if (fromTime != null && toTime != null && toTime.isBefore(fromTime)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "toTime have to go after fromTime");
        }

        Set<Integer> internalDaysOfWeek = daysOfWeek == null ?
                new HashSet<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5, 6)) :
                daysOfWeek.stream().map(it -> (it.getValue() + 6) % 7).collect(Collectors.toSet());

        return flightRepository.findArrivalFlights(airport_code, fromTime, toTime, flightNumber, internalDaysOfWeek)
                .stream()
                .map(flight -> FlightMapper.toDto(flight, SupportedLanguage.EN))
                .toList();
    }

    @GetMapping("/departures/{airport_code}")
    public List<FlightDto> getDeparturesSchedule(
            @PathVariable String airport_code,
            @RequestParam(required = false) Set<DayOfWeek> daysOfWeek,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime fromTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime toTime,
            @RequestParam(required = false) String flightNumber) {

        if (fromTime != null && toTime != null && toTime.isBefore(fromTime)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "toTime have to go after fromTime");
        }

        Set<Integer> internalDaysOfWeek = daysOfWeek == null ?
                new HashSet<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5, 6)) :
                daysOfWeek.stream().map(it -> (it.getValue() + 6) % 7).collect(Collectors.toSet());

        return flightRepository.findDepartureFlights(airport_code, fromTime, toTime, flightNumber, internalDaysOfWeek)
                .stream()
                .map(flight -> FlightMapper.toDto(flight, SupportedLanguage.EN))
                .toList();
    }

    @GetMapping("/routes")
    public List<RouteDto> getRoutes(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
            @RequestParam(required = false) Integer maxConnections,
            @RequestParam(required = false, defaultValue = "Economy") FareConditionDto bookingClass
    ) {
        if (maxConnections == null) {
            maxConnections = 10;
        }

        List<Route> routes = flightRepository.findRoutes(departureDate, source, destination, maxConnections, bookingClass.toString());

        return routes.stream()
                .map(FlightMapper::toDto)
                .toList();
    }
}