(ns e-commerce.curso5.aula3
  (:require [datomic.api :as d]
            [e-commerce.db.produto :as db.produto]
            [e-commerce.db.config :as db.config]
            [e-commerce.db.venda :as db.venda]
            [e-commerce.model :as model]
            [schema.core :as s]))

(s/set-fn-validation! true)
(db.config/apaga-banco!)
(def conn (db.config/abre-conexao!))

(db.config/cria-schema! conn)

;FIXME mover pra outro namespace
(db.config/cria-dados-de-exemplo! conn)

(def primeiro (first (db.produto/todos (d/db conn))))

(def venda-1 (db.venda/adiciona! conn (model/nova-venda primeiro 3)))

(db.venda/custo-total-errado (d/db conn) venda-1)

(db.produto/atualiza-preco! conn (:produto/id primeiro) (:produto/preco primeiro) (+ (:produto/preco primeiro) 20M))

(db.venda/custo-total-errado (d/db conn) venda-1)
(db.venda/custo-total (d/db conn) venda-1)
