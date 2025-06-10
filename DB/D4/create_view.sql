create view flight_prices as 
select 
        f.flight_no as "flight_no", 
        tf.fare_conditions as "class", 
        to_char(f.scheduled_departure, 'HH24:MI') as "day", 
        tf.amount as "price"
from flights f                                                 
inner join ticket_flights tf                             
on f.flight_id = tf.flight_id                                                                                      
order by tf.amount;
