;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at https://mozilla.org/MPL/2.0/.


(ns helins.mbus

  "Specs and default values related to Meter-Bus."

  {:author "Adam Helinski"}

  (:require [clojure.spec.alpha :as s]))




;;;;;;;;;; Default values


(def defaults

  "Defaults values and options used throughout this library."

  {::baud-rate       2400
   ::primary-address 0xfd
   ::timeout-ms      0})




;;;;;;;;;; Specs - Misc


(s/def ::pos-int

  (s/and int?
         #(>= %
              0)))


(s/def ::string

  (s/and string?
         not-empty))




;;;;;;;;;; Specs - MBus


(s/def ::path

  ::string)


(s/def ::host

  ::string)


(s/def ::port

  (s/int-in 0
            65537))


(s/def ::baud-rate

  ::pos-int)


(s/def ::timeout-ms

  ::pos-int)


(s/def ::records

  (s/coll-of ::records))


(s/def ::record

  (s/merge ::dlms-unit
           (s/keys :req [::value
                         ::value-type
                         ::function-field
                         ::storage-number
                         ::description]
                   :opt [::exp10
                         ::unit
                         ::unit-string
                         ::ud-description])))


(s/def ::value

  ;; TODO
  any?)


(s/def ::exp10

  (s/and int?
         pos?))


(s/def ::value-type

  #{:bcd
    :date
    :double
    :long
    :none
    :string})


(s/def ::function-field

  #{:error
    :instant
    :max
    :min})


(s/def ::storage-number

  ::pos-int)


(s/def ::description

  #{:access-code-operator
    :access-code-system-devlopper
    :access-code-system-operator
    :access-code-user
    :actuality-duration
    :address
    :apparent-energy
    :averaging-duration
    :baudrate
    :control-signal
    :cumulation-counter
    :current
    :customer
    :customer-location
    :date
    :date-time
    :day-ok-week
    :digital-input
    :digital-output
    :duration-last-readout
    :energy
    :error-flags
    :error-mask
    :extended-identification
    :external-temperature
    :fabrication-number
    :firmware-version
    :first-storage-number-cyclic
    :flow-temperature
    :frequency
    :future-value
    :hardware-version
    :hca
    :last-cumulation-duration
    :last-storage-number-cyclic
    :manufacturer-specific
    :mass
    :mass-flow
    :max-power
    :model-version
    :not-supported
    :number-stops
    :on-time
    :operating-time
    :operating-time-battery
    :operator-specific-date
    :other-software-version
    :paramter-activation-state
    :parameter-set-id
    :password
    :phase
    :power
    :pressure
    :reactive-energy
    :reactive-power
    :relative-humidity
    :remaining-battery-life-time
    :remote-control
    :reserved
    :reset-counter
    :response-delay-time
    :retry
    :return-temperature
    :security-key
    :size-storage-block
    :special-supplier-information
    :storage-interval
    :tariff-duration
    :tariff-period
    :tariff-start
    :temperature-difference
    :temperature-limit
    :time-point
    :time-point-day-change
    :user-defined
    :voltage
    :volume
    :volume-flow
    :volume-flow-ext
    :week-number})


(s/def ::ud-description

  (s/nilable ::string))


(s/def ::dlms-unit

  (s/keys :req [::unit-string
                ::unit]))


(s/def ::unit-string

  ::string)


