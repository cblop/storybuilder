(ns storybuilder.datastore
  (:require [monger.core :as mg]
            [monger.result :refer [acknowledged?]]
            [monger.collection :as mc])
  (:import [com.mongodb MongoOptions ServerAddress]
           org.bson.types.ObjectId))

(defonce conn (mg/connect))
(def db (mg/get-db conn "storybuilder"))

(defn reset-collection! [coll]
  (mc/remove db coll))

(defn stringify-ids [record]
  (dissoc (assoc record :id (str (:_id record))) :_id))

;; TROPES

(defn get-tropes []
  (map stringify-ids (mc/find-maps db "tropes")))

(defn new-trope [data]
  (str (acknowledged? (mc/insert db "tropes" (merge {:_id (ObjectId.)} (dissoc data :id))))))

(defn delete-trope [id]
  (let [new-id (ObjectId. (str id))]
    (str (acknowledged? (mc/remove-by-id db "tropes" new-id)))))

(defn edit-trope [data]
  (let [p (println data)
        new-id (ObjectId. (str (:id data)))]
    (str (acknowledged? (mc/update-by-id db "tropes" new-id (assoc (dissoc data :id) :_id new-id))))))


;; STORIES

(defn get-stories []
  (map stringify-ids (mc/find-maps db "stories")))

(defn get-story [id]
  (stringify-ids (mc/find-one-as-map db "stories" {:_id (ObjectId. id)})))

(defn new-story [data]
  (let [id (ObjectId.)]
    (do
      (mc/insert db "stories" (merge {:_id id} data))
      ;; (generate-story id)
      {:id (str id) :text "Testing..."}
      )))

(defn delete-story [id]
  (mc/remove-by-id db "stories" id))

(defn edit-story [id data]
  (mc/update-by-id db "stories" id data))

;; CHARACTERS

(defn get-characters []
  (map stringify-ids (mc/find-maps db "characters")))

;; won't work, need to find role inside list
;; (defn get-characters-by-role [role]
;;   (map stringify-ids (mc/find-maps db "characters" {:roles role})))

(defn new-character [data]
  (mc/insert db "characters" (merge {:_id (ObjectId.)} data)))

(defn delete-character [id]
  (mc/remove-by-id db "characters" id))

(defn edit-character [id data]
  (mc/update-by-id db "characters" id data))


;; OBJECTS

(defn get-objects []
  (map stringify-ids (mc/find-maps db "objects")))

(defn new-object [data]
  (mc/insert db "objects" (merge {:_id (ObjectId.)} data)))

(defn delete-object [id]
  (mc/remove-by-id db "objects" id))

(defn edit-object [id data]
  (mc/update-by-id db "objects" id data))


;; PLACES

(defn get-places []
  (map stringify-ids (mc/find-maps db "places")))

(defn new-place [data]
  (mc/insert db "places" (merge {:_id (ObjectId.)} data)))

(defn delete-place [id]
  (mc/remove-by-id db "places" id))

(defn edit-place [id data]
  (mc/update-by-id db "places" id data))
