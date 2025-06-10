package com.example.demo.mapper;

import com.example.demo.dto.AirportDto;
import com.example.demo.dto.CityDto;
import com.example.demo.entity.Airport;
import com.example.demo.entity.City;
import com.example.demo.entity.utils.Language;
import com.example.demo.entity.utils.SupportedLanguage;

public class AirportMapper {

    public static AirportDto toDto(Airport airport, SupportedLanguage language) {
        return new AirportDto(
                airport.getCode(),
                getLocalizedString(airport.getName(), language),
                getLocalizedString(airport.getCity(), language),
                airport.getTimezone()
        );
    }

    public static CityDto toDto(City city, SupportedLanguage language) {
        return new CityDto(
                getLocalizedString(city.getName(), language),
                city.getTimezone()
        );
    }

    private static String getLocalizedString(Language source, SupportedLanguage lang) {
        return lang == SupportedLanguage.RU ? source.getRu() : source.getEn();
    }
}