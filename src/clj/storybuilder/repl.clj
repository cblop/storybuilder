(ns storybuilder.repl
  (:require [storybuilder.datastore :refer :all]))

(new-trope {:label "The Hero's Journey"
            :source "Lots of code\nGoes here\n..."
            :roles ["Hero" "Villain"]
            :objects ["Weapon"]})

(new-trope {:label "Evil Empire"
            :source "Evil Empire code\nGoes here\n..."
            :roles ["Villain"]
            :objects ["Hidden Base"]})

(get-tropes)

(reset-collection! "tropes")


(new-character {:label "Luke Skywalker"
                :roles ["Hero"]})

(new-character {:label "Darth Vader"
                :roles ["Villain"]})

(get-characters)

(reset-collection! "characters")

(new-object {:label "Light Saber"
             :types ["Weapon"]})

(new-object {:label "Death Star"
             :types ["Hidden Base"]})

(get-objects)

(get-story
 (:id (first (get-stories))))

(get-stories)
(:id (first (get-stories)))

(get-stories)

(reset-collection! "stories")

(:id (first (get-stories)))


(reset-collection! "objects")


