# MBus

[![Clojars
Project](https://img.shields.io/clojars/v/io.helins/mbus.svg)](https://clojars.org/io.helins/mbus)

[![Cljdoc](https://cljdoc.org/badge/io.helins/mbus)](https://cljdoc.org/d/io.helins/mbus)

Thin clojure wrapper around [JMbus](https://www.openmuc.org/m-bus/) for
communicating with [Meter-Bus](https://en.wikipedia.org/wiki/Meter-Bus) slave
devices.

Supports a (very) wide variety of platforms.


## Installation

This library needs [jRxTx](https://github.com/openmuc/jrxtx) for using serial
ports. For instructions, you can also refer to
[helins/rxtx](https://github.com/helins/rxtx), our own clojure wrapper.

## Usage

This is just a very brief overview.

The [full API is available on Cljdoc](https://cljdoc.org/d/io.helins/mbus).

In short :

```clj
(require '[helins.mbus       :as mbus]
         '[helins.mbus.wired :as mbus.wired])


(with-open [connection (mbus.wired/serial-connection "/dev/ttyUSB0"
                                                     {:mbus/timeout   1000
                                                      :mbus/baud-rate 2400})]
  
  ;; Request user data (ie. a variable data structure) from slave 0

  (mbus.wired/req-ud2 connection
                      0))
```


## Status

For the time being, if you need wireless M-Bus, you need to interop with
[JMbus](https://www.openmuc.org/m-bus/).


## License

Copyright © 2018 Adam Helinski

Licensed under the term of the Mozilla Public License 2.0, see LICENSE.
