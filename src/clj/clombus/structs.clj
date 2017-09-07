(ns clombus.structs

  "Avoid brutal interop with jMbus objects.

   This namespace provides protocols for secondary addresses, data records and
   variable data structures."

  {:author "Adam Helinski"}

  (:import (org.openmuc.jmbus DeviceType
                              VariableDataStructure
                              DataRecord
                              DataRecord$DataValueType
                              DataRecord$FunctionField
                              DataRecord$Description
                              DlmsUnit
                              SecondaryAddress)))




;;;;;;;;;;


(defprotocol IMappify

  "Represent java objects in clojure data structures"


  (to-map [this]

    "Convert a java object into a nice clojure map"))




;;;;;;;;;;


(defprotocol IDataRecord

  "Extract information from a data record sent by a slave device via a variable data structure"


  (value [this]

    "Get the raw value")


  (exp10 [this]

    "The raw value might need to be scaled using scientific notation.

     This fn returns the base 10 exponent.")


  (scaled-value [this]

    "Get the scaled value.
    
     Cf. `value`
         `exp10`")


  (value-type [this]

    "Get the type of the raw value.

        :none
        :long
        :double
        :bcd
        :string
        :date")


  (function-field [this]

    "Get the function field, what is the function of this data.

       :instant  Instant value
       :max      Maximum value
       :min      Minimum value
       :error    Error   value")


  (storage-number [this]

    "Get the storage number.
    
     A meter can have several storages, usually to store short time-series. 

     Storage 0 signals the latest value.")


  (description [this]

    "Get the meaning of the data value such as :energy or :volume")


  (ud-description [this]

    "If the description is :user-defined, returns a string representing a user-defined
     description")


  (unit [this]

    "Get the unit of this value as defined in IEC 62056-6-2.

     A few non-standard units have been added for M-bus.

     Returns a map containing :unit      (eg. :cubic:meter)
                              :unit-str  (eg. \"m³\")"))




(extend-type DataRecord

  IDataRecord


    (value [this]
      (.getDataValue this))


    (exp10 [this]
      (.getMultiplierExponent this))


    (scaled-value [this]
      (.getScaledDataValue this))


    (value-type [this]
      (when-let [value-type ^DataRecord$DataValueType (.getDataValueType this)]
        (condp identical?
               value-type
          DataRecord$DataValueType/NONE   :none
          DataRecord$DataValueType/LONG   :long
          DataRecord$DataValueType/DOUBLE :double
          DataRecord$DataValueType/BCD    :bcd
          DataRecord$DataValueType/STRING :string
          DataRecord$DataValueType/DATE   :date
          value-type)))


    (function-field [this]
      (when-let [fn-field ^DataRecord$FunctionField (.getFunctionField this)]
        (condp identical?
               fn-field
          DataRecord$FunctionField/INST_VAL  :instant
          DataRecord$FunctionField/MAX_VAL   :max
          DataRecord$FunctionField/MIN_VAL   :min
          DataRecord$FunctionField/ERROR_VAL :error
          function-field)))


    (storage-number [this]
      (.getStorageNumber this))


    (description [this]
      (when-let [description ^DataRecord$Description (.getDescription this)]
        (condp identical?
               description
          DataRecord$Description/ENERGY                        :energy
          DataRecord$Description/VOLUME                        :volume
          DataRecord$Description/MASS                          :mass
          DataRecord$Description/ON_TIME                       :on-time
          DataRecord$Description/OPERATING_TIME                :operating-time
          DataRecord$Description/POWER                         :power
          DataRecord$Description/VOLUME_FLOW                   :volume-flow
          DataRecord$Description/VOLUME_FLOW_EXT               :volume-flow-ext
          DataRecord$Description/MASS_FLOW                     :mass-flow
          DataRecord$Description/FLOW_TEMPERATURE              :flow-temperature
          DataRecord$Description/RETURN_TEMPERATURE            :return-temperature
          DataRecord$Description/TEMPERATURE_DIFFERENCE        :temperature-difference
          DataRecord$Description/EXTERNAL_TEMPERATURE          :external-temperature
          DataRecord$Description/PRESSURE                      :pressure
          DataRecord$Description/DATE                          :date
          DataRecord$Description/DATE_TIME                     :date-time
          DataRecord$Description/VOLTAGE                       :voltage
          DataRecord$Description/CURRENT                       :current
          DataRecord$Description/AVERAGING_DURATION            :averaging-duration
          DataRecord$Description/ACTUALITY_DURATION            :actuality-duration
          DataRecord$Description/FABRICATION_NO                :fabrication-no
          DataRecord$Description/MODEL_VERSION                 :model-version
          DataRecord$Description/PARAMETER_SET_ID              :parameter-set-id
          DataRecord$Description/HARDWARE_VERSION              :hardware-version
          DataRecord$Description/FIRMWARE_VERSION              :firmware-version
          DataRecord$Description/ERROR_FLAGS                   :error-flags
          DataRecord$Description/CUSTOMER                      :customer
          DataRecord$Description/RESERVED                      :reserved
          DataRecord$Description/OPERATING_TIME_BATTERY        :operating-time-battery
          DataRecord$Description/HCA                           :hca
          DataRecord$Description/REACTIVE_ENERGY               :reactive-energy
          DataRecord$Description/TEMPERATURE_LIMIT             :temperature-limit
          DataRecord$Description/MAX_POWER                     :max-power
          DataRecord$Description/REACTIVE_POWER                :reactive-power
          DataRecord$Description/REL_HUMIDITY                  :relative-humidity
          DataRecord$Description/FREQUENCY                     :frequency
          DataRecord$Description/PHASE                         :phase
          DataRecord$Description/EXTENDED_IDENTIFICATION       :extended-identification
          DataRecord$Description/ADDRESS                       :address
          DataRecord$Description/NOT_SUPPORTED                 :not-supported
          DataRecord$Description/MANUFACTURER_SPECIFIC         :manufacturer-specific
          DataRecord$Description/FUTURE_VALUE                  :future-value
          DataRecord$Description/USER_DEFINED                  :user-defined
          DataRecord$Description/APPARENT_ENERGY               :apparent-energy
          DataRecord$Description/CUSTOMER_LOCATION             :customer-location
          DataRecord$Description/ACCSESS_CODE_OPERATOR         :access-code-operator
          DataRecord$Description/ACCSESS_CODE_USER             :access-code-user
          DataRecord$Description/PASSWORD                      :password
          DataRecord$Description/ACCSESS_CODE_SYSTEM_DEVELOPER :access-code-system-developer
          DataRecord$Description/OTHER_SOFTWARE_VERSION        :other-software-version
          DataRecord$Description/ACCSESS_CODE_SYSTEM_OPERATOR  :access-code-system-operator
          DataRecord$Description/ERROR_MASK                    :error-mask
          DataRecord$Description/SECURITY_KEY                  :security-key
          DataRecord$Description/DIGITAL_INPUT                 :digital-input
          DataRecord$Description/BAUDRATE                      :baud-rate
          DataRecord$Description/DIGITAL_OUTPUT                :digital-output
          DataRecord$Description/RESPONSE_DELAY_TIME           :response-delay-time
          DataRecord$Description/RETRY                         :retry
          DataRecord$Description/FIRST_STORAGE_NUMBER_CYCLIC   :first-storage-number-cyclic
          DataRecord$Description/REMOTE_CONTROL                :remote-control
          DataRecord$Description/LAST_STORAGE_NUMBER_CYCLIC    :last-storage-number-cyclic
          DataRecord$Description/SIZE_STORAGE_BLOCK            :size-storage-block
          DataRecord$Description/STORAGE_INTERVALL             :storage-interval
          DataRecord$Description/TARIF_START                   :tariff-start
          DataRecord$Description/DURATION_LAST_READOUT         :duration-last-readout
          DataRecord$Description/TIME_POINT                    :time-point
          DataRecord$Description/TARIF_DURATION                :tariff-duration
          DataRecord$Description/OPERATOR_SPECIFIC_DATA        :operator-specific-data
          DataRecord$Description/TARIF_PERIOD                  :tariff-period
          DataRecord$Description/NUMBER_STOPS                  :number-stops
          DataRecord$Description/LAST_CUMULATION_DURATION      :last-cumulation-duration
          DataRecord$Description/SPECIAL_SUPPLIER_INFORMATION  :special-supplier-information
          DataRecord$Description/PARAMETER_ACTIVATION_STATE    :parameter-activation-state
          DataRecord$Description/CONTROL_SIGNAL                :control-signal
          DataRecord$Description/WEEK_NUMBER                   :week-number
          DataRecord$Description/DAY_OF_WEEK                   :day-of-week
          DataRecord$Description/REMAINING_BATTERY_LIFE_TIME   :remaining-battery-life-time
          DataRecord$Description/TIME_POINT_DAY_CHANGE         :time-point-day-counter
          DataRecord$Description/CUMULATION_COUNTER            :cumulation-counter
          DataRecord$Description/RESET_COUNTER                 :reset-counter
          description)))


    (ud-description [this]
      (when (identical? (.getDescription this)
                        DataRecord$Description/USER_DEFINED)
        (.getUserDefinedDescription this)))




    (unit [this]
      (when-let [dlms-unit ^DlmsUnit (.getUnit this)]
        {:unit-str
         (.getUnit dlms-unit)

         :unit
         (condp identical?
                dlms-unit
           DlmsUnit/YEAR                                              :year
           DlmsUnit/MONTH                                             :month
           DlmsUnit/WEEK                                              :week
           DlmsUnit/DAY                                               :day
           DlmsUnit/HOUR                                              :hour
           DlmsUnit/MIN                                               :min
           DlmsUnit/SECOND                                            :second
           DlmsUnit/DEGREE                                            :degree
           DlmsUnit/DEGREE_CELSIUS                                    :degree-celsius
           DlmsUnit/CURRENCY                                          :currency
           DlmsUnit/METRE                                             :meter
           DlmsUnit/METRE_PER_SECOND                                  :meter-per-second
           DlmsUnit/CUBIC_METRE                                       :cubic-meter
           DlmsUnit/CUBIC_METRE_CORRECTED                             :cubic-meter-corrected
           DlmsUnit/CUBIC_METRE_PER_HOUR                              :cubic-meter-per-hour
           DlmsUnit/CUBIC_METRE_PER_HOUR_CORRECTED                    :cubic-meter-per-hour-corrected
           DlmsUnit/CUBIC_METRE_PER_DAY                               :cubic-meter-per-day
           DlmsUnit/CUBIC_METRE_PER_DAY_CORRECTED                     :cubic-meter-per-day-corrected
           DlmsUnit/LITRE                                             :litre
           DlmsUnit/KILOGRAM                                          :kilogram
           DlmsUnit/NEWTON                                            :newton
           DlmsUnit/NEWTONMETER                                       :newton-meter
           DlmsUnit/PASCAL                                            :pascal
           DlmsUnit/BAR                                               :bar
           DlmsUnit/JOULE                                             :joule
           DlmsUnit/JOULE_PER_HOUR                                    :joule-per-hour
           DlmsUnit/WATT                                              :watt
           DlmsUnit/VOLT_AMPERE                                       :volt-ampere
           DlmsUnit/VAR                                               :var
           DlmsUnit/WATT_HOUR                                         :watt-hour
           DlmsUnit/VOLT_AMPERE_HOUR                                  :volt-ampere-hour
           DlmsUnit/VAR_HOUR                                          :var-hour
           DlmsUnit/AMPERE                                            :ampere
           DlmsUnit/COULOMB                                           :coulomb
           DlmsUnit/VOLT                                              :volt
           DlmsUnit/VOLT_PER_METRE                                    :volt-per-meter
           DlmsUnit/FARAD                                             :farad
           DlmsUnit/OHM                                               :ohm
           DlmsUnit/OHM_METRE                                         :ohm-meter
           DlmsUnit/WEBER                                             :weber
           DlmsUnit/TESLA                                             :tesla
           DlmsUnit/AMPERE_PER_METRE                                  :ampere-per-meter
           DlmsUnit/HENRY                                             :henry
           DlmsUnit/HERTZ                                             :hertz
           DlmsUnit/ACTIVE_ENERGY_METER_CONSTANT_OR_PULSE_VALUE       :active-energy-meter-constant-or-pulse-value
           DlmsUnit/REACTIVE_ENERGY_METER_CONSTANT_OR_PULSE_VALUE     :reactive-energy-meter-constant-or-puslse-value
           DlmsUnit/APPARENT_ENERGY_METER_CONSTANT_OR_PULSE_VALUE     :apparent-energy-meter-constant-or-pulse-value
           DlmsUnit/VOLT_SQUARED_HOURS                                :volt-squared-hours
           DlmsUnit/AMPERE_SQUARED_HOURS                              :ampere-squared-hours
           DlmsUnit/KILOGRAM_PER_SECOND                               :kilogram-per-second
           DlmsUnit/KELVIN                                            :kelvin
           DlmsUnit/VOLT_SQUARED_HOUR_METER_CONSTANT_OR_PULSE_VALUE   :volt-squared-hour-meter-constant-or-pulse-value
           DlmsUnit/AMPERE_SQUARED_HOUR_METER_CONSTANT_OR_PULSE_VALUE :ampere-squared-hour-meter-constant-or-pulse-value
           DlmsUnit/METER_CONSTANT_OR_PULSE_VALUE                     :meter-constant-or-pulse-value
           DlmsUnit/PERCENTAGE                                        :percentage
           DlmsUnit/AMPERE_HOUR                                       :ampere-hour
           DlmsUnit/ENERGY_PER_VOLUME                                 :energy-per-volume
           DlmsUnit/CALORIFIC_VALUE                                   :calorific-value
           DlmsUnit/MOLE_PERCENT                                      :mole-percent
           DlmsUnit/MASS_DENSITY                                      :mass-density
           DlmsUnit/PASCAL_SECOND                                     :pascal-second
           DlmsUnit/SPECIFIC_ENERGY                                   :specific-energy
           DlmsUnit/SIGNAL_STRENGTH                                   :signal-strength
           DlmsUnit/RESERVED                                          :reserved
           DlmsUnit/OTHER_UNIT                                        :other-unit
           DlmsUnit/COUNT                                             :count
           ;; not mentioned in IEC 62056, added for m-bus
           DlmsUnit/CUBIC_METRE_PER_SECOND                            :cubic-meter-per-second
           DlmsUnit/CUBIC_METRE_PER_MINUTE                            :cubic-meter-per-minute
           DlmsUnit/KILOGRAM_PER_HOUR                                 :kilogram-per-hour
           DlmsUnit/CUBIC_FEET                                        :cubic-feet
           DlmsUnit/US_GALLON                                         :us-gallon
           DlmsUnit/US_GALLON_PER_MINUTE                              :us-gallon-per-minute
           DlmsUnit/US_GALLON_PER_HOUR                                :us-gallon-per-hour
           DlmsUnit/DEGREE_FAHRENHEIT                                 :degree-fahrenheit
           dlms-unit)}))




  IMappify


    (to-map [this]
      (merge {:value          (value          this)
              :exp10          (exp10          this)
              :value-type     (value-type     this)
              :function-field (function-field this)
              :storage-number (storage-number this)
              :description    (description    this)
              :ud-description (ud-description this)}
             (unit this))))




;;;;;;;;;;


(defn secondary-address

  "Make a secondary address from a hex string"

  ^SecondaryAddress

  [string]

  (SecondaryAddress/getFromHexString string))





(defprotocol ISecondaryAddress

  "Extract information from a secondary address"


  (to-ba [this]

    "Get this secondary address represented as a byte array")


  (device-id [this]

    "Get the device id")


  (device-type [this]

    "Get the type of device such as :water-meter or :gas-meter")


  (manufacturer-id [this]

    "Get the manufacturer id")


  (version [this]

    "Get the version"))




(extend-type SecondaryAddress

  ISecondaryAddress


    (to-ba [this]
      (.asByteArray this))


    (device-id [this]
      (.getDeviceId this))


    (device-type [this]
      (when-let [device-type ^DeviceType (.getDeviceType this)]
        (condp identical?
               device-type
          DeviceType/OTHER                              :other
          DeviceType/OIL_METER                          :oil-meter
          DeviceType/ELECTRICITY_METER                  :electricity-meter
          DeviceType/GAS_METER                          :gas-meter
          DeviceType/HEAT_METER                         :heat-meter
          DeviceType/STEAM_METER                        :steam-meter
          DeviceType/WARM_WATER_METER                   :warm-water-meter
          DeviceType/WATER_METER                        :water-meter
          DeviceType/HEAT_COST_ALLOCATOR                :heat-cost-allocator
          DeviceType/COMPRESSED_AIR                     :compressed-air
          DeviceType/COOLING_METER_OUTLET               :cooling-meter-outlet
          DeviceType/COOLING_METER_INLET                :cooling-meter-inlet
          DeviceType/HEAT_METER_INLET                   :heat-meter-inlet
          DeviceType/HEAT_COOLING_METER                 :heat-cooling-meter
          DeviceType/BUS_SYSTEM_COMPONENT               :bus-system-component
          DeviceType/UNKNOWN                            :unknown
          DeviceType/RESERVED_FOR_METER_16              :reserved-for-meter-16
          DeviceType/RESERVED_FOR_METER_17              :reserved-for-meter-17
          DeviceType/RESERVED_FOR_METER_18              :reserved-for-meter-18
          DeviceType/RESERVED_FOR_METER_19              :reserved-for-meter-19
          DeviceType/CALORIFIC_VALUE                    :calorific-value
          DeviceType/HOT_WATER_METER                    :hot-water-meter
          DeviceType/COLD_WATER_METER                   :cold-water-meter
          DeviceType/DUAL_REGISTER_WATER_METER          :dual-register-water-meter
          DeviceType/PRESSURE_METER                     :pressure-meter
          DeviceType/AD_CONVERTER                       :ad-converter
          DeviceType/SMOKE_DETECTOR                     :smoke-detector
          DeviceType/ROOM_SENSOR_TEMP_HUM               :room-sensor-temp-hum
          DeviceType/GAS_DETECTOR                       :gas-detector
          DeviceType/RESERVED_FOR_SENSOR_0X1D           :reserved-for-sensor-0x1d
          DeviceType/RESERVED_FOR_SENSOR_0X1E           :reserved-for-sensor-0x1e
          DeviceType/RESERVED_FOR_SENSOR_0X1F           :reserved-for-sensor-0x1f
          DeviceType/BREAKER_ELEC                       :breaker-elec
          DeviceType/VALVE_GAS_OR_WATER                 :valve-gas-or-water
          DeviceType/RESERVED_FOR_SWITCHING_DEVICE_0X22 :reserved-for-switching-device-0x22
          DeviceType/RESERVED_FOR_SWITCHING_DEVICE_0X23 :reserved-for-switching-device-0x23
          DeviceType/RESERVED_FOR_SWITCHING_DEVICE_0X24 :reserved-for-switching-device-0x24
          DeviceType/CUSTOMER_UNIT_DISPLAY_DEVICE       :customer-unit-display-device
          DeviceType/RESERVED_FOR_CUSTOMER_UNIT_0X26    :reserved-for-customer-unit-0x26
          DeviceType/RESERVED_FOR_CUSTOMER_UNIT_0X27    :reserved-for-customer-unit-0x26
          DeviceType/WASTE_WATER_METER                  :waste-water-meter
          DeviceType/GARBAGE                            :garbage
          DeviceType/RESERVED_FOR_CO2                   :reserved-for-co2
          DeviceType/RESERVED_FOR_ENV_METER_0X2B        :reserved-for-env-meter-0x2B
          DeviceType/RESERVED_FOR_ENV_METER_0X2C        :reserved-for-env-meter-0x2C
          DeviceType/RESERVED_FOR_ENV_METER_0X2D        :reserved-for-env-meter-0x2D
          DeviceType/RESERVED_FOR_ENV_METER_0X2E        :reserved-for-env-meter-0x2E
          DeviceType/RESERVED_FOR_ENV_METER_0X2F        :reserved-for-env-meter-0x2F
          DeviceType/RESERVED_FOR_SYSTEM_DEVICES_0X30   :reserved-for-system-devices-0x30
          DeviceType/COM_CONTROLLER                     :com-controller
          DeviceType/UNIDIRECTION_REPEATER              :unidirection-repeater
          DeviceType/BIDIRECTION_REPEATER               :bidirection-repeater
          DeviceType/RESERVED_FOR_SYSTEM_DEVICES_0X34   :reserved-for-system-devices-0x34
          DeviceType/RESERVED_FOR_SYSTEM_DEVICES_0X35   :reserved-for-system-devices-0x35
          DeviceType/RADIO_CONVERTER_SYSTEM_SIDE        :radio-converter-system-side
          DeviceType/RADIO_CONVERTER_METER_SIDE         :radio-converter-meter-side
          DeviceType/RESERVED_FOR_SYSTEM_DEVICES_0X38   :reserved-for-system-devices-0x38
          DeviceType/RESERVED_FOR_SYSTEM_DEVICES_0X39   :reserved-for-system-devices-0x39
          DeviceType/RESERVED_FOR_SYSTEM_DEVICES_0X3A   :reserved-for-system-devices-0x3A
          DeviceType/RESERVED_FOR_SYSTEM_DEVICES_0X3B   :reserved-for-system-devices-0x3B
          DeviceType/RESERVED_FOR_SYSTEM_DEVICES_0X3C   :reserved-for-system-devices-0x3C
          DeviceType/RESERVED_FOR_SYSTEM_DEVICES_0X3D   :reserved-for-system-devices-0x3D
          DeviceType/RESERVED_FOR_SYSTEM_DEVICES_0X3E   :reserved-for-system-devices-0x3E
          DeviceType/RESERVED_FOR_SYSTEM_DEVICES_0X3F   :reserved-for-system-devices-0x3F
          DeviceType/RESERVED                           :reserved
          device-type)))


    (manufacturer-id [this]
      (.getManufacturerId this))


    (version [this]
      (.getVersion this))




  IMappify


    (to-map [this]
      {:to-ba           (to-ba           this)
       :device-id       (device-id       this)
       :device-type     (device-type     this)
       :manufacturer-id (manufacturer-id this)
       :version         (version         this)}))




;;;;;;;;;;


(defprotocol IVariableDataStructure

  "Extract information from a variable data structure sent by a slave device following a req_ud2"


  (records [this]

    "Get data records")


  (more-records? [this]

    "Is there more records ?")


  (status [this]

    "Get the status")


  (access-number [this]

    "Get the access number")


  (manufacturer-data [this]

    "Get a byte array representing manufacturer data")


  (slave-address [this]

    "Get the secondary address of the slave device who sent the structure"))




(extend-type VariableDataStructure

  IVariableDataStructure


    (records [this]
      (.getDataRecords this))


    (more-records? [this]
      (.moreRecordsFollow this))


    (status [this]
      (.getStatus this))


    (access-number [this]
      (.getAccessNumber this))

    
    (manufacturer-data [this]
      (.getManufacturerData this))


    (slave-address [this]
      (.getSecondaryAddress this))




  IMappify


    (to-map [this]
      {:records           (map to-map
                               (records this))
       :more-records?     (more-records?     this)
       :status            (status            this)
       :access-number     (access-number     this)
       :manufacturer-data (manufacturer-data this)
       :slave-address     (to-map (slave-address this))}))