(s/def ::unit

  #{:active-energy-meter-constant-or-pulse-value
    :ampere
    :ampere-hour
    :ampere-per-meter
    :ampere-squared-hour-meter-constant-or-pulse-value
    :ampere-squared-hours
    :apparent-energy-meter-constant-or-pulse-value
    :bar
    :calorific-value
    :coulomb
    :count
    :cubic-feet
    :cubic-meter
    :cubic-meter-corrected
    :cubic-meter-per-day
    :cubic-meter-per-day-corrected
    :cubic-meter-per-hour
    :cubic-meter-per-hour-corrected
    :cubic-meter-per-minute
    :cubic-meter-per-second
    :currency
    :day
    :degree
    :degree-celsius
    :degree-fahrenheit
    :energy-per-volume
    :farad
    :henry
    :hertz
    :hour
    :joule
    :joule-per-hour
    :kelvin
    :kilogram
    :kilogram-per-hour
    :kilogram-per-second
    :litre
    :mass-density
    :meter-constant-or-pulse-value
    :meter
    :meter-per-second
    :min
    :mole-percent
    :month
    :newton
    :newton-meter
    :ohm
    :ohm-meter
    :other-unit
    :pascal
    :pascal-second
    :percentage
    :reactive-energy-meter-constant-or-pulse-value
    :reserved
    :second
    :signal-strength
    :specific-energy
    :tesla
    :us-gallon
    :us-gallon-per-hour
    :us-gallon-per-minute
    :var
    :var-hour
    :volt
    :volt-ampere
    :volt-ampere-hour
    :volt-per-meter
    :volt-squared-hour-meter-constant-or-pulse-value
    :volt-squared-hours
    :watt
    :watt-hour
    :weber
    :week
    :year})




;;;;;;;;;; Specs - Slave address


(s/def ::primary-address

  (s/int-in 0
            255))


(s/def ::secondary-address

  (s/keys :req [::device-id
                ::manufacturer-id
                ::version
                ::device-type]))


(s/def ::device-id

  number?)


(s/def ::device-type

  #{:other
    :oil-meter
    :electricity-meter
    :gas-meter
    :heat-meter
    :steam-meter
    :warm-water-meter
    :water-meter
    :heat-cost-allocator
    :compressed-air
    :cooling-meter-outlet
    :cooling-meter-inlet
    :heat-meter-inlet
    :heat-cooling-meter
    :bus-system-component
    :unknown
    :reserved-for-meter-16
    :reserved-for-meter-17
    :reserved-for-meter-18
    :reserved-for-meter-19
    :calorific-value
    :hot-water-meter
    :cold-water-meter
    :dual-register-water-meter
    :pressure-meter
    :ad-converter
    :smoke-detector
    :room-sensor-temp-hum
    :gas-detector
    :reserved-for-sensor-0x1d
    :reserved-for-sensor-0x1e
    :reserved-for-sensor-0x1f
    :breaker-elec
    :valve-gas-or-water
    :reserved-for-switching-device-0x22
    :reserved-for-switching-device-0x23
    :reserved-for-switching-device-0x24
    :customer-unit-display-device
    :reserved-for-customer-unit-0x26
    :reserved-for-customer-unit-0x27
    :waste-water-meter
    :garbage
    :reserved-for-co2
    :reserved-for-env-meter-0x2B
    :reserved-for-env-meter-0x2C
    :reserved-for-env-meter-0x2D
    :reserved-for-env-meter-0x2E
    :reserved-for-env-meter-0x2F
    :reserved-for-system-devices-0x30
    :com-controller
    :unidirection-repeater
    :bidirection-repeater
    :reserved-for-system-devices-0x34
    :reserved-for-system-devices-0x35
    :radio-converter-system-side
    :radio-converter-meter-side
    :reserved-for-system-devices-0x38
    :reserved-for-system-devices-0x39
    :reserved-for-system-devices-0x3A
    :reserved-for-system-devices-0x3B
    :reserved-for-system-devices-0x3C
    :reserved-for-system-devices-0x3D
    :reserved-for-system-devices-0x3E
    :reserved-for-system-devices-0x3F
    :reserved})


(s/def ::manufacturer-id

  string?)


(s/def ::version

  ::pos-int)




;;;;;;;;;; Specs - Variable data structure


(s/def ::variable-data-structure

  (s/keys :req [::records
                ::more-records?
                ::status
                ::access-number
                ::secondary-address]
          :opt [::manufacturer-data]))


(s/def ::more-records?

  boolean?)


(s/def ::access-number

  ::pos-int)


(s/def ::manufacturer-data

  bytes?)
