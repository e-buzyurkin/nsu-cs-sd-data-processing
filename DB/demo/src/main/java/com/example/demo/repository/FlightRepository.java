package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.demo.entity.Flight;
import com.example.demo.entity.route.Route;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    String queryPrefix = """
        SELECT 
            f.flight_id AS flight_id,
            f.flight_no AS flight_no,
            f.aircraft_code AS aircraft_code,
            f.arrival_airport AS arrival_airport,
            f.departure_airport AS departure_airport,
            f.scheduled_departure AT TIME ZONE d.timezone AS departure_date_time,
            d.timezone AS departure_timezone,
            EXTRACT(DOW FROM (f.scheduled_departure AT TIME ZONE d.timezone)) AS departure_dow,
            a.timezone AS arrival_timezone,
            f.scheduled_arrival AT TIME ZONE a.timezone AS arrival_date_time,
            EXTRACT(DOW FROM (f.scheduled_arrival AT TIME ZONE a.timezone)) AS arrival_dow
        FROM flights f
        JOIN airports_data a ON f.arrival_airport = a.airport_code
        JOIN airports_data d ON f.departure_airport = d.airport_code
    """;

    @Query(value = queryPrefix +
            """
            WHERE a.airport_code = :airportCode
                AND (:flightNumber IS NULL OR f.flight_no = :flightNumber)
                AND CAST(f.scheduled_arrival AT TIME ZONE a.timezone AS TIME) >= COALESCE(:fromTime, CAST('00:00:00' AS TIME))
                AND CAST(f.scheduled_arrival AT TIME ZONE a.timezone AS TIME) <= COALESCE(:toTime, CAST('23:59:59' AS TIME))
                AND EXTRACT(DOW FROM (f.scheduled_arrival AT TIME ZONE a.timezone)) IN (:daysOfWeek)
            """,
            nativeQuery = true)
    List<Flight> findArrivalFlights(
            @Param("airportCode") String airportCode,
            @Param("fromTime") LocalTime fromTime,
            @Param("toTime") LocalTime toTime,
            @Param("flightNumber") String flightNumber,
            @Param("daysOfWeek") Set<Integer> daysOfWeek);

    @Query(value = queryPrefix +
            """
            WHERE d.airport_code = :airportCode
                AND (:flightNumber IS NULL OR f.flight_no = :flightNumber)
                AND CAST(f.scheduled_departure AT TIME ZONE d.timezone AS TIME) >= COALESCE(:fromTime, CAST('00:00:00' AS TIME))
                AND CAST(f.scheduled_departure AT TIME ZONE d.timezone AS TIME) <= COALESCE(:toTime, CAST('23:59:59' AS TIME))
                AND EXTRACT(DOW FROM (f.scheduled_departure AT TIME ZONE d.timezone)) IN (:daysOfWeek)
            """,
            nativeQuery = true)
    List<Flight> findDepartureFlights(
            @Param("airportCode") String airportCode,
            @Param("fromTime") LocalTime fromTime,
            @Param("toTime") LocalTime toTime,
            @Param("flightNumber") String flightNumber,
            @Param("daysOfWeek") Set<Integer> daysOfWeek);

        /*
        http://localhost:8080/routes?source=LED&destination=UUS&departureDate=2016-12-11
        http://localhost:8080/routes?source=OVB&destination=%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0&departureDate=2016-12-11&maxConnections=2
        http://localhost:8080/routes?source=Moscow&destination=LED&departureDate=2016-12-11&maxConnections=2
         */

    @Query(nativeQuery = true, value = """
                 WITH RECURSIVE
                     seat_counts AS (
                         SELECT aircraft_code, COUNT(*) AS total_seats
                         FROM seats
                         WHERE fare_conditions = CAST(:fareClass AS text)
                         GROUP BY aircraft_code
                     ),
                     ticket_counts AS (
                         SELECT flight_id, COUNT(*) AS sold_seats
                         FROM ticket_flights
                         WHERE fare_conditions = CAST(:fareClass AS text)
                         GROUP BY flight_id
                     ),
                     flight_routes AS (
                     SELECT
                         f.departure_airport AS departure_airport,
                         ARRAY[jsonb_build_object(
                               'flight_id', f.flight_id,
                               'flight_no', f.flight_no,
                               'aircraft_code', f.aircraft_code,
                               'departure_airport', f.departure_airport,
                               'departure_datetime', f.scheduled_departure,
                               'arrival_airport', f.arrival_airport,
                               'arrival_datetime', f.scheduled_arrival,
                               'tickets_free', COALESCE(sc.total_seats, 0) - COALESCE(tc.sold_seats, 0)
                            )] AS route,
                         ARRAY[
                             a.city->>'en',
                             d.city->>'en'
                         ] AS visited,
                         0 AS connections,
                         f.scheduled_arrival AS last_arrival,
                         f.arrival_airport AS last_airport
                     FROM flights f
                     LEFT JOIN seat_counts sc ON sc.aircraft_code = f.aircraft_code
                     LEFT JOIN ticket_counts tc ON tc.flight_id = f.flight_id
                     JOIN airports_data AS a ON a.airport_code = f.arrival_airport
                     JOIN airports_data AS d ON d.airport_code = f.departure_airport
                     WHERE
                         f.scheduled_departure >= CAST(:departureDate AS timestamp)
                         AND f.scheduled_departure < CAST(:departureDate AS timestamp) + INTERVAL '1 day'
                         AND (f.departure_airport IN (SELECT airport_code
                             FROM airports_data
                             WHERE (LOWER(city->>'en') = LOWER(:departurePoint) OR LOWER(city->>'ru') = LOWER(:departurePoint))) OR f.departure_airport = :departurePoint)
                     UNION ALL
                     SELECT
                         fr.departure_airport AS departure_airport,
                         ARRAY_APPEND(fr.route,
                             jsonb_build_object(
                                   'flight_id', f.flight_id,
                                   'flight_no', f.flight_no,
                                   'aircraft_code', f.aircraft_code,
                                   'departure_airport', f.departure_airport,
                                   'departure_datetime', f.scheduled_departure,
                                   'arrival_airport', f.arrival_airport,
                                   'arrival_datetime', f.scheduled_arrival,
                                   'tickets_free', COALESCE(sc.total_seats, 0) - COALESCE(tc.sold_seats, 0))) AS route,
                         ARRAY_APPEND(fr.visited, a.city->>'en') AS visited,
                         fr.connections + 1 AS connections,
                         f.scheduled_arrival AS last_arrival,
                         f.arrival_airport AS last_airport
                     FROM flight_routes fr
                     JOIN airports_data AS d ON d.airport_code = fr.last_airport
                     JOIN
                         flights f ON f.scheduled_departure <= fr.last_arrival + INTERVAL '14 hour' AND (fr.last_airport = f.departure_airport OR
                             EXISTS (
                                 SELECT 1 FROM airports_data ad1
                                 JOIN airports_data ad2 ON\s
                                     ad1.city->>'en' = ad2.city->>'en'
                                 WHERE ad1.airport_code = fr.last_airport
                                 AND ad2.airport_code = f.departure_airport
                             ))
                     LEFT JOIN seat_counts sc ON sc.aircraft_code = f.aircraft_code
                     LEFT JOIN ticket_counts tc ON tc.flight_id = f.flight_id
                     JOIN airports_data AS a ON a.airport_code = f.arrival_airport
                     WHERE
                         fr.connections < :maxConnections
                         AND a.city->>'en' != ALL (visited)
                         AND
                             ((fr.last_airport = f.departure_airport AND f.scheduled_departure >= fr.last_arrival + INTERVAL '1 hour')
                             OR
                             (fr.last_airport <> f.departure_airport AND f.scheduled_departure >= fr.last_arrival + INTERVAL '5 hours'))
                        )
            
                 SELECT array_to_json(fr.route) AS fs FROM flight_routes fr WHERE fr.last_airport IN 
                      (SELECT airport_code
                             FROM airports_data
                             WHERE LOWER(city->>'en') = LOWER(:arrivalPoint) OR LOWER(city->>'ru') = LOWER(:arrivalPoint)) 
                                    OR fr.last_airport = :arrivalPoint
            ;
            """)
    List<Route> findRoutes(
            @Param("departureDate") LocalDate departureDate,
            @Param("departurePoint") String departurePoint,
            @Param("arrivalPoint") String arrivalPoint,
            @Param("maxConnections") Integer maxConnections,
            @Param("fareClass") String fareClass);


    @Query(value = "SELECT aircraft_code FROM flights WHERE flight_id = :id",
            nativeQuery = true)
    Optional<String> findAircraftCode(@Param("id") Integer id);

    @Query(value = """
            SELECT flights_price.price\s
            FROM flights_price\s
            JOIN flights ON flights.flight_no = flights_price.flight_no
            WHERE fare_conditions=:fareConditions AND flights.flight_id=:flightId;
            """, nativeQuery = true)
    Optional<Double> findPriceByFlightId(
            @Param("flightId") Integer flightId,
            @Param("fareConditions") String fareConditions);
}
