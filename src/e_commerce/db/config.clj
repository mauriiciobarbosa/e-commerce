(ns e-commerce.db.config
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [e-commerce.db.adapter :as db.adapter]
            [e-commerce.db.venda :as db.venda]
            [e-commerce.model :as model]
            [e-commerce.db.produto :as db.produto]
            [e-commerce.db.categoria :as db.categoria]
            [e-commerce.wire.db.categoria :as wire.db.categoria]
            [e-commerce.wire.db.produto :as wire.db.produto]
            [e-commerce.wire.db.variacao :as wire.db.variacao]
            [e-commerce.wire.db.venda :as wire.db.venda]))

(def db-uri "datomic:dev://localhost:4334/ecommerce")

(defn abre-conexao! []
  (d/create-database db-uri)
  (d/connect db-uri))

(defn apaga-banco! []
  (d/delete-database db-uri))

(defn cria-schema! [conn]
  (let [schemas (concat (db.adapter/schema-to-datomic wire.db.categoria/Categoria)
                        (db.adapter/schema-to-datomic wire.db.variacao/Variacao)
                        (db.adapter/schema-to-datomic wire.db.venda/Venda)
                        (db.adapter/schema-to-datomic wire.db.produto/Produto)
                        [{:db/ident       :tx-data/ip
                          :db/valueType   :db.type/string
                          :db/cardinality :db.cardinality/one}])]
    (d/transact conn schemas)))

(defn cria-dados-de-exemplo! [conn]
  (def eletronicos (model/nova-categoria "Eletrônicos"))
  (def esporte (model/nova-categoria "Esporte"))
  (pprint @(db.categoria/adiciona! conn [eletronicos, esporte]))

  (def computador (model/novo-produto (model/uuid) "Computador Novo", "/computador-novo", 2500.10M, 10))
  (def celular (model/novo-produto (model/uuid) "Celular Caro", "/celular", 888888.10M))
  (def celular-barato (model/novo-produto "Celular Barato", "/celular-barato", 0.1M))
  (def xadrez (model/novo-produto (model/uuid) "Tabuleiro de xadrez", "/tabuleiro-de-xadrez", 30M, 5))
  (def jogo (assoc (model/novo-produto (model/uuid) "Jogo online", "/jogo-online", 20M) :produto/digital true))
  (pprint @(db.produto/adiciona-ou-altera! conn [computador, celular, celular-barato, xadrez, jogo] "200.216.222.125"))

  (db.categoria/atribui! conn [computador, celular, celular-barato, jogo] eletronicos)
  (db.categoria/atribui! conn [xadrez] esporte)

  (db.venda/adiciona! conn (model/nova-venda computador 3))
  (db.venda/adiciona! conn (model/nova-venda computador 4))
  (db.venda/adiciona! conn (model/nova-venda computador 8)))



