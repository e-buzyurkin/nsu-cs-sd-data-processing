package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.example.demo.entity.utils.Language;

@Data
@AllArgsConstructor
public class City {
    Language name;
    String timezone;
}
