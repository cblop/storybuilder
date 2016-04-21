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
   :story-text [""]
   })
