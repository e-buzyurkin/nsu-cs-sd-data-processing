package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.example.demo.entity.utils.Language;

@Entity
@Data
@Table(name = "airports_data")
public class Airport {

    @Id
    @Column(name = "airport_code")
    private String code;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "airport_name", columnDefinition = "jsonb")
    private Language name;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Embedded
    private Language city;

    private String coordinates;
    private String timezone;
}

