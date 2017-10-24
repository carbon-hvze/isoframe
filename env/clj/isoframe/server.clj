(ns isoframe.server
  (:require [isoframe.core :as core]))

(core/start {:db-name :isoframe :port 3000})
