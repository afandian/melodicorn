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
      [:h1 "Melodicorn"]
      [:canvas {:width 500 :height 500 :id :the-canvas}]]
    (include-js "/js/main.js")]
    ))
