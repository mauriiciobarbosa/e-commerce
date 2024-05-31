(ns e-commerce.aula1
  (:require [datomic.api :as d]
            [e-commerce.db.produto :as db.produto]
            [e-commerce.db.config :as db.config]
            [schema.core :as s]))

(s/set-fn-validation! true)
(db.config/apaga-banco!)
(def conn (db.config/abre-conexao!))

(db.config/cria-schema! conn)

;FIXME mover pra outro namespace
(db.config/cria-dados-de-exemplo! conn)

(db.produto/todos (d/db conn))