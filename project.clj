(defproject melodicorn "0.0.1"
  :description "Something to do with editing or typesetting musical scores"
  :source-paths ["src-clj"]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2371"
                  ;:exclusions [org.apache.ant/ant]
                  ]
                 [compojure "1.1.6"]
                 [hiccup "1.0.4"]]
  :plugins [[lein-cljsbuild "1.0.3"]
            [lein-ring "0.8.7"]]
  :cljsbuild {
    :builds [{:source-paths ["src-cljs"]
              :compiler {:output-to "resources/public/js/main.js"
                         ;:optimizations :simple ;:simple ; :advanced ;nil; :whitespace ; :advanced
                         ;:pretty-print true
                         }}]}
  :ring {:handler melodicorn.routes/app})
