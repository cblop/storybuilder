(ns storybuilder.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))


(defn in?
  "true if coll contains elm"
  [coll elm]
  (some #(= elm %) coll))


(defn drop-nth [n coll]
  (keep-indexed #(if (not= %1 n) %2) coll))

(re-frame/register-sub
 :trope-for-id
 (fn [db [_ id]]
   (let [new-trope (re-frame/subscribe [:new-trope])
         match (if (= id :new) @new-trope
                 (first (filter #(= id (:id %)) (:tropes @db))))]
     (reaction match))))

(re-frame/register-sub
 :edit-facet
 (fn [db _]
   (reaction (:edit-facet @db))))


(re-frame/register-sub
 :new-trope
 (fn [db _]
   (reaction (:new-trope @db))))

(re-frame/register-sub
 :edit-trope-tab
 (fn [db _]
   (reaction (:edit-trope-tab @db))))

(re-frame/register-sub
 :current-tab
 (fn [db _]
   (reaction (:current-tab @db))))

(re-frame/register-sub
 :subverted?
 (fn [db [_ n]]
   (let [trope (nth (:our-tropes @db) n)]
     (reaction (:subverted trope)))))


(re-frame/register-sub
 :locations
 (fn [db [_ n]]
   (let [tropeid (:id (nth (:our-tropes @db) n))
         locations (:locations (first (filter #(= tropeid (:id %)) (:tropes @db))))]
     (reaction locations))))

(re-frame/register-sub
 :roles
 (fn [db [_ n]]
   (let [tropeid (:id (nth (:our-tropes @db) n))
         roles (:roles (first (filter #(= tropeid (:id %)) (:tropes @db))))]
     (reaction roles))))

(re-frame/register-sub
 :types
 (fn [db [_ n]]
   (let [tropeid (:id (nth (:our-tropes @db) n))
         types (:objects (first (filter #(= tropeid (:id %)) (:tropes @db))))]
     (reaction types))))



(re-frame/register-sub
 :chars-for-roles
 (fn [db [_ roles]]
   (let [chars (map (fn [x] (filter #(in? (:roles %) x) (:characters @db))) roles)]
     (reaction chars))
   ))


(re-frame/register-sub
 :charname-for-id
 (fn [db [_ id]]
   (let [match (first (filter #(= id (:id %)) (:characters @db)))]
     (reaction (:label match)))))


(re-frame/register-sub
 :places-for-locations
 (fn [db [_ locs]]
   (let [places (map (fn [x] (filter #(in? (:locations %) x) (:places @db))) locs)]
     (reaction places))
   ))


(re-frame/register-sub
 :placename-for-id
 (fn [db [_ id]]
   (let [match (first (filter #(= id (:id %)) (:places @db)))]
     (reaction (:label match)))))


(re-frame/register-sub
 :objname-for-id
 (fn [db [_ id]]
   (let [match (first (filter #(= id (:id %)) (:objects @db)))]
     (reaction (:label match)))))


(re-frame/register-sub
 :objs-for-types
 (fn [db [_ types]]
   (let [objs (map (fn [x] (filter #(in? (:types %) x) (:objects @db))) types)]
     (reaction objs))
   ))

(re-frame/register-sub
 :new-trope-name
 (fn [db _]
   (reaction (:label (:new-trope @db)))))

(re-frame/register-sub
 :editing-trope-name
 (fn [db _]
   (let [id (:editing-trope @db)
         trope (re-frame/subscribe [:trope-for-id id])]
     (reaction @trope))))

(re-frame/register-sub
 :editing-trope
 (fn [db _]
   (reaction (:editing-trope @db))))


(re-frame/register-sub
 :error
 (fn [db _]
   (reaction (:error @db))))

(re-frame/register-sub
 :story-text
 (fn [db _]
   (reaction (:story-text @db))))

(re-frame/register-sub
 :success
 (fn [db _]
   (reaction (:success @db))))

(re-frame/register-sub
 :tropes
 (fn [db _]
   (reaction (:tropes @db))))

(re-frame/register-sub
 :our-tropes
 (fn [db _]
   (reaction (:our-tropes @db))))

(re-frame/register-sub
 :tropes-cursor-pos
 (fn [db _]
   (reaction (:tropes-cursor-pos @db))))

(re-frame/register-sub
 :trope-text
 (fn [db _]
   (reaction (:trope-text @db))))



