(ns mobile.components.core
  (:require [reagent.core :as r]))

;; react-native
(def react-native (js/require "react-native"))

(def app-registry (.-AppRegistry react-native))
(def image (r/adapt-react-class (.-Image react-native)))
(def flat-list (r/adapt-react-class (.-FlatList react-native)))
(defn alert [title]
  (.alert (.-Alert react-native) title))


;; React Navigation
(def react-navigation (js/require "react-navigation"))

;; Stack-navigator
(defn stack-navigator
  ([routes]
   (stack-navigator routes nil))
  ([routes config]
   (if (= nil config)
     ((.-StackNavigator react-navigation) (clj->js routes))
     ((.-StackNavigator react-navigation) (clj->js routes) (clj->js config)))))

;; NativeBase component
(def NativeBase (js/require "native-base"))
;; Layout
(def container (r/adapt-react-class (.-Container NativeBase)))
(def content (r/adapt-react-class (.-Content NativeBase)))
(def header (r/adapt-react-class (.-Header NativeBase)))
(def view (r/adapt-react-class (.-View NativeBase)))
(def item (r/adapt-react-class (.-Item NativeBase)))
(def left (r/adapt-react-class (.-Left NativeBase)))
(def right (r/adapt-react-class (.-Right NativeBase)))
(def body (r/adapt-react-class (.-Body NativeBase)))
(def title (r/adapt-react-class (.-Title NativeBase)))
(def sub-title (r/adapt-react-class (.-Subtitle NativeBase)))
(def footer (r/adapt-react-class (.-Footer NativeBase)))
(def footer-tab (r/adapt-react-class (.-FooterTab NativeBase)))
(def card (r/adapt-react-class (.-Card NativeBase)))
(def card-item (r/adapt-react-class (.-CardItem NativeBase)))
(def list-component (r/adapt-react-class (.-List NativeBase)))
(def list-item (r/adapt-react-class (.-ListItem NativeBase)))
(def ds (react-native.ListView.DataSource.
         #js{:rowHasChanged (fn[a b] (not= a b))
             :sectionHeaderHasChanged (fn[a b] (not= a b))
             }))
(def separator (r/adapt-react-class (.-Separator NativeBase)))
(def grid (r/adapt-react-class (.-Grid NativeBase)))
(def row (r/adapt-react-class (.-Row NativeBase)))
(def col (r/adapt-react-class (.-Col NativeBase)))
(def tabs (r/adapt-react-class (.-Tabs NativeBase)))
(def tab (r/adapt-react-class (.-Tab NativeBase)))
(def scrollable-tab (r/adapt-react-class (.-ScrollableTab NativeBase)))


;; Content
(def text (r/adapt-react-class (.-Text NativeBase)))
(def h1 (r/adapt-react-class (.-H1 NativeBase)))
(def h2 (r/adapt-react-class (.-H2 NativeBase)))
(def h3 (r/adapt-react-class (.-H3 NativeBase)))

;; Forms
(def form (r/adapt-react-class (.-Form NativeBase)))
(def button (r/adapt-react-class (.-Button NativeBase)))
(def input (r/adapt-react-class (.-Input NativeBase)))
(def label (r/adapt-react-class (.-Label NativeBase)))
(def checkbox (r/adapt-react-class (.-CheckBox NativeBase)))
(def radio (r/adapt-react-class (.-Radio NativeBase)))

;; Component
(def spinner (r/adapt-react-class (.-Spinner NativeBase)))
(def action-sheet (r/adapt-react-class (.-ActionSheet NativeBase)))
(def badge (r/adapt-react-class (.-Badge NativeBase)))
(def thumbnail (r/adapt-react-class (.-Thumbnail NativeBase)))
(def deck-swiper  (r/adapt-react-class (.-DeckSwiper NativeBase)))
(def drawer (r/adapt-react-class (.-Drawer NativeBase)))
(def fab (r/adapt-react-class (.-Fab NativeBase)))
(def icon (r/adapt-react-class (.-Icon NativeBase)))


;; Resources
(def logo-img (js/require "./images/cljs.png"))
