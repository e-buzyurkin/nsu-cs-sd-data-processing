(ns task_c6)

;;;an empty route map
;;;it is enough to use either forward or backward part (they correspond to each other including shared reference to number of tickets)
;;;:forward is a map with route start point names as keys and nested map as values
;;;each nested map has route end point names as keys and route descriptor as values
;;;each route descriptor is a map (structure in fact) of the fixed structure where
;;;:price contains ticket price
;;;and :tickets contains reference to tickets number
;;;:backward has the same structure but start and end points are reverted
(def empty-map
  {:forward {},
   :backward {}})

(defn route
      "Add a new route (route) to the given route map
       route-map - route map to modify
       from - name (string) of the start point of the route
       to - name (string) of the end point of the route
       price - ticket price
       tickets-num - number of tickets available"
      [route-map from to price tickets-num]
      (let [tickets (ref tickets-num :validator (fn [state] (>= state 0))),     ;reference for the number of tickets
            orig-source-desc (or (get-in route-map [:forward from]) {}),
            orig-reverse-dest-desc (or (get-in route-map [:backward to]) {}),
            route-desc {:price price,                                            ;route descriptor
                        :tickets tickets},
            source-desc (assoc orig-source-desc to route-desc),
            reverse-dest-desc (assoc orig-reverse-dest-desc from route-desc)]
           (-> route-map
               (assoc-in [:forward from] source-desc)
               (assoc-in [:backward to] reverse-dest-desc))))


(defn dijkstra
      "Реализует алгоритм Дейкстры для поиска кратчайшего пути и восстановления маршрута."
      [route-map from to]
      (let [distances (atom (zipmap (keys (route-map :forward)) (repeat Integer/MAX_VALUE)))
            predecessors (atom {})
            queue (atom [[from 0]])] ;; Простая очередь в виде вектора пар [город, расстояние]

           ;; Инициализация начального расстояния
           (swap! distances assoc from 0)

           (while (not (empty? @queue))
                  (let [[current distance] (first (sort-by second @queue))]
                       (swap! queue #(remove #{[current distance]} %)) ;; Удаляем текущий элемент из очереди

                       ;; Обновляем соседей
                       (doseq [[neighbor {:keys [price tickets]}] (get-in route-map [:forward current])]
                              (when (> @tickets 0)
                                    (let [new-distance (+ distance price)]
                                    (when (< new-distance (@distances neighbor))
                                          ;; Обновляем минимальное расстояние и предшественника
                                          (swap! distances assoc neighbor new-distance)
                                          (swap! predecessors assoc neighbor current)
                                          ;; Добавляем соседа в очередь
                                          (swap! queue conj [neighbor new-distance])))))))

           ;; Восстановление пути
           (if (< (@distances to) Integer/MAX_VALUE)
             (let [path (loop [current to path []]
                              (if current
                                (recur (@predecessors current) (conj path current))
                                (reverse path)))]
                  ;; Уменьшаем количество билетов на пути
                  (doseq [[from to] (partition 2 1 path)]   ; (partition 2 1 [:a :b :c :d]) ==> ([:a :b] [:b :c] [:c :d])
                         (alter (get-in route-map [:forward from to :tickets]) dec))
                  {:path path, :price (@distances to)})
             nil)))


(def transactions-starts (atom 0))
(def transactions-finishes (atom 0))

(defn book-tickets
      "Tries to book tickets and decrement appropriate references in route-map atomically
       returns map with either :price (for the whole route) and :path (a list of destination names) keys
              or with :error key that indicates that booking is impossible due to lack of tickets"
      [route-map from to]
      (if (= from to)
        {:path '(), :price 0}
        (do
          (swap! transactions-starts inc)
          (let [result (dosync
                         (let [path (dijkstra route-map from to)]
                              (if path path {:error -1})))]
               (swap! transactions-finishes inc)
               result))))




;;;cities
(def spec1 (-> empty-map
               (route "City1" "Capital"    200 5)
               (route "Capital" "City1"    250 5)
               (route "City2" "Capital"    200 5)
               (route "Capital" "City2"    250 5)
               (route "City3" "Capital"    300 3)
               (route "Capital" "City3"    400 3)
               (route "City1" "Town1_X"    50 2)
               (route "Town1_X" "City1"    150 2)
               (route "Town1_X" "TownX_2"  50 2)
               (route "TownX_2" "Town1_X"  150 2)
               (route "Town1_X" "TownX_2"  50 2)
               (route "TownX_2" "City2"    50 3)
               (route "City2" "TownX_2"    150 3)
               (route "City2" "Town2_3"    50 2)
               (route "Town2_3" "City2"    150 2)
               (route "Town2_3" "City3"    50 3)
               (route "City3" "Town2_3"    150 2)))

(defn booking-future [route-map from to init-delay loop-delay]
      (future
        (Thread/sleep init-delay)
        (loop [bookings []]
              (Thread/sleep loop-delay)
              (let [booking (book-tickets route-map from to)]
                   (if (booking :error)
                     bookings
                     (recur (conj bookings booking)))))))

(defn print-bookings [name ft]
      (println (str name ":") (count ft) "bookings")
      (doseq [booking ft]
             (println "price:" (booking :price) "path:" (booking :path) )))

(defn run []
      ;;try to tune timeouts in order to all the customers gain at least one booking
      (let [f1 (booking-future spec1 "City1" "City3" 700 1),
            f2 (booking-future spec1 "City1" "City2" 300 10),
            f3 (booking-future spec1 "City2" "City3" 500 1)]
           (print-bookings "City1->City3" @f1)
           (print-bookings "City1->City2" @f2)
           (print-bookings "City2->City3" @f3)
           (println "Total starts:" @transactions-starts)
           (println "Total restarts:" (- @transactions-starts @transactions-finishes))
           ))

(run)