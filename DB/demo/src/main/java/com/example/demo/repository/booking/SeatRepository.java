package com.example.demo.repository.booking;

import com.example.demo.entity.booking.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
	Optional<Integer> countAllByAircraftCodeAndFareConditions(String aircraftCode, String fareConditions);
}
