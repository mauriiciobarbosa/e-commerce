(ns e-commerce.db.venda
  (:require [datomic.api :as d]
            [e-commerce.model :as model]
            [schema.core :as s]))

(s/defn adiciona! :- s/Uuid
  [conn
   {:venda/keys [id produto quantidade situacao]} :- model/Venda]
  (do (d/transact conn [{:db/id (str "venda-" id)
                         :venda/id         id
                         :venda/produto    [:produto/id (:produto/id produto)]
                         :venda/quantidade quantidade
                         :venda/situacao   situacao}])
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

(s/defn todas-nao-canceladas!                                              ;:- [model/Venda]
  [db]
  (-> (d/q '[:find ?id
             :where [?venda :venda/id ?id]
                    [?venda :venda/situacao ?situacao]
                    [(not= ?situacao "cancelada")]]
           db)))

(defn todas!
  [db]
  (-> (d/q '[:find ?id
             :where [?venda :venda/id ?id]]
           db)))

(defn todas-canceladas!
  [db]
  (-> (d/q '[:find ?id
             :where [?venda :venda/id ?id]
                    [?venda :venda/situacao "cancelada"]]
           db)))

(s/defn altera-situacao!
  [conn
   venda-id :- s/Uuid
   situacao :- s/Str]
  (d/transact conn [{:venda/id venda-id
                     :venda/situacao situacao}]))

(s/defn cancela!
  [conn venda-id :- s/Uuid]
  (altera-situacao! conn venda-id "cancelada"))

(s/defn historico
  [db venda-id :- s/Uuid]
  (->> (d/q '[:find ?instante ?situacao
              :in $ ?id
              :where [?venda :venda/id ?id]
                     [?venda :venda/situacao ?situacao ?tx true]
                     [?tx :db/txInstant ?instante]]
            (d/history db) venda-id)
       (sort-by first)))

(defn historico-geral
  [db
   instant-desde]
  (let [filtrado (d/since db instant-desde)]
    (->> (d/q '[:find ?instante ?situacao ?id
                :in $ $filtrado
                :where [$ ?venda :venda/id ?id]
                       [$filtrado ?venda :venda/situacao ?situacao ?tx]
                       [$filtrado ?tx :db/txInstant ?instante]]
              db filtrado)
         (sort-by first))))
