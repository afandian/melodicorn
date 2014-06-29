(ns melodicorn.level-1-2
  (:require [goog.dom :as dom])
  )

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
