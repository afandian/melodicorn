(ns melodicorn.core
  (:require [goog.dom :as dom]))


;(.log js/console "HELP")

; Naming

; There are four layers, described below.
; Broadly: 
; Layer 0 is 'semantic'.
; Layer 1 is the music laid out on an infinite stave.
; Layer 2 is the staves chopped up, with absolute positions.
; Layer 3 is a stream of glyphs which can be drawn.

; Functions with two numbers, e.g. translate-0-1 transition between those two layers.
; Functions with one number take that layer as an input.

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



(def example-level-0 [[:note :d :nil 0 [1 4]] [:note :e :nil 0 [1 4]] [:note :f :nil 0 [1 4]] [:note :g :nil 0 [1 4]] [:bar-line]  [:note :a :nil 0 [1 4]] [:note :b :nil 0 [1 4]] [:note :c :nil 1 [1 4]] [:note :d :nil 1 [1 4]] [:double-bar-line]])

;(def example-level-0 [[:note :d :nil 0 [1 4]]])s

; Level 1
; This is a sequence of entities.
; Measurements in 'em's, i.e. standard element widths.
; Coordiates expressed in terms of x offset from left-hand-side of the stave, y in terms of the centre line.

; Accumulator
; This is a 'state', carried forward between invocations of translate-0-1-f
; Keys:
; :x - the x offset, measured in em 

(def initial-translate-0-1-accumulator
  {:x 1})

; Entity handling functions
; Return [entity accumulator]
; Entity is [entity-type x y args]

(defn handle-1-note
  [[pitch-class accidentals octave duration-numerator duration-denominator] accumulator]
  ; TODO - pitch, accidentals, everything really.
  [[:note (:x accumulator) 0]
   (acc-apply accumulator :x inc-2)])

(defn handle-1-bar
  [_ accumulator]
  [[:bar-line (:x accumulator) 0]
   (acc-apply accumulator :x inc-2)])


(defn handle-1-double-bar
  [_ accumulator]
  [[:double-bar-line (:x accumulator) 0]
   (acc-apply accumulator :x inc-2)])

(def dispatch-0-1
  "Mapping of element type to handler function. Function will take element args and accumulator, return same."
  {:note handle-1-note
   :bar-line handle-1-bar
   :double-bar-line handle-1-double-bar})

(defn translate-0-1-f
  "This is applied to each element in the input. It stores state between executions in the accumulator."
  [[el-type & args] accumulator]
  ;(.log js/console "translate-0-1-f input" (str [el-type args accumulator]))
  (let [dispatch-f (get dispatch-0-1 el-type)
        result (when dispatch-f (dispatch-f args accumulator))]
    ; If the key didn't exist in the dispatch table, result will be nil.
    ; This shouldn't happen, but here's where that's handled.
    ;(.log js/console "translate-0-1-f result" (str result))
    (if result
      result
      [nil accumulator])))


(defn translate-0-1 
    "Translate from Level 0 to Level 1"
    [input]
    (map-with-accumulator translate-0-1-f initial-translate-0-1-accumulator input))

;(.log js/console "IN" example-level-0)
;(.log js/console "OUT" (str (translate-0-1 example-level-0)))


; Level 2

; Level-1-2 takes a stave of infinite width and chops it up.
; Level 2 describes the positions of systems.

; Level 2 is a vector of top-level entities. Each is a map with :x, :y, :type and :content.
; Types:
; :system - a chopped up section of Level 2 stave
; :trace-box - a box, for tracing things.
; Other types might be title or page number.

; Each system's coords expressed relative to page (i.e. absolute)
; Within each system each note position is expressed as x from left hande side of system, y from centre line.

; Layout Parameters
; :page-width - width in logical units
; :margin-left - margin-left in logical units
; :margin-right - margin-right in logical units
; :margin-top - margin-top in logical units
; :margin-bottom - margin-bottom in logical units
; :trace-margins - show margins

(defn translate-1-2
  "Translate from Level 1 to Level 2"
  [input {page-width :page-width
          page-height :page-height
          margin-left :margin-left
          margin-right :margin-right
          margin-top :margin-top
          margin-bottom :margin-bottom
          trace-margins :trace-margins
          }]
  ;(.log js/console "TM" trace-margins)
  ; TODO validate inputs (width - margins > 0 etc)
  (let [usable-width (- page-width margin-left margin-right)
        usable-height (- page-height margin-top margin-bottom)
    
        margin-boxes (when trace-margins
                        [{:type :trace-box
                          :x margin-left
                          :y margin-top
                          :width usable-width
                          :height usable-height}
                         {:type :trace-box
                          :x 1
                          :y 1
                          :width (- page-width 1)
                          :height (- page-height 1)}])
    
        ; For now just pass straight through.
        ; TODO: actually split up.    
        the-systems [{:type :system
          :x margin-left
          :y margin-top
          :content input}] ]
        (concat margin-boxes the-systems)))
  
  

