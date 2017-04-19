(ns storybuilder.repl
  (:require  [re-frame.core :as re-frame]
             [reagent.core :as reagent]))

(def db (re-frame/subscribe [:db]))

(:tropes @db)
(:our-tropes @db)

(def evs (mapcat (partial mapcat :occurred) (:story-sets @db)))
(def evlist (map (partial map :occurred) (:story-sets @db)))

(filter :viol evs)
(map #(:inst (:event %)) evs)
(map :inst evs)

(filter #(and (= (:inst %) "evilEmpire") (= (:event %) "go")) evs)

;; before
(:our-tropes @db)
;; => [{:id "58c2ae7f5d2a01351e4fab0f", :label "The Hero's Journey", :events [{:place "Home", :verb "go", :role "Hero"} {:or [{:place "Away", :verb "go", :role "Hero"} {:role-b "Villain", :role-a "Hero", :verb "kill"}]} {:or [{:place "Home", :verb "go", :role "Hero"} {:role-b "Villain", :role-a "Mentor", :verb "kill"}]}], :subverted false, :places [{:id "Away", :label "Away", :location "Away"} {:id "Home", :label "Home", :location "Home"}], :objects [], :characters [{:id "Hero", :label "Hero", :role "Hero"} {:id "Mentor", :label "Mentor", :role "Mentor"} {:id "Villain", :label "Villain", :role "Villain"}]}]
(:tropes @db)
;; => [{:source "The Villain may go Away\nThen the Villain kills the Hero", :situations [], :events [{:permission {:place "Away", :verb "go", :role "Villain"}} {:role-b "Hero", :verb "kill", :role "Villain"}], :locations ["Away"], :objects [], :roles ["Hero" "Villain"], :label "Evil Empire", :id "58c2ab975d2a01351e4fab0d"} {:source "", :situations [], :events [], :locations [], :objects [], :roles [], :label "Chekov's Gun", :id "58c2ab975d2a01351e4fab0e"} {:source "The Hero is at Home\nThen the Hero goes Away\n  Or the Hero kills the Villain\nThen the Hero goes Home\n  Or the Mentor kills the Villain", :situations [], :events [{:place "Home", :verb "go", :role "Hero"} {:or [{:place "Away", :verb "go", :role "Hero"} {:role-b "Villain", :role-a "Hero", :verb "kill"}]} {:or [{:place "Home", :verb "go", :role "Hero"} {:role-b "Villain", :role-a "Mentor", :verb "kill"}]}], :locations ["Away" "Home"], :objects [], :roles ["Hero" "Mentor" "Villain"], :label "The Hero's Journey", :id "58c2ae7f5d2a01351e4fab0f"}]

;; after
(:our-tropes @db)
;; => [{:id "58c2ae7f5d2a01351e4fab0f", :label "The Hero's Journey", :events [{:place "Home", :verb "go", :role "Hero"} {:or [{:place "Away", :verb "go", :role "Hero"} {:role-b "Villain", :role-a "Hero", :verb "kill"}]} {:or [{:place "Home", :verb "go", :role "Hero"} {:role-b "Villain", :role-a "Mentor", :verb "kill"}]}], :subverted false, :places [{:id "Away", :label "Away", :location "Away"} {:id "Home", :label "Home", :location "Home"}], :objects [], :characters [{:id "Hero", :label "Hero", :role "Hero"} {:id "Mentor", :label "Mentor", :role "Mentor"} {:id "Villain", :label "Villain", :role "Villain"}]}]
(:tropes @db)
;; => [{:source "The Villain may go Away\nThen the Villain kills the Hero", :situations [], :events [{:permission {:place "Away", :verb "go", :role "Villain"}} {:role-b "Hero", :verb "kill", :role "Villain"}], :locations ["Away"], :objects [], :roles ["Hero" "Villain"], :label "Evil Empire", :id "58c2ab975d2a01351e4fab0d"} {:source "", :situations [], :events [], :locations [], :objects [], :roles [], :label "Chekov's Gun", :id "58c2ab975d2a01351e4fab0e"} {:source "The Hero is at Home\nThen the Hero goes Away\n  Or the Hero kills the Villain\nThen the Hero goes Home\n  Or the Villain kills the Hero\n  Or the Mentor kills the Villain", :situations [], :events [{:place "Home", :verb "go", :role "Hero"} {:or [{:place "Away", :verb "go", :role "Hero"} {:role-b "Villain", :role-a "Hero", :verb "kill"}]} {:or [{:place "Home", :verb "go", :role "Hero"} {:role-b "Hero", :role-a "Villain", :verb "kill"} {:role-b "Villain", :role-a "Mentor", :verb "kill"}]}], :locations ["Away" "Home"], :objects [], :roles ["Hero" "Mentor" "Villain"], :label "The Hero's Journey", :id "58c2ae7f5d2a01351e4fab0f"}]

(first (:story-sets @db))

(re-frame/dispatch [:load-tropes])
(:editing-trope @db)

(:compiling @db)

(:story-graph @db)

(map :story-sets @db)

(:story-sets @db)

(map :occurred (first (:story-sets @db)))

(filter (fn [x] (some? #())))

(for [set (:story-sets @db)]
  (for [ev set]
    ;; (type ev)
    (let [o (:occurred ev)]
      (if-not (empty? o) o))
    ;; ev
    ))

;; (loop [sets (:story-sets @db) res []]
;;   (if (empty? sets) res
;;       (loop [events (first sets) ev-res []]
;;         (if (empty? events) ev-res
;;             ))))

(for [set (:story-sets @db)]
  (for [ev set]
    (let [occ (remove empty? (:occurred ev))]
      (for [o occ]
        ;; (remove #(empty? (apply concat %)) occ)
        ;; (apply concat occ)
        ;; occ
        o
        ))))

(def graph (re-frame/subscribe [:story-graph]))
;; => #'storybuilder.repl/graph

(:nodes @graph)
;; => ({:id 0, :label "START", :level 0, :color "#FF3333"} {:label "go lukeSkywalker england", :id 183, :level 1, :event {:event "go", :params ["lukeSkywalker" "england"], :inst "herosJourney"}} {:label "go lukeSkywalker england", :id 184, :level 2, :event {:event "go", :params ["lukeSkywalker" "england"], :inst "herosJourney"}} {:label "kill lukeSkywalker professorMoriarty", :id 185, :level 3, :event {:event "kill", :params ["lukeSkywalker" "professorMoriarty"], :inst "herosJourney"}} {:label "kill lukeSkywalker professorMoriarty", :id 186, :level 4, :event {:event "kill", :params ["lukeSkywalker" "professorMoriarty"], :inst "herosJourney"}} {:label " ", :id 187, :level 5, :event nil} {:label "go lukeSkywalker england", :id 188, :level 4, :event {:event "go", :params ["lukeSkywalker" "england"], :inst "herosJourney"}} {:label " ", :id 189, :level 5, :event nil} {:label " ", :id 190, :level 3, :event nil} {:label " ", :id 191, :level 4, :event nil} {:label " ", :id 192, :level 5, :event nil} {:label " ", :id 193, :level 4, :event nil} {:label " ", :id 194, :level 5, :event nil} {:label " ", :id 195, :level 2, :event nil} {:label " ", :id 196, :level 3, :event nil} {:label " ", :id 197, :level 4, :event nil} {:label " ", :id 198, :level 5, :event nil} {:label "go lukeSkywalker jungle", :id 199, :level 3, :event {:event "go", :params ["lukeSkywalker" "jungle"], :inst "herosJourney"}} {:label " ", :id 200, :level 4, :event nil} {:label " ", :id 201, :level 5, :event nil} {:label " ", :id 202, :level 1, :event nil} {:label " ", :id 203, :level 2, :event nil} {:label " ", :id 204, :level 3, :event nil} {:label " ", :id 205, :level 4, :event nil} {:label " ", :id 206, :level 5, :event nil} {:label "kill lukeSkywalker professorMoriarty", :id 207, :level 4, :event {:event "kill", :params ["lukeSkywalker" "professorMoriarty"], :inst "herosJourney"}} {:label " ", :id 208, :level 5, :event nil} {:label "go lukeSkywalker england", :id 209, :level 4, :event {:event "go", :params ["lukeSkywalker" "england"], :inst "herosJourney"}} {:label " ", :id 210, :level 5, :event nil})

(remove nil? (map :event (:nodes @graph)))

;; => ({:event "go", :params ["lukeSkywalker" "england"], :inst "herosJourney"} {:event "go", :params ["lukeSkywalker" "england"], :inst "herosJourney"} {:event "kill", :params ["lukeSkywalker" "professorMoriarty"], :inst "herosJourney"} {:event "kill", :params ["lukeSkywalker" "professorMoriarty"], :inst "herosJourney"} {:event "go", :params ["lukeSkywalker" "england"], :inst "herosJourney"} {:event "go", :params ["lukeSkywalker" "jungle"], :inst "herosJourney"} {:event "kill", :params ["lukeSkywalker" "professorMoriarty"], :inst "herosJourney"} {:event "go", :params ["lukeSkywalker" "england"], :inst "herosJourney"})

;; => (nil {:event "go", :params ["lukeSkywalker" "england"], :inst "herosJourney"} {:event "go", :params ["lukeSkywalker" "england"], :inst "herosJourney"} {:event "kill", :params ["lukeSkywalker" "professorMoriarty"], :inst "herosJourney"} {:event "kill", :params ["lukeSkywalker" "professorMoriarty"], :inst "herosJourney"} nil {:event "go", :params ["lukeSkywalker" "england"], :inst "herosJourney"} nil nil nil nil nil nil nil nil nil nil {:event "go", :params ["lukeSkywalker" "jungle"], :inst "herosJourney"} nil nil nil nil nil nil nil {:event "kill", :params ["lukeSkywalker" "professorMoriarty"], :inst "herosJourney"} nil {:event "go", :params ["lukeSkywalker" "england"], :inst "herosJourney"} nil)

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


