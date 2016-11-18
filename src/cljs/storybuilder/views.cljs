(ns storybuilder.views
    (:require [re-frame.core :as re-frame]
              [reagent.core :as reagent]
              [re-com.core :as com]
              [strokes :refer d3]
              ;; [cljsjs.d3]
              ))


(strokes/bootstrap)


;; GENERAL

(def tab-list [{:id :tab1 :label "Edit"}
               {:id :tab2 :label "Arrange"}
               {:id :tab3 :label "Play"}])

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
                         :width "600px"
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

(defn save-trope-button []
  [com/button
   :label "Save Trope"
   :class "btn-primary"
   :on-click #(re-frame/dispatch [:parse-trope])])

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
                ;; [com/h-box
                ;;  :justify :center
                ;;  :children [
                ;;             [com/horizontal-bar-tabs
                ;;              :tabs [{:id :tropes :label "Tropes"} {:id :characters :label "Characters"} {:id :objects :label "Objects"}]
                ;;              :model @edit-facet
                ;;              :on-change #()
                ;;              ]]]
                ;; gap
                ;; gap
                (if @trope-name
                  [com/title :style {:padding-left "40px" :padding-bottom "10px"} :level :level3 :label (str "\"" (if (= (:label @trope-name) "") "<no name>" (:label @trope-name)) "\" is a trope where:")])
                [com/h-box
                 :justify :center
                 :children [
                            [codemirror-inner {:text @trope-text
                                               :cursor @cursor}]
                            gap
                            [com/v-box
                             :width "300px"
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
                 :on-change #(re-frame/dispatch [:change-place n % loc])]]]))


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
                 :on-change #(re-frame/dispatch [:change-char n % role])]]]))


(defn places [n]
  (let [
        locs (re-frame/subscribe [:locations n])
        all-places (re-frame/subscribe [:places-for-locations @locs])
        our-tropes (re-frame/subscribe [:our-tropes])
        sel-places (:places (nth @our-tropes n))
        p (println "PLACES: ")
        triples (map vector (set @locs) (set @all-places) sel-places)
        p (println triples)
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
        p (println "OBJS: ")
        triples (map vector (set @types) (set @all-objs) sel-objs)
        p (println triples)
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
        p (println "ROLES: ")
        triples (map vector (set @roles) (set @all-chars) sel-chars)
        p (println triples)
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
                             :width "300px"
                             :choices @all-tropes
                             :model (:id (nth @our-tropes n))
                             :filter-box? true
                             :on-change #(re-frame/dispatch [:change-trope n %])]
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
        p (println "NTH: ")
        p (println n)
        ]
    [com/v-box
     :style (if @subverted {:background-color "#ffdddd" :border "#ff9999 solid 2px"}
                {:background-color "#ddffdd"
                 :border "2px solid #99ff99"})
     :padding "10px"
     :children [[trope-select n]
                gap
                [com/h-box
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
                            (if (> (count @roles) 1)
                              [subvert-trope n])
                            (if (and (> n 0) (> (count @roles) 1))
                              gap)
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
               :width "725px"
               :children [
                          [trope-boxes]
                          [add-trope]
                          ]]]])


(defn arrange-tab []
  (let [our-tropes (re-frame/subscribe [:our-tropes])]
    (do
      (if (empty? @our-tropes) (re-frame/dispatch [:add-trope]))
      [trope-content])))



;; STORY


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
                            [com/title :label "Who do you want to be?" :level :level3]]]
                gap
                [com/single-dropdown
                 :width "300px"
                 :choices info
                 :placeholder "(Random selection)"
                 :model @player
                 :filter-box? true
                 :on-change #(re-frame/dispatch [:change-player %])]]]))

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
  (let [story-text (re-frame/subscribe [:story-text])]
    [com/v-box
     :children
     [
      (if (empty? @story-text)
        [com/h-box
         :justify :center
         :padding "40px 60px"
         :children [
                    [com/v-box
                     :children [
                                [player-select]
                                gap
                                [com/h-box
                                 :justify :center
                                 :children [
                                            [go-button]]]]]]]
        [com/v-box
         :children [
                    [com/h-box
                     :padding "20px 0px 0px 210px"
                     :children [
                                [com/label
                                 :label "What next?"
                                 :style {:font-size "small"}]]]
                    spacer
                    [action-boxes]
                    [com/h-box
                     :justify :center
                     :padding "40px 60px"
                     :children [
                                [output]]]
                    ]]
        )
      ;; [com/h-box
      ;;  :justify :center
      ;;  :children [[:span {:style {:font-weight "bold" :font-size "22px"}} ">"]
      ;;             spacer
      ;;             [prompt]
      ;;             gap
      ;;             [go-button]]]
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
      :tab3 [play-tab]
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
       :children [[title]
                  [tabs]
                  [content]
                  (when @error
                    [error-dialog @error]
                    )
                  ]])))
