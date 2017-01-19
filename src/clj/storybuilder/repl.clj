(ns storybuilder.repl
  (:require [storybuilder.datastore :refer :all]
            [tropic.parser :refer [parse-trope]]
            [tropic.gen :refer [make-map]]
            [tropic.solver :refer [make-story solve-story]]))

(map :story-id (get-events))
(nth (map :story-id (get-events)) 4)
(get-events-for-story (nth (map :story-id (get-events)) 4))
(get-events-for-story (nth (map :story-id (get-events)) 5))
(get-tropes-for-story (nth (map :story-id (get-events)) 5))
(get-events-for-story (last (map :story-id (get-events))))
(get-tropes-for-story (last (map :story-id (get-events))))

(parse-trope "\"The Hero's Journey\" is a trope where:\n    The Hero is at Home\n  Then the Hero goes away\n    Or the Hero kill the Villain")

(let [id (last (map :story-id (get-events)))]
  (solve-story
   id
   (get-tropes-for-story id)
   (get-events-for-story id)))

(count (get-events))
(count (get-stories))
(:id (first (get-stories)))
(get-tropes-for-story "585d003359111a1296f87c88")
(get-story "585d003359111a1296f87c88")
(get-story (:id (first (get-stories))))
(get-tropes-for-story (:id (nth (get-stories) 70)))
(first (get-tropes-for-story (:id (last (get-stories)))))
;; => {:characters [{:role "Hero", :label "Luke Skywalker", :id "586d153259111a0f09895659"} {:role "Villain", :label "Darth Vader", :id "586d153259111a0f0989565a"}], :objects [], :places [{:location "Away", :label "Space", :id "582487a35d2a0108a93fd8fa"} {:location "Home", :label "Tatooine", :id "582487a35d2a0108a93fd8fe"}], :subverted false, :events [{:role "Hero", :verb "go", :place "Home"} {:or [{:role "Hero", :verb "go", :place "Away"} {:role "Hero", :verb "kill", :role-b "Villain"}]}], :label "The Hero's Journey", :id "58806384a7986c11cd473ee5"}


(reset-collection! "events")


(new-event {:player "Luke Skywalker"
            :verb "go"
            :object-a "Tatooine"})
(do

  (new-trope {:label "The Hero's Journey"
              :source ""
              :roles ["Hero" "Villain" "Dispatcher"]
              :locations ["Home" "Away"]
              :objects ["Weapon"]})

  (new-trope {:label "Evil Empire"
              :source ""
              :roles ["Villain"]
              :locations ["Hidden Base"]
              :objects ["MacGuffin"]})

  (new-trope {:label "Chekov's Gun"
              :source ""
              :roles ["Hero"]
              :locations []
              :objects ["Weapon"]})
  )

(get-tropes)
(reset-collection! "tropes")

(do
 (new-character {:label "Luke Skywalker"
                 :roles ["Hero"]})

 (new-character {:label "Darth Vader"
                 :roles ["Villain"]})

 (new-character {:label "Obi Wan"
                 :roles ["Dispatcher"]})


 (new-character {:label "Batman"
                 :roles ["Hero"]})


 (new-character {:label "Abraham Lincoln"
                 :roles ["Hero" "Mentor"]})

 (new-character {:label "Professor Moriarty"
                 :roles ["Villain"]})

 (new-character {:label "Merlin"
                 :roles ["Dispatcher"]})


 (new-character {:label "Harry Potter"
                 :roles ["Hero"]})

 (new-character {:label "Forrest Gump"
                 :roles ["Hero"]})

 (new-character {:label "Robin Hood"
                 :roles ["Hero"]})

 (new-character {:label "James Bond"
                 :roles ["Hero"]})

 (new-character {:label "Goldfinger"
                 :roles ["Villain"]})

 (new-character {:label "Blofeld"
                 :roles ["Villain"]})

 (new-character {:label "Lord Voldemort"
                 :roles ["Villain"]})

 (new-character {:label "The Joker"
                 :roles ["Villain"]})

 (new-character {:label "Commissioner Gordon"
                 :roles ["Dispatcher"]})

 (new-character {:label "Albus Dumbledore"
                 :roles ["Dispatcher" "Mentor"]}))

(get-characters)

(reset-collection! "characters")


