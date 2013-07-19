(ns game-of-life.core)

(def f (fn 
         "This function passes the game-of-life tests on 4clojure."
         [grid]
         (let [live-cells (set (for [y (range (count grid)), x (range (count (grid y))) :when (= (get-in grid [y x]) \#)]
                                 [x y]))
               height (count grid)
               width (count (grid 0))
               neighbors (fn [[x y]]
                           (disj (set (for [i (range (- x 1) (+ x 2))
                                            j (range (- y 1) (+ y 2))]
                                        [i j])) 
                                 [x y]))
               count-live-neighbors-matches (fn [pred]
                                              #(pred (count (filter live-cells (neighbors %)))))
               dead-cells-nearby (filter (complement live-cells) 
                                         (distinct (mapcat neighbors live-cells)))
               next-live-cells (set (concat
                                      (filter (count-live-neighbors-matches #{2 3}) live-cells)
                                      (filter (count-live-neighbors-matches #{3}) dead-cells-nearby)))]
           (for [y (range height)]
             (apply str (for [x (range width)]
                          (if (next-live-cells [x y])
                            "#"
                            " ")))))))

