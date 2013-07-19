(ns game-of-life.core)

(use '[clojure.string :only [join]])

(def live-cells-test #{[2 1] [2 3] [0 0] [1 3] [2 2] [3 3]})



(defn next-live-cells [live-cells]
  (let [neighbors (fn [[x y]]
                    (disj (set (for [i (range (- x 1) (+ x 2))
                                     j (range (- y 1) (+ y 2))]
                                 [i j])) 
                          [x y]))
        count-live-neighbors-matches (fn [pred]
                                       #(pred (count (filter live-cells (neighbors %)))))
        dead-cells-nearby (filter (complement live-cells) 
                                       (distinct (mapcat neighbors live-cells)))]
    (set (concat
           (filter (count-live-neighbors-matches #{2 3}) live-cells)
           (filter (count-live-neighbors-matches #{3}) dead-cells-nearby)))))

(defn print-cells [live-cells width height]
  (println (apply str (repeat width "-")))
  (println (join "\n" (for [y (range height)]
                        (apply str (for [x (range width)]
                                     (if (live-cells [x y])
                                       "#"
                                       " ")))))))

(defn play [initial-live-cells width height millisecs]
  (loop [live-cells initial-live-cells]
    (when (not-empty live-cells)
      (print-cells live-cells width height)
      (Thread/sleep millisecs)
      (recur (next-live-cells live-cells)))))

