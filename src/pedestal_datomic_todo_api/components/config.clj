(ns pedestal-datomic-todo-api.components.config
  (:require [com.stuartsierra.component :as component]))

(defrecord Config [config]
  component/Lifecycle
  (start [this] this)
  (stop  [this] this))

(defn new-config [config] (map->Config {:config config}))
