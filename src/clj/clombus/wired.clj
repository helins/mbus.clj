(ns clombus.wired

  "Wired meter-bus through serial ports"

  {:author "Adam Helinski"}

  (:import java.io.IOException
           org.openmuc.jrxtx.SerialPortTimeoutException
           (org.openmuc.jmbus MBusSap
                              SecondaryAddress)))




;;;;;;;;;;


(defprotocol IWrapper

  "Extend a MBusSap object (meter-bus service access point)"

  (raw [this]

    "Get the raw MBusSap object")


  (select [this secondary-address]

    "Select a slave using its secondary address.

     The secondary address might be a hex string or SecondaryAddress object.

     This slave will then be accessible using primary address 0xfd.

     Returns ::success  if the operation is successful
             ::timeout  if the slave did not answer within the timeout
             ::closed   if the access point is closed

     Throws an IOException is something goes wrong.
    
     Cf. `structs/secondary-address`")


  (selected [this]

    "Get the selected secondary address if there is one.

     Cf. `select`")


  (deselect [this]

    "Deselect a slave using its secondary address.

     Returns ::success  if the operation is successful
             ::timeout  if the slave did not answer within the timeout
             ::closed   if the access point is closed
    
     Throws an IOException if something goes wrong.
    
     Cf. `select`")


  (req-ud2 [this]
           [this primary-address]

    "Request user data from the slave and wait for its answer.

     Default address is 0xfd, the placeholder for selected slaves.

     Returns a variable data structure  if the operation is successful
             ::timeout                  if the slave did not answer within the timeout
             ::closed                   if the access point is closed

     Throws an IOException if something goes wrong.

     Cf. `select`")

  
  (snd-nke [this]
           [this primary-address]

    "Send a SND_NKE message to reset the FCB (frame counter bit).

     Default address is 0xfd, the placeholder for selected slaves.

     Returns ::success  if the operation is successful
             ::timeout  if the slave did not answer with a 0xe5 message within
                        the timeout
             ::closed   if the access point is closed

     Throws an IOException if something goes wrong.
    
     Cf. `selected`")


  (snd-ud [this ba]
          [this primary-address ba]

    "Send user data to a slave.

     Default address is 0xfd, the placeholder for selected slaves.
    
     Returns ::success  if the write is successful
             ::fail     if the write failed
             ::timeout  if the slave did not answer within the timeout
             ::closed   if the access point is closed
    
     Throws an IOException if something goes wrong.
    
     Cf. `selected`")


  (reset-application [this]
                     [this primary-address]

    "Send an application reset to the requested slave.

     Default address is 0xfd, the placeholder for selected slaves.

     Returns ::success  if the operation is successful
             ::timeout  if the slave did not answer with a 0xe5 message within
                        the timeout
             ::closed   if the access point is closed

     Throws an IOException if something goes wrong.
    
     Cf. `selected`")


  (closed? [this]

    "Is this meter-bus access point closed ?")


  (close [this]

    "Close the access point"))




(defmacro ^:private -try

  "Helper for IWrapper fns"

  [& body]

  `(try
     ~@body
     (catch SerialPortTimeoutException _#
       ::timeout)
     (catch IOException e#
       (if (deref @~'closed?)
         ::closed
         (throw e#)))))




(deftype Wrapper [^MBusSap access-point
                  *closed?
                  *secondary-address]

  IWrapper


    (raw [_]
      access-point)


    (select [_ secondary-address]
      (-try
        (let [secondary-address' (if (string? secondary-address)
                                   (SecondaryAddress/getFromHexString secondary-address)
                                   secondary-address)]
        (.selectComponent access-point
                          secondary-address')
        (reset! *secondary-address
                secondary-address')
        ::success)))


    (selected [_]
      @*secondary-address)


    (deselect [_]
      (-try
        (.deselectComponent access-point)
        (reset! *secondary-address
                nil)
        ::success))


    (req-ud2 [_ primary-address]
      (-try
        (.read access-point
               primary-address)))


    (req-ud2 [this]
      (req-ud2 this
               0xfd))


    (snd-nke [_ primary-address]
      (-try
        (.linkReset access-point
                    primary-address)
        ::success))


    (snd-nke [this]
      (snd-nke this
               0xfd))


    (snd-ud [_ primary-address ba]
      (-try
        (if (.write access-point
                    primary-address
                    ba)
          ::success
          ::fail)))


    (snd-ud [this ba]
      (snd-ud this
              0xfd))


    (reset-application [_ primary-address]
      (-try
        (.resetReadout access-point
                       primary-address)
        ::success))


    (reset-application [this]
      (reset-application this
                         0xfd))


    (closed? [_]
      @*closed?)


    (close [_]
      (.close access-point)
      (reset! *closed?
              true)
      nil))




;;;;;;;;;;


(defn open

  "Open a meter-bus access point on the given path to the serial port.

   The configuration map may contains :

       :baud-rate
         A preferably standard baud rate

       :timeout
         How many milliseconds it must wait for a slave answer before giving up

   Throws an IOException if something goes wrong."

  ^MBusSap

  [path & [{:as   config
            :keys [baud-rate
                   timeout]
            :or   {baud-rate 2400
                   timeout   500}}]]

  (let [access-point ^MBusSap (MBusSap. path
                                        baud-rate)]
    (.setTimeout access-point
                 timeout)
    (.open access-point)
    (Wrapper. access-point
              (atom false)
              (atom nil))))
