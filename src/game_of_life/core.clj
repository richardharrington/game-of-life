(ns game-of-life.core)

(use '[clojure.string :only [join]])


(def full-sequence [ "   " " · " " • " "(•)" ])

(def dead-to-live-anim (vec (rest full-sequence)))
(def live-to-dead-anim (vec (rest (reverse full-sequence))))

(def dead-cell-print (last live-to-dead-anim))
(def live-cell-print (last dead-to-live-anim))

(def anim-frames-num (count dead-to-live-anim))

(def cell-print-width (count live-cell-print))

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
                    (re-matches #"\S" (str (get-in grid coord-pair))))
                  (count grid)
                  (count (grid 0))))

(defn generate-random-cells [prob width height]
  (generate-cells (fn [_] (> (rand) prob)) width height))

(defn cells->print-grid [cells width height]
  (for [y (range height)
        x (range width)]
    (if (cells [x y])
      true
      false)))

(defn animate [prev-grid grid row-width millisecs]
  (let [transitions (partition row-width (map vector prev-grid grid))
        anim-time (quot millisecs anim-frames-num)
        get-rows (fn [n]
                   (apply str (map (fn [row] 
                                     (str (apply str (map (fn [transition]
                                                            (case transition
                                                              [true true] live-cell-print
                                                              [false false] dead-cell-print
                                                              [false true] (dead-to-live-anim n)
                                                              [true false] (live-to-dead-anim n)))
                                                          row))
                                          "\n"))
                                   transitions)))]
    (dorun (map (fn [n]
                  (println (apply str (repeat (* row-width cell-print-width) "-")))
                  (println (get-rows n))
                  (Thread/sleep anim-time))
                (range anim-frames-num)))))

                                                    
(defn play [width height millisecs & [initial-grid]]
  (loop [live-cells (if initial-grid
                      (text-grid->cells initial-grid)
                      (generate-random-cells 0.7 width height))
         prev-print-grid (cells->print-grid live-cells width height)]
    (let [print-grid (cells->print-grid live-cells width height)]
      (animate prev-print-grid print-grid width millisecs)
      (if (empty? live-cells)
        "That's all folks!"
        (do
          (recur (next-live-cells live-cells) print-grid))))))

(def live-cells-test-grid ["X   " 
                           "  X " 
                           "  X " 
                           " XXX"])

; #{[2 1] [2 3] [0 0] [1 3] [2 2] [3 3]})
