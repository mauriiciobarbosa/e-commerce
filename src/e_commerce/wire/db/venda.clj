(ns e-commerce.wire.db.venda
  (:require [schema.core :as s]
            [e-commerce.wire.db.produto :as wire.db.produto]))

(def Venda
  {:venda/id                   {:schema s/Uuid :id true :doc "Id da venda"}
   :venda/produto              {:schema wire.db.produto/Produto :doc "Produto vendido"}
   :venda/quantidade           {:schema s/Int :doc "Quantidade de produtos vendidos"}
   :venda/situacao             {:schema s/Str :doc "Situação da venda"}})
