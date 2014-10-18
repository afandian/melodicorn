(ns melodicorn.canvas
  (:require [goog.dom :as dom]))

; Canvas drawing from Level 3.

; TODO move these (and access) into params
; TODO scale!
(def note-head-width 25)
(def note-head-height 15)
(def stem-height 50)

; thanks to http://stackoverflow.com/questions/2172798/how-to-draw-an-oval-in-html5-canvas
(defn drawEllipse [ctx fill? x y w h]
  (let [kappa  .5522848
        tx x ; Transform the context so new x and y will be zero.
        ty y
        ox (* (/ w 2) kappa) ; control point offset horizontal
        oy (* (/ h 2) kappa) ; control point offset vertical
        xe (+ tx w)           ; x-end
        ye (+ ty h)           ; y-end
        xm (+ tx (/ w 2))       ; x-middle
        ym (+ ty (/ h 2))       ; y-middle
] 
  (.beginPath ctx)
  (.moveTo ctx tx ym)
  (.bezierCurveTo ctx tx (- ym oy) (- xm ox) ty xm ty)
  (.bezierCurveTo ctx (+ xm ox) ty xe (- ym oy) xe ym)
  (.bezierCurveTo ctx xe (+ ym oy) (+ xm ox) ye xm ye)  
  (.bezierCurveTo ctx (- xm ox) ye tx (+ ym oy) tx ym)  
  (when fill? (.fill ctx))
  (.stroke ctx)))

(defn drawEllipseByCenter [ctx fill? cx cy w h] 
  (.save ctx)
  (.translate ctx cx cy)
  (.transform ctx 1 (Math/tan (* -10 (/ Math/PI 180))) 0 1 0 0)  
  (drawEllipse ctx true (- 0 (/ w 2.0)) (- 0 (/ h 2.0)) w h)
  (.restore ctx))

(defn position-on-stave [y]
  "Given a line-coordinate position on the stave relative to the middle line, return y position."
  (* y note-head-height))

(defn draw-3-trace-box
  [ctx [x y width height]]
  (aset ctx "strokeStyle" "rgba(50,50,250,1)")
  (.strokeRect ctx x y width height))

(defn draw-3-stave
  [ctx [x y width]]
  (.fillRect ctx x (+ y (position-on-stave -2)) width 2)
  (.fillRect ctx x (+ y (position-on-stave -1)) width 2)
  (.fillRect ctx x (+ y (position-on-stave 0)) width 2)
  (.fillRect ctx x (+ y (position-on-stave 1)) width 2)
  (.fillRect ctx x (+ y (position-on-stave 2)) width 2))


(defn draw-3-note-head
  [ctx [x y]]
  (drawEllipseByCenter ctx true x y note-head-width note-head-height))  

(defn draw-3-up-stem
  [ctx [x y]]
  (.fillRect ctx (- (+ x (/ note-head-width 2)) 2) (- y stem-height) 2 stem-height)))

(defn draw-3-down-stem
  [ctx [x y]]
  (.fillRect ctx (+ (- x (/ note-head-width 2)) 0) y 2 stem-height))

(defn draw-3-bar-line
  [ctx [x y]]
  (.fillRect ctx x (- y (* 2 note-head-height) ) 2 (* 4 note-head-height)))

(defn draw-3-double-bar-line
  [ctx [x y]]
  (.fillRect ctx x (- y (* 2 note-head-height)) 2 (* 4 note-head-height)
  (.fillRect ctx (+ x (/ note-head-width 4)) (- y (* 2 note-head-height)) 2 (* 4 note-head-height))))

(defn draw-3-clef
  [ctx [x y width height]]
  (aset ctx "strokeStyle" "rgba(0,0,0,1)")
  (.strokeRect ctx (- x (/ note-head-width 2)) (- y (/ note-head-height 2)) note-head-width note-head-height))  

(defn draw-3-key-signature
  [ctx [x y width height]]
  (aset ctx "strokeStyle" "rgba(0,0,0,1)")
  (.strokeRect ctx x y width height))

(def draw-3-glyph-dispatch
  {:trace-box draw-3-trace-box
   :stave draw-3-stave
   :key-signature draw-3-key-signature
   :clef draw-3-clef
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
    (fun context (rest glyph)))
  )

(defn draw-3-canvas
  [context layer-3-input]
  (doseq
    [_ (map (partial draw-3-glyph context) layer-3-input)]
    )
  
  )