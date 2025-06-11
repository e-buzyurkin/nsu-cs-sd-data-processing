CREATE TABLE flights_price AS

SELECT DISTINCT f.flight_no AS flight_no,
                tf.fare_conditions AS fare_conditions,
                AVG(tf.amount) AS price,
                to_char(f.scheduled_departure, 'day') as day_of_week,
                EXTRACT(DOW FROM f.scheduled_departure) AS dow_num
FROM ticket_flights tf
         JOIN flights f ON f.flight_id = tf.flight_id
GROUP BY f.flight_no, tf.fare_conditions, day_of_week, dow_num
ORDER BY f.flight_no, tf.fare_conditions;

-- дни недели добавить