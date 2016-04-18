(ns storybuilder.handlers
    (:require [re-frame.core :as re-frame]
              [ajax.core :refer [GET POST PUT DELETE]]
              [storybuilder.db :as db]))

(def host "http://localhost:3449")


(defn drop-nth [n coll]
  (keep-indexed #(if (not= %1 n) %2) coll))


(re-frame/register-handler
 :bad-response
 (fn [db [_ response]]
   (println (str "BAD RESPONSE: " response))
   db
   ))

(re-frame/register-handler
 :load-tropes-handler
 (fn [db [_ response]]
   (assoc db :tropes response)))


(re-frame/register-handler
 :load-tropes
 (fn [db _]
   (GET (str host "/tropes/") {:handler #(re-frame/dispatch [:load-tropes-handler %1])
                               :bad-response #(re-frame/dispatch [:bad-response %1])
                               :response-format :json
                               :keywords? true})
   db))


(re-frame/register-handler
 :load-characters-handler
 (fn [db [_ response]]
   (assoc db :characters response)))


(re-frame/register-handler
 :load-characters
 (fn [db _]
   (GET (str host "/characters/") {:handler #(re-frame/dispatch [:load-characters-handler %1])
                               :bad-response #(re-frame/dispatch [:bad-response %1])
                               :response-format :json
                               :keywords? true})
   db))


(re-frame/register-handler
 :load-objects-handler
 (fn [db [_ response]]
   (assoc db :objects response)))


(re-frame/register-handler
 :load-objects
 (fn [db _]
   (GET (str host "/objects/") {:handler #(re-frame/dispatch [:load-objects-handler %1])
                                   :bad-response #(re-frame/dispatch [:bad-response %1])
                                   :response-format :json
                                   :keywords? true})
   db))


(re-frame/register-handler
 :subvert-trope
 (fn [db [_ n]]
   (let [sub (:subverted (nth (:our-tropes db) n))
         ;; p (println (nth (:our-tropes db) n))
         old-chars (:characters (nth (:our-tropes db) n))
         roles (map :role old-chars)
         new-chars (map #(assoc %1 :role %2) old-chars (reverse roles))]
     (assoc db :our-tropes
            (-> (:our-tropes db)
                (assoc-in [n :characters] new-chars)
                (assoc-in [n :subverted] (not sub))))
     )))

(re-frame/register-handler
 :remove-trope
 (fn [db [_ n]]
   (let [a (:our-tropes db)]
     (assoc db :our-tropes (drop-nth n a)))))



(re-frame/register-handler
 :change-trope
 (fn [db [_ n id]]
   (let [trope (first (filter #(= (:id %) id) (:tropes db)))
         roles (:roles trope)
         objects (:objects trope)
         ]
     ;; (println (:our-tropes db))
     (assoc db :our-tropes (assoc (:our-tropes db) n {:id id :subverted false :objects (into [] (take (count objects) (repeat nil))) :characters (into [] (take (count roles) (repeat nil)))})))))

(re-frame/register-handler
 :tropes-changed
 (fn [db [_ cm]]
   (let [text (.getValue cm)
         cursor (.getCursor cm "head")]
     (do
       ;; (println text)
       (assoc
        (assoc db :trope-text text)
        :tropes-cursor-pos cursor)
       ))))

(re-frame/register-handler
 :add-trope
 (fn [db [_ id]]
   (assoc db :our-tropes (conj (vec (:our-tropes db)) {:id nil :subverted false}))))

(re-frame/register-handler
 :editing-trope
 (fn [db [_ id]]
   (let [trope (re-frame/subscribe [:trope-for-id id])]
     (assoc (assoc db :editing-trope id) :trope-text (:source @trope)))))

(re-frame/register-handler
 :edit-tab-changed
 (fn [db [_ tab-id]]
   (assoc db :edit-trope-tab tab-id)))

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
