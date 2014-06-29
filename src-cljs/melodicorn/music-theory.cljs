(ns melodicorn.music-theory
  (:require [clojure.set :as set]))


(def major-scale [2 2 1 2 2 2 1])

(def scale-length (count major-scale))

; TODO modes can be done by cycle and take.
(def modes {:major major-scale})

(def diatonic-pitch-classes (set (range scale-length)))

; Natural not an accidental in this scope as it entails context of key-signature.
(def accidentals #{:double-sharp :sharp :no-accidental :natural :flat :double-flat})

(def accidental-modifiers {:double-flat -2
                           :flat -1
                           :no-accidental 0
                           :sharp 1
                           :double-sharp 2})

(def accidental-modifiers-inverse (set/map-invert accidental-modifiers))

(defn apply-accidental [accidental pitch]
  {:pre [(accidental accidentals)]}
  ((accidental {
      :double-flat #(- % 2)
      :flat dec
      :no-accidental identity
      :sharp inc
      :double-sharp #(+ % 2)
      }) pitch))

; Infinite sequence of chromatic pitch indexed by diatonic pitch.
; I.e. the chromatic pitches of the white keys, starting with zero.
(def major-scale-chromatic-degrees (cons 0 (reductions + (cycle major-scale))))

; TODO - will this work with other modes? Perhaps provide as argument when other modes are added.
(defn key-accidentals
  "Produce accidentals of key signature as an infinite list. Result is a vector of accidentals of [-2 -1 0 1 2] for [double-flat flat natural sharp double-sharp] indexed by position in scale."
  [degree accidental]
  {:pre [(accidental accidentals)]}
  
  (let [; The equivalent white notes that comprise this scale.
        white-notes (drop degree major-scale-chromatic-degrees)
        
        pairs (partition 2 1 white-notes)
        
        ; Intervals were these white notes played without accidentals.
        intervals-in-white-notes (map (fn [[a b]] (- b a)) pairs)
        
        ; Zip the actual intervals with the major scale intervals.
        invervals-and-scale (map vector intervals-in-white-notes (cycle major-scale))
        
        tonic-accidentalized (apply-accidental accidental 0)
        
        ; Accumulating the most recent accidental, calculate the sharps of flats needed for each degree.
        accidentals-needed (reductions (fn [last-accidental [actual target]] (- target (- actual last-accidental ))) tonic-accidentalized invervals-and-scale)]

        accidentals-needed))

(defn key-signature 
  "Produce sharps and flats for the given key signature. Return vector of accidental symbols indexed by white-note position."
  [degree accidental]
  {:pre [(accidental accidentals)]}
  (take scale-length (drop (- scale-length degree) (key-accidentals degree accidental))))

(def clefs {:treble {:position -2 }
            :bass   {:position 2  }
            })

