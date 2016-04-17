(ns storybuilder.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [storybuilder.handlers]
              [storybuilder.subs]
              [storybuilder.views :as views]
              [storybuilder.config :as config]))

(when config/debug?
  (println "dev mode"))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init []
  (re-frame/dispatch-sync [:initialize-db])
  (mount-root)
  )
