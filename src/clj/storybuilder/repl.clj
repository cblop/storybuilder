(ns storybuilder.repl
  (:require [storybuilder.datastore :refer :all]
            [tropic.solver :refer [make-story solve-story]]))


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


