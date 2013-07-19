(ns game-of-life.core)

(use '[clojure.string :only [join]])

(def live-cells-test #{[2 1] [2 3] [0 0] [1 3] [2 2] [3 3]})

(defn neighbors [[x y]]
  (disj (set (for [i (range (- x 1) (+ x 2))
                   j (range (- y 1) (+ y 2))]
               [i j])) 
        [x y]))

(defn next-live-cells [live-cells]
  (let [all-dead-neighbors (filter (complement live-cells) (distinct (mapcat neighbors live-cells)))                                       
        live-neighbors #(filter live-cells (neighbors %))
        come-alive? #(#{3} (count (live-neighbors %)))
        stay-alive? #(#{2 3} (count (live-neighbors %)))]
    (set (concat
           (filter come-alive? all-dead-neighbors)
           (filter stay-alive? live-cells)))))

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
