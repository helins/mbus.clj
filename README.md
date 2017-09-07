# Clombus

Thin clojure wrapper around [JMbus](https://www.openmuc.org/m-bus/) for
communicating with [Meter-Bus](https://en.wikipedia.org/wiki/Meter-Bus) slave
devices.

Supports a (very) wide variety of platforms.


## Usage

This library needs [jRxTx](https://github.com/openmuc/jrxtx) for using serial
ports. For instructins, refer to [Clotty](https://github.com/dvlopt/clotty), our
own clojure wrapper.

Then :

```clj
(require '[clombus.wired   :as mbus.wired]
         '[clombus.structs :as mbus.structs])


;; create an access point
(def ap
     (mbus.wired/open "/dev/ttyUSB0"
                      {:baud-rate 2400
                       :timeout   1000}))


;; request user data from slave 0
(def variable-data-structure
     (mbus.wired/req-ud2 ap
                         0))


;; get data records from the variable data structure
;; and convert them to nice clojure maps
(map mbus.structs/to-map
     (mbus.structs/records variable-data-structures))
```

Read the full [API](https://dvlopt.github.io/doc/clombus/index.html).

## Status

If you need wireless M-Bus, you need to interop with
[JMbus](https://www.openmuc.org/m-bus/). We will a wrapper as soon as we have
the relevant hardware to test it.

## License

Copyright © 2017 Adam Helinski

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
