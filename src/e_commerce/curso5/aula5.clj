(ns e-commerce.curso5.aula5
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
(def venda-2 (db.venda/adiciona! conn (model/nova-venda primeiro 4)))
(def venda-3 (db.venda/adiciona! conn (model/nova-venda primeiro 8)))

(db.venda/altera-situacao! conn venda-1 "preparando")
(db.venda/altera-situacao! conn venda-2 "preparando")
(db.venda/altera-situacao! conn venda-2 "a caminho")
(db.venda/altera-situacao! conn venda-2 "entregue!")

(db.venda/historico (d/db conn) venda-1)
(db.venda/historico (d/db conn) venda-2)

(db.venda/cancela! conn venda-1)

(db.venda/todas-nao-canceladas! (d/db conn))
(db.venda/todas-canceladas! (d/db conn))
(db.venda/todas! (d/db conn))

(db.venda/historico-geral (d/db conn) #inst "2024-05-31T19:11:19.074-00:00")
