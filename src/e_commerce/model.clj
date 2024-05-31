(ns e-commerce.model
  (:require [schema.core :as s]))

(defn uuid [] (random-uuid))

(def Categoria
  {:categoria/id   s/Uuid
   :categoria/nome s/Str})

(def Variacao
  {:variacao/id   s/Uuid
   :variacao/nome s/Str
   :variacao/preco s/Num})

(def Produto
  {:produto/id                             s/Uuid
   (s/optional-key :produto/nome)          s/Str
   (s/optional-key :produto/slug)          s/Str
   (s/optional-key :produto/preco)         BigDecimal
   (s/optional-key :produto/palavra-chave) [s/Str]
   (s/optional-key :produto/categoria)     Categoria
   (s/optional-key :produto/estoque)       s/Int
   (s/optional-key :produto/digital)       s/Bool
   (s/optional-key :produto/variacao)      [Variacao]
   (s/optional-key :produto/visualizacoes) s/Int})

(def Venda
  {:venda/id                          s/Uuid
   (s/optional-key :venda/produto)    Produto
   (s/optional-key :venda/quantidade) s/Int
   (s/optional-key :venda/situacao)   s/Str})

(s/defn novo-produto :- Produto
  ([nome slug preco]
   (novo-produto (uuid) nome slug preco))
  ([uuid nome slug preco]
   (novo-produto uuid nome slug preco 0))
  ([uuid nome slug preco estoque]
   {:produto/id      uuid
    :produto/nome    nome
    :produto/slug    slug
    :produto/preco   preco
    :produto/estoque estoque
    :produto/digital false}))

(defn nova-categoria
  ([nome]
   (nova-categoria (uuid) nome))
  ([uuid nome]
   {:categoria/id   uuid
    :categoria/nome nome}))

(s/defn nova-venda :- Venda
  ([produto :- Produto
    quantidade :- s/Int]
   (nova-venda (uuid) produto quantidade))
  ([uuid :- s/Uuid
    produto :- Produto
    quantidade :- s/Int]
   {:venda/id   uuid
    :venda/produto produto
    :venda/quantidade quantidade
    :venda/situacao "nova"}))