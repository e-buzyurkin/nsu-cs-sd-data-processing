package com.example.demo.entity.booking;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Data;

@Entity
@Data
@IdClass(SeatId.class)
public class Seat {
	@Id
	@Column(name = "aircraft_code")
	private String aircraftCode;
	@Id
	@Column(name = "seat_no")
	private String seatNo;
	@Column(name = "fare_conditions")
	private String fareConditions;
}
