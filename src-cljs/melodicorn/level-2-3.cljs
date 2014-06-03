(ns melodicorn.level-2-3
  (:require [melodicorn.util :as util]))

; Level 3

; Translate entities into glyphs.

; List of glyphs:
; note-head
; up-stem
; down-stem
; bar-line
; double-bar-line



; Each entity -> glyphs renders function should emit a vector.
; First argument is the vector of arguments to the entity, less the first (type) item.

(defn render-2-trace-box
  [[x y width height] layout-parameters]
  [[:trace-box x y width height]]
  )

(defn render-2-note
  [[x y] {note-head-width :note-head-width}]
  (.log js/console "render-2-note" note-head-width)
  ; TODO more args will be forthcoming.
  (if (< y 0)
    [[:note-head (* x note-head-width) y] [:down-stem (* x note-head-width) y]]
    [[:note-head (* x note-head-width) y] [:up-stem (* x note-head-width) y]]))

(defn render-2-bar-line
  [[x y] {note-head-width :note-head-width}]
  [[:bar-line (* x note-head-width) y]]
)

(defn render-2-double-bar-line
  [[x y] {note-head-width :note-head-width}]
  [[:double-bar-line (* x note-head-width) y]]
)

(def render-2-dispatch
  {:trace-box render-2-trace-box
   :note render-2-note
   :bar-line render-2-bar-line
   :double-bar-line render-2-double-bar-line})

(defn render-2-entity
  [layout-parameters [entity & args]]
  ;(.log js/console "RENDER ENTITY" (str entity) )
  (let [fun (get render-2-dispatch entity)
        result (fun args layout-parameters)]
    ;(.log js/console "RENDER ENTITY RESULT" (str result))
    result))
    
;(defn handle-2-entity-note
;  []
;  )

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
        
        ; Render the content to glyphs.
        rendered-content (map (partial render-2-entity layout-parameters) content)
        ; Each rendering will emit a vector, so concat these into a stream.
        rendered-content-flat (apply concat rendered-content)
        
        ; Create a stave for the content.
        stave [:stave x y content-width]
        
        ; Translate coordinates of entities in the stave.
        translation (partial translate-entity x y)
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
  ;(.log js/console "translate-2-3-f" entity accumulator)
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



;(.log js/console "IN" example-level-0)
;(.log js/console "OUT" (str (-> example-level-0 translate-0-1 translate-1-2 translate-2-3)))

