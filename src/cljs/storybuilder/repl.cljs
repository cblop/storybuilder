(ns storybuilder.repl
  (:require  [re-frame.core :as re-frame]
             [reagent.core :as reagent]))

(def db (re-frame/subscribe [:db]))

(println @db)

(:characters @db)
(:objects @db)
(:tropes @db)
(keys @db)


