CREATE TABLE AS

SELECT DISTINCT flights.flight_no AS flight_no,
                ticket_flights.fare_conditions AS fare_conditions,
                AVG(ticket_flights.amount) AS price
FROM ticket_flights
         JOIN flights ON flights.flight_id = ticket_flights.flight_id
GROUP BY flights.flight_no, ticket_flights.fare_conditions
ORDER BY flights.flight_no, ticket_flights.fare_conditions;
