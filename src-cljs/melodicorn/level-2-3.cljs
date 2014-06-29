(ns melodicorn.level-2-3
  (:require [melodicorn.util :as util]
            [goog.dom :as dom]))

; Level 3

; Translate entities into glyphs at specific coordinates.

; List of glyphs:
; note-head
; up-stem
; down-stem
; bar-line
; double-bar-line
; clef
; key signature

; Each entity -> glyphs renders function should emit a vector.
; First argument is the vector of arguments to the entity, less the first (type) item.

(defn position-from-stave-coordinate [y note-head-height]
  "Take stave coordinate and return canvas coordinate position"
  (- (* (* y -1) (/ note-head-height 2)) 0))

(defn render-2-trace-box
  [[x y width height] layout-parameters]
  [[:trace-box x y width height]])

(defn render-2-note
  [[x y] {note-head-width :note-head-width note-head-height :note-head-height}]
  
  (let [head-y-pos (position-from-stave-coordinate y note-head-height)]
  ; TODO accidentals and duration.
    (if (> y 0)
      [[:note-head (* x note-head-width) head-y-pos] [:down-stem (* x note-head-width) head-y-pos]]
      [[:note-head (* x note-head-width) head-y-pos] [:up-stem (* x note-head-width) head-y-pos]])))

(defn render-2-bar-line
  [[x y] {note-head-width :note-head-width}]
  [[:bar-line (* x note-head-width) y]]
)

(defn render-2-double-bar-line
  [[x y] {note-head-width :note-head-width}]
  [[:double-bar-line (* x note-head-width) y]]
)

(defn render-2-clef
  [[x y] {note-head-width :note-head-width note-head-height :note-head-height}]
  [[:clef (* x note-head-width) (position-from-stave-coordinate y note-head-height)]])

(defn render-2-key-signature
  ; TODO 
  [[x y] {note-head-width :note-head-width}]
  [[:key-signature (* x note-head-width) y]])

(def render-2-dispatch
  {:trace-box render-2-trace-box
   :clef render-2-clef
   :key-signature render-2-key-signature
   :note render-2-note
   :bar-line render-2-bar-line
   :double-bar-line render-2-double-bar-line})

(defn render-2-entity
  [layout-parameters [entity & args]]
  
  (let [fun (get render-2-dispatch entity)
        result (fun args layout-parameters)]
    result))

(defn translate-entity
  "Affine transform of entity's x and y coordinates."
  [xx yy [entity-type x y & the-rest]]
  (concat [entity-type (+ xx x) (+ yy y)] the-rest))

; Handle the top-level items.
; Each handler function can return multiple entities, hence returning a vector.

(defn handle-2-system 
  [entity accumulator]
  (let [x (:x entity)
        y (:y entity)
        layout-parameters (:layout-params accumulator)
        content-width (- (:page-width layout-parameters) (:margin-left layout-parameters) (:margin-right layout-parameters))
        content (:content entity)
        
        ; Stave coordinates are in terms of the middle line. Offset represents top line of stave. 
        stave-y-offset (* 2 (:note-head-height layout-parameters))
        
        ; Render the content to glyphs.
        rendered-content (map (partial render-2-entity layout-parameters) content)
        ; Each rendering will emit a vector, so concat these into a stream.
        rendered-content-flat (apply concat rendered-content)
        
        ; Create a stave for the content.
        stave [:stave x (+ y stave-y-offset) content-width]
        
        ; Translate coordinates of entities in the stave.
        translation (partial translate-entity x (+ y stave-y-offset))
        translated-entities (map translation rendered-content-flat)]
    
  [(conj translated-entities stave) accumulator])
)

(defn handle-2-trace-box
  [{x :x y :y width :width height :height} accumulator]
  [[[:trace-box x y width height]] accumulator])

(def translate-2-3-f-dispatch
  {:system handle-2-system
   :trace-box handle-2-trace-box})

(defn translate-2-3-f
  [entity accumulator]

  (let [entity-type (:type entity)
        func (get translate-2-3-f-dispatch entity-type)
        result (func entity accumulator)
        [entity accumulator] result]
    [entity accumulator]
    ))


(defn translate-2-3
  "Translate from Level 2 to Level 3"
  [input layout-parameters]
  (let [initial-accumulator {:x 0 :y 0 :layout-params layout-parameters}]
  (apply concat (util/map-with-accumulator translate-2-3-f initial-accumulator input))))



