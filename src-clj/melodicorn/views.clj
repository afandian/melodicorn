(ns melodicorn.views
  (:require
    [hiccup
      [page :refer [html5]]
      [page :refer [include-js]]]))

(defn index-page []
  (html5
    [:head
      [:title "Melodicorn"]
    [:body
      [:canvas {:width 1400 :height 500 :id :the-canvas}]]
    (include-js "/js/main.js")]
    ))
