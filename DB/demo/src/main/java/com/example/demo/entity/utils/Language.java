package com.example.demo.entity.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class Language {
    private String en;
    private String ru;

    public Language() {

    }

    @JsonCreator
    public Language(
            @JsonProperty("en") String en,
            @JsonProperty("ru") String ru) {
        this.en = en;
        this.ru = ru;
    }
}
