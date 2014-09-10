(ns eve.db
  (:require [clojure.math.numeric-tower :as math]
            [korma.core :refer :all]
            [korma.db :refer [defdb sqlite3]]))

(def sqll (sqlite3 {:db "/home/tramsey/proj/lein/eve/resources/db/eve.db"}))

(def activityManufacturing 1)

(defdb evedb sqll)

(declare types materials blueprints activities)

(defentity types
  (database evedb)
  (pk :typeID)
  (table :invTypes)
  (has-many materials {:fk :typeID})
  (has-one blueprints {:fk :blueprintTypeID}))

(defentity materials
  (database evedb)
  (table :invTypeMaterials)
  (belongs-to types {:fk :typeID})
  )

(defentity blueprints
  (database evedb)
  (table :industryBlueprints)
  (pk :typeID)
  (belongs-to types {:fk :typeID}))

(defentity activities
  (database evedb)
  (table :ramActivities)
  (pk :activityID)
  )

(defentity activityTimes
  (database evedb)
  (table :industryActivities)
  (belongs-to activities {:fk :activityID})
  (belongs-to types {:fk :typeID})
  )

(defentity activityMaterials
  (database evedb)
  (table :industryActivityMaterials)
  (belongs-to activities {:fk :activityID})
  (belongs-to types {:fk :materialTypeID})
  )

(defentity activityProducts
  (database evedb)
  (table :industryActivityProducts)
  (pk :typeID)
  (belongs-to types {:fk :typeID})
  )

(defentity activityProbabilities
  (database evedb)
  (table :industryActivityProbabilities)
  (belongs-to types {:fk :typeID})
  (belongs-to types {:fk :productTypeID})
  )

(defentity activitySkills
  (database evedb)
  (table :industryActivitySkills)
  (belongs-to types {:fk :typeID})
  (belongs-to types {:fk :skillID})
  )

(defentity blueprints
  (database evedb)
  (table :industryBlueprints)
  (pk :typeID)
  (belongs-to types {:fk :typeID})
  )

(defentity attributes
  (database evedb)
  (table :dgmAttributeTypes)
  (pk :attributeID)
  )
(defentity typeAttributes
  (database evedb)
  (table :dgmTypeAttributes)
  (belongs-to attributes {:fk :attributeID})
  )

(defn encrypt-skill [bpid]
  (:skillID (first (select activitySkills
                           (where (or (= :skillID 21790)
                                      (= :skillID 21791)
                                      (= :skillID 23087)
                                      (= :skillID 23121)))
                           (where {:activityID 8
                                   :typeID bpid})
                           (fields :skillID)))))

(defn eskill-to-group [es]
  (case es
    23087 728
    21791 729
    23121 730
    21790 731))


(defn decryptor [item]
  (let [attr-map {1112 :prob-mult
                  1113 :me-mod
                  1114 :te-mod
                  1124 :run-mod}
        attributes (select typeAttributes
                           (where {:typeID (:id item)})
                           (where (or (= :attributeID 1112)
                                      (= :attributeID 1113)
                                      (= :attributeID 1114)
                                      (= :attributeID 1124))))

        afun (fn [i {:keys [attributeID valueFloat]}]
               (let [k (attr-map attributeID)
                     v (if (= k :prob-mult)
                         valueFloat
                         (int valueFloat))]
                   (assoc i k v)))]
    (reduce afun item attributes))
  )

;; /*
;; 21790|Caldari Encryption Methods
;; 21791|Minmatar Encryption Methods
;; 23087|Amarr Encryption Methods
;; 23121|Gallente Encryption Methods


;; 33315|728|Occult Parity (Amarr 23087)
;; 33318|729|Cryptic Parity (Minmitar 21791)
;; 33319|731|Esoteric Parity (Caldari 21790)
;; 33320|730|Incognito Parity (Gallente 23121)

;; */
;; select case skillID
;;        when 23087 then 728
;;        when 21791 then 729
;;        when 23121 then 730
;;        when 21790 then 731
;;        end as groupID
;; from industryActivitySkills
;; where typeID = 2204
;; and activityID = 8
;; and (skillID = 21700 or skillID = 21791 or skillID = 23087 or skillID = 23121);

;; select t.typeID, t.typeName from invTypes t join industryActivitySkills s on
;; case skillID
;;        when 23087 then 728
;;        when 21791 then 729
;;        when 23121 then 730
;;        when 21790 then 731
;;        end = t.groupID
;; where s.typeID = 2203
;; and s.activityID = 8
;; and (s.skillID = 21700 or s.skillID = 21791 or s.skillID = 23087 or s.skillID = 23121);


;; select count(*) bpcount from  industryBlueprints b;

;; -- select t.typeName from invTypes t
;; -- join industryBlueprints b on t.typeID = b.typeID
;; -- left join dgmTypeAttributes a on t.typeID = a.typeID and a.attributeID = 422
;; -- where a.valueInt is null
;; -- ;


;; select p.*, t.typeName, tp.typeName
;; from industryActivityProducts p
;; join invTypes t on t.typeID = p.typeID
;; join invTypes tp on tp.typeID = p.productTypeID
;; where activityID = 8
;; limit 20
;; ;


