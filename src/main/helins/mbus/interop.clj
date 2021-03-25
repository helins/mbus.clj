;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at https://mozilla.org/MPL/2.0/.


(ns helins.mbus.interop

  "Utilities and specs for java objects.
  
   Should not be used directly by the user unless needed for some weird reasons."

  {:author "Adam Helinski"}

  (:require [clojure.spec.alpha     :as s]
            [clojure.spec.gen.alpha :as gen]
            [helins.mbus            :as mbus]
            [helins.void            :as void])
  (:import java.util.Date
           (org.openmuc.jmbus DataRecord
                              DataRecord$DataValueType
                              DataRecord$FunctionField
                              DataRecord$Description
                              DeviceType
                              DlmsUnit
                              MBusConnection
                              SecondaryAddress
                              VariableDataStructure)))




;;;;;;;;;; Declarations


(declare data-record$data-value-type->clj
         data-record$description->clj
         data-record$function-field->clj
         dlms-unit->clj)




;;;;;;;;;; Specs - Helper functions


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




;;;;;;;;;; Conversion - Java classes to clojure data structures


(s/fdef data-record->clj

  :args (s/cat :dr ::DataRecord)
  :ret ::mbus/record)


(defn data-record->clj

  [^DataRecord dr]

  (void/assoc (let [value-type (data-record$data-value-type->clj (.getDataValueType dr))]
                (assoc (merge {::mbus/value-type     value-type
                               ::mbus/function-field (data-record$function-field->clj (.getFunctionField dr))
                               ::mbus/storage-number (.getStorageNumber dr)
                               ::mbus/description    (data-record$description->clj (.getDescription dr))}
                              (some-> (.getUnit dr)
                                      dlms-unit->clj))
                       ::mbus/value
                       (let [value (.getDataValue dr)]
                         (if (identical? value-type
                                         :date)
                           (.getTime ^Date value)
                           value))))
              ::mbus/exp10          (let [exp10 (.getMultiplierExponent dr)]
                                      (when (pos? exp10)
                                        exp10))
              ::mbus/ud-description (when (identical? (.getDescription dr)
                                                      DataRecord$Description/USER_DEFINED)
                                      (.getUserDefinedDescription dr))))


(s/fdef data-record$data-value-type->clj

  :args (s/cat :dr$dvt ::DataRecord$DataValueType)
  :ret ::mbus/value-type)


(defn data-record$data-value-type->clj

  [^DataRecord$DataValueType dr$dvt]

  (condp identical?
         dr$dvt
    DataRecord$DataValueType/BCD    :bcd
    DataRecord$DataValueType/DATE   :date
    DataRecord$DataValueType/DOUBLE :double
    DataRecord$DataValueType/LONG   :long
    DataRecord$DataValueType/NONE   :none
    DataRecord$DataValueType/STRING :string))




(s/fdef data-record$description->clj

  :args (s/cat :dr$d ::DataRecord$Description)
  :ret  ::mbus/description)


