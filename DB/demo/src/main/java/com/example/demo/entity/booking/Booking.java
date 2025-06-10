package com.example.demo.entity.booking;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "bookings")
public class Booking {

	@Id
	@GeneratedValue(generator = "custom-id-generator")
	@GenericGenerator(
			name = "custom-id-generator",
			strategy = "com.example.demo.entity.generator.BookRefGenerator"
	)
	@Column(name = "book_ref", length = 6)
	private String bookRef;

	@Column(name = "book_date")
	private Instant bookDate;

	@Column(name = "total_amount")
	private Double totalAmount;

	public Booking(Instant bookDate, Double totalPrice) {
		this.bookDate = bookDate;
		this.totalAmount = totalPrice;
	}
}
