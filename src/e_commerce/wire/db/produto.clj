(ns e-commerce.wire.db.produto
  (:require [schema.core :as s]
            [e-commerce.wire.db.categoria :as wire.db.categoria]
            [e-commerce.wire.db.variacao :as wire.db.variacao]))

(def Produto
  {:produto/id                      {:schema s/Uuid :id true :doc "Id do produto"}
   :produto/nome                    {:schema s/Str :doc "Nome do produto"}
   :produto/slug                    {:schema s/Str :doc "O caminho para acessar esse produto via http (ex.: /computador-novo)"}
   :produto/preco                   {:schema BigDecimal :index true :doc "Preço do produto"}
   :produto/palavra-chave           {:schema [s/Str] :doc "Palavras chaves para encontrar o produto (ex.: digital, infantil, etc)"}
   :produto/categoria               {:schema wire.db.categoria/Categoria :doc "Categoria do produto"}
   :produto/estoque                 {:schema Long :doc "Quantidade de itens disponíveis em estoque"}
   :produto/digital                 {:schema s/Bool :doc "Pra saber se o produto é digital ou não"}
   :produto/variacao                {:schema [wire.db.variacao/Variacao] :component true :doc "Variações do produto (ex.: computador 8GB, computador 16GB)"}
   :produto/visualizacoes           {:schema Long :noHistory true :doc "Número de vezes que o produto foi visualizado"}})