(defn data-record$description->clj

  [^DataRecord$Description dr$d]
  
  (condp identical?
         dr$d
    DataRecord$Description/ACCSESS_CODE_OPERATOR         :access-code-operator
    DataRecord$Description/ACCSESS_CODE_SYSTEM_DEVELOPER :access-code-system-devlopper
    DataRecord$Description/ACCSESS_CODE_SYSTEM_OPERATOR  :access-code-system-operator
    DataRecord$Description/ACCSESS_CODE_USER             :access-code-user
    DataRecord$Description/ACTUALITY_DURATION            :actuality-duration
    DataRecord$Description/ADDRESS                       :address
    DataRecord$Description/APPARENT_ENERGY               :apparent-energy
    DataRecord$Description/AVERAGING_DURATION            :averaging-duration
    DataRecord$Description/BAUDRATE                      :baudrate
    DataRecord$Description/CONTROL_SIGNAL                :control-signal
    DataRecord$Description/CUMULATION_COUNTER            :cumulation-counter
    DataRecord$Description/CURRENT                       :current
    DataRecord$Description/CUSTOMER                      :customer
    DataRecord$Description/CUSTOMER_LOCATION             :customer-location
    DataRecord$Description/DATE                          :date
    DataRecord$Description/DATE_TIME                     :date-time
    DataRecord$Description/DAY_OF_WEEK                   :day-ok-week
    DataRecord$Description/DIGITAL_INPUT                 :digital-input
    DataRecord$Description/DIGITAL_OUTPUT                :digital-output
    DataRecord$Description/DURATION_LAST_READOUT         :duration-last-readout
    DataRecord$Description/ENERGY                        :energy
    DataRecord$Description/ERROR_FLAGS                   :error-flags
    DataRecord$Description/ERROR_MASK                    :error-mask
    DataRecord$Description/EXTENDED_IDENTIFICATION       :extended-identification
    DataRecord$Description/EXTERNAL_TEMPERATURE          :external-temperature
    DataRecord$Description/FABRICATION_NO                :fabrication-number
    DataRecord$Description/FIRMWARE_VERSION              :firmware-version
    DataRecord$Description/FIRST_STORAGE_NUMBER_CYCLIC   :first-storage-number-cyclic
    DataRecord$Description/FLOW_TEMPERATURE              :flow-temperature
    DataRecord$Description/FREQUENCY                     :frequency
    DataRecord$Description/FUTURE_VALUE                  :future-value
    DataRecord$Description/HARDWARE_VERSION              :hardware-version
    DataRecord$Description/HCA                           :hca
    DataRecord$Description/LAST_CUMULATION_DURATION      :last-cumulation-duration
    DataRecord$Description/LAST_STORAGE_NUMBER_CYCLIC    :last-storage-number-cyclic
    DataRecord$Description/MANUFACTURER_SPECIFIC         :manufacturer-specific
    DataRecord$Description/MASS                          :mass
    DataRecord$Description/MASS_FLOW                     :mass-flow
    DataRecord$Description/MAX_POWER                     :max-power
    DataRecord$Description/MODEL_VERSION                 :model-version
    DataRecord$Description/NOT_SUPPORTED                 :not-supported
    DataRecord$Description/NUMBER_STOPS                  :number-stops
    DataRecord$Description/ON_TIME                       :on-time
    DataRecord$Description/OPERATING_TIME                :operating-time
    DataRecord$Description/OPERATING_TIME_BATTERY        :operating-time-battery
    DataRecord$Description/OPERATOR_SPECIFIC_DATA        :operator-specific-date
    DataRecord$Description/OTHER_SOFTWARE_VERSION        :other-software-version
    DataRecord$Description/PARAMETER_ACTIVATION_STATE    :paramter-activation-state
    DataRecord$Description/PARAMETER_SET_ID              :parameter-set-id
    DataRecord$Description/PASSWORD                      :password
    DataRecord$Description/PHASE                         :phase
    DataRecord$Description/POWER                         :power
    DataRecord$Description/PRESSURE                      :pressure
    DataRecord$Description/REACTIVE_ENERGY               :reactive-energy
    DataRecord$Description/REACTIVE_POWER                :reactive-power
    DataRecord$Description/REL_HUMIDITY                  :relative-humidity
    DataRecord$Description/REMAINING_BATTERY_LIFE_TIME   :remaining-battery-life-time
    DataRecord$Description/REMOTE_CONTROL                :remote-control
    DataRecord$Description/RESERVED                      :reserved
    DataRecord$Description/RESET_COUNTER                 :reset-counter
    DataRecord$Description/RESPONSE_DELAY_TIME           :response-delay-time
    DataRecord$Description/RETRY                         :retry
    DataRecord$Description/RETURN_TEMPERATURE            :return-temperature
    DataRecord$Description/SECURITY_KEY                  :security-key
    DataRecord$Description/SIZE_STORAGE_BLOCK            :size-storage-block
    DataRecord$Description/SPECIAL_SUPPLIER_INFORMATION  :special-supplier-information
    DataRecord$Description/STORAGE_INTERVALL             :storage-interval
    DataRecord$Description/TARIF_DURATION                :tariff-duration
    DataRecord$Description/TARIF_PERIOD                  :tariff-period
    DataRecord$Description/TARIF_START                   :tariff-start
    DataRecord$Description/TEMPERATURE_DIFFERENCE        :temperature-difference
    DataRecord$Description/TEMPERATURE_LIMIT             :temperature-limit
    DataRecord$Description/TIME_POINT                    :time-point
    DataRecord$Description/TIME_POINT_DAY_CHANGE         :time-point-day-change
    DataRecord$Description/USER_DEFINED                  :user-defined
    DataRecord$Description/VOLTAGE                       :voltage
    DataRecord$Description/VOLUME                        :volume
    DataRecord$Description/VOLUME_FLOW                   :volume-flow
    DataRecord$Description/VOLUME_FLOW_EXT               :volume-flow-ext
    DataRecord$Description/WEEK_NUMBER                   :week-number))




