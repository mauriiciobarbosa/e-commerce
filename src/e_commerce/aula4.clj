(ns e-commerce.aula4
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

(db.venda/todas-nao-canceladas! (d/db conn))

(db.venda/cancela! conn venda-1)

(db.venda/todas-nao-canceladas! (d/db conn))
(db.venda/todas-canceladas! (d/db conn))
(db.venda/todas! (d/db conn))

(db.produto/adiciona-ou-altera! conn [{:produto/id (:produto/id primeiro)
                                       :produto/preco 300M}])
(db.produto/adiciona-ou-altera! conn [{:produto/id (:produto/id primeiro)
                                       :produto/preco 250M}])
(db.produto/adiciona-ou-altera! conn [{:produto/id (:produto/id primeiro)
                                       :produto/preco 277M}])
(db.produto/adiciona-ou-altera! conn [{:produto/id (:produto/id primeiro)
                                       :produto/preco 21M}])

(db.produto/historico-de-precos (d/db conn) (:produto/id primeiro))

