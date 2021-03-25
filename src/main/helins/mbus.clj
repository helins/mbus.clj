;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at https://mozilla.org/MPL/2.0/.


(ns helins.mbus

  "Specs and default values related to Meter-Bus."

  {:author "Adam Helinski"})


;;;;;;;;;; Default values


(def default+

  "Defaults values and options used throughout this library."

  {:mbus/baud-rate       2400
   :mbus/primary-address 0xfd
   :mbus/timeout-ms      0})