(s/fdef data-record$function-field->clj

  :args (s/cat :dr$ff ::DataRecord$FunctionField)
  :ret  ::mbus/function-field)


(defn data-record$function-field->clj

  [^DataRecord$FunctionField dr$ff]

  (condp identical?
         dr$ff
    DataRecord$FunctionField/INST_VAL  :instant
    DataRecord$FunctionField/ERROR_VAL :error
    DataRecord$FunctionField/MAX_VAL   :max
    DataRecord$FunctionField/MIN_VAL   :min))




(s/fdef device-type->clj

  :args (s/cat :dt ::DeviceType)
  :ret  ::mbus/device-type)


(defn device-type->clj

  ;; TODO put in alphabetical order

  [^DeviceType dt]

  (condp identical?
         dt
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
    DeviceType/RESERVED_FOR_CUSTOMER_UNIT_0X27    :reserved-for-customer-unit-0x27
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
    DeviceType/RESERVED                           :reserved))



(s/fdef dlms-unit->clj

  :args (s/cat :du ::DlmsUnit)
  :ret  ::mbus/dlms-unit)


(defn dlms-unit->clj

  [^DlmsUnit du]

  {::mbus/unit-string
   (.getUnit du)

   ::mbus/unit
   (condp identical?
          du
     DlmsUnit/ACTIVE_ENERGY_METER_CONSTANT_OR_PULSE_VALUE       :active-energy-meter-constant-or-pulse-value
     DlmsUnit/AMPERE                                            :ampere
     DlmsUnit/AMPERE_HOUR                                       :ampere-hour
     DlmsUnit/AMPERE_PER_METRE                                  :ampere-per-meter
     DlmsUnit/AMPERE_SQUARED_HOUR_METER_CONSTANT_OR_PULSE_VALUE :ampere-squared-hour-meter-constant-or-pulse-value
     DlmsUnit/AMPERE_SQUARED_HOURS                              :ampere-squared-hours
     DlmsUnit/APPARENT_ENERGY_METER_CONSTANT_OR_PULSE_VALUE     :apparent-energy-meter-constant-or-pulse-value
     DlmsUnit/BAR                                               :bar
     DlmsUnit/CALORIFIC_VALUE                                   :calorific-value
     DlmsUnit/COULOMB                                           :coulomb
     DlmsUnit/COUNT                                             :count
     DlmsUnit/CUBIC_FEET                                        :cubic-feet
     DlmsUnit/CUBIC_METRE                                       :cubic-meter
     DlmsUnit/CUBIC_METRE_CORRECTED                             :cubic-meter-corrected
     DlmsUnit/CUBIC_METRE_PER_DAY                               :cubic-meter-per-day
     DlmsUnit/CUBIC_METRE_PER_DAY_CORRECTED                     :cubic-meter-per-day-corrected
     DlmsUnit/CUBIC_METRE_PER_HOUR                              :cubic-meter-per-hour
     DlmsUnit/CUBIC_METRE_PER_HOUR_CORRECTED                    :cubic-meter-per-hour-corrected
     DlmsUnit/CUBIC_METRE_PER_MINUTE                            :cubic-meter-per-minute
     DlmsUnit/CUBIC_METRE_PER_SECOND                            :cubic-meter-per-second
     DlmsUnit/CURRENCY                                          :currency
     DlmsUnit/DAY                                               :day
     DlmsUnit/DEGREE                                            :degree
     DlmsUnit/DEGREE_CELSIUS                                    :degree-celsius
     DlmsUnit/DEGREE_FAHRENHEIT                                 :degree-fahrenheit
     DlmsUnit/ENERGY_PER_VOLUME                                 :energy-per-volume
     DlmsUnit/FARAD                                             :farad
     DlmsUnit/HENRY                                             :henry
     DlmsUnit/HERTZ                                             :hertz
     DlmsUnit/HOUR                                              :hour
     DlmsUnit/JOULE                                             :joule
     DlmsUnit/JOULE_PER_HOUR                                    :joule-per-hour
     DlmsUnit/KELVIN                                            :kelvin
     DlmsUnit/KILOGRAM                                          :kilogram
     DlmsUnit/KILOGRAM_PER_HOUR                                 :kilogram-per-hour
     DlmsUnit/KILOGRAM_PER_SECOND                               :kilogram-per-second
     DlmsUnit/LITRE                                             :litre
     DlmsUnit/MASS_DENSITY                                      :mass-density
     DlmsUnit/METER_CONSTANT_OR_PULSE_VALUE                     :meter-constant-or-pulse-value
     DlmsUnit/METRE                                             :meter
     DlmsUnit/METRE_PER_SECOND                                  :meter-per-second
     DlmsUnit/MIN                                               :min
     DlmsUnit/MOLE_PERCENT                                      :mole-percent
     DlmsUnit/MONTH                                             :month
     DlmsUnit/NEWTON                                            :newton
     DlmsUnit/NEWTONMETER                                       :newton-meter
     DlmsUnit/OHM                                               :ohm
     DlmsUnit/OHM_METRE                                         :ohm-meter
     DlmsUnit/OTHER_UNIT                                        :other-unit
     DlmsUnit/PASCAL                                            :pascal
     DlmsUnit/PASCAL_SECOND                                     :pascal-second
     DlmsUnit/PERCENTAGE                                        :percentage
     DlmsUnit/REACTIVE_ENERGY_METER_CONSTANT_OR_PULSE_VALUE     :reactive-energy-meter-constant-or-pulse-value
     DlmsUnit/RESERVED                                          :reserved
     DlmsUnit/SECOND                                            :second
     DlmsUnit/SIGNAL_STRENGTH                                   :signal-strength
     DlmsUnit/SPECIFIC_ENERGY                                   :specific-energy
     DlmsUnit/TESLA                                             :tesla
     DlmsUnit/US_GALLON                                         :us-gallon
     DlmsUnit/US_GALLON_PER_HOUR                                :us-gallon-per-hour
     DlmsUnit/US_GALLON_PER_MINUTE                              :us-gallon-per-minute
     DlmsUnit/VAR                                               :var
     DlmsUnit/VAR_HOUR                                          :var-hour
     DlmsUnit/VOLT                                              :volt
     DlmsUnit/VOLT_AMPERE                                       :volt-ampere
     DlmsUnit/VOLT_AMPERE_HOUR                                  :volt-ampere-hour
     DlmsUnit/VOLT_PER_METRE                                    :volt-per-meter
     DlmsUnit/VOLT_SQUARED_HOUR_METER_CONSTANT_OR_PULSE_VALUE   :volt-squared-hour-meter-constant-or-pulse-value
     DlmsUnit/VOLT_SQUARED_HOURS                                :volt-squared-hours
     DlmsUnit/WATT                                              :watt
     DlmsUnit/WATT_HOUR                                         :watt-hour
     DlmsUnit/WEBER                                             :weber
     DlmsUnit/WEEK                                              :week
     DlmsUnit/YEAR                                              :year)})




(s/fdef secondary-address->clj

  :args (s/cat :sa ::SecondaryAddress)
  :ret  ::mbus/secondary-address)


(defn secondary-address->clj

  [^SecondaryAddress sa]

  {::mbus/device-id       (.getDeviceId       sa)
   ::mbus/manufacturer-id (.getManufacturerId sa)
   ::mbus/version         (.getVersion        sa)
   ::mbus/device-type     (device-type->clj (.getDeviceType sa))})




(s/fdef variable-data-structure->clj

  :args (s/cat :vds ::VariableDataStructure)
  :ret  ::mbus/variable-data-structure)


(defn variable-data-structure->clj

  [^VariableDataStructure vds]

  (void/assoc {::mbus/records           (map data-record->clj
                                             (.getDataRecords vds))
               ::mbus/more-records?     (.moreRecordsFollow vds)
               ::mbus/status            (.getStatus           vds)
               ::mbus/access-number     (.getAccessNumber     vds)
               ::mbus/secondary-address (secondary-address->clj (.getSecondaryAddress vds))}
              ::mbus/manufacturer-data (not-empty (.getManufacturerData vds))))
