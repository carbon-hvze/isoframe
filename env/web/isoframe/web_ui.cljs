(ns ^:figwheel-no-load isoframe.web-ui
  (:require [isoframe.core :as core]
            [re-frisk.core :refer [enable-re-frisk!]]
            [devtools.core :as devtools]))

(devtools/install!)

(enable-console-print!)

(enable-re-frisk!)

(core/init!)

