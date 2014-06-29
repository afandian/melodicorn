; As an infinite list.
;(def major-scale-chromatic-degrees-inf (lazy-cat major-scale-chromatic-degrees major-scale-chromatic-degrees-inf))

; Three octaves of this. Hacky way of dealing with negative indicies.
;(def major-scale-three-octaves (apply vector (take (* 3 num-chromatic-pitch-classes) major-scale-chromatic-degrees-inf)))

; Sequence of octaves in major scale, rising in chromatic pitch classes.
;(def major-scale-in-octaves (map
;                              (fn [scale octave]
;                                (map #(+ % (* octave 12)) scale))
;                              (repeat major-scale-chromatic-degrees)
;                              (range)))

; Three octaves of this. Hacky way of dealing with negative indicies. Should be possible to lazily concatenate an infinite series.
;(def major-scale-three-octaves (apply concat (take 3 major-scale-in-octaves)))

; The degrees of the a major scale.
;(def num-white-notes (inc (count major-scale)))
;(def white-note-degrees (apply vector (range 0 num-white-notes)))
;(def white-note-degrees-inf (lazy-cat white-note-degrees white-note-degrees-inf))

;(defn recurse [white-notes]
;  (let [head (first white-notes)
;        tail (rest white-notes)]
;
;      (if (nil? head) [] (cons head (recurse tail)))
;    
;    )
;  
;  )




;(def diatonic-pitch-classes (range 0 7))
;(def diatonic-pitch-classes-loop (lazy-cat diatonic-pitch-classes diatonic-pitch-classes-loop))
;
;(def chromatic-pitch-classes (range 0 12))
;(def chromatic-pitch-classes-loop (lazy-cat chromatic-pitch-classes chromatic-pitch-classes-loop))
;
;(def cycle-of-fifths (take-nth 7 chromatic-pitch-classes-loop))
;