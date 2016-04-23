(ns storybuilder.repl
  (:require [storybuilder.datastore :refer :all]))

(new-trope {:label "The Hero's Journey"
            :source "Lots of code\nGoes here\n..."
            :roles ["Hero" "Villain" "Dispatcher"]
            :locations ["Home" "Away"]
            :objects ["Weapon"]})

(new-trope {:label "Evil Empire"
            :source "Evil Empire code\nGoes here\n..."
            :roles ["Villain"]
            :locations ["Hidden Base"]
            :objects ["MacGuffin"]})

(get-tropes)

(reset-collection! "tropes")


(new-character {:label "Luke Skywalker"
                :roles ["Hero"]})

(new-character {:label "Darth Vader"
                :roles ["Villain"]})

(new-character {:label "Obi Wan"
                :roles ["Dispatcher"]})

(get-characters)

(reset-collection! "characters")

(new-object {:label "Light Saber"
             :types ["Weapon"]})

(new-object {:label "Secret Plans"
             :types ["MacGuffin"]})


(get-objects)

(reset-collection! "objects")


(new-place {:label "Death Star"
            :locations ["Hidden Base"]})

(new-place {:label "Space"
            :locations ["Away"]})

(new-place {:label "Tatooine"
            :locations ["Home"]})

(get-places)

(get-story
 (:id (first (get-stories))))

(get-stories)
(:id (first (get-stories)))

(get-stories)

(reset-collection! "stories")

(:id (first (get-stories)))


(reset-collection! "objects")


