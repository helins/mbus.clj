(ns dvlopt.mbus.interop.clj

  "Converting java objects from the wrapped library to clojure data structures."

  {:author "Adam Helinski"}

  (:require [clojure.spec.alpha  :as s]
            [dvlopt.void         :as void]
            [dvlopt.mbus         :as mbus]
            [dvlopt.mbus.interop :as mbus.interop])
  (:import java.util.Date
           (org.openmuc.jmbus DlmsUnit
                              DataRecord$DataValueType
                              DataRecord$FunctionField
                              DataRecord$Description
                              DataRecord
                              DeviceType
                              SecondaryAddress
                              VariableDataStructure)))




;;;;;;;;;; Data record



;;;;;;;;;; Secondary address

