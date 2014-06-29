(ns melodicorn.level-0-1
  (:require [melodicorn.util :as util]
            [melodicorn.music-theory :as theory]
            [goog.dom :as dom])
  )

; Level 0
; This is the pure semantic representation of the music.
; Entities: 
; [:note :bar-line :double-bar-line]

; Durations
; Because this has to work with clojurescript, the Rational number system can't be used.
; So numerator and denomenator are split.

; Notes
; [:note pitch-class accidental octave duration-numerator duration-denominator]

; Pitches
; Expressed as pitch-class (one of [:a :b :c :d :e :f :g]), accidental and octave (first octave above middle-C is 0).

; Sharps and flats
; :double-flat, :flat, :natural, :nil, :sharp, :double-sharp
; If :nil, inferred from the key signature.

; Level 1
; This is a sequence of entities.
; Measurements in 'em's, i.e. standard element widths.
; Coordiates expressed in terms of x offset from left-hand-side of the stave, y in terms of the centre line.


; Accumulator
; This is a 'state', carried forward between invocations of translate-0-1-f
; Keys:
; :x - the x offset, measured in device independent pixels.

(def initial-translate-0-1-accumulator
  {:x 1})

; Entity handling functions
; Return [entity accumulator]
; Entity is [entity-type x y args]

(defn handle-0-key-signature
  [[etype pitch-class accidental mode] accumulator]
  ; TODO throwing away mode for now.
  
  (let [key-signature (theory/key-signature pitch-class accidental)
        with-index (map vector key-signature (range))
        
        ]
   )
  
  ; TODO - pitch, accidentals, everything really.
  [[:note (:x accumulator) 0]
   (util/acc-apply accumulator :x util/inc-2)])

(defn handle-0-clef
  [[etype clef-type] accumulator]
  
  ; TODO - everything
  [[:clef (:x accumulator) 0]
   (util/acc-apply accumulator :x util/inc-2)])



(defn handle-0-note
  [[pitch-class accidentals octave duration-numerator duration-denominator] accumulator]
  ; TODO - pitch, accidentals, everything really.
  [[:note (:x accumulator) 0]
   (util/acc-apply accumulator :x util/inc-2)])

(defn handle-0-bar
  [_ accumulator]
  [[:bar-line (:x accumulator) 0]
   (util/acc-apply accumulator :x util/inc-2)])

(defn handle-0-double-bar
  [_ accumulator]
  [[:double-bar-line (:x accumulator) 0]
   (util/acc-apply accumulator :x util/inc-2)])

(def dispatch-0-1
  "Mapping of element type to handler function. Function will take element args and accumulator, return same."
  {:key-signature handle-0-key-signature
   :clef handle-0-clef
   :note handle-0-note
   :bar-line handle-0-bar
   :double-bar-line handle-0-double-bar})

(defn translate-0-1-f
  "This is applied to each element in the input. It stores state between executions in the accumulator."
  [element accumulator]
  (let [dispatch-f (get dispatch-0-1 (first element))
        result (when dispatch-f (dispatch-f element accumulator))]
    ; If the key didn't exist in the dispatch table, result will be nil.
    ; This shouldn't happen, but here's where that's handled.
    (if result
      result
      [nil accumulator])))


(defn translate-0-1 
    "Translate from Level 0 to Level 1"
    [input]
    (util/map-with-accumulator translate-0-1-f initial-translate-0-1-accumulator input))




