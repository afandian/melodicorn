(ns melodicorn.level-0
    (:require [melodicorn.util :refer [third fourth]]
              [melodicorn.music-theory :as theory]
              [goog.dom :as dom]
    ))

; Description of Level 0.

; Entity validation.

; Entity validators return true on success.
(defn clef [[etype clef-type]]
  (and (= etype :clef)
       (clef-type theory/clefs)))

(defn bar-line [[etype]]
  (= etype :bar-line))

(defn double-bar-line [[etype]]
  (= etype :double-bar-line))

(defn note
  "A note. Pitch-class is relative to tonic."
  [[etype pitch-class accidental octave duration-numerator duration-denominator]]
  (and (= etype :note)
       (theory/accidentals pitch-class)
       (theory/accidentals accidental)
       (integer? octave)
       (and (integer? duration-numerator) (pos? duration-numerator))
       (and (integer? duration-denominator) (pos? duration-denominator))))

(defn key-signature [[etype pitch-class accidental mode]]
  (and (= etype :key-signature)
       (theory/diatonic-pitch-classes pitch-class)
       (theory/accidentals accidental)
       (theory/modes mode)))

(def enitity-validation-dispatch
  {:clef clef
   :key-signature key-signature
   :note note
   :bar-line bar-line
   :double-bar-line double-bar-line})

(defn validate-entity
  [entity]
  (if-let [entity-type (first entity)]
    (if-let [validator (enitity-validation-dispatch entity-type)]
      (if-let [validation-result (validator entity)]
        ; Success nil.
        nil 
        ; Failure return failing entity-type.
        entity-type)
      :entity-not-recognised)
    :no-entity-type))

(defn validate-entities
  "Return list of failing entity types or rules. Empty list is success."
  [entities]
  (let [results (remove nil? (map validate-entity entities))]
    results))
        
; Structural validation

; These take a stream of Level 0 entities. Return descriptive symbol of problem nor nil on success.
(defn clef-before-first-note
    [input]
    (let [results (reductions (fn [[note clef] entity]
                    [(or note (= (first entity) :note))
                     (or clef (= (first entity) :clef))]) [false false] input)
          bad (some #{[true false]} results)]
    (when bad :note-before-clef)))

(defn key-signature-before-first-note
    [input]
    (let [results (reductions (fn [[note clef] entity]
                    [(or note (= (first entity) :note))
                     (or clef (= (first entity) :clef))]) [false false] input)
          bad (some #{[true false]} results)]      
    (when bad :note-before-key-signature)))

(defn clef-before-key-signature
    [input]
    (let [results (reductions (fn [[key-signature clef] entity]
                    [(or key-signature (= (first entity) :key-signature))
                     (or clef (= (first entity) :clef))]) [false false] input)
          bad (some #{[true false]} results)]
    (when bad :key-signature-before-clef)))

(def rules [clef-before-first-note key-signature-before-first-note clef-before-key-signature])

(defn validate-0
  "Return a list of the failing rules for a given level-0 input."
  [input]
  (let [rule-results (vec (remove nil? (map (fn [rule] (rule input)) rules)))]
    rule-results))
