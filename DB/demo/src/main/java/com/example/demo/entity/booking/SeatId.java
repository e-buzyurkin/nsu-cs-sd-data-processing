package com.example.demo.entity.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatId implements Serializable {
	private String aircraftCode;
	private Integer seatNo;
}