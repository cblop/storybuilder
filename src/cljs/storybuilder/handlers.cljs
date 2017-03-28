(ns storybuilder.handlers
  (:require [re-frame.core :as re-frame]
            [ajax.core :refer [GET POST]]
            [cljsjs.chance]
            [storybuilder.db :as db]
            [storybuilder.parser :refer [parse-trope]]
            ;; [storybuilder.output-parser :refer [trace-to-options]]
            [instaparse.core :as insta]
            [storybuilder.gen :refer [make-map]]
            [clojure.string :as str]))

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
   ;; (re-frame/dispatch [:story-event])
   ;; (assoc (assoc db :story-id (:id response)) :story-text (clojure.string/split-lines (:text response)))
   (assoc (assoc db :story-id (:id response)) :story-sets (:sets response))
   ))

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

(defn slugify [n]
  (-> n
      (.toLowerCase)
      (str/replace #"\s" "-")))

(defn random-character [role]
  (let [chance (js/Chance.)
        cname (str (.first chance) " " (.last chance))]
      {:id (slugify cname) :random true :label cname :role role}))

(defn random-object [type]
  (do
    (let [chance (js/Chance.)
          oname (.word chance)]
      {:id (slugify oname) :random true :label oname :type type})))

(defn random-place [loc]
  (do
    (let [chance (js/Chance.)
          pname (.city chance)]
      {:id (slugify pname) :random true :label pname :location loc})))

(re-frame/register-handler
 :change-trope
 (fn [db [_ n id]]
   (let [trope (first (filter #(= (:id %) id) (:tropes db)))
         roles (:roles trope)
         objects (:objects trope)
         locs (:locations trope)
         ]
     ;; (println (:our-tropes db))
     (assoc db :our-tropes (assoc (:our-tropes db) n {:id id :label (:label trope) :events (:events trope) :subverted false :places (into [] (take (count locs) (repeat nil))) :objects (into [] (take (count objects) (repeat nil))) :characters (into [] (take (count roles) (repeat nil)))})))))


(re-frame/register-handler
 :change-nil-char
 (fn [db [_ n i char]]
   (let [trope (nth (:our-tropes db) n)
         chars (:characters trope)]
     (println (str "nILCHAR: " n i))
     (assoc db :our-tropes (assoc-in (:our-tropes db) [n :characters] (assoc chars i char))))))


(re-frame/register-handler
 :change-nil-obj
 (fn [db [_ n i obj]]
   (let [trope (nth (:our-tropes db) n)
         objs (:objects trope)]
     (assoc db :our-tropes (assoc-in (:our-tropes db) [n :objects] (assoc objs i obj))))))


(re-frame/register-handler
 :change-nil-place
 (fn [db [_ n i place]]
   (let [trope (nth (:our-tropes db) n)
         plcs (:places trope)]
     (assoc db :our-tropes (assoc-in (:our-tropes db) [n :places] (assoc plcs i place))))))

(re-frame/register-handler
 :change-char
 (fn [db [_ n id role]]
   (let [trope (nth (:our-tropes db) n)
         chars (:characters trope)
         tro (first (filter #(= (:id %) (:id trope)) (:tropes db)))
         i (first (indices #(= % role) (:roles tro)))
         charname (re-frame/subscribe [:charname-for-id id])]
     (assoc db :our-tropes (assoc-in (:our-tropes db) [n :characters] (assoc chars i {:id id :label @charname :role role}))))))


(re-frame/register-handler
 :change-place
 (fn [db [_ n id loc]]
   (let [trope (nth (:our-tropes db) n)
         places (:places trope)
         tro (first (filter #(= (:id %) (:id trope)) (:tropes db)))
         i (first (indices #(= % loc) (:locations tro)))
         placename (re-frame/subscribe [:placename-for-id id])]
     (assoc db :our-tropes (assoc-in (:our-tropes db) [n :places] (assoc places i {:id id :label @placename :location loc}))))))

(re-frame/register-handler
 :change-obj
 (fn [db [_ n id type]]
   (let [trope (nth (:our-tropes db) n)
         objs (:objects trope)
         tro (first (filter #(= (:id %) (:id trope)) (:tropes db)))
         i (first (indices #(= % type) (:objects tro)))
         objname (re-frame/subscribe [:objname-for-id id])]
     (assoc db :our-tropes (assoc-in (:our-tropes db) [n :objects] (assoc objs i {:id id :label @objname :type type}))))))

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
 :scroll-down
 (fn [db _]
   (let [scroller (.getElementById js/document "scroller")]
     (do
       (aset scroller "scrollTop" (.-scrollHeight scroller))
       db)))
 )

;; (re-frame/register-handler
;;  :go-button
;;  (fn [db _]
;;    db))

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


(defn nil-indices [items]
  (->> items
       (map-indexed vector)
       (filter #(nil? (second %)))
       (map first)
       ))

(defn nil-types [items key tropeid]
  (let [indices (nil-indices items)
        trope (re-frame/subscribe [:trope-for-id tropeid])
        types (get @trope key)
        ]
    (map #(vector % (nth types %)) indices)))

(re-frame/register-handler
 :generate-randoms
 (fn [db _]
   (let [our-tropes (re-frame/subscribe [:our-tropes])
         nil-roles (keep-indexed #(vector %1 (nil-types (:characters %2) :roles (:id %2))) @our-tropes)
         nil-objects (keep-indexed #(vector %1 (nil-types (:objects %2) :objects (:id %2))) @our-tropes)
         nil-places (keep-indexed #(vector %1 (nil-types (:places %2) :locations (:id %2))) @our-tropes)
         ]
     (do
       (println "ROLES:")
       (println nil-roles)
       (println "OBJs:")
       (println nil-objects)
       (println "PLACES:")
       (println nil-places)
       (doseq [nrs nil-roles]
           (doseq [nss (second nrs)]
             (re-frame/dispatch [:change-nil-char (first nrs) (first nss) (random-character (second nss))])))

       (doseq [nrs nil-objects]
         (if-not (empty? (second nrs))
           (doseq [nss (second nrs)]
             (re-frame/dispatch [:change-nil-obj (first nrs) (first nss) (random-object (second nss))]))))

       (doseq [nrs nil-places]
         (if-not (empty? (second nrs))
           (doseq [nss (second nrs)]
             (re-frame/dispatch [:change-nil-place (first nrs) (first nss) (random-place (second nss))]))))
       db)
     )
     ))

(re-frame/register-handler
 :tab-changed
 (fn [db [_ tab-id]]
   (do
     (println db)
     (if (= tab-id :tab3) (re-frame/dispatch [:generate-randoms]))
     (assoc db :current-tab tab-id))))

(re-frame/register-handler
 :change-player
 (fn [db [_ player]]
   (assoc db :player player)))

(re-frame/register-handler
 :update-graph
 (fn [db [_ graph]]
   (assoc db :story-graph graph)))

(re-frame/register-handler
 :update-story-verb
 (fn [db [_ verb]]
   (assoc db :story-verb verb)))


(re-frame/register-handler
 :update-story-object-a
 (fn [db [_ object]]
   (assoc db :story-object-a object)))


(re-frame/register-handler
 :update-story-object-b
 (fn [db [_ object]]
   (assoc db :story-object-a object)))

(re-frame/register-handler
 :story-event-handler
 (fn [db [_ response]]
   ;; (println (trace-to-options (:text response)))
   (do
     (println (:sets response))
     (assoc db :story-sets (:sets response))
     )))

(re-frame/register-handler
 :story-refresh
 (fn [db _]
   (re-frame/dispatch [:story-action {:event nil :params []}])
   db))

(re-frame/register-handler
 :story-action
 (fn [db [_ event]]
   (let [player (re-frame/subscribe [:player])
         story-id (re-frame/subscribe [:story-id])
         lookahead (re-frame/subscribe [:lookahead])]
     (do
       (println "PLAYER: " @player)
       (println "EVENT: " event)
       (POST (str host "/stories/event") {:params
                                          {:story-id @story-id
                                           ;; :player @player
                                           :player (first (:params event))
                                           :verb (:event event)
                                           ;; :lookahead @lookahead
                                           :lookahead 5 ; ignore
                                           ;; :object-a (first (:params event))
                                           :object-a (if (second (:params event)) (second (:params event)) nil)
                                           :object-b (if (> (count (:params event)) 2) (nth (:params event) 2) nil)}
                                          :handler #(re-frame/dispatch [:story-event-handler %1])
                                          :error-handler #(re-frame/dispatch [:error-handler %1])
                                          :format :json
                                          :response-format :json
                                          :keywords? true})
       db))))


(re-frame/register-handler
 :story-event
 (fn [db _]
   (let [verb (re-frame/subscribe [:story-verb])
         object-a (re-frame/subscribe [:story-object-a])
         object-b (re-frame/subscribe [:story-object-b])
         player (re-frame/subscribe [:player])
         story-id (re-frame/subscribe [:story-id])
         lookahead (re-frame/subscribe [:lookahead])
         ]
     (do
       (POST (str host "/stories/event") {:params
                                          {:id @story-id
                                           :story-id @story-id
                                           :player @player
                                           :verb @verb
                                           ;; :lookahead @lookahead
                                           :lookahead 5 ; ignoring
                                           :object-a @object-a
                                           :object-b @object-b}
                                          :handler #(re-frame/dispatch [:story-event-handler %1])
                                          :error-handler #(re-frame/dispatch [:error-handler %1])
                                          :format :json
                                          :response-format :json
                                          :keywords? true
                                          })
       db))
   ))


(re-frame/register-handler
 :reset-vis
 (fn [db _]
   (re-frame/dispatch [:generate-story])
   db))


(re-frame/register-handler
 :change-lookahead
 (fn [db [_ la]]
   ;; (assoc db :lookahead la)
   db ;; ignore
   ))

(re-frame/register-handler
 :generate-story
 (fn [db _]
   (let [our-tropes (re-frame/subscribe [:our-tropes])
         our-characters (re-frame/subscribe [:our-characters])
         our-objects (re-frame/subscribe [:our-objects])
         our-places (re-frame/subscribe [:our-places])
         lookahead (re-frame/subscribe [:lookahead])
         player (re-frame/subscribe [:player])
         role-pairs (map #(hash-map :class (:role %) :iname (:label %)) @our-characters)
         obj-pairs (map #(hash-map :class (:type %) :iname (:label %)) @our-objects)
         place-pairs (map #(hash-map :class (:location %) :iname (:label %)) @our-places)
         story {:storyname "story"
                :tropes (map :label @our-tropes)
                :instances (concat role-pairs obj-pairs place-pairs)
                }]
     (do (POST (str host "/stories/new") {:params {:story story
                                                   :tropes @our-tropes
                                                   :characters @our-characters
                                                   :objects @our-objects
                                                   :places @our-places
                                                   ;; :lookahead @lookahead
                                                   :lookahead 5 ; ignore
                                                   :player @player}
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
   (do
     (re-frame/dispatch [:save-trope])
     db
     )
   ))

(re-frame/register-handler
 :initialize-db
 (fn  [_ _]
   db/default-db))
