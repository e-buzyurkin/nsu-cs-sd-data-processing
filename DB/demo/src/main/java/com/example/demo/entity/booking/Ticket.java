package com.example.demo.entity.booking;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.example.demo.dto.ContactInfoDto;

@Entity
@Data
@Builder
@Table(name = "tickets")
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(generator = "ticket-id-generator")
    @GenericGenerator(
            name = "ticket-id-generator",
            strategy = "com.example.demo.entity.generator.TicketIdGenerator"
    )
    @Column(name = "ticket_no", length = 13)
    private String ticketNo;

    @Column(name = "book_ref")
    private String bookRef;

    @Column(name = "passenger_id")
    private String passengerId;

    @Column(name = "passenger_name")
    private String passengerName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "contact_data", columnDefinition = "jsonb")
    private ContactInfoDto contactData;
}
