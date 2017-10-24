(defproject isoframe "0.10.1"
  :description "isomorphic reframe application"
  :url "https://github.com/Zallin/isoframe.git"
  :dependencies [[org.clojure/clojure "1.9.0-alpha16"]
                 [org.clojure/clojurescript "1.9.671" :scope "provided"]
                 [reagent "0.7.0"]
                 [re-frame "0.10.1"]
                 [binaryage/devtools "0.9.4"]
                 [secretary "1.2.3"]

                 [io.pedestal/pedestal.service       "0.5.3"]
                 [io.pedestal/pedestal.service-tools "0.5.3"]
                 [io.pedestal/pedestal.jetty         "0.5.3"]
                 [refactor-nrepl "2.3.1"]
                 [org.clojure/tools.nrepl "0.2.13"]
                 [com.cemerick/piggieback "0.2.2-SNAPSHOT"]
                 [org.clojure/java.jdbc "0.7.3"]
                 [org.postgresql/postgresql           "9.4.1212.jre7"]
                 [nilenso/honeysql-postgres           "0.2.2"]
                 [hikari-cp "1.8.1"]]


  :plugins [[lein-cljsbuild "1.1.5"]
            [lein-figwheel  "0.5.14" :exclusions [org.clojure/clojure]]
            [cider/cider-nrepl "0.16.0-SNAPSHOT"]
            [lein-environ "1.1.0"]]

  :figwheel {:server-port 3450
             :repl        true
             :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl
                                cider.nrepl/cider-middleware]}

  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :clean-targets ^{:protect false} ["resources/public/js" "target"]

  :test-paths ["test/clj"]
  :resource-paths ["resources"]
  :source-paths ["src/clj" "src/cljc"]
  :main "isoframe.core"

  :cljsbuild {:builds {:client {:source-paths ["src/cljs" "src/cljc"]
                                :compiler     {:asset-path           "js"
                                               :optimizations        :none
                                               :source-map           true
                                               :source-map-timestamp true
                                               :main                 "isoframe.core"
                                               :output-dir "resources/public/js"
                                               :output-to  "resources/public/js/client.js"}
                                :figwheel {:on-jsload "isoframe.core/main"}}}})

