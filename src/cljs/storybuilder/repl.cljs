(ns storybuilder.repl
  (:require  [re-frame.core :as re-frame]
             [reagent.core :as reagent]))

(def db (re-frame/subscribe [:db]))
(def graph (re-frame/subscribe [:story-graph]))

(:nodes @graph)

(println @db)

(:characters @db)
(:objects @db)
(:tropes @db)
(:editing-trope @db)
(:lookahead @db)
(:story-graph @db)
(first (filter #(= (:editing-trope @db) (:id %)) (:tropes @db)))
(keys @db)


