(defproject dvlopt/mbus
            "1.0.0-beta0"

  :description  "Meter-Bus clojure library"
  :url          "https://github.com/dvlopt/mbus"
  :license      {:name "Mozilla Public License version 2.0"
                 :url  "https://www.mozilla.org/en-US/MPL/2.0/"}
  :repl-options {:timeout 180000}
  :jvm-opts     ["-Djava.library.path=/usr/lib/jni"]
  :dependencies [[dvlopt/void       "0.0.0"]
                 [org.openmuc/jmbus "3.0.1"]]
  :profiles     {:dev {:source-paths ["dev"]
                       :main         user
                       :dependencies [[com.taoensso/nippy     "2.14.0"]
                                      [criterium              "0.4.4"]
                                      [org.clojure/clojure    "1.9.0"]
                                      [org.clojure/test.check "0.10.0-alpha2"]]
                       :plugins      [[lein-codox      "0.10.3"]
                                      [venantius/ultra "0.5.1"]]
                       :codox        {:output-path  "doc/auto"
                                      :source-paths ["src"]}
                       :global-vars  {*warn-on-reflection* true}}})
