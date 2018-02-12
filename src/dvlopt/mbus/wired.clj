(ns dvlopt.mbus.wired

  "Wired meter-bus through the serial ports or TCP/IP"

  {:author "Adam Helinski"}

  (:require [clojure.spec.alpha  :as s]
            [dvlopt.mbus         :as mbus]
            [dvlopt.mbus.interop :as mbus.interop]
            [dvlopt.void         :as void])
  (:import (java.io InterruptedIOException
                    IOException)
           (org.openmuc.jmbus MBusConnection
                              SecondaryAddress)
           org.openmuc.jmbus.transportlayer.Builder))




;;;;;;;;;; Declarations


(declare WiredMBus)




;;;;;;;;;; Specs - Wired Meter-bus


(s/def ::connection

  #(satisfies? WiredMBus
               %))


(s/def ::status

  (s/keys :req [::mbus/closed?]
          :opt [::mbus/secondary-address]))




;;;;;;;;;; Specs - Results - Opening a connection


(s/def ::result.open

  (s/or :success ::success.open
        :failure ::failure.open))


(s/def ::success.open

  (s/keys :req [::connection]))


(s/def ::failure.open

  (s/keys :req [::blame.open
                ::mbus/exception]))


(s/def ::blame.open

  #{:io
    :unknown})




;;;;;;;;;; Specs - Results - IO


(s/def ::result.io

  (s/or :success ::success.io
        :failure ::failure.io))


(s/def ::success.io

  nil?)

(s/def ::failure.io

  (s/keys :req [::blame.io]
          :opt [::mbus/exception]))


(s/def ::blame.io

  #{:io
    :timeout
    :unknown})




;;;;;;;;;; Specs - Results - UD2


(s/def ::result.req-ud2

  (s/or :success ::success.req-ud2
        :failure ::failure.io))


(s/def ::success.req-ud2

  (s/keys :req [::mbus/variable-data-structure]))




;;;;;;;;;; Private


