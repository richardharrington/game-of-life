(ns game-of-life.core)

(use '[clojure.set :only [union difference]])
(use '[clojure.string :only [join]])

(def live-cells-test #{[2 1] [2 3] [0 0] [1 3] [2 2] [3 3]})

(defn neighbors [[x y]]
	(disj (set (for [i (range (- x 1) (+ x 2))
		  j (range (- y 1) (+ y 2))]
		  [i j])) [x y]))

(defn live-neighbors [cell, live-cells]
	(set (filter live-cells (neighbors cell))))

(defn dead-neighbors [cell, live-cells]
	(difference (neighbors cell) (live-neighbors cell live-cells)))

(defn num-live-neighbors
	[cell, live-cells]
	(count (live-neighbors cell live-cells)))

(defn stay-alive? [cell live-cells]
	(#{2 3} (num-live-neighbors cell live-cells)))

(defn come-alive? [cell live-cells]
	(= 3 (num-live-neighbors cell live-cells)))

(defn next-live-cells [live-cells]
	(let [all-dead-neighbors (apply union (map #(dead-neighbors % live-cells) live-cells))]
		(set (concat
			(filter #(come-alive? % live-cells) all-dead-neighbors)
			(filter #(stay-alive? % live-cells) live-cells)))))

(defn print-cells [live-cells width height]
	(println (apply str (repeat width "-")))
	(println (join "\n" (for [y (range height)]
							(apply str (for [x (range width)]
									     (if (live-cells [x y])
										    "*"
										    " ")))))))

(defn play [initial-live-cells]
	(loop [live-cells initial-live-cells]
		(when (not-empty live-cells)
			(print-cells live-cells 10 10)
			(Thread/sleep 150)
			(recur (next-live-cells live-cells)))))