(ns storybuilder.repl
  (:require  [re-frame.core :as re-frame]
             [reagent.core :as reagent]))

(def db (re-frame/subscribe [:db]))

(println @db)

(:characters @db)
(:objects @db)
(:tropes @db)
(:editing-trope @db)
(:lookahead @db)
(first (filter #(= (:editing-trope @db) (:id %)) (:tropes @db)))
(keys @db)