(defmacro ^:private -try-io

  "Helper for WiredMBus functions.
  
   Wraps forms in order to catch timeouts and exceptions."

  [& body]

  `(try
     ~@body
     (catch InterruptedIOException _#
       {::blame.io :timeout})
     (catch IOException e#
       {::blame.io       :io
        ::mbus/exception e#})
     (catch Throwable e#
       {::blame.io       :unknown
        ::mbus/exception e#})))




;;;;;;;;;; API - Protocol and implementation


(defprotocol WiredMBus

  "Handles with wired M-Bus slaves."


  (close [this]

    "Closes the connection.")


  (^MBusConnection raw [this]

    "Gets the raw java object, for expert users.")


  (req-ud2 [this]
           [this primary-address]

    "Requests user data from the slave and waits for its answer.")


  (reset-application [this]
                     [this primary-address]

    "Sends an application reset to the requested slave.")


  (snd-nke [this]
           [this primary-address]

    "Sends a SND_NKE message to reset the FCB (frame counter bit).")


  (snd-ud [this ba]
          [this primary-address ba]

    "Sends user data to a slave.")

  (status [this]

    "Gets the current status of the connection.")


  #_(select [this secondary-address]

    "Selects a slave using its secondary address (given as a hex string).

     This slave will then be accessible using primary address 0xfd.")


  #_(deselect [this]

    "Deselects a slave."))




(s/fdef close

  :args (s/cat :connection ::connection)
  :ret  nil?)


(s/fdef raw

  :args (s/cat :connection ::connection)
  :ret  ::mbus.interop/MBusConnection)


(s/fdef req-ud2

  :args (s/cat :connection      ::connection
               :primary-address (s/? ::mbus/primary-address))
  :ret  ::result.req-ud2)


(s/fdef reset-application

  :args (s/cat :connection      ::connection
               :primary-address (s/? ::primary-address))
  :ret  ::result.io)


(s/fdef snd-nke

  :args (s/cat :connection      ::connection
               :primary-address (s/? ::mbus/primary-address))
  :ret  ::result.io)


(s/fdef snd-ud

  :args (s/cat :connection      ::connection
               :primary-address (s/? ::mbus/primary-address)
               :byte-array      bytes?)
  :ret  ::result.io)


(s/fdef status

  :args (s/cat :connection ::connection)
  :ret  ::status)




(deftype WiredMBusConnection [^MBusConnection    cnx
                              ^:volatile-mutable -closed?
                              ^:volatile-mutable -secondary-address]

  WiredMBus


    (close [_]
      (when (not -closed?)
        (.close cnx)
        (set! -closed?
              true))
      nil)


    (raw [_]
      cnx)


    (req-ud2 [this]
      (req-ud2 this
               0xfd))


    (req-ud2 [_ primary-address]
      (-try-io
        {::mbus/variable-data-structure (mbus.interop/variable-data-structure->clj (.read cnx
                                                                                          primary-address))}))


    (reset-application [this]
      (reset-application this 
                         0xfd))


    (reset-application [_ primary-address]
      (-try-io
        (.resetReadout cnx
                       primary-address)
        nil))


    (snd-nke [this]
      (snd-nke this
               0xfd))


    (snd-nke [_ primary-address]
      (-try-io
        (.linkReset cnx
                    primary-address)
        nil))


    (snd-ud [this ba]
      (snd-ud this
              0xfd))


    (snd-ud [_ primary-address ba]
      (-try-io
        (.write cnx
                primary-address
                ba)
        nil))


    (status [_]
      (void/assoc-some {::mbus/closed? -closed?}
                       ::mbus/secondary-address -secondary-address))


    #_(select [_ secondary-address]

      ;; java secondary addresses should not be stored as metadata as it makes data no serializable
      (-try-io
        (.selectComponent cnx
                          (if (map? secondary-address)
                            (::mbus.interop/SecondaryAddress (meta secondary-address))
                            secondary-address))
        nil))


    #_(deselect [_]
      (-try-io
        (.deselectComponent cnx)
        nil)))




;;;;;;;;;; API - Opening connections


(s/fdef -open

  :args (s/cat :builder #(instance? Builder
                                    %)
               :opts    (s/keys :opt [::mbus/timeout-ms]))
  :ret  ::result.open)


(defn- -open

  "Helper for opening a Meter-Bus connection.
  
   Configures last settings and establishes the connection."

  [^Builder builder opts]

  (.setTimeout builder
               (max 0
                    (void/obtain ::mbus/timeout-ms
                                 opts
                                 mbus/defaults)))
  (try
    {::connection (WiredMBusConnection. (.build builder)
                                        false
                                        nil)}
    (catch IOException e
      {::mbus/failure   :io
       ::mbus/exception e})
    (catch Throwable e
      {::mbus/failure   :unknown
       ::mbus/exception e})))




(s/fdef open-serial

  :args (s/cat :path ::mbus/path
               :opts (s/? (s/keys :opt [::mbus/timeout-ms
                                        ::mbus/baud-rate])))
  :ret  ::result.open)


(defn open-serial

  "Establishes a Meter-Bus connection on the given serial port."

  ([path]

   (open-serial path
                nil))


  ([path opts]

   ;; TODO more options related to jRXTX

   (let [builder (MBusConnection/newSerialBuilder path)]
     (.setBaudrate builder
                   (void/obtain ::mbus/baud-rate
                                opts
                                mbus/defaults))
     (-open builder
            opts))))




(s/fdef open-tcp

  :args (s/cat :host ::mbus/host
               :port ::mbus/port
               :opts (s/? (s/keys :opt [::mbus/timeout-ms])))
  :ret  ::result.open)


(defn open-tcp

  "Establishes a Meter-Bus connection via TCP/IP.
  
   <!> Experimental."

  ([host port]

   (open-tcp host
             port
             nil))


  ([host port opts]

   (-open (MBusConnection/newTcpBuilder host
                                        port)
          opts)))




;;;;;;;;;; TODO

;; send long and short messages ?

 
;; select a device using its secondary address (needs user-friendly address building)

#_(s/fdef select

  :args (s/cat :connection ::connection
               :secondary-address (s/or :clj  ::mbus/secondary-address
                                        :java ::mbus.interop/SecondaryAddress))
  :ret  (s/or :success nil?
              :error   ::mbus/error))


#_(s/fdef deselect

  :args (s/cat :connection ::connection)
  :ret  (s/or :success nil?
              :error   ::mbus/error))
