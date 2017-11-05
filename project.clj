(defproject dvlopt/clombus
            "0.0.0-alpha1"

  :description       "Clojure library for wired and wireless meter-bus"
  :url               "https://github.com/dvlopt/clombus"
  :license           {:name "Mozilla Public License version 2.0"
                      :url  "https://www.mozilla.org/en-US/MPL/2.0/"}
  :repl-options      {:timeout 180000}
  :jvm-opts          ["-Djava.library.path=/usr/lib/jni"]
  :source-paths      ["src/clj"]
  :java-source-paths ["src/java"]
  :dependencies      [[dvlopt/clotty "0.0.0-alpha1"]]
  :profiles          {:dev     {:source-paths ["dev"]
                                :main         user
                                :dependencies [[org.clojure/clojure    "1.9.0-beta3"]
                                               [org.clojure/test.check "0.10.0-alpha2"]
                                               [criterium              "0.4.4"]]
                                :plugins      [[venantius/ultra "0.5.1"]
                                               [lein-codox      "0.10.3"]]
                                :codox        {:output-path  "doc/auto"
                                               :source-paths ["src/clj"]}
                                :global-vars  {*warn-on-reflection* true}}})
