;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at https://mozilla.org/MPL/2.0/.


(ns helins.mbus

  "Specs and default values related to Meter-Bus."

  {:author "Adam Helinski"})


;;;;;;;;;; Default values


(def defaults

  "Defaults values and options used throughout this library."

  {::baud-rate       2400
   ::primary-address 0xfd
   ::timeout-ms      0})
