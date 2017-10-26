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
                 [figwheel-sidecar "0.5.10"]
                 [org.clojure/java.jdbc "0.7.3"]
                 [org.postgresql/postgresql           "9.4.1212.jre7"]
                 [nilenso/honeysql-postgres           "0.2.2"]
                 [hikari-cp "1.8.1"]
                 [clj-jwt                             "0.1.1"]
                 [com.cognitect/transit-clj "0.8.300"]
                 [com.cognitect/transit-cljs          "0.8.239"]
                 [matcho                              "0.1.0-RC6"]
                 [org.mindrot/jbcrypt                 "0.3m"]
                 [mpg                                 "1.3.0"]
                 [re-frisk "0.5.0"]]


  :plugins [[lein-cljsbuild "1.1.5"]
            [lein-figwheel  "0.5.14" :exclusions [org.clojure/clojure]]
            [cider/cider-nrepl "0.16.0-SNAPSHOT"]
            [lein-environ "1.1.0"]]

  :figwheel {:server-port 3450
             :repl        true
             :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl
                                cider.nrepl/cider-middleware]}

  :repl-options {:init-ns isoframe.server
                 :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :clean-targets ^{:protect false} ["resources/public/js" "target" "main.js"]

  :aliases {"figwheel"        ["run" "-m" "user" "--figwheel"]
                                        ; TODO: Remove custom extern inference as it's unreliable
                                        ;"externs"         ["do" "clean"
                                        ;                   ["run" "-m" "externs"]]
            "rebuild-modules" ["run" "-m" "user" "--rebuild-modules"]
            "prod-build"      ^{:doc "Recompile code with prod profile."}
            ["with-profile" "prod" "cljsbuild" "once" "main"]}

  :test-paths ["test/clj"]
  :resource-paths ["resources"]
  :source-paths ["src/clj" "src/cljc" "env/clj"]
  :main isoframe.core

  :cljsbuild {:builds [{:id "web"
                        :source-paths ["src/web" "env/web" "src/cljc"]
                        :compiler     {:asset-path           "js"
                                       :optimizations        :none
                                       :source-map           true
                                       :source-map-timestamp true
                                       :main                 "isoframe.dev"
                                       :output-dir "resources/public/js"
                                       :output-to  "resources/public/js/client.js"}}
                       {:id "mobile"
                        :source-paths ["src/mobile" "env/mobile" "src/cljc"]
                        :figwheel     true
                        :compiler     {:output-to     "target/not-used.js"
                                       :main          "env.mobile.main"
                                       :output-dir    "target"
                                       :optimizations :none}}]})