(do
  (new-object {:label "Light Saber"
               :types ["Weapon"]})

  (new-object {:label "Secret Plans"
               :types ["MacGuffin"]})

  (new-object {:label "Priceless Ming Vase"
               :types ["MacGuffin"]})

  (new-object {:label "Axe"
               :types ["Weapon"]})

  (new-object {:label "Sword of Destiny"
               :types ["Weapon"]})

  (new-object {:label "Pointy Stick"
               :types ["Weapon"]})

  (new-object {:label "Dagger"
               :types ["Weapon"]})

  (new-object {:label "Shield"
               :types ["Weapon"]})

  (new-object {:label "Maltese Falcon"
               :types ["MacGuffin"]}))


(get-objects)

(reset-collection! "objects")


(do
  (new-place {:label "Death Star"
              :locations ["Hidden Base"]})

  (new-place {:label "Inside a Volcano"
              :locations ["Hidden Base"]})

  (new-place {:label "Secret Lair"
              :locations ["Hidden Base"]})

  (new-place {:label "Space"
              :locations ["Away" "Land Of Adventure"]})

  (new-place {:label "The Jungle"
              :locations ["Away" "Land Of Adventure"]})

  (new-place {:label "Haunted Forest"
              :locations ["Away" "Land Of Adventure"]})

  (new-place {:label "Across the Ocean"
              :locations ["Away" "Land Of Adventure"]})

  (new-place {:label "Tatooine"
              :locations ["Home"]})

  (new-place {:label "England"
              :locations ["Home"]})

  (new-place {:label "The USA"
              :locations ["Home"]})

  (new-place {:label "Planet Earth"
              :locations ["Home"]})

  (new-place {:label "4 Privet Drive"
              :locations ["Home"]})

  )

(count (get-places))
(get-places)
(reset-collection! "places")

(get-story
 (:id (first (get-stories))))

(get-stories)
(:id (first (get-stories)))

(get-stories)

(reset-collection! "stories")

(:id (first (get-stories)))


(reset-collection! "objects")

(defn dec [i]
  (/ (- i (mod i 10)) 10))

(dec 1)

(defn index->event
  [events index]
  (let [dec (- (/ (- index (mod index 10)) 10) 1)
        rem (- (mod index 10) 1)]
    (nth (nth events rem) dec))
  )


(index->event [; answer set
              [; time step
               {:observed [{:event "go"
                            :params ["south"]
                            :inst "Hero's Journey"}]
                :fluents []}
               {:observed [{:event "run"
                            :params ["away"]
                            :inst "Hero's Journey"}]
                :fluents []}
               ]
              [; time step
               {:observed [{:event "take"
                            :params ["sword"]
                            :inst "Hero's Journey"}]
                :fluents []}]
              [; time step
               {:observed [{:event "go"
                            :params ["north"]
                            :inst "Evil Empire"}]
                :fluents []}]
              ] 21)



;; move this to handlers.cljs
;; don't forget: you _could_ have multiple events in each timestep!
;; my encoding here is a _little_ fragile (based on decimal numbers)!
(defn data->graph [data]
  (loop [answer-sets data as-num 1 nodes [{:id 0 :label "start" :level 0 :color "#FF3333"}] edges []]
    (if (empty? answer-sets) {:nodes nodes :edges edges}
        (let [options
              (loop [time-step (first answer-sets) ts-nodes [] ts-edges [] ts-num 1]
                (if (empty? time-step) {:nodes ts-nodes :edges ts-edges}
                    (let [event (first (:observed (first time-step))) ; NOTE: this is just the FIRST event
                          label (str (:event event) " " (apply str (interpose " " (:params event))))
                          prev-id (if (= 1 ts-num) 0 (+ (- (* 10 ts-num) 10) as-num))
                          this-id (+ (* 10 ts-num) as-num)
                          e {:from prev-id :to this-id :label (:inst event) :font {:align "bottom"}}
                          n {:label label :id this-id :level ts-num}]
                      (recur (rest time-step) (conj ts-nodes n) (conj ts-edges e) (inc ts-num)))))]
          (recur (rest answer-sets) (inc as-num) (concat nodes (:nodes options)) (concat edges (:edges options))))))
  )

(data->graph [; answer set
              [; time step
               {:observed [{:event "go"
                            :params ["south"]
                            :inst "Hero's Journey"}]
                :fluents []}
               {:observed [{:event "run"
                            :params ["away"]
                            :inst "Hero's Journey"}]
                :fluents []}
               ]
              [; time step
               {:observed [{:event "take"
                            :params ["sword"]
                            :inst "Hero's Journey"}]
                :fluents []}]
              [; time step
               {:observed [{:event "go"
                            :params ["north"]
                            :inst "Evil Empire"}]
                :fluents []}]
              ])

