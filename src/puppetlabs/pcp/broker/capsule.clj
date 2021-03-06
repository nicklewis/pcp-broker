(ns puppetlabs.pcp.broker.capsule
  (:require [clj-time.coerce :as time-coerce]
            [clj-time.core :as time]
            [puppetlabs.pcp.message :as message :refer [Message]]
            [puppetlabs.pcp.protocol :as p]
            [puppetlabs.kitchensink.core :as ks]
            [schema.core :as s])
  (:import (org.joda.time DateTime)))

(defprotocol CapsuleInterface
  (summarize [capsule]
    "Summarize the capsule")
  (add-hop [capsule server stage]
    [capsule server stage timestamp]
    "Add a debugging hop to the capsule")
  (expired? [capsule]
    "Has the capsule expired?")
  (encode [capsule]
    "Return the bytes to send when sending this message"))

;; A Capsule is a message as it moves across the broker from queue to
;; queue.  Currently it contains an actual Message, but in future it
;; might make sense to just contain a message-id with the message
;; itself in some other persistent storage.

(declare -summarize -add-hop -expired? -encode)

(s/defrecord Capsule
             [expires :- DateTime
              message :- Message
              hops    :- (:hops p/DebugChunk)
              target  :- (s/maybe p/Uri)]
  CapsuleInterface
  (summarize [capsule] (-summarize capsule))
  (add-hop [capsule server stage] (-add-hop capsule server stage))
  (add-hop [capsule server stage timestamp] (-add-hop capsule server stage timestamp))
  (expired? [capsule] (-expired? capsule))
  (encode [capsule] (-encode capsule)))

(def CapsuleLog
  "Schema for a loggable summary of a capsule"
  {:messageid p/MessageId
   :source p/Uri
   :destination (s/either p/Uri [p/Uri])})

(s/defn ^:always-validate -summarize :- CapsuleLog
  [capsule :- Capsule]
  {:messageid (get-in capsule [:message :id])
   :source (get-in capsule [:message :sender])
   :destination (or (:target capsule)
                    (get-in capsule [:message :targets]))})

(s/defn ^:always-validate -add-hop :- Capsule
  "Adds a debug hop to the message state"
  ([capsule :- Capsule server :- p/Uri stage :- s/Str]
   (add-hop capsule server stage (ks/timestamp)))
  ([capsule :- Capsule server :- p/Uri stage :- s/Str timestamp :- p/ISO8601]
   (let [hop {:server server
              :time   timestamp
              :stage  stage}]
     (assoc capsule :hops (conj (vec (:hops capsule)) hop)))))

(s/defn ^:always-validate -expired? :- s/Bool
  "Check whether a message has expired or not"
  [message :- Capsule]
  (let [expires (:expires message)
        now     (time/now)]
    (time/after? now expires)))

(s/defn ^:always-validate -encode :- message/ByteArray
  "Return the bytes we should send when sending this Capsule.  Adds
  the debug chunk to the message"
  [capsule :- Capsule]
  (let [message (:message capsule)
        debug   {:hops (:hops capsule)}]
    (s/validate p/DebugChunk debug)
    (message/encode (message/set-json-debug message debug))))

(s/defn ^:always-validate wrap :- Capsule
  "Wrap a Message producing a Capsule"
  [message :- Message]
  (map->Capsule
   {:expires  (time-coerce/to-date-time (:expires message))
    :message  message
    :hops     []}))
