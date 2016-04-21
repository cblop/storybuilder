(ns storybuilder.solver
  (:require
   [storybuilder.instal :refer [event-name instal-file]]
   [me.raynes.conch :refer [programs with-programs let-programs] :as sh]))

(programs python clingo)

(defn random-character []
  "Random Character")

(defn random-object []
  "Random Object")

(defn random-place []
  "Random Place")

(defn stringer [item]
  (reduce str (interpose " " (map #(if (nil? %) "random" (event-name %)) item))))

(defn make-domain [hmap id]
  (let [tropes (:tropes hmap)
        p (println "TROPES:")
        p (println hmap)
        p (println (:characters hmap))
        tropenames (stringer (vec (set (map :label tropes))))
        characters (:characters hmap)
        charnames (stringer (map :label characters))
        roles (stringer (vec (set (map :role characters))))
        places (:places hmap)
        placenames (stringer (map :label places))
        locations (stringer (vec (set (map :location places))))
        objects (:objects hmap)
        objectnames (stringer (map :label objects))
        types (stringer (vec (set (map :type objects))))
        phases (reduce str (interpose " " ["inactive" "done" "phaseA" "phaseB" "phaseC" "phaseD" "phaseE" "phaseF" "phaseG" "phaseH" "phaseI" "phaseJ"]))
        strings [(if (seq tropes) (str "Trope: " tropenames)) "\nPhase: " phases (if (seq characters) (str "\nAgent: " charnames)) (if (seq characters) (str "\nRole: " roles)) (if (seq places) (str "\nPlace: " locations)) (if (seq places) (str "\nPlaceName: " placenames)) (if (seq objects) (str "\nObject: " types)) (if (seq objects) (str "\nObjectName: " objectnames))]
        final (reduce str strings)
        ]
    (do (spit (str "resources/domain-" id ".idc") final)
        {:text final})
    ))

(defn make-instal [hmap id]
  (instal-file hmap (str "resources/story-" id ".ial")))

(defn make-query [events id]
  (spit (str "resources/query-" id ".lp") "observed(go(lukeSkywalker,space))"))

(defn make-story [hmap id]
  (do
    (make-domain hmap id)
    (make-instal hmap id)
    (make-query [] id)
    (let [output (python "instal/instalsolve.py" "-v" "-i" (str "resources/story-" id ".ial") "-d" (str "resources/domain-" id ".idc") "-o" (str "resources/temp-" id ".lp") (str "resources/query-" id ".lp"))]
      (do
        (spit (str "resources/output-" id ".lp") output)
        {:text output}))
    ))

(defn solve-story [hmap event])

