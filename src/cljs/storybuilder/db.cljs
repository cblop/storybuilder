(ns storybuilder.db)

(def default-db
  {:tropes-cursor-pos {"line" 0, "ch" 0}
   :trope-text ""
   :our-tropes []
   :tropes []
   :places []
   :current-tab :tab1
   :edit-trope-tab :edit
   :editing-trope nil
   :edit-facet :tropes
   :error nil
   :success nil
   :story-text []
   :story-verb "start"
   :story-object-a "story"
   :story-object-b nil
   :story-id nil
   :story-perms nil
   :story-graph {:cx 100 :cy 100 :r 200 :fill "blue"}
   :svg nil
   })
