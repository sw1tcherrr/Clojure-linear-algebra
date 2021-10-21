(defn shape [obj]
  (if (not (vector? obj))
    []
    (apply conj [(count obj)] (shape (first obj)))))

(defn same-shape? [args]
  (if (empty? args)
    true
    (apply = (mapv shape args))))

(defn vect? [v]
  (and (vector? v)
       (or (every? number? v) (empty? v))))

(defn matrix? [m]
  (and (vector? m)
       (every? vect? m)
       (same-shape? m)))

(defn tensor? [t]
  (or (and (vector? t) (same-shape? t))
      (number? t)))

(defn mapper [op type]
  (fn [& args]
    {:pre [(every? type args) (same-shape? args)]}
    (apply mapv op args)))

(def v+ (mapper + vect?))
(def v- (mapper - vect?))
(def v* (mapper * vect?))
(def vd (mapper / vect?))

(def m+ (mapper v+ matrix?))
(def m- (mapper v- matrix?))
(def m* (mapper v* matrix?))
(def md (mapper vd matrix?))

(defn *s [op type]
  (fn [obj & ss]
    {:pre [(type obj) (every? number? ss)]}
    (mapv #(op % (apply * ss)) obj)))

(def v*s (*s * vect?))

(def m*s (*s v*s matrix?))

(defn vect [& vs]
  {:pre [(every? vect? vs) (same-shape? vs) (== (count (first vs)) 3)]}
  (reduce (fn [v1 v2] (mapv #(- (* (nth v1 %1) (nth v2 %2)) (* (nth v1 %2) (nth v2 %1))) [1 2 0] [2 0 1])) vs))

(defn scalar [& vs]
  {:pre [(every? vect? vs) (same-shape? vs)]}
  (apply + (apply v* vs)))

(defn m*v [m v]
  {:pre [(matrix? m) (vect? v)]}
  (mapv #(scalar % v) m))

(defn transpose [m]
  {:pre [(matrix? m)]}
  (apply mapv vector m))

(defn m*m [& ms]
  {:pre [(every? matrix? ms)]}
  (reduce (fn [m1 m2]
            (let [m2t (transpose m2)]
            (mapv #(m*v m2t %) m1)))
          ms))

(defn can-broadcast? [from to]
  (= (subvec to 0 (count from)) from))

(defn broadcast [t sh]
  {:pre [(can-broadcast? (shape t) sh)]}
  (if (= (shape t) sh)
    t
    (letfn [(rpt [cnts vct]
            (if (== (count cnts) 1)
              (vec (repeat (last cnts) vct))
              (recur (pop cnts) (vec (repeat (last cnts) vct)))))]
      (if (number? t)
        (rpt sh t)
        (mapv #(broadcast % (vec (rest sh))) t)))))

(defn broadcast-all [ts]
  (let [msh (apply max-key count (mapv shape ts))]
    (mapv #(broadcast % msh) ts)))

(defn tensor-op [op]
  (fn [& ts]
    {:pre [(every? tensor? ts)]}
    (apply
      (fn f [& ts]
        (if (number? (first ts))
          (apply op ts)
          (apply mapv f ts)))
      (broadcast-all ts))))

(def tb+ (tensor-op +))
(def tb- (tensor-op -))
(def tb* (tensor-op *))
(def tbd (tensor-op /))