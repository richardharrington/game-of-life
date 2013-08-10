(ns game-of-life.core
  (:use [clojure.core.match :only [match]]))

(def full-sequence [ "   " " · " " • " "(•)" ])

(def dead-to-live-anim (vec (rest full-sequence)))
(def live-to-dead-anim (vec (rest (reverse full-sequence))))

(def dead-cell-print (last live-to-dead-anim))
(def live-cell-print (last dead-to-live-anim))

(def anim-frames-num (count dead-to-live-anim))

(def cell-print-width (count live-cell-print))

(defn next-live-cells [live-cells]
  (let [neighbors (fn [[x y]]
                    (set (for [dx [-1 0 1]
                               dy [-1 0 1]]
                           [(+ x dx) (+ y dy)])))
        count-live-neighbors-matches (fn [pred]
                                       #(pred (count (filter live-cells (neighbors %)))))
        dead-cells-nearby (filter (complement live-cells) 
                                  (distinct (mapcat neighbors live-cells)))]
    (set (concat
           (filter (count-live-neighbors-matches #{3 4}) live-cells)
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
    (cells [x y])))

(defn animate [prev-grid grid row-width millisecs game-over?] 
  (let [transitions (partition row-width (map vector prev-grid grid))
        anim-time (quot millisecs anim-frames-num)
        header-footer (apply str (repeat (* row-width cell-print-width) "-"))
        get-rows (fn [n]
                   (let [dead-to-live-cell-print (dead-to-live-anim n)
                         live-to-dead-cell-print (live-to-dead-anim n)]
                     (apply str (map (fn [row] 
                                       (str (apply str (map (fn [transition]
                                                              (match [transition]
                                                                 [[nil nil]] dead-cell-print
                                                                 [[nil _]]   dead-to-live-cell-print
                                                                 [[_ nil]]   live-to-dead-cell-print
                                                                 :else       live-cell-print))
                                                            row))
                                            "\n"))
                                     transitions))))]
    (doseq [n (range anim-frames-num)]
      (println header-footer)
      (print (get-rows n))
      (println header-footer)
      (when (or (not game-over?) (< n (dec anim-frames-num)))
        (println) 
        (Thread/sleep anim-time)))))

(defn play [width height millisecs & [initial-grid]]
  (loop [live-cells (if initial-grid
                      (text-grid->cells initial-grid)
                      (generate-random-cells 0.7 width height))
         prev-print-grid (cells->print-grid live-cells width height)]
    (let [print-grid (cells->print-grid live-cells width height)
          game-over? (empty? live-cells)]
      (animate prev-print-grid print-grid width millisecs game-over?)
      (if game-over?
        "That's all folks!"
        (recur (next-live-cells live-cells) print-grid)))))

(def live-cells-test-grid ["     " 
                           " X   " 
                           "   X " 
                           "   X " 
                           "  XXX"])

; #{[2 1] [2 3] [0 0] [1 3] [2 2] [3 3]})
