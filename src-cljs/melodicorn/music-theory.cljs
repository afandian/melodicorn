(ns melodicorn.music-theory)


(def major-scale [2 2 1 2 2 2 1])

(def scale-length (count major-scale))

(def accidentals #{:double-sharp :sharp :natural :flat :double-flat})

(defn apply-accidental [accidental pitch]
  {:pre [(accidental accidentals)]}
  ((accidental {:double-flat #(- % 2) :flat dec :natural identity :sharp inc :double-sharp #(+ % 2)}) pitch))

; Infinite sequence of chromatic pitch indexed by diatonic pitch.
; I.e. the chromatic pitches of the white keys, starting with zero.
(def major-scale-chromatic-degrees (cons 0 (reductions + (cycle major-scale))))

(defn key-signature
  "Produce one octave of the major scale in the given key. Result is a vector of accidentals of [-2 -1 0 1 2] for [double-flat flat natural sharp double-sharp]"
  [degree accidental]
  {:pre [(accidental accidentals)]}
  
  (let [; The equivalent white notes that comprise this scale.
        white-notes (take scale-length (drop degree major-scale-chromatic-degrees))
        
        pairs (partition 2 1 white-notes)
        
        ; Intervals were these white notes played without accidentals.
        intervals-in-white-notes (map (fn [[a b]] (- b a)) (partition 2 1 white-notes))
        
        ; Zip the actual intervals with the major scale intervals.
        invervals-and-scale (map vector intervals-in-white-notes major-scale)
        
        tonic-accidentalized (apply-accidental accidental 0)
        
        ; Accumulating the most recent accidental, calculate the sharps of flats needed for each degree.
        accidentals-needed (reductions (fn [last-accidental [actual target]] (- target (- actual last-accidental ))) tonic-accidentalized invervals-and-scale)]

        accidentals-needed))

