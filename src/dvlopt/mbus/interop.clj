(ns dvlopt.mbus.interop

  "Utilities and specs for java objects."

  {:author "Adam Helinski"}

  (:require [clojure.spec.alpha     :as s]
            [clojure.spec.gen.alpha :as gen])
  (:import (org.openmuc.jmbus DataRecord
                              DataRecord$DataValueType
                              DataRecord$FunctionField
                              DataRecord$Description
                              DeviceType
                              DlmsUnit
                              MBusConnection
                              SecondaryAddress
                              VariableDataStructure)))




;;;;;;;;;;


(defn spec-enum

  "Creates a spec with its generator for a java enum."

  [klass values]

  (s/with-gen #(instance? klass
                          %)
              (fn gen []
                (gen/fmap (fn value [i]
                            (nth values
                                 i))
                          (s/gen (s/int-in 0
                                           (count values)))))))




;;;;;;;;;; Specs - Java Classes



(s/def ::DataRecord

  #(instance? DataRecord
              %))


(s/def ::DataRecord$DataValueType

  (spec-enum DataRecord$DataValueType
             (DataRecord$DataValueType/values)))


(s/def ::DataRecord$FunctionField

  (spec-enum DataRecord$FunctionField
             (DataRecord$FunctionField/values)))


(s/def ::DataRecord$Description

  (spec-enum DataRecord$Description
             (DataRecord$Description/values)))


(s/def ::DeviceType

  (spec-enum DeviceType
             (DeviceType/values)))


(s/def ::DlmsUnit

  (spec-enum DlmsUnit
             (DlmsUnit/values)))


(s/def ::MBusConnection

  #(instance? MBusConnection
              %))


(s/def ::SecondaryAddress

  (s/with-gen #(instance? SecondaryAddress
                          %)
              (fn gen []
                (gen/fmap (fn make [^bytes ba]
                            (SecondaryAddress/newFromLongHeader ba
                                                                0))
                          (s/gen bytes?)))))


(s/def ::VariableDataStructure

  #(instance? VariableDataStructure
              %))
