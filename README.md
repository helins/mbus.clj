# Clombus

Thin clojure wrapper around [JMbus](https://www.openmuc.org/m-bus/) for
communicating with [Meter-Bus](https://en.wikipedia.org/wiki/Meter-Bus) slave
devices.

Supports a (very) wide variety of platforms.

## Installation

This library needs [jRxTx](https://github.com/openmuc/jrxtx) for using serial
ports. For instructions, refer to [Clotty](https://github.com/dvlopt/clotty), our
own clojure wrapper.

Then, simply add the following dependency to your project :

[![Clojars Project](https://img.shields.io/clojars/v/dvlopt/clombus.svg)](https://clojars.org/dvlopt/clombus)

## Usage

Read the full [API](https://dvlopt.github.io/doc/clombus/index.html).

```clj
(require '[clombus.wired :as mbus.wired])


;; create an access point
(def access-point
     (mbus.wired/open "/dev/ttyUSB0"
                      {:baud-rate 2400
                       :timeout   1000}))


;; request user data from slave 0
(mbus.wired/req-ud2 access-point
                    0)
```

## Status

If you need wireless M-Bus, you need to interop with
[JMbus](https://www.openmuc.org/m-bus/). We will write a wrapper as soon as we
have the relevant hardware to test it.

## License

Copyright © 2017 Adam Helinski

Distributed under the Mozilla Public License version 2.0.
