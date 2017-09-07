(defproject dvlopt/clombus
            "0.0.0-alpha0"

  :repl-options      {:timeout 180000}
  :jvm-opts          ["-Djava.library.path=/usr/lib/jni"]
  :description       "Clojure library for wired and wireless meter-bus"
  :url               "https://github.com/dvlopt/clombus"
  :license           {:name "Mozilla Public License version 2.0"
                      :url  "https://www.mozilla.org/en-US/MPL/2.0/"}
  :plugins           [[lein-codox "0.10.3"]]
  :codox             {:output-path  "doc/auto"
                      :source-paths ["src/clj"]}
  :source-paths      ["src/clj"]
  :java-source-paths ["src/java"]
  :dependencies      [[dvlopt/clotty "0.0.0-alpha0"]]
  :profiles          {:dev     {:source-paths ["dev"]
                                :main         user
                                :plugins      [[venantius/ultra "0.5.1"]
                                               [lein-midje      "3.0.0"]]
                                :dependencies [[org.clojure/clojure    "1.9.0-alpha19"]
                                               [org.clojure/spec.alpha "0.1.123"]
                                               [org.clojure/test.check "0.9.0"]
                                               [criterium              "0.4.4"]]
                                :global-vars  {*warn-on-reflection* true}}})