; TODO justification of elements.

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
  (apply concat (map-with-accumulator translate-2-3-f initial-accumulator input))))



;(.log js/console "IN" example-level-0)
;(.log js/console "OUT" (str (-> example-level-0 translate-0-1 translate-1-2 translate-2-3)))




; Layer 4, Canvas drawing

; TODO move these (and access) into params
; TODO scale!
(def note-head-width 20)
(def note-head-height 15)
(def stem-height 50)

(defn draw-3-trace-box
  [ctx [x y width height]]
  (aset ctx "strokeStyle" "rgba(50,50,250,1)")
  (.strokeRect ctx x y width height))

(defn draw-3-stave
  [ctx [x y width]]
  ;(.log js/console "STAVE"  ctx x (+ y (* note-head-height 0)) 200 2 )
  ;(.log js/console "CTX" ctx)
  (.fillRect ctx x (+ y (* note-head-height 0)) width 2)
  (.fillRect ctx x (+ y (* note-head-height 1)) width 2)
  (.fillRect ctx x (+ y (* note-head-height 2)) width 2)
  (.fillRect ctx x (+ y (* note-head-height 3)) width 2)
  (.fillRect ctx x (+ y (* note-head-height 4)) width 2))

(defn draw-3-note-head
  [ctx [x y]]
  (.fillRect ctx (- x (/ note-head-width 2)) y note-head-width note-head-height)
  ;(.beginPath ctx)
  ;(.arc ctx
  ;      (- x (/ note-head-width 2))
  ;      (+ y (/ note-head-height 2))
  ;      note-head-width
  ;      note-head-height
  ;      0 (* 2 js/Math.PI)
  ;      false)
  ;(.fill ctx)
  )  


(defn draw-3-up-stem
  [ctx [x y]]
  (.fillRect ctx (+ x (/ note-head-width 2)) (- (+ y (/ note-head-height 2)) stem-height) 2 stem-height)
  )

(defn draw-3-down-stem
  [ctx [x y]]
  (.fillRect ctx (+ x (/ note-head-width 2)) (+ y (/ note-head-height 2)) 2 stem-height)
  )

(defn draw-3-bar-line
  [ctx [x y]]
  (.fillRect ctx x y 2 (* 4 note-head-height))
  )

(defn draw-3-double-bar-line
  [ctx [x y]]
  (.fillRect ctx x y 2 (* 4 note-head-height)
 (.fillRect ctx (+ x (/ note-head-width 4)) y 2 (* 4 note-head-height)
  )))

(def draw-3-glyph-dispatch
  {:trace-box draw-3-trace-box
   :stave draw-3-stave
   :note-head draw-3-note-head
   :up-stem draw-3-up-stem
   :down-stem draw-3-down-stem
   :bar-line draw-3-bar-line
   :double-bar-line draw-3-double-bar-line})

(defn draw-3-glyph
  [context glyph]
  (let [glyph-type (first glyph)
        fun (get draw-3-glyph-dispatch glyph-type)
        ]
    (.log js/console "GLYPH" (str glyph-type) (str (rest glyph)))
    (fun context (rest glyph)))
  )

(defn draw-3-canvas
  [context layer-3-input]
  (doseq
    [_ (map (partial draw-3-glyph context) layer-3-input)]
    )
  
  )

(def layout-parameters {
    :page-width 500
    :page-height 250
    :margin-left 10
    :margin-right 10
    :margin-top 50 
    :margin-bottom 15
    :trace-margins true
    :note-head-width 20
    :note-head-height 15
    })


(let [canvas (dom/getElement "the-canvas")
      graphics-context (.getContext canvas "2d")
      layer-1 (translate-0-1 example-level-0)
      layer-2 (translate-1-2 layer-1 layout-parameters)
      layer-3 (translate-2-3 layer-2 layout-parameters)
      ]
  (.log js/console "Rendering layer-1 to canvas:" (str layer-1))
  (.log js/console "Rendering layer-2 to canvas:" (str layer-2))
  (.log js/console "Rendering layer-3 to canvas:" (str layer-3))
  
      (draw-3-canvas graphics-context layer-3))




