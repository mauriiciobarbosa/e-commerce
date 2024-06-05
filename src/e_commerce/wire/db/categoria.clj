(ns e-commerce.wire.db.categoria
  (:require [schema.core :as s]))

(def Categoria
  {:categoria/id   {:schema s/Uuid :id true :doc "identificador do produto"}
   :categoria/nome {:schema s/Str :doc "nome do produto"}})
