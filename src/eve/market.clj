(ns eve.market
  (require [clojure.xml :as xml]
           [clojure.pprint :as pprint]
           [clojure.math.numeric-tower :as math]))


(def market-cache (atom {}))

(def market-uri-template "http://api.eve-central.com/api/marketstat?typeid=%d&regionlimit=%d")


(def test-data
  {:tag :evec_api,
   :attrs {:version "2.0", :method "marketstat_xml"},
   :content
   [{:tag :marketstat,
     :attrs nil,
     :content
     [{:tag :type,
       :attrs {:id "34"},
       :content
       [{:tag :buy,
         :attrs nil,
         :content
         [{:tag :volume, :attrs nil, :content ["21154325270"]}
          {:tag :avg, :attrs nil, :content ["4.98"]}
          {:tag :max, :attrs nil, :content ["5.52"]}
          {:tag :min, :attrs nil, :content ["2.50"]}
          {:tag :stddev, :attrs nil, :content ["0.67"]}
          {:tag :median, :attrs nil, :content ["5.15"]}
          {:tag :percentile, :attrs nil, :content ["5.33"]}]}
        {:tag :sell,
         :attrs nil,
         :content
         [{:tag :volume, :attrs nil, :content ["25948560282"]}
          {:tag :avg, :attrs nil, :content ["5.99"]}
          {:tag :max, :attrs nil, :content ["12.00"]}
          {:tag :min, :attrs nil, :content ["5.10"]}
          {:tag :stddev, :attrs nil, :content ["0.98"]}
          {:tag :median, :attrs nil, :content ["6.07"]}
          {:tag :percentile, :attrs
           nil, :content ["5.39"]}]}
        {:tag :all,
         :attrs nil,
         :content
         [{:tag :volume, :attrs nil, :content ["47604619952"]}
          {:tag :avg, :attrs nil, :content ["5.49"]}
          {:tag :max, :attrs nil, :content ["12.00"]}
          {:tag :min, :attrs nil, :content ["1.00"]}
          {:tag :stddev, :attrs nil, :content ["1.14"]}
          {:tag :median, :attrs nil, :content ["5.42"]}
          {:tag :percentile, :attrs nil, :content ["3.42"]}]}]}
      ]}]})

(defn trace [o]
  (pprint/pprint o)
  o)

(defn skim-values [xml]
  (let [tag (:tag xml)]
    (reduce #(assoc-in %1 [tag (:tag %2)] (read-string (first (:content %2))))
            {}
            (:content xml))))

(defn- raw-process [market-xml]
  (let [buy-sell-all (get-in market-xml [:content 0 :content 0 :content])]
    (reduce #(merge %1 (skim-values %2)) {} buy-sell-all)))

(def TheForge 2)
(def SinqLaison 32)

(def default-region (atom SinqLaison))

(defn reset-cache []
  (reset! market-cache {}))

(defn lookup-id
  ([id] (lookup-id id @default-region))
  ([id region]
     (or (and (or (= id :none)
                  (= id 0))
              {:all {:percentile 0,:median 0 :stddev 0 :min 0 :max 0 :avg 0 :volume 0}
               :sell {:percentile 0,:median 0 :stddev 0 :min 0 :max 0 :avg 0 :volume 0}
               :buy  {:percentile 0,:median 0 :stddev 0 :min 0 :max 0 :avg 0 :volume 0}})
         (and (nil? id) (throw (Exception. "Attempt to lookup nil ID")))
         (get @market-cache id)
         (let [market-data (xml/parse (format market-uri-template id (+ region 10000000)))
               market-data (raw-process market-data)]
           (get (swap! market-cache #(assoc %1 id market-data)) id)))))


(defn median-sell [id]
  (rationalize (get-in (lookup-id id) [:sell :median])))

(defn median-buy [id]
  (rationalize (get-in (lookup-id id) [:buy :median])))

(defn max-buy [id]
  (rationalize (get-in (lookup-id id) [:buy :max])))

(defn min-sell [id]
  (rationalize (get-in (lookup-id id) [:sell :min])))

(defn prices [id]
  (map float [(median-sell id)
              (min-sell id)
              (max-buy id)
              (median-buy id)]))

(defn component-cost [{id :typeID q :quantity :as comp} {:keys [runs me cfun] :or {runs 1 me 0 cfun min-sell}}]
  (let [unit-cost (cfun id)
        meq (int (math/ceil (* runs q (- 1 me))))]
    (* unit-cost meq)))

(defn component-cost* [comps opts]
  (reduce #(+ %1 (component-cost %2 opts)) 0 comps))

(defn item-cost [{comps :components :as item} opts]
  (float (component-cost* comps opts)))

(defn job-cost [{:keys [materials]
                 {{comps :components} :item} :bp}]
  (component-cost* materials {}))

(defn man-cost [{me :me runs :runs {{comp :components} :item} :bp}]
  (component-cost* comp {:me me :runs runs}))

(defn unit-job-cost [{:keys [runs cost prob materials] {me :me {comps :components} :item :or {me 1}} :bp :as job}]
  (let [cfun min-sell
        pb-cost (component-cost* materials {:cfun cfun})
        total-cost (+ pb-cost (component-cost* comps {:runs runs :me me :cfun cfun}))]
    (/ total-cost prob runs)))

(defn assoc-cost
  ([h] (assoc-cost median-sell h))
  ([cost-fn {quantity :quantity, item :typeID :as h}]
     (let [unit-cost (cost-fn item)]
       (-> (assoc h :unitCost unit-cost)
           (assoc :totalCost (* unit-cost quantity))))))

(defn assoc-cost*
  ([items] (assoc-cost* median-sell items))
  ([cost-fn items]
     (map assoc-cost (repeat cost-fn) items)))

(defn cost* [items]
  (reduce #(+ %1 (:totalCost %2)) 0 items))
