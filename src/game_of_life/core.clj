(ns game-of-life.core)

(use '[clojure.string :only [join]])

(def live-char \M)
(def dead-char \space)


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

(defn generate-cells [pred width height]
  (set (for [y (range height)
             x (range width)
             :when (pred [y x])]
         [x y])))

(defn text-grid->cells [grid]
  (generate-cells (fn [coord-pair]
                    (#{live-char} (get-in grid coord-pair)))
                  (count grid)
                  (count (grid 0))))

(defn generate-random-cells [prob width height]
  (generate-cells (fn [_] (> (rand) prob)) width height))

(defn cells->text-grid [cells width height]
  (for [y (range height)]
    (apply str (for [x (range width)]
                 (if (cells [x y])
                  live-char
                  dead-char)))))

(defn print-cells [cells width height]
  (println (apply str (repeat width "-")))
  (println (join "\n" (cells->text-grid cells width height))))

(defn play [width height millisecs & [initial-grid]]
  (loop [live-cells (if initial-grid
                      (text-grid->cells initial-grid)
                      (generate-random-cells 0.7 width height))]
    (print-cells live-cells width height)
    (if (empty? live-cells)
      "That's all folks!"
      (do
        (println)
        (Thread/sleep millisecs)
        (recur (next-live-cells live-cells))))))

(def live-cells-test-grid ["M   " 
                           "  M " 
                           "  M " 
                           " MMM"])

; #{[2 1] [2 3] [0 0] [1 3] [2 2] [3 3]})
