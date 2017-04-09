(ns storybuilder.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            [re-com.core :as com]
            [cljsjs.vis]

            [instaparse.print :as print]))


;; (strokes/bootstrap)


;; GENERAL

(def tab-list [{:id :tab1 :label "Edit"}
               {:id :tab2 :label "Arrange"}
               ])

(def spacer [com/gap :size "5px"])
(def gap [com/gap :size "15px"])


(defn title []
  (fn []
    [com/h-box
     :justify :center
     :children [
                [com/title
                 :label "Story Builder"
                 :level :level1]
                ]
     ]))


;; EDIT

;; -- TROPES

(defn codemirror-inner []
  (let [codem (atom nil)
        options (clj->js {"mode" "python", "lineNumbers" "true"})
        ;; trope-text (re-frame/subscribe [:trope-text])
        update (fn [comp]
                   (do
                     (.setValue @codem (:text (reagent/props comp)))
                     (.setCursor @codem (clj->js (:cursor (reagent/props comp))))
                     ))
        ]
    (reagent/create-class
     {:reagent-render (fn []
                        [com/input-textarea
                         :attr {:id "tropes-editor"}
                         :width "300px"
                         :height "400px"
                         :model ""
                         :on-change #()
                         ;; :on-change #(re-frame/dispatch [:tropes-changed %])
                         ]
                        )
      :component-did-mount (fn [comp]
                             (let [canvas (.getElementById js/document "tropes-editor")
                                   cm (.fromTextArea js/CodeMirror canvas options)]
                               (do
                                 (.on cm "change" #(re-frame/dispatch [:tropes-changed %]))
                                 (reset! codem cm)))
                             (update comp))
      :component-did-update update
      :display-name "codemirror-inner"})
    ))


(defn new-trope []
  (let [new-trope (re-frame/subscribe [:new-trope])]
    [com/v-box
     :children [
                [com/label :label "Trope Name"]
                spacer
                [com/input-text
                 :width "100%"
                 :model (if-not (nil? @new-trope) (:label @new-trope) "")
                 :change-on-blur? false
                 :on-change #(re-frame/dispatch [:new-trope-name %])]
                ]]))

(defn edit-trope-select []
  (let [
        ;; our-tropes (re-frame/subscribe [:our-tropes])
        all-tropes (re-frame/subscribe [:tropes])
        editing-trope (re-frame/subscribe [:editing-trope])
        ]
    [com/v-box
     :children [
                [com/label :label "Trope Name"]
                spacer
                [com/single-dropdown
                 :width "100%"
                 :choices @all-tropes
                 ;; :choices @all-tropes
                 ;; :model (:id (nth @our-tropes n))
                 :model @editing-trope
                 :filter-box? true
                 :on-change #(re-frame/dispatch [:editing-trope %])
                 ]]]))


(defn edit-trope-tabs
  []
  (let [tabid (re-frame/subscribe [:edit-trope-tab])]
    [com/v-box
     :children [
                [com/horizontal-bar-tabs
                 :tabs [{:id :edit :label "Edit"} {:id :new :label "New"}]
                 :model @tabid
                 :on-change #(re-frame/dispatch [:edit-tab-changed %])
                 ]
                gap
                (case @tabid
                  :edit [edit-trope-select]
                  :new [new-trope]
                  gap)
                ]]))



(defn delete-trope-button []
  [com/button
   :label "Delete"
   :class "btn-danger"
   :on-click #(re-frame/dispatch [:delete-trope])])

(defn sleep [msec]
  (let [deadline (+ msec (.getTime (js/Date.)))]
    (while (> deadline (.getTime (js/Date.))))))

(defn save-trope-button []
  [com/button
   :label "Save Trope"
   :class "btn-primary"
   :on-click #(do
                (re-frame/dispatch-sync [:parse-trope])
                (re-frame/dispatch-sync [:load-tropes])
                (re-frame/dispatch-sync [:compiling true])
                (sleep 100)
                (re-frame/dispatch-sync [:refresh-trope])
                (re-frame/dispatch-sync [:generate-story])
                (re-frame/dispatch-sync [:compiling false])
                )])


(defn edit-tab []
  (let [trope-text (re-frame/subscribe [:trope-text])
        cursor (re-frame/subscribe [:tropes-cursor-pos])
        edit-facet (re-frame/subscribe [:edit-facet])
        trope-name (re-frame/subscribe [:editing-trope-name])
        error (re-frame/subscribe [:error])
        success (re-frame/subscribe [:success])
        ]
    [com/v-box
     :padding "25px"
     :children [
                (if @trope-name
                  [com/title :style {:padding-left "40px" :padding-bottom "10px"} :level :level3 :label (str "\"" (if (= (:label @trope-name) "") "<no name>" (:label @trope-name)) "\" is a trope where:")])
                [com/v-box
                 :children [
                            [codemirror-inner {:text @trope-text
                                               :cursor @cursor}]
                            gap
                            [com/v-box
                             :width "200px"
                             :children [
                                        [edit-trope-tabs]
                                        gap
                                        [com/h-box
                                         :justify :end
                                         :children [
                                                    spacer
                                                    [delete-trope-button]
                                                    spacer
                                                    [save-trope-button]]
                                         ]
                                        gap
                                        [com/h-box
                                         :justify :end
                                         :children [
                                                    (if @success
                                                      [:span {:style {:font-size "30px" :color "green"}} \u2713])
                                                    (if @error
                                                      [:span {:style {:font-size "30px" :color "red"}} \u2718])]]
                                        ]
                             ]
                            ]]]]))

;; SCENES


(defn place-select [loc places sel n]
  (let [
        ;; chars (re-frame/subscribe [:chars-for-archetype role])
        ;; our-tropes (re-frame/subscribe [:our-tropes])
        ;; this-trope (nth @our-tropes n)
        ]
    [com/v-box
     :children [
                [com/label :label loc :style {:font-size "smaller"}]
                spacer
                [com/single-dropdown
                 :width "180px"
                 :choices places
                 :placeholder "(Randomly generated)"
                 ;; TODO: make random
                 :model (:id sel)
                 ;; :model nil
                 :filter-box? true
                 :on-change #(do
                               (re-frame/dispatch [:change-place n % loc])
                               (re-frame/dispatch [:generate-story]))]]]))


(defn obj-select [type objs sel n]
  (let [
        ;; chars (re-frame/subscribe [:chars-for-archetype role])
        ;; our-tropes (re-frame/subscribe [:our-tropes])
        ;; this-trope (nth @our-tropes n)
        ]
    [com/v-box
     :children [
                [com/label :label type :style {:font-size "smaller"}]
                spacer
                [com/single-dropdown
                 :width "180px"
                 :choices objs
                 :placeholder "(Randomly generated)"
                 ;; TODO: make random
                 :model (:id sel)
                 ;; :model nil
                 :filter-box? true
                 :on-change #(re-frame/dispatch [:change-obj n % type])]]]))


(defn char-select [role chars sel n]
  (let [
        ;; chars (re-frame/subscribe [:chars-for-archetype role])
        ;; our-tropes (re-frame/subscribe [:our-tropes])
        ;; this-trope (nth @our-tropes n)
        ]
    [com/v-box
     :children [
                [com/label :label role :style {:font-size "smaller"}]
                spacer
                [com/single-dropdown
                 :width "180px"
                 :choices chars
                 :placeholder "(Randomly generated)"
                 ;; TODO: make random
                 :model (:id sel)
                 ;; :model nil
                 :filter-box? true
                 :on-change #(do
                               (re-frame/dispatch [:change-char n % role])
                               (re-frame/dispatch [:generate-story]))]]]))


(defn places [n]
  (let [
        locs (re-frame/subscribe [:locations n])
        all-places (re-frame/subscribe [:places-for-locations @locs])
        our-tropes (re-frame/subscribe [:our-tropes])
        sel-places (:places (nth @our-tropes n))
        ;; p (println "PLACES: ")
        triples (map vector (set @locs) (set @all-places) sel-places)
        ;; p (println triples)
        ;; our-tropes (re-frame/subscribe [:our-tropes])
        ;; archetypes (:archetypes (nth @our-tropes n))
        ]
    (if-not (or (empty? @locs) (nil? @locs))
      [com/v-box
       :style {:padding "20px" :background-color "#ffdddd" :border "#ff9999 solid 2px"}
       :children (concat [[com/label :label "Places"] gap] (into []
                                                                  (apply concat (for [[x y z] triples]
                                                                                  [[place-select x y z n] spacer]))
                                                                  ))
       ])
    ))

(defn objects [n]
  (let [
        types (re-frame/subscribe [:types n])
        all-objs (re-frame/subscribe [:objs-for-types @types])
        our-tropes (re-frame/subscribe [:our-tropes])
        sel-objs (:objects (nth @our-tropes n))
        ;; p (println "OBJS: ")
        triples (map vector (set @types) (set @all-objs) sel-objs)
        ;; p (println triples)
        ;; our-tropes (re-frame/subscribe [:our-tropes])
        ;; archetypes (:archetypes (nth @our-tropes n))
        ]
    (if-not (or (empty? @types) (nil? @types))
      [com/v-box
       :style {:padding "20px" :background-color "#ffdd77" :border "#ffbb00 solid 2px"}
       :children (concat [[com/label :label "Objects"] gap] (into []
                             (apply concat (for [[x y z] triples]
                                             [[obj-select x y z n] spacer]))
                             ))
       ])
     ))

(defn characters [n]
  (let [
        roles (re-frame/subscribe [:roles n])
        subverted (re-frame/subscribe [:subverted? n])
        all-chars (re-frame/subscribe [:chars-for-roles @roles])
        our-tropes (re-frame/subscribe [:our-tropes])
        sel-chars (:characters (nth @our-tropes n))
        ;; s-chars (if (nil? sel-chars) (take (count @archetypes) (repeat nil)) sel-chars)
        ;; chars (if @subverted (reverse @all-chars) @all-chars)
        ;; p (println chars)
        ;; p (println "ROLES: ")
        triples (map vector (set @roles) (set @all-chars) sel-chars)
        ;; p (println triples)
        ;; our-tropes (re-frame/subscribe [:our-tropes])
        ;; archetypes (:archetypes (nth @our-tropes n))
        ]
    (if-not (or (empty? @roles) (nil? @roles))
      [com/v-box
       :style {:padding "20px" :background-color "#ddddff" :border "#9999ff solid 2px"}
       :children (concat [[com/label :label "Characters"] gap] (into []
                             (apply concat (for [[x y z] triples]
                                             [[char-select x y z n] spacer]))
                             ))
       ])
     ))


(defn trope-select [n]
  (let [our-tropes (re-frame/subscribe [:our-tropes])
        all-tropes (re-frame/subscribe [:tropes])]
    [com/v-box
     :children [
                [com/label :label "Trope Name"]
                spacer
                [com/v-box
                 :children [
                            [com/single-dropdown
                             :width "250px"
                             :choices @all-tropes
                             :model (:id (nth @our-tropes n))
                             :filter-box? true
                             :on-change #(do (re-frame/dispatch-sync [:change-trope n %])
                                             (re-frame/dispatch-sync [:compiling true])
                                             (re-frame/dispatch-sync [:generate-blanks])
                                             (re-frame/dispatch-sync [:reset-vis])
                                             (re-frame/dispatch-sync [:compiling false])
                                             )]
                            ;; gap

                            ;; [save-trope-button]
                            ]]]]))




(defn subvert-trope [n]
  (let [subverted (re-frame/subscribe [:subverted? n])]
    [com/h-box
     :justify :center
     :children [
                [com/button
                 :class "btn-warning"
                 :label (if @subverted "Un-subvert" "Subvert")
                 :on-click #(re-frame/dispatch [:subvert-trope n])]]])
  )


(defn remove-trope [n]
  [com/h-box
   :justify :center
   :children [
              [com/button
               :class "btn-danger"
               :label "Delete"
               :on-click #(re-frame/dispatch [:remove-trope n])]]]
  )


(defn trope-box [n]
  (let [
        subverted (re-frame/subscribe [:subverted? n])
        roles (re-frame/subscribe [:roles n])
        ;; p (println "NTH: ")
        ;; p (println n)
        ]
    [com/v-box
     :style (if @subverted {:background-color "#ffdddd" :border "#ff9999 solid 2px"}
                {:background-color "#ddffdd"
                 :border "2px solid #99ff99"})
     :padding "10px"
     :children [[trope-select n]
                gap
                [com/v-box
                 :children [
                            [characters n]
                            gap
                            [places n]
                            gap
                            [objects n]
                            ]]
                gap
                [com/h-box
                 :justify :center
                 :children [
                            ;; (if (> (count @roles) 1)
                            ;;   [subvert-trope n])
                            ;; (if (and (> n 0) (> (count @roles) 1))
                            ;;   gap)
                            (if (> n 0)
                              [remove-trope n])
                            ]]
                ]
     ]))

(defn trope-boxes []
  (let [our-tropes (re-frame/subscribe [:our-tropes])
        boxes (into [] (apply concat (for [t (range (count @our-tropes))] [[trope-box t] gap])))]
    [com/v-box
     :children boxes
     ]))

(defn add-trope []
  [com/h-box
   :justify :center
   :children [
              [com/md-circle-icon-button
               :md-icon-name "zmdi-plus"
               :emphasise? true
               :on-click #(re-frame/dispatch [:add-trope])]]])

(defn trope-content []
  [com/h-box
   :justify :center
   :children [
              [com/v-box
               :margin "50px"
               :width "300px"
               :children [
                          [trope-boxes]
                          [add-trope]
                          ]]]])


(defn arrange-tab []
  (let [our-tropes (re-frame/subscribe [:our-tropes])]
    (do
      (if (empty? @our-tropes) (re-frame/dispatch [:add-trope]))
      [com/scroller
       :v-scroll :auto
       :height "800px"

       :child
       [trope-content]])))

;; again, just the first event
(defn index->event
  [index]
  (let [graph (re-frame/subscribe [:story-graph])]
    (:event (first (filter #(= (:id %) index) (:nodes @graph))))
    ))


(defn index->node
  [index]
  (let [graph (re-frame/subscribe [:story-graph])]
    (first (filter #(= (:id %) index) (:nodes @graph)))
    ))

(defn merge-nodes
  [graph]
  (let [parts (partition-by :level (:nodes graph))]))

;; move this to handlers.cljs
;; don't forget: you _could_ have multiple events in each timestep!
;; will want [events data] to prepend previous events
(defn data->graph [data]
  (let [graph
        (loop [answer-sets data as-num 1 nodes [{:id 0 :label "START" :level 0 :color "#FF3333"}] edges []]
          (if (empty? answer-sets) {:nodes nodes :edges edges}
              (let [options
                    (loop [time-step (first answer-sets) ts-nodes [] ts-edges [] ts-num 1 prev-id 0]
                      (if (empty? time-step) {:nodes ts-nodes :edges ts-edges}
                          (let [viol-inst (first (map :inst (filter :inst (map :viol (:occurred (first time-step))))))
                                event (first (remove #(or (= (:inst %) viol-inst) (:viol %) (= (apply str (take 3 (:event %))) "int")) (:occurred (first time-step)))) ; NOTE: this is just the FIRST event
                                label (str (:event event) " " (apply str (interpose " " (:params event))))
                                peers (filter #(and (= (:inst (:event %)) (:inst event)) (= (:label %) label) (= (:level %) ts-num)) nodes)
                                ;; unique (if (seq (filter #(= (:label %) label) peers)) false true)
                                ;; peer-id (:id (first (filter #(= (:label %) label) peers)))
                                peer-id (:id (first peers))
                                peer-ids (map :id peers)
                                ;; p (println (count peers))
                                linked (seq (filter #(and (= (:from %) prev-id) (some (fn [x] (= (:to %) x)) peer-ids)) edges))
                                ;; linked false
                                ;; p (if-not linked (println (:from (first peers))))
                                this-id (if-not linked (int (gensym "")) peer-id)
                                ;; e (merge {:from prev-id :to this-id :label (:inst event) :font (if (> ts-num 1) {:align "bottom" :color "#dddddd"} {:align "bottom"})} (if (> ts-num 1) {:color "#dddddd"}))
                                e (if linked (assoc (apply merge (map :edges peers)) :to this-id) {:from prev-id :to this-id :label (:inst event) :font {:align "bottom"}})
                                n (if linked (assoc (apply merge (map :nodes peers)) :id this-id) {:label label :id this-id :level ts-num :event event :prev prev-id})]
                            (if (and event (not linked))
                              (recur (rest time-step) (conj ts-nodes n) (conj ts-edges e) (inc ts-num) this-id)
                              (recur (rest time-step) ts-nodes ts-edges (inc ts-num) this-id)
                              ))))]
                (recur (rest answer-sets) (inc as-num) (concat nodes (:nodes options)) (concat edges (:edges options))))))]
    (if (= (count (:nodes graph)) 1) (assoc-in graph [:nodes 0] {:id 0 :label "COMPILE ERROR" :level 0 :color "#ffffff" :shape "text"}) graph)
  ))

;; (defn data->graph [data]
;;   (println (str (count data) " answer sets."))
;;   (let [graph
;;         (loop [answer-sets data as-num 1 nodes [{:id 0 :label "START" :level 0 :color "#FF3333"}] edges []]
;;           (if (empty? answer-sets) {:nodes nodes :edges edges}
;;               (let [options
;;                     (loop [time-step (first answer-sets) ts-nodes [] ts-edges [] ts-num 1 prev-id 0]
;;                       (if (empty? time-step) {:nodes ts-nodes :edges ts-edges}
;;                           (let [viol-inst (first (map :inst (filter :inst (map :viol (:occurred (first time-step))))))
;;                                 events (remove #(or (= (:inst %) viol-inst) (:viol %) (= (apply str (take 3 (:event %))) "int")) (:occurred (first time-step))) ; NOTE: this is just the FIRST event
;;                                 new-nodes (loop [es events e-nodes [] e-edges [] last-id prev-id]
;;                                             (if (empty? es) {:nodes e-nodes :edges e-edges :last last-id}
;;                                                 (let [event (first es)
;;                                                       label (str (:event event) " " (apply str (interpose " " (:params event))))
;;                                                       ;; peers (filter #(= (:level %) ts-num) nodes)
;;                                                       ;; peers (filter #(and (= (:inst (:event %)) (:inst event)) (= (:label %) label) (= (:level %) ts-num)) nodes)
;;                                                       peers (filter #(and (= (:inst (:event %)) (:inst event)) (= (:label %) label) (= (:level %) ts-num)) nodes)
;;                                                       ;; peer-id (:id (first (filter #(= (:label %) label) peers)))
;;                                                       ;; peer-ids (map :id (filter #(and (= (:inst (:event %)) (:inst event)) (= (:label %) label)) peers))
;;                                                       peer-id (:id (first peers))

;;                                                       ;; linked-nodes (remove nil? (for [e edges]                             ;; return ones that are already linked
;;                                                       ;;                             (if (and (= (:from e) prev-id)
;;                                                       ;;                                      (some #(= (:to e %)) peer-ids))
;;                                                       ;;                               (:to e))))

;;                                                       p (println (count peers))
;;                                                       p (println peers)
;;                                                       ;; p (println linked-nodes)
;;                                                       ;; linked (first linked-nodes)               ;; get the first result
;;                                                       linked (first (filter #(and (= (:from %) prev-id) (= (:to %) peer-id)) edges))
;;                                                       linked false
;;                                                       ;; peer-id (first peer-ids)
;;                                                       ;; linked (first (filter #(and (= (:from %) last-id) (= (:to %) peer-id)) edges))
;;                                                       ;; linked (seq (filter #(and (= (:inst last-id) (:inst peer-id)) (= (:from %) last-id) (= (:to %) peer-id)) edges))
;;                                                       this-id (if-not linked (int (gensym "")) linked)
;;                                                       inst (if-not linked (:inst event) (:inst (:event (first (filter #(= (:id %) this-id) nodes)))))
;;                                                       ;; inst (:inst event)
;;                                                       ;; e (merge {:from prev-id :to this-id :label (:inst event) :font (if (> ts-num 1) {:align "bottom" :color "#dddddd"} {:align "bottom"})} (if (> ts-num 1) {:color "#dddddd"}))
;;                                                       ee {:from prev-id :to this-id :label inst :font {:align "bottom"}}
;;                                                       en {:label label :id this-id :level ts-num :event event}]
;;                                                   (if (and event (not linked))
;;                                                     (recur (rest es) (conj e-nodes en) (conj e-edges ee) this-id)
;;                                                     (recur (rest es) e-nodes e-edges this-id)
;;                                                     ))))]
;;                             (recur (rest time-step) (concat ts-nodes (:nodes new-nodes)) (concat ts-edges (:edges new-nodes)) (inc ts-num) (:last new-nodes)))))]
;;                 (recur (rest answer-sets) (inc as-num) (concat nodes (:nodes options)) (concat edges (:edges options))))))]
;;     (if (= (count (:nodes graph)) 1) (assoc-in graph [:nodes 0] {:id 0 :label "COMPILE ERROR" :level 0 :color "#ffffff" :shape "text"})
;;         graph)
;;   ))

;; (empty? (clojure.set/intersection (set [1 5 7]) (set [2 3 4 1])))

;; (defn data->graph [data]
;;   (let [graph
;;         (loop [answer-sets data as-num 1 nodes [{:id 0 :label "START" :level 0 :color "#FF3333"}] edges []]
;;           (if (empty? answer-sets) {:nodes nodes :edges edges}
;;               (let [options
;;                     (loop [time-step (first answer-sets) ts-nodes [] ts-edges [] ts-num 1 prev-ids [0]]
;;                       (if (empty? time-step) {:nodes ts-nodes :edges ts-edges}
;;                           (let [events (remove #(or (:viol %) (= (apply str (take 3 (:event %))) "int")) (:occurred (first time-step)))
;;                                 mknode (fn [ev]
;;                                          (let [label (str (:event ev) " " (apply str (interpose " " (:params ev))))
;;                                                peers (filter #(= (:level %) ts-num) nodes)             ;; are there any nodes already at the same level?
;;                                                peer-ids (map :id (filter #(= (:label %) label) peers)) ;; get their ids
;;                                                linked-nodes (for [e edges]                             ;; return ones that are already linked
;;                                                         (if (and (some #(= (:from e) %) prev-ids)
;;                                                                  (some #(= (:to e %)) peers))
;;                                                           (:to e)))
;;                                                ;; linked-nodes (clojure.set/intersection (set peer-ids) (set (map :to edges)))
;;                                                linked (first (remove nil? linked-nodes))               ;; get the first result
;;                                                ;; linked false
;;                                                p (println (map #(str "from: " (:from %) " to: " (:to %)) edges))
;;                                                p (println linked-nodes)
;;                                                p (println linked)
;;                                                p (println (str "<-: " prev-ids))
;;                                                p (println (str "==: " peer-ids))
;;                                                ;; linked (seq (filter #(and (= (:from %) prev-id) (= (:to %) peer-id)) edges))
;;                                                ;; linked (not (and (empty? (clojure.set/intersection (set prev-ids) (set (map :from edges)))) (empty? (clojure.set/intersection (set peer-ids) (set (map :to edges))))))
;;                                                ;; linked false
;;                                                this-id (if-not linked (int (gensym "")) linked)        ;; if it's already linked, that's the current node, otherwise make a new id
;;                                                es (map #(hash-map :from % :to this-id :label (:inst ev) :font {:align "bottom"}) prev-ids)   ;; link previous nodes to the current one
;;                                                n {:label label :id this-id :level ts-num :event ev}]
;;                                            {:node n :edges es :linked linked}))
;;                                 newgraph (remove :linked (map mknode events))
;;                                 newnodes (map :node newgraph)
;;                                 newedges (mapcat :edges newgraph)
;;                                 newids (map :id newnodes)
;;                                 ]
;;                             (recur (rest time-step) (concat ts-nodes newnodes) (concat ts-edges newedges) (inc ts-num) newids)
;;                             )))]
;;                 (recur (rest answer-sets) (inc as-num) (concat nodes (:nodes options)) (concat edges (:edges options))))))]
;;     (if (= (count (:nodes graph)) 1) (assoc-in graph [:nodes 0] {:id 0 :label "COMPILE ERROR" :level 0 :color "#ffffff" :shape "text"}) graph)
;;   ))

;; FORCE-DIRECTED GRAPH ---------------------------------------------


(defn vis-inner []
   (let [visi (atom nil)
         update (fn [comp]
                  (let [graph (data->graph (:graph (reagent/props comp)))]
                    (do
                      (re-frame/dispatch [:update-graph graph])
                      ;; (println (str "COMP: " (:graph (reagent/props comp))))
                      (.setData (:network @visi) (clj->js graph))
                      (.redraw (:network @visi))
                      )))]
     (reagent/create-class
      {:reagent-render (fn [] [:div#graph {:style {:width 1000 :height 1000}}])
       :component-did-mount (fn [comp]
                              (let [
                                    container (.getElementById js/document "graph")
                                    options {
                                             :physics {
                                                       :hierarchicalRepulsion {:springLength 200
                                                                               :nodeDistance 180}}
                                             :layout {:hierarchical {:direction "LR"}}
                                             }
                                    network (js/vis.Network. container (clj->js {:nodes [{:id 0 :label "brap"}] :edges []}) (clj->js options))
                                    ]
                                (do
                                  ;; (println (str "COMP0: " (prn-str (:graph (reagent/props comp)))))
                                  ;; (.on network "selectNode"  #(re-frame/dispatch [:story-action (index->event (js/parseInt (first (get (js->clj %) "nodes"))))]))
                                  (.on network "selectNode" #(println (index->node (js/parseInt (first (get (js->clj %) "nodes"))))))
                                  (reset! visi {:network network})))
                              (update comp))
       :component-did-update update
       :display-name "vis-inner"})))


(defn embellish [word]
  (->> word
       (split-with #(not (= (first %) (.toUpperCase (first %)))))
       (map #(clojure.string/capitalize (apply str %)))
       (interpose " ")
       (reduce str)
       (clojure.string/trim)))

(defn describe-norms [text-list]
  (let [intro (first text-list)
        perms (map :perms text-list)
        obls (map #(map :obl %) (map :obls text-list))
        cgroups (map (fn [x] (group-by #(first (:params %)) x)) perms)
        cogroups (map (fn [x] (group-by #(first (:params %)) x)) obls)
        pstr-f (fn [x] (for [k (keys x)] (str (embellish k) " may: " (reduce str (interpose ", " (map #(str (:perm %) " " (reduce str (map embellish (rest (:params %))))) (get x k)))))))
        ;; o-str (fn [{:keys [event params deadline viol]}]
        ;;         (str (embellish (first params)) " must: " event (reduce str (rest params))))
        ostr-f (fn [x] (for [k (keys x)] (str (embellish k) " must: " (reduce str (interpose ", " (map #(str (:event %) " " (reduce str (map embellish (rest (:params %))))) (get x k)))))))
        c-perms (map pstr-f cgroups)
        c-obls (map ostr-f cogroups)
        p-strs (map #(interpose "; \n" %) c-perms)
        o-strs (map #(interpose "; \n" %) c-obls)
        ;; o-strs (map str cogroups)
        ;; o-strs (map #(map :obl %) (map :obls text-list))

        c-strs (mapcat vector p-strs o-strs)

        ;; chars (map (fn [x] (remove nil? (vec (set (map #(first (:params %)) x))))) perms)
        ;; c-perms (remove nil? (map (fn [x] (filter #(= (first (:params %)) x) perms)) chars))
        ]
    ;; (concat [intro] (for [c c-perms] [(str (first (:params c)) ": " (map :perm c))]))
    ;; (concat [intro] perms)
    ;; (str perms)
    (concat [intro] (map #(reduce str %) c-strs))
    ;; (map str text-list)
    ))

(defn text-div []
  (let [story (re-frame/subscribe [:story-text])]
    ;; [:div
    ;;  [:p (reduce str (interpose "\n" @story))]
    ;;  ]
    [:div
     (for [x (describe-norms @story)] [:div [:p x] [:br]])]
    ))

(defn output []
  [com/scroller
   :attr {:id "scroller"}
   :v-scroll :auto
   :height "390px"
   :child [text-div]])

(defn prompt []
  [com/input-text
   :model ""
   :width "400px"
   :on-change #()])

(defn go-button []
  [com/button
   :label "Go!"
   :class "btn-success"
   :on-click #(re-frame/dispatch [:generate-story])])

(defn player-select []
  (let [
        chars (re-frame/subscribe [:our-characters])
        info (map #(assoc % :label (str (:label %) " (" (:role %) ")")) (remove nil? @chars))
        player (re-frame/subscribe [:player])
        ]
    [com/v-box
     :children [
                [com/h-box
                 :justify :center
                 :children [
                            [com/title :label "Player character:" :level :level3]
                            gap
                            [com/single-dropdown
                             :width "200px"
                             :choices info
                             :placeholder "(Random selection)"
                             :model @player
                             :filter-box? true
                             :on-change #(re-frame/dispatch [:change-player %])]]]]]))

(defn lookahead []
  (let [la (re-frame/subscribe [:lookahead])]
    [com/h-box
     :justify :center
     :children [[com/title :label "Lookahead:"]
                gap
                [com/single-dropdown
                 :width "50px"
                 :choices [
                           {:id 1 :label "1"}
                           {:id 2 :label "2"}
                           {:id 3 :label "3"}
                           {:id 4 :label "4"}
                           {:id 5 :label "5"}
                           ]
                 :model @la
                 :on-change #(do
                               (re-frame/dispatch [:change-lookahead %])
                               (re-frame/dispatch [:story-refresh]))
                 ]
                ]]))

(defn action-boxes []
  (let [verbs (re-frame/subscribe [:story-verbs])
        verb (re-frame/subscribe [:story-verb])
        object-as (re-frame/subscribe [:story-objectas])
        nice-as (map #(assoc % :label (embellish (:label %))) @object-as)
        object-a (re-frame/subscribe [:story-object-a])
        object-b (re-frame/subscribe [:story-object-b])]
    [com/h-box
     :justify :center
     :children [
                [com/single-dropdown
                 :width "250px"
                 :placeholder "<verb>"
                 :choices @verbs
                 :model @verb
                 :on-change #(re-frame/dispatch [:update-story-verb %])]
                ;; [:span {:style {:padding "5px 10px"}} "the"]
                gap
                [com/single-dropdown
                 :width "250px"
                 :placeholder "<object>"
                 :choices nice-as
                 :model @object-a
                 :on-change #(re-frame/dispatch [:update-story-object-a %])]
                gap
                [com/button
                 :label "Go!"
                 :class "btn-success"
                 :on-click #(re-frame/dispatch [:story-event])]
                ]
     ]))

(defn play-tab []
  (let [
        story-graph (re-frame/subscribe [:story-sets])
        compiling (re-frame/subscribe [:compiling])
        ]
    [com/v-box
     :width "100%"
     :children
     [
      (if @story-graph
        (if @compiling
          [com/label "Compiling..."]
          [vis-inner {:graph @story-graph}]))
      ;; (if-not @story-graph
      ;;   [com/h-box
      ;;    :justify :center
      ;;    :padding "40px 60px"
      ;;    :children [
      ;;               [com/v-box
      ;;                :children [
      ;;                           [player-select]
      ;;                           [lookahead]
      ;;                           [com/h-box
      ;;                            :justify :center
      ;;                            :children [
      ;;                                       [go-button]]]]]]]
      ;;   [com/v-box
      ;;    :children [
      ;;               [com/h-box
      ;;                :justify :center
      ;;                :padding "20px 30px"
      ;;                :children [
      ;;                           [lookahead]
      ;;                           gap
      ;;                           gap
      ;;                           [player-select]
      ;;                           gap
      ;;                           gap
      ;;                           [com/button
      ;;                            :label "Reset Story"
      ;;                            :class "btn-danger"
      ;;                            :on-click #(re-frame/dispatch [:reset-vis])]
      ;;                           ]
      ;;                ]
      ;;               [vis-inner {:graph @story-graph}]
      ;;               ]]
      ;;   )
      ]]))


(defn tabs []
  (let [current-tab (re-frame/subscribe [:current-tab])]
    [com/horizontal-tabs
     :model @current-tab
     :tabs tab-list
     :on-change #(re-frame/dispatch [:tab-changed %])]))

(defn content []
  (let [current-tab (re-frame/subscribe [:current-tab])]
    (case @current-tab
      :tab1 [edit-tab]
      :tab2 [arrange-tab]
      gap)))


(defn error-dialog [message]
  (let [lines (clojure.string/split-lines message)]
                [com/alert-box
                 :id 1
                 :alert-type :danger
                 :heading (first lines)
                 :body [:div (for [s (rest lines)] [:p s])]
                 :closeable? true
                 :on-close #(re-frame/dispatch [:hide-error])
                 ]
                )
    )


(defn main-panel []
  (fn []
    (let [error (re-frame/subscribe [:error])]
      [com/v-box
       :height "100%"
       :width "100%"
       :children [
                  [title]
                  [com/h-box
                   :children [
                              [com/v-box
                               :width "30%"
                               :children [
                                          [tabs]
                                          [content]
                                          (when @error
                                            [error-dialog @error]
                                            )]]
                              [play-tab]
                              ]]
                  ]])))
