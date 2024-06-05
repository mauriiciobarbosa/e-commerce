(ns e-commerce.wire.db.variacao
  (:require [schema.core :as s]))

(def Variacao
  {:variacao/id       {:schema s/Uuid :id true :doc "Id da variação"}
   :variacao/nome     {:schema s/Str :doc "Nome da variação"}
   :variacao/preco    {:schema BigDecimal :doc "Preço da variação"}})