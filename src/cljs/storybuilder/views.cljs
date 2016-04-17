(ns storybuilder.views
    (:require [re-frame.core :as re-frame]
              [reagent.core :as reagent]
              [re-com.core :as com]))


(def tab-list [{:id :tab1 :label "Tropes"}
               {:id :tab2 :label "Scenes"}
               {:id :tab3 :label "Story"}])

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

(defn tropes-tab []
  (let [trope-text (re-frame/subscribe [:trope-text])
        cursor (re-frame/subscribe [:tropes-cursor-pos])
        ]
    [com/v-box
     :padding "25px"
     :children [
                [com/h-box
                 :justify :center
                 :children [
                            [codemirror-inner {:text @trope-text
                                               :cursor @cursor}]]]]]))

(defn scenes-tab []
  [:p "scenes"])

(defn story-tab []
  [:p "story"])


(defn tabs []
  (let [current-tab (re-frame/subscribe [:current-tab])]
    [com/horizontal-tabs
     :model @current-tab
     :tabs tab-list
     :on-change #(re-frame/dispatch [:tab-changed %]
                     )]))

(defn content []
  (let [current-tab (re-frame/subscribe [:current-tab])]
    (case @current-tab
      :tab1 [tropes-tab]
      :tab2 [scenes-tab]
      :tab3 [story-tab])))

(defn main-panel []
  (fn []
    [com/v-box
     :height "100%"
     :children [[title]
                [tabs]
                [content]]]))
