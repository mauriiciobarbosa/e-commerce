(ns e-commerce.aula1
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [e-commerce.db :as db]
            [schema.core :as s]))

(s/set-fn-validation! true)
(db/apaga-banco!)
(def conn (db/abre-conexao!))
(db/cria-schema! conn)
(db/cria-dados-de-exemplo conn)

(def produtos (db/todos-os-produtos (d/db conn)))
(def computer (-> produtos first))

(def increment-visualizacao
  #db/fn {:lang :clojure
          :params [db produto-id]
          :code  (let [visualizacoes (d/q '[:find ?v .
                                            :in $ ?produto-id
                                            :where [?p :produto/id ?produto-id]
                                            [?p :produto/visualizacoes ?v]]
                                          db produto-id)
                       atual (or visualizacoes 0)
                       total-novo (inc atual)]
                   [{:produto/id produto-id
                     :produto/visualizacoes total-novo}])})

; instalar a função
@(d/transact conn [{:db/doc "Incrementa atributo :produto/visualizacao dessa entidade"
                    :db/ident :incrementa-visualizacao
                    :db/fn increment-visualizacao}])

(dotimes [_ 10] (db/visualizacoes! conn (:produto/id computer)))
(db/um-produto! (d/db conn) (:produto/id computer))