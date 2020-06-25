(ns propeller.push.utils
  (:require [propeller.push.core :refer [def-instruction]]
            [propeller.push.state :refer :all]))

;; A utility function for making Push instructions. Takes a state, a function
;; to apply to the args, the stacks to take the args from, and the stack to
;; return the result to. Applies the function to the args (popped from the
;; given stacks), and pushes the result onto the return-stack
(defn make-instruction
  [state function arg-stacks return-stack]
  (let [popped-args (get-args-from-stacks state arg-stacks)]
    (if (= popped-args :not-enough-args)
      state
      (let [result (apply function (:args popped-args))
            new-state (:state popped-args)]
        (push-to-stack new-state return-stack result)))))

;; Given a sequence of stacks, e.g. [:float :integer], and a sequence of suffix
;; function strings, e.g. [_+, _*, _=], automates the generation of all possible
;; combination instructions, which here would be :float_+, :float_*, :float_=,
;; :integer_+, :integer_*, and :integer_=
(defmacro generate-functions [stacks functions]
  `(do ~@(for [stack stacks
               function functions
               :let [instruction-name (keyword (str (name stack) function))]]
           `(def-instruction ~instruction-name (partial ~function ~stack)))))

;; Pretty-prints a Push state, for logging or debugging purposes
(defn print-state
  [state]
  (doseq [stack stacks]
    (printf "%-15s = " stack)
    (prn (if (get state stack) (get state stack) '()))
    (flush)))
