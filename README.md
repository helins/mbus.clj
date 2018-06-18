# dvlopt.mbus

[![Clojars
Project](https://img.shields.io/clojars/v/dvlopt/mbus.svg)](https://clojars.org/dvlopt/mbus)

Thin clojure wrapper around [JMbus](https://www.openmuc.org/m-bus/) for
communicating with [Meter-Bus](https://en.wikipedia.org/wiki/Meter-Bus) slave
devices.

Supports a (very) wide variety of platforms.

## Installation

This library needs [jRxTx](https://github.com/openmuc/jrxtx) for using serial
ports. For instructions, you can also refer to
[dvlopt/rxtx](https://github.com/dvlopt/rxtx), our own clojure wrapper.

## Usage

Read the [API](https://dvlopt.github.io/doc/clojure/dvlopt/mbus/index.html).

In short :

```clj
(require '[dvlopt.mbus       :as mbus]
         '[dvlopt.mbus.wired :as mbus.wired])


(with-open [^java.lang.AutoCloseable connection (mbus.wired/serial-connection "/dev/ttyUSB0"
                                                                              {::mbus/timeout   1000
                                                                               ::mbus/baud-rate 2400})]
  
  ;; Request user data (ie. a variable data structure) from slave 0

  (mbus.wired/req-ud2 connection
                      0))
```

## Status

For the time being, if you need wireless M-Bus, you need to interop with
[JMbus](https://www.openmuc.org/m-bus/).

## License

Copyright © 2018 Adam Helinski

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
