(ns e-commerce.curso6.aula2
  (:require [datomic.api :as d]
            [e-commerce.db.produto :as db.produto]
            [e-commerce.db.config :as db.config]
            [e-commerce.model :as model]
            [e-commerce.generators :as generators]
            [schema.core :as s]
            [schema-generators.generators :as g]))

(s/set-fn-validation! true)
(db.config/apaga-banco!)
(def conn (db.config/abre-conexao!))

(db.config/cria-schema! conn)

;FIXME mover pra outro namespace
(db.config/cria-dados-de-exemplo! conn)

(def primeiro (first (db.produto/todos (d/db conn))))

(defn gera-1000-produtos
  [conn]
  (dotimes [atual 50]
    (let [produtos-gerados (g/sample 200 model/Produto generators/leaf-generators)]
      (println atual
               (count @(db.produto/adiciona-ou-altera! conn produtos-gerados))))))

(println "Levou " (time (gera-1000-produtos conn)))

(time (dotimes [_ 100] (db.produto/busca-mais-caro (d/db conn))))

(time (dotimes [_ 100] (count (db.produto/busca-mais-caro-que (d/db conn) 500.0M))))
