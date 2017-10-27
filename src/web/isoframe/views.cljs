(ns isoframe.views
  (:require [reagent.core  :as reagent]
            [re-frame.core :as rf]
            [clojure.string :as str]))


;; (defn todo-input [{:keys [title on-save on-stop]}]
;;   (let [val  (reagent/atom title)
;;         stop #(do (reset! val "")
;;                   (when on-stop (on-stop)))
;;         save #(let [v (-> @val str str/trim)]
;;                 (when (seq v) (on-save v))
;;                 (stop))]
;;     (fn [props]
;;       [:input (merge props
;;                      {:type        "text"
;;                       :value       @val
;;                       :auto-focus  true
;;                       :on-blur     save
;;                       :on-change   #(reset! val (-> % .-target .-value))
;;                       :on-key-down #(case (.-which %)
;;                                       13 (save)
;;                                       27 (stop)
;;                                       nil)})])))


;; (defn footer-controls
;;   []
;;   (let [[active done] @(rf/subscribe [:footer-counts])
;;         showing       @(rf/subscribe [:showing])
;;         a-fn          (fn [filter-kw txt]
;;                         [:a {:class (when (= filter-kw showing) "selected")
;;                              :href (str "#/" (name filter-kw))} txt])]
;;     [:footer#footer
;;      [:span#todo-count
;;       [:strong active] " " (case active 1 "item" "items") " left"]
;;      [:ul#filters
;;       [:li (a-fn :all    "All")]
;;       [:li (a-fn :active "Active")]
;;       [:li (a-fn :done   "Completed")]]
;;      (when (pos? done)
;;        [:button#clear-completed {:on-click #(rf/dispatch [:clear-completed])}
;;         "Clear completed"])]))

(defn todo-input [{:keys [title on-save on-stop]}]
  (let [val  (reagent/atom title)
        stop #(do (reset! val "")
                  (when on-stop (on-stop)))
        save #(let [v (-> @val str str/trim)]
                (when (seq v) (on-save v))
                (stop))]
    (fn [props]
      [:input (merge props
                     {:type        "text"
                      :value       @val
                      :auto-focus  true
                      :on-blur     save
                      :on-change   #(reset! val (-> % .-target .-value))
                      :on-key-down #(case (.-which %)
                                      13 (save)
                                      27 (stop)
                                      nil)})])))

(defn task-input []
  [:header#header
   [:h1 "todos"]
   [todo-input
    {:id "new-todo"
     :placeholder "What needs to be done?"
     :on-save #(rf/dispatch [:add-todo %])}]])

(defn task-item
  []
  [:div "hui"]
  #_(let [editing (reagent/atom false)]
    (fn [{:keys [id done title]}]
      [:li {:class (str (when done "completed ")
                        (when @editing "editing"))}
       [:div.view
        [:input.toggle
         {:type "checkbox"
          :checked done
          :on-change #(rf/dispatch [:toggle-done id])}]
        [:label
         {:on-double-click #(reset! editing true)}
         title]
        [:button.destroy
         {:on-click #(rf/dispatch [:delete-todo id])}]]
       (when @editing
         [todo-input
          {:class "edit"
           :title title
           :on-save #(rf/dispatch [:save id %])
           :on-stop #(reset! editing false)}])])))

(defn task-list [todo-id]
  (let [visible-tasks @(rf/subscribe [:visible-tasks todo-id])
        ;; all-complete? @(rf/subscribe [:all-complete? todo-id])
        ]
    [:section#main
     #_[:input#toggle-all
      {:type "checkbox"
       :checked all-complete?
       :on-change #(rf/dispatch [:complete-all-toggle])}]
     #_[:label
      {:for "toggle-all"}
      "Mark all as complete"]
     [:ul#todo-list
      (for [task visible-tasks]
        ^{:key (:id task)} [task-item task])]]))

(defn todo-app
  []
  [:div
   [:section#todoapp
    (when-let [todo (first @(rf/subscribe [:todos]))]
      [task-input]
      [task-list (:id todo)])
    #_[footer-controls]]
   #_[:footer#info
    [:p "Double-click to edit a todo"]]])
