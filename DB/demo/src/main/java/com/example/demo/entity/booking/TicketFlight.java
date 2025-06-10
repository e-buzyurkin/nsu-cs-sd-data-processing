package com.example.demo.entity.booking;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.example.demo.dto.booking.FareConditionDto;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ticket_flights")
@IdClass(TicketFlightId.class)
public class TicketFlight {

    @Id
    @Column(name = "ticket_no")
    private String ticketNo;

    @Id
    @Column(name = "flight_id")
    private Integer flightId;

    @Column(name = "fare_conditions")
    private String fareConditions;

    private Double amount;
}
