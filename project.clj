(defproject storybuilder "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [reagent "0.5.1"]
                 [re-frame "0.7.0"]
                 [re-com "0.8.0"]
                 [cljs-ajax "0.5.4"]
                 [eigenhombre/namejen "0.1.12"]
                 [cljsjs/codemirror "5.11.0-1"]
                 [cljsjs/chance "0.7.3-0"]
                 [compojure "1.5.0"]
                 [com.novemberain/monger "3.0.2"]
                 [me.raynes/conch "0.8.0"]
                 [ring/ring-defaults "0.2.0"]
                 [ring/ring-json "0.4.0"]
                 [tropic "0.6.3"]
                 [cljsjs/vis "4.17.0-0"]
                 [com.lucasbradstreet/instaparse-cljs "1.4.1.1"]
                 [figwheel-sidecar "0.5.8"]
                 [com.cemerick/piggieback "0.2.1"]
                 [ring "1.4.0"]]

  :min-lein-version "2.5.3"

  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :source-paths ["src/clj"
                 "cljs_src"]

  :plugins [[lein-cljsbuild "1.1.3"]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :figwheel {:css-dirs ["resources/public/css"]
             :ring-handler storybuilder.handler/app}

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/cljs"]
                        :figwheel {:on-jsload "storybuilder.core/mount-root"}
                        :compiler {:main storybuilder.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :externs ["resources/public/js/cytoscape.min.js"]
                                   :output-dir "resources/public/js/compiled/out"
                                   :asset-path "js/compiled/out"
                                   :source-map-timestamp true}}

                       {:id "min"
                        :source-paths ["src/cljs"]
                        :compiler {:main storybuilder.core
                                   :output-to "resources/public/js/compiled/app.js"
                                   :externs ["resources/public/js/cytoscape.min.js"]
                                   :optimizations :advanced
                                   :closure-defines {goog.DEBUG false}
                                   :pretty-print false}}]})
