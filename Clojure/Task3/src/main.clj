(ns main)

(defn async-filter-chunk [pred chunk]
      "Функция для создания future, фильтрующая каждый набор данных"
      (future (doall (filter pred chunk))))

(defn lazy-combine-futures [processed-results remaining-futures pred]
      "Функция для ленивой обработки результатов futures"
      (if-let [remaining (seq remaining-futures)]

              (lazy-seq (lazy-cat (deref (first processed-results))
                                  (lazy-combine-futures (rest processed-results) (rest remaining) pred)))

              (apply concat (map deref processed-results))))

(defn pfilter
      "Реализация главной функции. Принимаем предикат и коллекцию и делаем свой параллельный фильтр."
      ([pred coll]
       (let [n (.availableProcessors (Runtime/getRuntime))
             chunk-size 60

             parts (map doall (partition-all chunk-size coll))

             pool (map #(async-filter-chunk pred %) parts)]

            (lazy-combine-futures pool (drop n pool) pred))))

(defn num-divisors [x]
      (count (filter (comp zero? (partial rem x)) (range 1 (inc x)))))