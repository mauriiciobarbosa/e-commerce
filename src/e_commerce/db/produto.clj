(ns e-commerce.db.produto
  (:require [datomic.api :as d]
            [e-commerce.model :as model]
            [schema.core :as s]
            [e-commerce.adapter.entidade :as adapter.entidade]))

(s/defn adiciona-ou-altera!
  ([conn, produtos :- [model/Produto]]
   (d/transact conn produtos))
  ([conn, produtos :- [model/Produto], ip]
   (let [db-add-ip [:db/add "datomic.tx" :tx-data/ip ip]]
     (d/transact conn (conj produtos db-add-ip)))))

(s/defn um :- (s/maybe model/Produto) [db, produto-id :- java.util.UUID]
  (let [resultado (d/pull db '[* {:produto/categoria [*]}] [:produto/id produto-id])
        produto (adapter.entidade/datomic-para-entidade resultado)]
    (if (:produto/id produto)
      produto
      nil)))

(s/defn um! :- model/Produto [db, produto-id :- java.util.UUID]
  (let [produto (um db produto-id)]
    (when (nil? produto)
      (throw (ex-info "Não encontrei uma entidade"
                      {:type :errors/not-found, :id produto-id})))
    produto))

(s/defn todos :- [model/Produto] [db]
  (adapter.entidade/datomic-para-entidade
    (d/q '[:find [(pull ?entidade [* {:produto/categoria [*]}]) ...]
           :where [?entidade :produto/nome]] db)))

(def regras
  '[
    [(estoque ?produto ?estoque)
     [?produto :produto/estoque ?estoque]]
    [(estoque ?produto ?estoque)
     [?produto :produto/digital true]
     [(ground 100) ?estoque]]
    [(pode-vender? ?produto)
     (estoque ?produto ?estoque)
     [(> ?estoque 0)]]
    [(produto-na-categoria ?produto ?nome-da-categoria)
     [?categoria :categoria/nome ?nome-da-categoria]
     [?produto :produto/categoria ?categoria]]
    ])

(s/defn todos-vendaveis :- [model/Produto] [db]
  (adapter.entidade/datomic-para-entidade
    (d/q '[:find [(pull ?produto [* {:produto/categoria [*]}]) ...]
           :in $ %
           :where (pode-vender? ?produto)]
         db regras)))

(s/defn um-vendavel :- (s/maybe model/Produto) [db, produto-id :- java.util.UUID]
  (let [query '[:find (pull ?produto [* {:produto/categoria [*]}]) .
                :in $ % ?id
                :where [?produto :produto/id ?id]
                (pode-vender? ?produto)]
        resultado (d/q query db regras produto-id)
        produto (adapter.entidade/datomic-para-entidade resultado)]
    (if (:produto/id produto)
      produto
      nil)))

(s/defn todos-da-categoria :- [model/Produto]
  [db categorias :- [s/Str]]
  (adapter.entidade/datomic-para-entidade
    (d/q '[:find [(pull ?produto [* {:produto/categoria [*]}]) ...]
           :in $ % [?nome-das-categorias ...]
           :where (produto-na-categoria ?produto ?nome-das-categorias)]
         db regras categorias)))

(s/defn todos-da-categoria-e-digital :- [model/Produto]
  [db
   categorias :- [s/Str]
   digital? :- s/Bool]
  (adapter.entidade/datomic-para-entidade
    (d/q '[:find [(pull ?produto [* {:produto/categoria [*]}]) ...]
           :in $ % [?nome-das-categorias ...] ?digital
           :where (produto-na-categoria ?produto ?nome-das-categorias)
           [?produto :produto/digital ?digital]]
         db regras categorias digital?)))

(s/defn atualiza-preco!
  [conn
   produto-id :- s/Uuid
   preco-antigo :- s/Num
   preco-novo :- s/Num]
  (d/transact conn [[:db/cas [:produto/id produto-id] :produto/preco preco-antigo preco-novo]]))

(s/defn total :- s/Int
  [db]
  (d/q '[:find (count ?p) .
         :where [?p :produto/id]]
       db))

(s/defn remove!
  [conn produto-id :- s/Uuid]
  (d/transact conn [[:db/retractEntity [:produto/id produto-id]]]))

(s/defn visualizacoes!
  [conn produto-id :- s/Uuid]
  (d/transact conn [[:incrementa-visualizacao produto-id]]))

(s/defn historico-de-precos
  [db produto-id :- s/Uuid]
  (->> (d/q '[:find ?instante ?preco
              :in $ ?id
              :where [?produto :produto/id ?id]
              [?produto :produto/preco ?preco ?tx true]
              [?tx :db/txInstant ?instante]]
            (d/history db) produto-id)
       (sort-by first)))




