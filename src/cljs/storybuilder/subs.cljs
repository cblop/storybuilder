(ns storybuilder.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(re-frame/register-sub
 :current-tab
 (fn [db _]
   (reaction (:current-tab @db))))


(re-frame/register-sub
 :tropes-cursor-pos
 (fn [db _]
   (reaction (:tropes-cursor-pos @db))))

(re-frame/register-sub
 :trope-text
 (fn [db _]
   (reaction (:trope-text @db))))

