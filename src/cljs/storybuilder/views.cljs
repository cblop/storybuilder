(ns storybuilder.views
    (:require [re-frame.core :as re-frame]
              [reagent.core :as reagent]
              [re-com.core :as com]))


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
                 :width "250px"
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
                 :width "250px"
                 :choices chars
                 :placeholder "(Randomly generated)"
                 ;; TODO: make random
                 :model (:id sel)
                 ;; :model nil
                 :filter-box? true
                 :on-change #(re-frame/dispatch [:change-char n % role])]]]))


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
                            [objects n]]]
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
               :width "630px"
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

(defn text-div []
  [:div
   [:p "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque pretium mattis massa, in dapibus turpis. Mauris non consequat metus, vitae ultrices ante. Duis tincidunt consequat euismod. Sed luctus nisi a tincidunt tempor. Ut lacinia nisi neque, non vulputate odio ultrices vel. Sed elit elit, bibendum at orci ac, pulvinar molestie augue. Curabitur eget nunc dictum, tempor mi ac, dignissim nisl. Donec erat dui, rhoncus eu sem a, rutrum tempus tellus. Sed vestibulum, velit non lobortis placerat, lacus risus sagittis eros, a finibus velit libero eu ex. Praesent pretium porta mauris et efficitur. Sed efficitur eleifend iaculis."]
   [:p "Sed ullamcorper orci eget velit sodales varius. Nam in dolor accumsan, gravida nisi nec, scelerisque ligula. Etiam urna mauris, semper sed dui ac, maximus scelerisque leo. Etiam sed velit et lacus aliquam semper. Curabitur velit nisl, mollis vel mi vitae, tempus rhoncus risus. Mauris bibendum imperdiet nulla, ut porta sem euismod vel. Nam ac sodales nunc. Fusce a turpis tellus. Vivamus sit amet sem eget enim tincidunt iaculis. Sed risus metus, auctor sed mi non, hendrerit feugiat justo. Praesent ornare, leo non rhoncus scelerisque, turpis augue iaculis elit, tincidunt imperdiet magna lectus sit amet neque. Interdum et malesuada fames ac ante ipsum primis in faucibus. Mauris nisl orci, gravida quis hendrerit ultrices, varius at purus"]
   [:p "Phasellus odio nulla, vestibulum non feugiat ac, maximus eu neque. Praesent suscipit urna at eros pellentesque maximus. Proin eleifend mi dictum, eleifend neque condimentum, venenatis arcu. Sed id risus ac sapien sagittis congue ac a nunc. Vestibulum molestie tortor id mauris euismod, vel molestie ante feugiat. Proin vehicula, purus vitae sodales vulputate, orci quam ultricies mi, sit amet varius enim elit at nibh. Suspendisse et turpis luctus, pulvinar odio vitae, commodo nisi. Ut dignissim ligula vel nulla efficitur, non ultricies ante dignissim. Cras vel pellentesque est, sed malesuada lacus. Donec ut quam dolor. Proin dapibus sem lectus, quis imperdiet ante dapibus ac. Duis tincidunt dui magna, vitae euismod libero finibus ut."]
   [:p "Curabitur aliquet rutrum ligula a fermentum. Pellentesque dignissim mauris maximus fermentum consequat. Morbi varius nulla ut eros malesuada lacinia. Donec a ipsum sit amet augue luctus fermentum. Morbi bibendum ligula nec mi semper tempor. Fusce quis rhoncus mauris. Integer quam mi, dapibus ac quam dignissim, faucibus ultrices leo. Donec rutrum commodo eros, eu placerat diam bibendum ac. Integer pretium, sem vitae ornare tempus, nisl lectus faucibus sem, in rhoncus purus est at elit. Nunc varius nulla augue, nec tincidunt tortor rutrum ac. Praesent dapibus lectus in congue ultricies. Fusce malesuada et sapien in consectetur. Integer quis pharetra ante."]
   [:p "Praesent vehicula libero eget volutpat scelerisque. Maecenas eget ante dui. Nullam vel lacus sit amet magna aliquam maximus in id ante. Sed iaculis maximus purus, vitae molestie tellus porta in. Proin sagittis vestibulum felis quis gravida. Donec tristique in leo et lacinia. Nullam vestibulum faucibus elit id malesuada. Curabitur eget tortor a leo tempus porttitor."]])

(defn output []
  [com/scroller
   :attr {:id "scroller"}
   :v-scroll :auto
   :height "400px"
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
   :on-click #(re-frame/dispatch [:go-button])])

(defn play-tab []
  [com/v-box
   :children
   [
    [com/h-box
     :justify :center
     :padding "40px 60px"
     :children [
                [output]]]
    [com/h-box
     :justify :center
     :children [[:span {:style {:font-weight "bold" :font-size "22px"}} ">"]
                spacer
                [prompt]
                gap
                [go-button]]]]])


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
