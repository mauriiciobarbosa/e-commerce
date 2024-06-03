(ns e-commerce.generators
  (:require [clojure.test.check.generators :as gen]))

(def bigdecimal (gen/fmap #(bigdec (abs %)) (gen/double* {:infinite? false :NaN? false})))

(def leaf-generators {BigDecimal bigdecimal})