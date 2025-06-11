package com.example.demo.entity.booking;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@IdClass(SeatId.class)
@Table(name = "seats")
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
