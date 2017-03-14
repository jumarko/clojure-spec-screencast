(ns clojure-spec-screencast.01-leverage
  (:require [clojure
             [repl :refer [doc]]
             [spec :as s]
             [string :as str]]
            [clojure.spec.test :as test]))

;; example fn
(defn my-index-of
  "Returns the index at which search appears in source"
  [source search]
  (str/index-of source search))

(my-index-of "foobar" "b")
(apply my-index-of ["foobar" "b"])

;; spec regex
(s/def ::index-of-args (s/cat :source string? :search string?))

;;; check validity
(s/valid? ::index-of-args ["foo" "f"])
(s/valid? ::index-of-args ["foo" 3]) 
;;; conform - if you want more information
(s/conform ::index-of-args ["foo" "f"])
;;=> {:source "foo", :search "f"}
(s/unform ::index-of-args {:source "foo" :search "f"})
;;=> ("foo" "f")

;;; use explain to get precise errors
;; explain prints to stdout
(s/explain ::index-of-args ["foo" 3])
;; explain-str will return string description
(s/explain-str ::index-of-args ["foo" 3])
;; explain-data will return data structure
(s/explain-data ::index-of-args ["foo" 3])


;;; Specs are composable
(s/explain (s/every ::index-of-args) [["good" "a"]
                                     ["ok" "b"]
                                     ["bad" 42]])


;;; Data Generation
;;; Useful for testing, verifying your specs and understading others' specs
(s/exercise ::index-of-args)
;;=> ([("" "") {:source "", :search ""}] [("z" "1") {:source "z", :search "1"}] [("wJ" "") {:source "wJ", :search ""}] [("6u6" "ik9") {:source "6u6", :search "ik9"}] [("i" "h1") {:source "i", :search "h1"}] [("47qEt" "A5") {:source "47qEt", :search "A5"}] [("t" "p7") {:source "t", :search "p7"}] [("5M" "3") {:source "5M", :search "3"}] [("cw99gDl" "42A3Gq") {:source "cw99gDl", :search "42A3Gq"}] [("24QQVFo" "48JH") {:source "24QQVFo", :search "48JH"}])


;;; Assertions
;; turn on assertions - throw exception if data are invalid
;; assertions have no runtime footprint when they are disabled
(s/check-asserts true)

(s/assert ::index-of-args ["foo" "f"])
;; following will fail with an exception
;;(s/assert ::index-of-args ["foo" 42])


;;; Specing a function
;; Example of rich specification - far richer than Java type system
(s/fdef my-index-of
        ;; args to the function
        :args (s/cat :source string? :search string?)
        ;; return value of the function
        :ret nat-int?
        ;; semantics of invoking the function - relationship between arguments and return value
        :fn #(<= (:ret %) (-> % :args :source :count)))

;;; With function specification you will get following for free

;; 1. enhanced documentation
(doc my-index-of)

;; 2. generative testing
(test/summarize-results (test/check `my-index-of))

;; 3. Instrumentation
;; allows to ensure that functions are called with proper arguments
(test/instrument `my-index-of)
(my-index-of "foo" 42)

