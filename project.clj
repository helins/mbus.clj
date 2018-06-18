(defproject dvlopt/mbus
            "1.0.0-beta1"

  :description  "Meter-Bus clojure library"
  :url          "https://github.com/dvlopt/mbus"
  :license      {:name "Eclipse Public License"
                 :url  "http://www.eclipse.org/legal/epl-v10.html"}
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
                                      :source-paths ["src"]}}})
