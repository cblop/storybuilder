(ns storybuilder.handlers
    (:require [re-frame.core :as re-frame]
              [storybuilder.db :as db]))

(re-frame/register-handler
 :tropes-changed
 (fn [db [_ cm]]
   (let [text (.getValue cm)
         cursor (.getCursor cm "head")]
     (do
       (println text)
       (assoc
        (assoc db :trope-text text)
        :tropes-cursor-pos cursor)
       ))))

(re-frame/register-handler
 :tab-changed
 (fn [db [_ tab-id]]
   (do
     (println db)
     (assoc db :current-tab tab-id))))

(re-frame/register-handler
 :initialize-db
 (fn  [_ _]
   db/default-db))
