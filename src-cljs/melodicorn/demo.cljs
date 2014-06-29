(ns melodicorn.demo
  (:require [goog.dom :as dom]
            [melodicorn.level-0-1 :as level-0-1]
            [melodicorn.level-1-2 :as level-1-2]
            [melodicorn.level-2-3 :as level-2-3]
            [melodicorn.level-0 :as level-0]
            [melodicorn.canvas :as canvas]))

; Demo drawing with a canvas.

(def example-level-0 [[:clef :bass]
                      [:note 0 :no-accidental -1 1 4]
                      [:note 2 :no-accidental -1 1 4] 
                      [:note 4 :no-accidental -1 1 4]
                      [:bar-line] 
                      [:clef :treble]
                      [:note 0 :no-accidental 0 1 4]
                      [:note 2 :no-accidental 0 1 4]
                      [:note 4 :no-accidental 0 1 4]
                      [:bar-line] 
                      [:note 0 :no-accidental 1 1 4]
                      [:note 4 :no-accidental 0 1 4]
                      [:note 2 :no-accidental 0 1 4]                      
                      [:bar-line]
                      [:clef :bass]
                      [:note 0 :no-accidental 0 1 4]
                      [:note 4 :no-accidental -1 1 4]
                      [:note 2 :no-accidental -1 1 4] 
                      [:bar-line]
                      [:note 0 :no-accidental -1 1 4]
                      [:double-bar-line]
                      
                      ])
;(def example-level-0 [[:clef :treble] [:key-signature 4 :natural] [:note :d :nil 0 [1 4]] [:note :e :nil 0 [1 4]] [:note :f :nil 0 [1 4]] [:note :g :nil 0 [1 4]] [:bar-line]  [:note :a :nil 0 [1 4]] [:note :b :nil 0 [1 4]] [:note :c :nil 1 [1 4]] [:note :d :nil 1 [1 4]] [:double-bar-line]])

;(def example-level-0 [[:note :d :nil 0 [1 4]]])


(def layout-parameters {
    :page-width 1000
    :page-height 250
    :margin-left 10
    :margin-right 10
    :margin-top 50 
    :margin-bottom 15
    :trace-margins true
    :note-head-width 20
    :note-head-height 15})

(let [errors (level-0/validate-0 example-level-0)
      ok (= 0 (count errors))]
  (if (not ok)
    (.log js/console "Errors:" (str errors))
    (let [canvas (dom/getElement "the-canvas")
          graphics-context (.getContext canvas "2d")
          layer-1 (level-0-1/translate-0-1 example-level-0)
          layer-2 (level-1-2/translate-1-2 layer-1 layout-parameters)
          layer-3 (level-2-3/translate-2-3 layer-2 layout-parameters)]
          (.log js/console "Render 0 to 1:" (str layer-1))
          (.log js/console "Render 1 to 2:" (str layer-2))
          (.log js/console "Render 2 to 3:" (str layer-3))  
          (canvas/draw-3-canvas graphics-context layer-3))))