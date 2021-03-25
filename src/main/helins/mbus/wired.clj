;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at https://mozilla.org/MPL/2.0/.


(ns helins.mbus.wired

  "Wired meter-bus through the serial port or TCP/IP.

   All functions are specified by clojure.spec and might throw in case of IO failure.
  
   When a timeout in milliseconds is given, IO operations will only block for that amount of time
   when waiting an answer / confirmation from a slave."

  {:author "Adam Helinski"}

  (:require [helins.mbus         :as mbus]
            [helins.mbus.interop :as mbus.interop])
  (:import org.openmuc.jmbus.MBusConnection
           org.openmuc.jrxtx.SerialPortTimeoutException))


;;;;;;;;;; Private helpers


(defn- -obtain

  ;; Retrieves value or rely on default one.

  [k hmap]

  (or (get hmap
           k)
      (get mbus/defaults
           k)))


;;;;;;;;;; Opening and closing a connection


(defn close

  "Closes the given connection."

  [^MBusConnection connection]

  (.close connection))



(defn serial-connection

  "Establishes a Meter-Bus connection on the given serial port."

  (^MBusConnection
    
   [path]

   (serial-connection path
                      nil))


  (^MBusConnection
    
   [path serial-options]

   ;; More options such as parity ?
   ;; MBus standardizes does, doesn't it ?

   (.build (doto (MBusConnection/newSerialBuilder path)
             (.setBaudrate (-obtain ::mbus/baud-rate
                                    serial-options))
             (.setTimeout (max 0
                               (-obtain ::mbus/timeout-ms
                                        serial-options)))))))



(defn tcp-connection

  "Establishes a Meter-Bus connection via TCP/IP.

   <!> Experimental."

  (^MBusConnection
  
   [host port]

   (tcp-connection host
                   port
                   nil))


  (^MBusConnection
    
   [host port tcp-options]

   (.build (doto (MBusConnection/newTcpBuilder host
                                                     port)
                   (.setTimeout (max 0
                                     (-obtain ::mbus/timeout-ms
                                              tcp-options)))))))


;;;;;;;;; Doing IO


(defn req-ud2

  "Requests user data from the slave and waits for a variable data structure.

   Returns nil if a timeout was set and is up."

  ([connection]

   (req-ud2 connection
            (get mbus/defaults
                 ::mbus/primary-address)))


  ([^MBusConnection connection primary-address]

   (try
     (mbus.interop/variable-data-structure->clj (.read connection
                                                       primary-address))
     (catch SerialPortTimeoutException _
       nil))))



(defn reset-application

  "Sends an application reset to the requested slave.

   Returns false if a timeout was set and is up, true if everything went well."

  ([connection]

   (reset-application connection
                      (get mbus/defaults
                           ::mbus/primary-address)))


  ([^MBusConnection connection primary-address]

   (try
     (.resetReadout connection
                    primary-address)
     true
     (catch SerialPortTimeoutException _
       false))))



(defn snd-nke

  "Sends a SND_NKE message to reset the FCB (frame counter bit).

   Returns false if a timeout was set and is up, true if everything went well."

  ([connection]

   (reset-application connection
                      (get mbus/defaults
                           ::mbus/primary-address)))


  ([^MBusConnection connection primary-address]

   (try
     (.linkReset connection
                 primary-address)
     true
     (catch SerialPortTimeoutException _
       false))))



(defn send-ud

  "Sends user data to a slave.

   Returns false if a timeout was set and is up, true if everything went well."

  ([connection ba]

   (send-ud connection
            ba
            (get mbus/defaults
                 ::mbus/primary-address)))


  ([^MBusConnection connection ba primary-address]

   (try
     (.write connection
             primary-address
             ba)
     true
     (catch SerialPortTimeoutException _
       false))))
