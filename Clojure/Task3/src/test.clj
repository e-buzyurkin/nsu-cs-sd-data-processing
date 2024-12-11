(ns test
    (:require [clojure.test :refer :all]
      [main :refer [pfilter, num-divisors]]))

(deftest test-pfilter-basic-big
         (testing
                  (is (= (pfilter even? (range 500))
                         (filter even? (range 500)))
                      "Просто чётные числа до 500"
                  )
        )
)

(deftest test-pfilter-performance-big
         (testing "Сравниваем скорость моего фильтра и библиотечного, оставляя только кратные 17 числа"
                  (println "Тест скорости на больших данных")
                  (let [test-data (range)]
                       (println "Тестирования скорости обычного фильтра:")
                       (time (doall (take 10000 (filter #(> (num-divisors %) 10) test-data))))
                       (println "Тестирования скорости моего фильтра:")
                       (time (doall (take 10000 (pfilter #(> (num-divisors %) 10) test-data)))))))



(deftest test-pfilter-lazy
         (testing "Тесты на ленивость сравнения моего фильтра и библиотечному на бесконечной последовательности"
                  (let [infinite-coll (range)
                        result (pfilter even? infinite-coll)]
                       (is (= (take 10 result)
                              (take 10 (filter even? infinite-coll)))))))

