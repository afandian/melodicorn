(ns melodicorn.util)

; Utilities 

(defn map-with-accumulator
  "Map over input but with an accumulator. func accepts [value, accumulator] and returns the same."
  [func accumulator collection]
  (if (empty? collection)
    nil
    (let [[this-value new-accumulator] (func (first collection) accumulator)]
      (cons this-value (map-with-accumulator func new-accumulator (rest collection))))))

(defn acc-apply
  "Apply function to key of accumulator."
  [accumulator k fun]
  (into accumulator {k (fun (k accumulator))}))

(def inc-2 #(+ 2 %))
