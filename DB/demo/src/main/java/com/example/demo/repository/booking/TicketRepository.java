package com.example.demo.repository.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.booking.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, String> {
}