;; -- select valueInt techLevel, count(*) from industryBlueprints b
;; -- left join dgmTypeAttributes a on b.typeID = a.typeID and a.attributeID = 422
;; -- group by valueInt;


(defn activity [activityName]
  (:activityID (first (select activities (where {:activityName activityName})))))

(defn invention-materials [id]
  (select activityMaterials
          (fields [:materialTypeID :typeID] :quantity)
          (where {:typeID id
                  :consume 1
                  :activityID 8})))
(defn name [id]
  (:typeName (first (select types (where {:typeID id})))))

(defn search [typeName]
  (map #(vector (:typeID %1) (:typeName %1))
       (select types (where {:typeName [like (str "%" typeName "%")]}))))

(defn item-components [id]
  (select materials (fields :quantity [:materialTypeID :typeID]) (where {:typeID id})))

(defn bp-components [bp]
  (select activityMaterials
          (fields [:materialTypeID :typeID] :quantity)
          (where {:typeID bp
                  :activityID activityManufacturing})))

(defn parent-bp [bp]
  (:typeID (first (select activityProbabilities
                          (fields :typeID)
                          (where {:productTypeID bp})))))

(defn item-runs
  ([item] (or (:runs item) 1))
  ([item n]
     (assoc item :runs n)))

(defn bp-me
  ([item] (or (:me item) 0))
  ([item me]
     (assoc item :me me)))

(defn bp-te
  ([item] (or (:te item) 1))
  ([item te]
     (assoc item :te te)))

(defn get-item [id]
  {:id id
   :name (name id)})

(defn group-items [g]
  (map (comp get-item :typeID) (select types (fields :typeID) (where {:groupID g}))))

(defn bp-decryptors [bp]
  (let [decrypts (select types (where {:groupID (eskill-to-group (encrypt-skill bp))}))
        decrypts (map (comp get-item :typeID) decrypts)]
    (map decryptor decrypts)))

(defn bp2-decryptors [bp2]
  (bp-decryptors (parent-bp bp2)))

(defn item-decryptors [id]
  (bp-decryptors (get-bp (parent-bp (:id (item-bp id))))))

(defn bp-item [bp]
  (get-item (:typeID (first (select activityProducts
                                    (fields [:productTypeID :typeID])
                                    (where {:typeID bp
                                            :activityID activityManufacturing}))))))

(defn get-bp
  ([bp] (get-bp bp 0))
  ([bp me]
     (bp-me (merge (get-item bp) {:item (merge (bp-item bp)
                                               {:components (bp-components bp)})})
            (- 1 me))))

(defn item-bp [id]
  (get-bp (:typeID (first (select activityProducts
                                  (fields :typeID)
                                  (where {:activityID activityManufacturing
                                          :productTypeID id}))))))
(defn update-components [{id :id :as item}]
  (assoc item :components (item-components (:id id))))

(defn invention-runs [bp]
  (:quantity (first (select activityProducts
                            (where {:activityID 8
                                    :productTypeID bp})))))

(defn invention-prob [id {:keys [prob-mult me-mod te-mod run-mod]
                          did :id
                          :or {prob-mult 1
                               me-mod 0
                               te-mod 0
                               run-mod 0
                               did :none}
                          :as decrypt}]
  (let [{prob :probability bp :typeID} (first (select activityProbabilities
                                                      (fields :typeID, :probability)
                                                      (where {:productTypeID id})))
        runs (invention-runs id)
        materials (invention-materials bp)]
    {:prob (* prob 1.25 prob-mult)
     :runs (+ runs run-mod)
     :materials (conj materials {:quantity 1 :typeID did})
     :me (+ 0.02 (/ me-mod 100))
     :pbp bp
     :bp (get-bp id)}))

(defn show-invention [id]
  (let [pbp (parent-bp id)
        decrypt (conj (bp-decryptors pbp) {:id :none :name "none"})]
    (doseq [d decrypt]
      (let [i (invention-prob id d)
            prob (:prob i)
            runs (:runs i)
            me (:me i)
            job-mats (:materials i)
            job-cost (mkt/component-cost* job-mats {})
            unit-components (get-in i [:bp :item :components])
            comp-cost (mkt/component-cost* unit-components {:runs runs :me me})]
        (prn (:name d)
             prob
             runs
             me
             (float job-cost)
             (float comp-cost)
             (float (/ (+ (/ job-cost prob) comp-cost) runs)))))))

(defn per-run-cost [{:keys [prob runs cost] :as job}]
  (/ cost prob runs))



(defn new-quant [p]
  (fn [q] (int (* q p))))

(defn me-fn [me]
  (new-quant (- 1 me)))

(def refine-fn (new-quant (* 0.5 1.1)))

(defn item-refine [id]
  (apply-saving* refine-fn (item-materials id)))

(defn item-times* [c items]
  (apply-saving* (repeat (new-quant c)) items))

(defn apply-saving [sav-fn item]
  (update-in item [:quantity] sav-fn))

(defn apply-saving* [sav-fn items]
  (map apply-saving (repeat sav-fn) items))
