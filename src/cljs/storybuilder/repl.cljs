(ns storybuilder.repl
  (:require  [re-frame.core :as re-frame]
             [reagent.core :as reagent]))

(def db (re-frame/subscribe [:db]))
(def graph (re-frame/subscribe [:story-graph]))

(:nodes @graph)

(println @db)


(:story-sets @db)

(:characters @db)
(:objects @db)

(remove #(= (apply str (take 3 (:event %))) "int") (:occurred (first (first (:story-sets @db)))))
(:occurred (first (first (:story-sets @db))))

(:story-sets @db)
;; => [[{:occurred [{:event "intHerosJourney", :params ["lukeSkywalker" "darthVader" "space" "tatooine"], :inst "herosJourney"} {:event "go", :params ["lukeSkywalker" "tatooine"], :inst "herosJourney"}], :fluents [{:fluent "role", :params ["lukeSkywalker" "hero"]} {:fluent "role", :params ["darthVader" "villain"]} {:fluent "place", :params ["tatooine" "home"]} {:fluent "place", :params ["space" "away"]} {:fluent "phase", :params ["herosJourney" "phaseA"]}], :viols [], :obls [], :observed [], :perms [{:perm "null"} {:perm "intHerosJourney", :params ["lukeSkywalker" "darthVader" "space" "tatooine"]} {:perm "go", :params ["lukeSkywalker" "tatooine"]}]} {:occurred [{:event "intHerosJourney", :params ["lukeSkywalker" "darthVader" "space" "tatooine"], :inst "herosJourney"} {:event "go", :params ["lukeSkywalker" "tatooine"], :inst "herosJourney"}], :fluents [{:fluent "role", :params ["lukeSkywalker" "hero"]} {:fluent "role", :params ["darthVader" "villain"]} {:fluent "place", :params ["tatooine" "home"]} {:fluent "place", :params ["space" "away"]} {:fluent "phase", :params ["herosJourney" "phaseB"]}], :viols [], :obls [], :observed [], :perms [{:perm "null"} {:perm "kill", :params ["lukeSkywalker" "darthVader"]} {:perm "intHerosJourney", :params ["lukeSkywalker" "darthVader" "space" "tatooine"]} {:perm "go", :params ["lukeSkywalker" "space"]}]} {:occurred [{:event "intHerosJourney", :params ["lukeSkywalker" "darthVader" "space" "tatooine"], :inst "herosJourney"} {:event "go", :params ["lukeSkywalker" "space"], :inst "herosJourney"}], :fluents [{:fluent "role", :params ["lukeSkywalker" "hero"]} {:fluent "role", :params ["darthVader" "villain"]} {:fluent "place", :params ["tatooine" "home"]} {:fluent "place", :params ["space" "away"]} {:fluent "phase", :params ["herosJourney" "done"]}], :viols [], :obls [], :observed [], :perms [{:perm "null"} {:perm "intHerosJourney", :params ["lukeSkywalker" "darthVader" "space" "tatooine"]}]}] [{:occurred [{:event "intHerosJourney", :params ["lukeSkywalker" "darthVader" "space" "tatooine"], :inst "herosJourney"} {:event "go", :params ["lukeSkywalker" "tatooine"], :inst "herosJourney"}], :fluents [{:fluent "role", :params ["lukeSkywalker" "hero"]} {:fluent "role", :params ["darthVader" "villain"]} {:fluent "place", :params ["tatooine" "home"]} {:fluent "place", :params ["space" "away"]} {:fluent "phase", :params ["herosJourney" "phaseA"]}], :viols [], :obls [], :observed [], :perms [{:perm "null"} {:perm "intHerosJourney", :params ["lukeSkywalker" "darthVader" "space" "tatooine"]} {:perm "go", :params ["lukeSkywalker" "tatooine"]}]} {:occurred [{:event "intHerosJourney", :params ["lukeSkywalker" "darthVader" "space" "tatooine"], :inst "herosJourney"} {:event "go", :params ["lukeSkywalker" "tatooine"], :inst "herosJourney"}], :fluents [{:fluent "role", :params ["lukeSkywalker" "hero"]} {:fluent "role", :params ["darthVader" "villain"]} {:fluent "place", :params ["tatooine" "home"]} {:fluent "place", :params ["space" "away"]} {:fluent "phase", :params ["herosJourney" "phaseB"]}], :viols [], :obls [], :observed [], :perms [{:perm "null"} {:perm "kill", :params ["lukeSkywalker" "darthVader"]} {:perm "intHerosJourney", :params ["lukeSkywalker" "darthVader" "space" "tatooine"]} {:perm "go", :params ["lukeSkywalker" "space"]}]} {:occurred [{:event "kill", :params ["lukeSkywalker" "darthVader"], :inst "herosJourney"} {:event "intHerosJourney", :params ["lukeSkywalker" "darthVader" "space" "tatooine"], :inst "herosJourney"}], :fluents [{:fluent "role", :params ["lukeSkywalker" "hero"]} {:fluent "role", :params ["darthVader" "villain"]} {:fluent "place", :params ["tatooine" "home"]} {:fluent "place", :params ["space" "away"]} {:fluent "phase", :params ["herosJourney" "done"]}], :viols [], :obls [], :observed [], :perms [{:perm "null"} {:perm "intHerosJourney", :params ["lukeSkywalker" "darthVader" "space" "tatooine"]}]}]]
(:tropes @db)
(:our-tropes @db)
(count (map #(str "hello " %) (map :label (:our-tropes @db))))
(count (:our-tropes @db))
(:editing-trope @db)
(:lookahead @db)
(:story-graph @db)
(first (filter #(= (:editing-trope @db) (:id %)) (:tropes @db)))
(keys @db)


