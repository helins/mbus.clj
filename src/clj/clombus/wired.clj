(ns clombus.wired

  "Wired meter-bus through serial ports."

  {:author "Adam Helinski"}

  (:require [clombus.interop :as $.interop])
  (:import java.io.IOException
           org.openmuc.jrxtx.SerialPortTimeoutException
           (org.openmuc.jmbus MBusSap
                              SecondaryAddress)))




;;;;;;;;;; Private


(defmacro ^:private -try

  "Helper for IWrapper fns.
  
   Wraps forms in order to catch timeout and certain io exceptions."

  [& body]

  `(try
     ~@body
     (catch SerialPortTimeoutException _#
       ::timeout)
     (catch IOException e#
       (if (deref @~'closed?)
         ::closed
         (throw e#)))))




;;;;;;;;;; API


(defprotocol IWrapper

  "Handles with wired M-Bus slaves."


  (raw [access-point]

    "Gets the raw org.openmuc.jmbus.MBusSap object.")


  (status [access-point]

    "Gets the current status of the access point.")


  (select [access-point secondary-address]

    "Selects a slave using its secondary address (given as a hex string).

     This slave will then be accessible using primary address 0xfd.


     Returns
    
       ::success
         If the operation is successful.
       
       ::timeout
         If the slave did not answer within the timeout.
             
       ::closed
         If the access point is closed.


     Throws
    
       java.io.IOException
         If something goes wrong.")


  (selected [access-point]

    "Gets the selected secondary address if there is one.

     Cf. `select`")


  (deselect [access-point]

    "Deselects a slave.


     Returns
    
       ::success
         If the operation is successful.
       
       ::timeout
         If the slave did not answer within the timeout.
             
       ::closed
         If the access point is closed.


     Throws
    
       java.io.IOException
         If something goes wrong.
    

     Cf. `select`")


  (req-ud2 [access-point]
           [access-point primary-address]

    "Requests user data from the slave and waits for its answer.

     Default address is 0xfd, the placeholder for selected slaves.


     Returns
    
       variable data structure
         Response from the slave, map of various data.
         Cf. `clombus.interop/variable-data-structure`
       
       ::timeout
         If the slave did not answer within the timeout.
             
       ::closed
         If the access point is closed.


     Throws
    
       java.io.IOException
         If something goes wrong.


     Cf. `select`")

  
  (snd-nke [access-point]
           [access-point primary-address]

    "Sends a SND_NKE message to reset the FCB (frame counter bit).

     Default address is 0xfd, the placeholder for selected slaves.
  

     Returns
    
       ::success
         If the operation is successful.
       
       ::timeout
         If the slave did not answer with a 0xe5 message within the timeout.
             
       ::closed
         If the access point is closed.


     Throws
    
       java.io.IOException
         If something goes wrong.

    
     Cf. `selected`")


  (snd-ud [access-point ba]
          [access-point primary-address ba]

    "Sends user data to a slave.

     Default address is 0xfd, the placeholder for selected slaves.
    

     Returns
    
       ::success
         If the operation is successful.

       ::fail
         If the write failed.
       
       ::timeout
         If the slave did not answer within the timeout.
             
       ::closed
         If the access point is closed.


     Throws
    
       java.io.IOException
         If something goes wrong.

    
     Cf. `selected`")


  (reset-application [access-point]
                     [access-point primary-address]

    "Sends an application reset to the requested slave.

     Default address is 0xfd, the placeholder for selected slaves.


     Returns
    
       ::success
         If the operation is successful.
       
       ::timeout
         If the slave did not answer with a 0xe5 message within the timeout.
             
       ::closed
         If the access point is closed.


     Throws
    
       java.io.IOException
         If something goes wrong.

    
     Cf. `selected`")


  (closed? [access-point]

    "Is this meter-bus access point closed ?")


  (close [access-point]

    "Closes the access point"))




(deftype Wrapper [^MBusSap           access-point
                                     baud-rate
                                     timeout-ms
                  ^:volatile-mutable -closed?
                  ^:volatile-mutable -secondary-address]

  IWrapper


    (raw [_]
      access-point)

    
    (status [_]
      {:baud-rate         baud-rate
       :timeout-ms        timeout-ms
       :closed?           -closed?
       :secondary-address -secondary-address})


    (select [_ secondary-address]
      (-try
        (let [secondary-address' (if (string? secondary-address)
                                   (SecondaryAddress/getFromHexString secondary-address)
                                   secondary-address)]
        (.selectComponent access-point
                          secondary-address')
        (set! -secondary-address
              secondary-address')
        ::success)))


    (selected [_]
      -secondary-address)


    (deselect [_]
      (-try
        (.deselectComponent access-point)
        (set! -secondary-address
              nil)
        ::success))


    (req-ud2 [access-point]
      (req-ud2 access-point
               0xfd))


    (req-ud2 [_ primary-address]
      (let [res (-try
                  (.read access-point
                         primary-address))]
        (if (or (identical? res
                            ::closed)
                (identical? res
                            ::timeout))
          res
          ($.interop/variable-data-structure res))))


    (snd-nke [access-point]
      (snd-nke access-point
               0xfd))


    (snd-nke [_ primary-address]
      (-try
        (.linkReset access-point
                    primary-address)
        ::success))


    (snd-ud [access-point ba]
      (snd-ud access-point
              0xfd))


    (snd-ud [_ primary-address ba]
      (-try
        (if (.write access-point
                    primary-address
                    ba)
          ::success
          ::fail)))


    (reset-application [access-point]
      (reset-application access-point
                         0xfd))


    (reset-application [_ primary-address]
      (-try
        (.resetReadout access-point
                       primary-address)
        ::success))


    (closed? [_]
      -closed?)


    (close [_]
      (.close access-point)
      (set! -closed?
            true)
      nil))




;;;;;;;;;;


(defn open

  "Opens an M-Buss access point on the given path to the serial port.

   The configuration map may contain :

       :baud-rate
         A preferably standard baud rate.

       :timeout
         How many milliseconds it must wait for a slave answer before giving up.


   Throws
  
     java.io.IOException
       If something goes wrong."

  ^MBusSap

  ([path]

   (open path
         nil))


  ([path {:as   ?config
          :keys [baud-rate
                 timeout]
          :or   {baud-rate 2400
                 timeout   500}}]
 
   (let [access-point ^MBusSap (MBusSap. path
                                         baud-rate)]
     (.setTimeout access-point
                  timeout)
     (.open access-point)
     (Wrapper. access-point
               baud-rate
               timeout
               false
               nil))))
