package com.example.demo.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.dto.AirportDto;
import com.example.demo.dto.CityDto;
import com.example.demo.entity.Airport;
import com.example.demo.entity.City;
import com.example.demo.entity.utils.SupportedLanguage;
import com.example.demo.mapper.AirportMapper;
import com.example.demo.repository.AirportRepository;

import java.util.List;

@RestController
@AllArgsConstructor
public class AirportController {
    private AirportRepository airportRepository;


    @GetMapping("/airports/{city}")
    public ResponseEntity<List<AirportDto>> getAirportsByCity(
            @PathVariable String city,
            @RequestParam(required = false, defaultValue = "EN") SupportedLanguage lang
    ) {
        List<Airport> airports = airportRepository.findByCityInAnyLanguage(city);

        if (airports.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<AirportDto> dto = airports
                .stream()
                .map((Airport airport) -> AirportMapper.toDto(airport, lang))
                .toList();

        return ResponseEntity.ok(dto);
    }


    @GetMapping("/cities")
    public List<CityDto> getCities(
            @RequestParam(required = false, defaultValue = "EN")
            SupportedLanguage lang
    ) {
        return airportRepository.findDistinctCities()
                .stream()
                .map((City city) -> AirportMapper.toDto(city, lang))
                .toList();
    }
}
