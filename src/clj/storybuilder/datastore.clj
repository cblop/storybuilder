(ns storybuilder.datastore
  (:require [tropic.solver :refer [make-story solve-story]]
            [monger.core :as mg]
            [monger.result :refer [acknowledged?]]
            [monger.collection :as mc])
  (:import [com.mongodb MongoOptions ServerAddress]
           org.bson.types.ObjectId))

(defonce conn (mg/connect))
(def db (mg/get-db conn "storybuilder"))

(declare get-character-by-id)

(defn reset-collection! [coll]
  (mc/remove db coll))

(defn stringify-ids [record]
  (dissoc (assoc record :id (str (:_id record))) :_id))

;; EVENTS

(defn get-events []
  (map stringify-ids (mc/find-maps db "events")))

(defn get-events-for-story [id]
  (map stringify-ids (mc/find-maps db "events" {:story-id id})))

(defn get-event-by-id [trp]
  (let [oid (ObjectId. (:id trp))]
    (stringify-ids (mc/find-one-as-map db "events" {:_id oid}))))

(defn new-event [data]
  (str (acknowledged? (mc/insert db "events" (merge {:_id (ObjectId.)} (dissoc data :id))))))

(defn delete-event [id]
  (let [new-id (ObjectId. (str id))]
    (str (acknowledged? (mc/remove-by-id db "events" new-id)))))

(defn edit-event [data]
  (let [p (println data)
        new-id (ObjectId. (str (:id data)))]
    (str (acknowledged? (mc/update-by-id db "events" new-id (assoc (dissoc data :id) :_id new-id))))))


;; TROPES

(defn get-tropes []
  (map stringify-ids (mc/find-maps db "tropes")))


(defn get-trope-by-id [trp]
  (let [oid (ObjectId. (:id trp))]
    (stringify-ids (mc/find-one-as-map db "tropes" {:_id oid}))))

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

(defn get-tropes-for-story [id]
  (:tropes (stringify-ids (mc/find-one-as-map db "stories" {:_id (ObjectId. id)}))))


(defn get-story [id]
  (stringify-ids (mc/find-one-as-map db "stories" {:_id (ObjectId. id)})))

(defn new-story [data]
  (let [id (ObjectId.)]
    (do
      (mc/insert db "stories" (merge {:_id id} data))
      ;; (make-story (assoc data :tropes (map get-trope-by-id (:tropes data))))
      (println "NEW STORY")
      (make-story data (str id))
      ;; {:id (str id) :text "Testing..."}
      )))

(defn update-story [data]
  (let [id (:id data)
        player (get-character-by-id (:player data))
        event (assoc data :player (:label player))
        story-id (:story-id data)
        ;; story (get-story id)
        ]
    (do
      (new-event data)
      (println "UPDATE STORY:")
      (println "Tropes: ")
      (println (get-tropes-for-story story-id))
      (println "Events: ")
      (println (get-events-for-story story-id))
      (println (solve-story story-id (get-tropes-for-story story-id) (get-events-for-story story-id)))
      (solve-story story-id (get-tropes-for-story story-id) (get-events-for-story story-id))))
    )

(defn delete-story [id]
  (mc/remove-by-id db "stories" id))

(defn edit-story [id data]
  (mc/update-by-id db "stories" id data))

;; CHARACTERS

(defn get-characters []
  (map stringify-ids (mc/find-maps db "characters")))


(defn get-character-by-id [char]
  (let [oid (ObjectId. char)]
    (stringify-ids (mc/find-one-as-map db "characters" {:_id oid}))))

;; won't work, need to find role inside list
;; (defn get-characters-by-role [role]
;;   (map stringify-ids (mc/find-maps db "characters" {:roles role})))

(defn new-character [data]
  (let [id (ObjectId.)]
    (do
      (mc/insert db "characters" (merge {:_id id} data))
      )))

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
