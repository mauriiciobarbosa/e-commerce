(ns e-commerce.curso6.aula4
  (:require [e-commerce.db.config :as db.config]
            [schema.core :as s]))

(s/set-fn-validation! true)
(db.config/apaga-banco!)
(def conn (db.config/abre-conexao!))

(db.config/cria-schema! conn)
(db.config/cria-dados-de-exemplo! conn)