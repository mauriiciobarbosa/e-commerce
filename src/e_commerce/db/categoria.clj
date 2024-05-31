(ns e-commerce.db.categoria
  (:require [datomic.api :as d]
            [e-commerce.model :as model]
            [schema.core :as s]
            [e-commerce.adapter.entidade :as adapter.entidade]))

(defn db-adds-de-atribuicao [produtos categoria]
  (reduce (fn [db-adds produto] (conj db-adds [:db/add
                                               [:produto/id (:produto/id produto)]
                                               :produto/categoria
                                               [:categoria/id (:categoria/id categoria)]]))
          []
          produtos))

(defn atribui! [conn produtos categoria]
  (let [a-transacionar (db-adds-de-atribuicao produtos categoria)]
    (println a-transacionar)
    (d/transact conn a-transacionar)))


(s/defn adiciona! [conn, categorias :- [model/Categoria]]
  (d/transact conn categorias))

(s/defn todas-as-categorias :- [model/Categoria] [db]
  (adapter.entidade/datomic-para-entidade
    (d/q '[:find [(pull ?categoria [*]) ...]
           :where [?categoria :categoria/id]]
         db)))
