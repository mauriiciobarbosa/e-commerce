(ns e-commerce.db.venda
  (:require [datomic.api :as d]
            [e-commerce.adapter.entidade :as adapter.entidade]
            [e-commerce.model :as model]
            [schema.core :as s]))

(s/defn adiciona! :- s/Uuid
  [conn
   {:venda/keys [id produto quantidade]} :- model/Venda]
  (do (d/transact conn [{:db/id (str "venda-" id)
                         :venda/id         id
                         :venda/produto    [:produto/id (:produto/id produto)]
                         :venda/quantidade quantidade}])
      id))

; Tem um problema se o preço do produto mudar.
; Poderia resolver passando a informação do preço pra entidade de venda.
; Mas o que aconteceria se a descrição do produto mudar e eu precisar informar ela? E a imagem?
(s/defn custo-total-errado
  [db
   venda-id :- s/Uuid]
  (d/q '[:find (sum ?custo-por-produto) .
         :in $ ?id
         :where [?venda :venda/id ?id]
                [?venda :venda/quantidade ?quantidade]
                [?venda :venda/produto ?produto]
                [?produto :produto/preco ?preco]
                [(* ?quantidade ?preco) ?custo-por-produto]]
       db venda-id))

(s/defn instante-da-venda
  [db venda-id]
  (d/q '[:find ?inst .
         :in $ ?id
         :where [_ :venda/id ?id ?tx true]
         [?tx :db/txInstant ?inst]]
       db venda-id))

; Poderia simplificar em uma query só, utilizando nested queries ou novas condições.
(s/defn custo-total
  [db
   venda-id :- s/Uuid]
  (let [instante (instante-da-venda db venda-id)]
    (d/q '[:find (sum ?custo-por-produto) .
           :in $ ?id
           :where [?venda :venda/id ?id]
                  [?venda :venda/quantidade ?quantidade]
                  [?venda :venda/produto ?produto]
                  [?produto :produto/preco ?preco]
                  [(* ?quantidade ?preco) ?custo-por-produto]]
         (d/as-of db instante) venda-id)))

(s/defn cancela!
  [conn venda-id :- s/Uuid]
  (d/transact conn [[:db/retractEntity [:venda/id venda-id]]]))

(s/defn todas-nao-canceladas!                                              ;:- [model/Venda]
  [db]
  (-> (d/q '[:find ?id
             :where [?venda :venda/id ?id]]
           db)))

(defn todas!
  [db]
  (-> (d/q '[:find ?id
             :where [?venda :venda/id ?id _ true]]
           (d/history db))))

(defn todas-canceladas!
  [db]
  (-> (d/q '[:find ?id
             :where [?venda :venda/id ?id _ false]]
           (d/history db))))
