(ns storybuilder.gen
  (:require [clojure.string :as str]
            ;; [com.rpl.specter :refer :all]
            [instaparse.core :as insta]))

(defn copy-meta
  [old new]
  (with-meta new (meta old)))


(defn make-string [words]
  (reduce str (interpose " " words)))

(defn param-map [key & args]
  (hash-map key (make-string args)))

(defn get-by-key [key xs]
  (map #(get % key) (filter #(get % key) xs)))

;; (defn get-unique-key [key xs]
;;   (map #(get % key) (filter #(get % key) xs)))


(defn walk-get-key [key xs]
  (->> (tree-seq #(or (map? %) (vector? %)) identity xs)
       (filter #(if (and (map? %) (get % key)) true false))
       (map #(str/capitalize (get % key)))
       set
       vec))

(defn remove-blank [xs]
  (filter #(not (= (:verb %) "")) xs))

(defn process-ors [events]
  (loop [evs events acc [] ors []]
    (if (empty? evs) acc
        (cond
          (and (not (:or (first evs))) (:or (second evs))) (recur (rest evs) acc (conj ors (first evs)))
          (and (:or (first evs)) (:or (second evs))) (recur (rest evs) acc (conj ors (:or (first evs))))
          (and (:or (first evs)) (not (:or (second evs)))) (recur (rest evs) (conj acc {:or (conj ors (:or (first evs)))}) [])
          :else (recur (rest evs) (conj acc (first evs)) ors)
          )
        )))

(defn make-map [ptree]
  (insta/transform
   {:verb (partial param-map :verb)
    :item (partial param-map :object)
    :move (fn [& args] {:verb "go" :place (first (get-by-key :place args))})
    :mverb (partial param-map :mverb)
    :pverb (partial param-map :verb)
    :place (partial param-map :place)
    :conditional (fn [& args] {:if (apply merge args)})
    :outcome (fn [& args] {:then (first args)})
    :adjective (partial param-map :adjective)
    :object (partial param-map :object)
    :fluent (partial merge)
    :character (partial param-map :role)
    :sequence (fn [& args] {:events (into [] (remove-blank args))})
    :or (partial param-map :or)
    :situation (fn [& args] (first args))
    :situationdef (fn [& args] {:situation {:when (first args) :norms (into [] (map first (rest args)))}})
    :consequence (fn [& args] (hash-map :consequence (apply merge args)))
    :permission (fn [& args] (hash-map :permission (apply merge args)))
    :deadline (fn [& args] (hash-map :deadline (:consequence (first args))))
    :violation (fn [& args] (hash-map :violation (first (first args))))
    :visit (fn [& args] (hash-map :verb (first args) :place (:object (second args))))
    :give (fn [& args] (let [chars (get-by-key :role args)] {:verb "give" :from (first chars) :to (second chars) :object (first (get-by-key :object args))}))
    :sell (fn [& args] (let [chars (get-by-key :role args)] {:verb "give" :from (first chars) :to (second chars) :object (first (get-by-key :object args))}))
    :meet (fn [& args] (let [chars (get-by-key :role args)] {:verb "meet" :role-a (first chars) :role-b (second chars)}))
    :kill (fn [& args] (let [chars (get-by-key :role args)] {:verb "kill" :role-a (first chars) :role-b (second chars)}))
    :pay (fn [& args] (let [chars (get-by-key :role args)] {:verb "pay" :role-b (first chars)}))
    :task (partial merge)
    :norms (fn [& args] (first args))
    :obligation (fn [& args] {:obligation (apply merge args)})
    :event (partial merge)
    :tverb (fn [& args] (let [chars (get-by-key :role args) objs (get-by-key :object args)] {:verb (first (get-by-key :verb args)) :role-a (first chars) :role-b (second chars) :object (first objs)}))
    :bverb (fn [& args] (let [chars (get-by-key :role args)] {:verb (first (get-by-key :verb args)) :role-b (first chars) :object (first (get-by-key :object args))}))
    :cverb (fn [& args] (let [char (first (get-by-key :role args)) obj (first (get-by-key :object args))] (merge {:verb (make-string (filter string? args))} (if (nil? char) {:object obj} {:role-b char}))))
    :happens (partial merge)
    :block (fn [& args] {:block (:subtrope (first args))})
    ;; :trope (fn [& args] {:trope {:roles (walk-get-key :role args)}})
    :subtrope (fn [& args] {:subtrope (make-string args)})
    :label (fn [& args] {:label (make-string args)})
    :trope (fn [& args] {:trope {:label (first (filter some? (map :label args)))
                                 :roles (vec (set (concat (mapcat #(walk-get-key :role %) args) (mapcat #(walk-get-key :role-a %) args) (mapcat #(walk-get-key :role-b %) args))))
                                 :objects (vec (set (mapcat #(walk-get-key :object %) args)))
                                 :locations (vec (set (mapcat #(walk-get-key :place %) args)))
                                 :events (process-ors (mapcat :events args))
                                 ;; :events (mapcat :events args)
                                 :situations (mapcat :situation args)}})
    }
   ptree))
