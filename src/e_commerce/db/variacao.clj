(ns e-commerce.db.variacao
  (:require [datomic.api :as d]
            [schema.core :as s]))

(s/defn adiciona!
        [conn produto-id :- s/Uuid variacao :- s/Str preco :- s/Num]
        (d/transact conn [{:db/id "adiciona-variacao"
                           :variacao/nome variacao
                           :variacao/preco preco
                           :variacao/id (random-uuid)}
                          {:produto/id produto-id
                           :produto/variacao "adiciona-variacao"}]))
