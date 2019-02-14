(ns pedestal-datomic-todo-api.protocols.storage-client)

(defprotocol StorageClient
  "Protocol for simple storage mechanism; simple but not practical in any way"
  (query [conn data args] "Return the contents of storage based on datalog")
  (exec! [conn data] "Mutate the storage with the provided function"))

