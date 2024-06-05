(ns e-commerce.db.adapter
  (:require [schema.core :as s])
  (:import (java.util UUID)
           (schema.core OptionalKey)))

(defn ^:private merge-some
  [map value attr]
  (cond-> map (true? value) (merge attr)))

(defn ^:private build-cardinality
  [schema]
  (if (vector? schema)
    :db.cardinality/many
    :db.cardinality/one))

(defn ^:private build-value-type
  [schema]
  (cond (= schema UUID) :db.type/uuid
        (= schema s/Str) :db.type/string
        (= schema BigDecimal) :db.type/bigdec
        (= schema s/Bool) :db.type/boolean
        (contains? #{s/Int Long} schema) :db.type/long
        (map? schema) :db.type/ref
        (vector? schema) (build-value-type (first schema))
        :else {:db/valueType (str "unknown: " (type key) key)}))

(defn ^:private build-value-properties
  [{:keys [schema id doc component index noHistory] :or {id false doc "" component false index false noHistory false}}]
  (let [unique-attr {:db/unique :db.unique/identity}
        component-attr {:db/isComponent true}
        index-attr {:db/index true}
        noHistory-attr {:db/noHistory true}]
    (-> {:db/valueType   (build-value-type schema)
         :db/cardinality (build-cardinality schema)
         :db/doc         doc}
      (merge-some id unique-attr)
      (merge-some component component-attr)
      (merge-some index index-attr)
      (merge-some noHistory noHistory-attr))))

(defn ^:private extract-key-name
  [key]
  (cond (keyword? key) key
        (instance? OptionalKey key) (get key :k)
        :else key))

(defn ^:private key-value-to-definition
  [[key value]]
  (let [base {:db/ident (extract-key-name key)}
        extra (build-value-properties value)
        schema-do-datomic (merge base extra)]
    schema-do-datomic))

(defn schema-to-datomic
  [definition]
  (mapv key-value-to-definition definition))