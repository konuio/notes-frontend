(defproject notes-frontend "1.0.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [org.clojure/core.async "0.2.374"]
                 [cljsjs/react "0.14.0-1"]
                 [cljsjs/react-dom "0.14.0-1"]
                 [sablono "0.4.0"]
                 [org.omcljs/om "0.9.0"]
                 [shodan "0.4.2"]
                 [devcards "0.2.1"]
                 [binaryage/devtools "0.4.1"]
                 [funcool/promesa "0.5.1"]
                 [funcool/cats "1.0.0"]
                 [secretary "1.2.3"]
                 [ring "1.4.0"]
                 [compojure "1.4.0"]]

  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-figwheel "0.5.0-1"]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src-cljs"]
                :figwheel {}
                :compiler {:main notes-frontend.dev
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/notes_frontend.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true}}

               {:id "prod"
                :source-paths ["src-cljs"]
                :figwheel {}
                :compiler {:main notes-frontend.prod
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/notes_frontend.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true}}

               {:id "devcards"
                :source-paths ["src-cljs"]
                :figwheel {:devcards true}
                :compiler {:main notes-frontend.cards.core
                           :asset-path "js/compiled/devcards_out"
                           :output-to "resources/public/js/compiled/notes_frontend_devcards.js"
                           :output-dir "resources/public/js/compiled/devcards_out"
                           :source-map-timestamp true}}

               ;; This next build is an compressed minified build for
               ;; production. You can build this with:
               ;; lein cljsbuild once min
               {:id "min"
                :source-paths ["src-cljs"]
                :compiler {:output-to "resources/public/js/compiled/notes_frontend.js"
                           :main notes-frontend.core
                           :optimizations :advanced
                           :pretty-print false}}]}

  :figwheel {:server-port 8888
             :css-dirs ["resources/public/css"]
             :ring-handler notes-frontend.server/handler})
