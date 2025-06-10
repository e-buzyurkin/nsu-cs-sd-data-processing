package com.example.demo.repository.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.booking.Booking;

public interface BookingRepository extends JpaRepository<Booking, String> {
}
