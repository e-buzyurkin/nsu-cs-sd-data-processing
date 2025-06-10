package com.example.demo.entity.booking;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketFlightId implements Serializable {
    private String ticketNo;
    private Integer flightId;
}
