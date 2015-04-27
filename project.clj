(def tk-version "1.1.1")
(def ks-version "1.1.0")

(defproject puppetlabs/cthun "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  ;; Abort when version ranges or version conflicts are detected in
  ;; dependencies. Also supports :warn to simply emit warnings.
  ;; requires lein 2.2.0+.
  :pedantic? :abort

  :dependencies [[org.clojure/clojure "1.6.0"]

                 [org.clojure/tools.logging "0.3.1"]
                 [org.clojure/core.async "0.1.338.0-5c5012-alpha"]
                 [puppetlabs/trapperkeeper ~tk-version]
                 [puppetlabs/kitchensink ~ks-version]
                 [puppetlabs/trapperkeeper-webserver-jetty9 "1.3.1"]

                 [info.sunng/ring-jetty9-adapter "0.8.2" :exclusions [ring/ring-servlet]]

                 [cheshire "5.4.0"]
                 [prismatic/schema "0.4.0"]
                 [clj-time "0.9.0"]

                 [com.taoensso/nippy "2.7.1"]

                 [org.clojure/java.jmx "0.3.0"]
                 [metrics-clojure "0.7.0" :exclusions [org.clojure/clojure org.slf4j/slf4j-api]]

                 ;; managed ThreadPool/ExecutorService
                 [io.aleph/dirigiste "0.1.0-alpha4"]

                 ;; try+/throw+
                 [slingshot "0.12.2"]

                 [puppetlabs/cthun-message "0.1.0"]

                 ;; MQ - activemq
                 [clamq/clamq-activemq "0.4" :exclusions [org.slf4j/slf4j-api]]
                 [org.apache.activemq/activemq-core "5.6.0" :exclusions [org.slf4j/slf4j-api org.fusesource.fuse-extra/fusemq-leveldb]]
                 ;; bridge to allow some spring/activemq stuff to log over slf4j
                 [org.slf4j/jcl-over-slf4j "1.7.10"]]

  :repositories [["releases" "http://nexus.delivery.puppetlabs.net/content/repositories/releases/"]
                 ["snapshots"  "http://nexus.delivery.puppetlabs.net/content/repositories/snapshots/"]]

  :test-paths ["test" "test-resources"]

  :profiles {:dev {:dependencies [[puppetlabs/trapperkeeper ~tk-version :classifier "test" :scope "test"]
                                  [puppetlabs/kitchensink ~ks-version :classifier "test" :scope "test"]]}}

  ;; Enable occasionally to check we have no interop hotspots that need better type hinting
  ; :global-vars {*warn-on-reflection* true}

  :aliases {"tk" ["trampoline" "run" "--config" "test-resources/config.ini"]}

  :main puppetlabs.trapperkeeper.main)
