(ns e-commerce.curso6.aula1
  (:require [datomic.api :as d]
            [e-commerce.db.produto :as db.produto]
            [e-commerce.db.config :as db.config]
            [e-commerce.model :as model]
            [e-commerce.generators :as generators]
            [schema.core :as s]
            [schema-generators.generators :as g]))

(s/set-fn-validation! true)
(db.config/apaga-banco!)
(def conn (db.config/abre-conexao!))

(db.config/cria-schema! conn)

;FIXME mover pra outro namespace
(db.config/cria-dados-de-exemplo! conn)

(def primeiro (first (db.produto/todos (d/db conn))))

(g/sample 100 model/Categoria)
(g/sample 100 model/Variacao generators/leaf-generators)