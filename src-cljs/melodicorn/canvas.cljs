(ns melodicorn.canvas
  (:require [goog.dom :as dom]))

; Canvas drawing from Level 3.

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
  (.fillRect ctx x (+ y (* note-head-height 0)) width 2)
  (.fillRect ctx x (+ y (* note-head-height 1)) width 2)
  (.fillRect ctx x (+ y (* note-head-height 2)) width 2)
  (.fillRect ctx x (+ y (* note-head-height 3)) width 2)
  (.fillRect ctx x (+ y (* note-head-height 4)) width 2))

(defn draw-3-note-head
  [ctx [x y]]
  (.fillRect ctx (- x (/ note-head-width 2)) y note-head-width note-head-height))  

(defn draw-3-up-stem
  [ctx [x y]]
  (.fillRect ctx (+ x (/ note-head-width 2)) (- (+ y (/ note-head-height 2)) stem-height) 2 stem-height))

(defn draw-3-down-stem
  [ctx [x y]]
  (.fillRect ctx (+ x (/ note-head-width 2)) (+ y (/ note-head-height 2)) 2 stem-height))

(defn draw-3-bar-line
  [ctx [x y]]
  (.fillRect ctx x y 2 (* 4 note-head-height)))

(defn draw-3-double-bar-line
  [ctx [x y]]
  (.fillRect ctx x y 2 (* 4 note-head-height)
 (.fillRect ctx (+ x (/ note-head-width 4)) y 2 (* 4 note-head-height)
  )))

(defn draw-3-clef
  [ctx [x y width height]]
  (aset ctx "strokeStyle" "rgba(50,50,250,1)")
  (.strokeRect ctx x y width height))

(def draw-3-glyph-dispatch
  {:trace-box draw-3-trace-box
   :stave draw-3-stave
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