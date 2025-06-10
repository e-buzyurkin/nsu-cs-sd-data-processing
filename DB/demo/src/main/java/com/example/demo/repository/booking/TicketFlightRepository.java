package com.example.demo.repository.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.booking.TicketFlight;
import com.example.demo.entity.booking.TicketFlightId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TicketFlightRepository extends JpaRepository<TicketFlight, TicketFlightId> {
	Optional<Integer> countByFlightIdAndFareConditions(Integer flightId, String fareConditions);
}
