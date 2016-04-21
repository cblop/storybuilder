(ns storybuilder.handlers
    (:require [re-frame.core :as re-frame]
              [ajax.core :refer [GET POST]]
              [storybuilder.db :as db]
              [storybuilder.parser :refer [parse-trope]]
              [instaparse.core :as insta]
              [storybuilder.gen :refer [make-map]]
              ))

(def host "http://localhost:3449")


(defn drop-nth [n coll]
  (keep-indexed #(if (not= %1 n) %2) coll))


(defn indices [pred coll]
  (keep-indexed #(when (pred %2) %1) coll))

(re-frame/register-handler
 :bad-response
 (fn [db [_ response]]
   (do
     (println (str "BAD RESPONSE: " response))
     db)
   ))


(re-frame/register-handler
 :error-handler
 (fn [db [_ response]]
   (do
     (println (str "SERVER ERROR: " response))
     db)
   ))

(re-frame/register-handler
 :load-tropes-handler
 (fn [db [_ response]]
   (assoc db :tropes response)))

(re-frame/register-handler
 :storygen-handler
 (fn [db [_ response]]
   (println "RESPONSE:")
   (println response)
   (assoc (assoc db :story-id (:id response)) :story-text (clojure.string/split-lines (:text response)))))

(re-frame/register-handler
 :success
 (fn [db _]
   (assoc db :success true)))


(re-frame/register-handler
 :clear-text
 (fn [db _]
   (assoc db :trope-text "")))


(re-frame/register-handler
 :delete-trope-handler
 (fn [db [_ response]]
   (if response (do
                  (re-frame/dispatch [:load-tropes])
                  (re-frame/dispatch [:clear-text])
                  (re-frame/dispatch [:success])
                  db)
       (assoc db :error true))
   ))

(re-frame/register-handler
 :edit-trope-handler
 (fn [db [_ response]]
   (if response (do
                  (re-frame/dispatch [:load-tropes])
                  (assoc db :success true))
       (assoc db :error true))
   ))

(re-frame/register-handler
 :load-tropes
 (fn [db _]
   (GET (str host "/tropes/") {:handler #(re-frame/dispatch [:load-tropes-handler %1])
                               :bad-response #(re-frame/dispatch [:bad-response %1])
                               :response-format :json
                               :keywords? true})
   db))

(re-frame/register-handler
 :delete-trope
 (fn [db _]
   (let [del-id (re-frame/subscribe [:editing-trope])]
     (if (= :new @del-id)
       (assoc db :trope-text "")
       (POST (str host "/tropes/delete") {:params {:id @del-id}
                                          :format :json
                                          :handler #(re-frame/dispatch [:delete-trope-handler %1])
                                          :error-handler #(re-frame/dispatch [:error-handler %1])}))
     db
     )))

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
 :load-places-handler
 (fn [db [_ response]]
   (assoc db :places response)))

(re-frame/register-handler
 :load-places
 (fn [db _]
   (GET (str host "/places/") {:handler #(re-frame/dispatch [:load-places-handler %1])
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
 :hide-error
 (fn [db _]
   (assoc db :error nil)))

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
         locs (:locations trope)
         ]
     ;; (println (:our-tropes db))
     (assoc db :our-tropes (assoc (:our-tropes db) n {:id id :subverted false :places (into [] (take (count locs) (repeat nil))) :objects (into [] (take (count objects) (repeat nil))) :characters (into [] (take (count roles) (repeat nil)))})))))


(re-frame/register-handler
 :change-char
 (fn [db [_ n id role]]
   (let [trope (nth (:our-tropes db) n)
         chars (:characters trope)
         tro (first (filter #(= (:id %) (:id trope)) (:tropes db)))
         i (first (indices #(= % role) (:roles tro)))
         charname (re-frame/subscribe [:charname-for-id id])]
     (assoc db :our-tropes (assoc-in (:our-tropes db) [n :characters] (assoc chars i {:id id :name @charname :role role}))))))


(re-frame/register-handler
 :change-place
 (fn [db [_ n id loc]]
   (let [trope (nth (:our-tropes db) n)
         places (:places trope)
         tro (first (filter #(= (:id %) (:id trope)) (:tropes db)))
         i (first (indices #(= % loc) (:locations tro)))
         placename (re-frame/subscribe [:placename-for-id id])]
     (assoc db :our-tropes (assoc-in (:our-tropes db) [n :places] (assoc places i {:id id :name @placename :location loc}))))))

(re-frame/register-handler
 :change-obj
 (fn [db [_ n id type]]
   (let [trope (nth (:our-tropes db) n)
         objs (:objects trope)
         tro (first (filter #(= (:id %) (:id trope)) (:tropes db)))
         i (first (indices #(= % type) (:objects tro)))
         objname (re-frame/subscribe [:objname-for-id id])]
     (assoc db :our-tropes (assoc-in (:our-tropes db) [n :objects] (assoc objs i {:id id :name @objname :type type}))))))

(re-frame/register-handler
 :tropes-changed
 (fn [db [_ cm]]
   (let [text (.getValue cm)
         cursor (.getCursor cm "head")]
     (do
       ;; (println text)
       (assoc
        (assoc
         (assoc db :trope-text text)
         :tropes-cursor-pos cursor)
        :success nil)
       ))))

(re-frame/register-handler
 :go-button
 (fn [db _]
   (let [scroller (.getElementById js/document "scroller")]
     (do
       (aset scroller "scrollTop" (.-scrollHeight scroller))
       db)))
 )

(re-frame/register-handler
 :add-trope
 (fn [db [_ id]]
   (assoc db :our-tropes (conj (vec (:our-tropes db)) {:id nil :subverted false}))))

(re-frame/register-handler
 :new-trope-name
 (fn [db [_ text]]
   (assoc-in db [:new-trope :label] text)
   ))

(re-frame/register-handler
 :editing-trope
 (fn [db [_ id]]
   (let [trope (re-frame/subscribe [:trope-for-id id])]
     (assoc (assoc db :editing-trope id) :trope-text (:source @trope)))))

(re-frame/register-handler
 :edit-tab-changed
 (fn [db [_ tab-id]]
   (let [editing (if (= tab-id :new) :new nil)]
     (assoc (assoc (assoc db :edit-trope-tab tab-id) :editing-trope editing) :trope-text ""))))

(re-frame/register-handler
 :update-trope
 (fn [db [_ hmap]]
   (let [editing (re-frame/subscribe [:editing-trope])
         name (re-frame/subscribe [:editing-trope-name])
         removed (remove #(= (:id %) @editing) (:tropes db))
         ]
     (assoc db :tropes (merge removed (merge {:id @editing} {:label (:label @name)} (:trope hmap)))))))


(re-frame/register-handler
 :save-trope
 (fn [db _]
   (let [
         trope-text (re-frame/subscribe [:trope-text])
         trope (re-frame/subscribe [:editing-trope-name])
         editing (re-frame/subscribe [:editing-trope])
         new? (= :new @editing)
         new-trope (assoc @trope :source @trope-text)]
     (if new?
       (do
         (POST (str host "/tropes/new") {:params new-trope
                                          :handler #(re-frame/dispatch [:edit-trope-handler %1])
                                          :error-handler #(re-frame/dispatch [:error-handler %1])
                                          :format :json
                                         })
         db)
       (do
         (POST (str host "/tropes/edit") {:params new-trope
                                          :handler #(re-frame/dispatch [:edit-trope-handler %1])
                                          :error-handler #(re-frame/dispatch [:error-handler %1])
                                          :format :json
                                          })
         db)))
   ))

(re-frame/register-handler
 :tab-changed
 (fn [db [_ tab-id]]
   (do
     (println db)
     (if (= tab-id :tab3) (re-frame/dispatch [:generate-story]))
     (assoc db :current-tab tab-id))))

(re-frame/register-handler
 :generate-story
 (fn [db _]
   (let [our-tropes (re-frame/subscribe [:our-tropes])]
     (do (POST (str host "/stories/new") {:params {:tropes @our-tropes}
                                       :handler #(re-frame/dispatch [:storygen-handler %1])
                                       :error-handler #(re-frame/dispatch [:error-handler %1])
                                          :format :json
                                          :response-format :json
                                          :keywords? true
                                          })))
   db))

(defn str-failure
  "Takes an augmented failure object and prints the error message"
  [{:keys [line column text reason]}]
  (let [
        ermsg (str "Parse error at line " line ", column " column ".\n")
        sorry "Check the javascript console for details."
        ]
    (str ermsg sorry)
    ))

(re-frame/register-handler
 :parse-trope
 (fn [db _]
   (let [trope-text (re-frame/subscribe [:trope-text])
         ptree (parse-trope @trope-text)
         tmap (make-map ptree @trope-text)]
     (do
       (if (insta/failure? ptree)
         (do
           (println ptree)
           (assoc db :error (str-failure (insta/get-failure ptree))))
         (do
           (println "PTREE: ")
           (println ptree)
           (println "MAP: ")
           (println tmap)
           (re-frame/dispatch [:update-trope tmap])
           (re-frame/dispatch [:save-trope])
           db
           ))
       ))
   ))

(re-frame/register-handler
 :initialize-db
 (fn  [_ _]
   db/default-db))
