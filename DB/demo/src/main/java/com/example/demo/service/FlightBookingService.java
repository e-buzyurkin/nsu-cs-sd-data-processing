package com.example.demo.service;

import com.example.demo.dto.ContactInfoDto;
import com.example.demo.dto.booking.BookingDto;
import com.example.demo.dto.booking.BookingException;
import com.example.demo.dto.booking.CreateBookingDto;
import com.example.demo.entity.booking.Booking;
import com.example.demo.entity.booking.Ticket;
import com.example.demo.entity.booking.TicketFlight;
import com.example.demo.repository.FlightRepository;
import com.example.demo.repository.booking.BookingRepository;
import com.example.demo.repository.booking.SeatRepository;
import com.example.demo.repository.booking.TicketFlightRepository;
import com.example.demo.repository.booking.TicketRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional()
@AllArgsConstructor
public class FlightBookingService {

	private final SeatRepository seatRepository;
	private TicketFlightRepository ticketFlightRepository;
	private BookingRepository bookingRepository;
	private TicketRepository ticketRepository;
	private FlightRepository flightRepository;

	public BookingDto bookFlight(CreateBookingDto createBookingDto) {
		String fareConditions = createBookingDto.getFareConditions().toString();
		System.out.println(fareConditions);
		System.out.println(createBookingDto.getFlightIds().toString());

		// цены
		Map<Integer, Double> prices = new HashMap<>();
		createBookingDto.getFlightIds().forEach((Integer flightId) -> {
			Optional<String> aircraft = flightRepository.findAircraftCode(flightId);
			if (aircraft.isEmpty()) {
				throw new BookingException("Flight with flightId " + flightId + " does not exist.");
			}

			Optional<Integer> totalSeats = seatRepository.countAllByAircraftCodeAndFareConditions(aircraft.get(), fareConditions);
			Optional<Integer> usedSeats = ticketFlightRepository.countByFlightIdAndFareConditions(flightId, fareConditions);
			Optional<Double> price = flightRepository.findPriceByFlightId(flightId, fareConditions);
			if (totalSeats.isEmpty() || usedSeats.isEmpty() || price.isEmpty()) {
				throw new BookingException("Internal error");
			}

			if (totalSeats.get() <= usedSeats.get()) {
				throw new BookingException("There is no more empty seats on flightId " + flightId
						+ "with fare condition " + fareConditions);
			}

			prices.put(flightId, price.get());
		});
		Double totalPrice = prices.values().stream().reduce(0.0, Double::sum);

		Booking booking = Booking.builder()
				.bookDate(Instant.now())
				.totalAmount(totalPrice)
				.build();
		String bookRef = bookingRepository.save(booking).getBookRef();

		Ticket ticket = Ticket.builder()
				.bookRef(bookRef)
				.passengerId(createBookingDto.getPassengerId())
				.passengerName("Evgeniy Buzyurkin")
				.contactData(new ContactInfoDto("666"))
				.build();
		String ticketNo = ticketRepository.save(ticket).getTicketNo();

		System.out.println(bookRef);
		System.out.println(ticketNo);


		for (var flightId : createBookingDto.getFlightIds()) {
			TicketFlight ticketFlight = new TicketFlight(
					ticketNo,
					flightId,
					fareConditions,
					prices.get(flightId)
			);
			ticketFlightRepository.save(ticketFlight);
		}


		return new BookingDto(bookRef, ticketNo, totalPrice);
	}

}
