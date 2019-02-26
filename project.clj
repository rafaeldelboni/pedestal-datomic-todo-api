(defproject pedestal-datomic-todo-api "1.0.0"
  :description "Clojure, Pedestal and Datomic to create an Todos API"
  :url "https://github.com/rafaeldelboni/pedestal-datomic-todo-api"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [cheshire "5.8.1"]
                 [com.datomic/datomic-free "0.9.5697"]
                 [com.stuartsierra/component "0.4.0"]
                 [failjure "1.3.0"]
                 [io.pedestal/pedestal.jetty "0.5.5"]
                 [io.pedestal/pedestal.service "0.5.5"]
                 [io.pedestal/pedestal.service-tools "0.5.5"]
                 [prismatic/schema "1.1.10"]]
  :main ^:skip-aot pedestal-datomic-todo-api.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :uberjar-name "api.jar"
  :test-selectors {:default (complement :integration)
                   :integration :integration })
