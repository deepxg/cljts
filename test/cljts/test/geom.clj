(ns cljts.test.geom
  (:refer-clojure :exclude [empty?])
  (:use [clojure.test])
  (:use [cljts.geom])
  (:use [midje.sweet])
  (:import [com.vividsolutions.jts.geom
            Coordinate
            GeometryFactory
            PrecisionModel
            LinearRing
            Polygon
            Point]))

(def geom-factory (GeometryFactory.))
(def the-point (.createPoint geom-factory
                             (Coordinate. 20 30)))

(fact
 (c 50 23) => (Coordinate. 50 23)
 (c 118 43 50.3) => (Coordinate. 118 43 50.3))

;(unfinished point)

(fact
 (point (c 20 30)) => the-point)

;(unfinished linestring)

(def cseq [(c 20 30) (c 30 40) (c 40 50)])

(fact
 (line-string cseq) =>
 (.createLineString geom-factory (into-array Coordinate cseq)))

(def cseq-ring [(c 20 30) (c 30 40) (c 40 55) (c 20 30)])
(def cseq-ring-inner [(c 22 28) (c 28 42) (c 38 50) (c 22 28)])
(fact
 (linear-ring cseq-ring) =>
 (.createLinearRing geom-factory (into-array Coordinate cseq-ring)))

(facts
 (let [outter-ring (linear-ring cseq-ring)
       inner-ring (linear-ring cseq-ring-inner)]
   (polygon outter-ring nil) => (.createPolygon geom-factory outter-ring nil)
   (polygon outter-ring [inner-ring]) =>
   (.createPolygon geom-factory outter-ring
                   (into-array LinearRing [inner-ring]))))

;; multi-point
(fact
 (let [p1 (point (c 20 30))
       p2 (point (c 30 40))]
   (multi-point [p1 p2]) =>
   (.createMultiPoint geom-factory (into-array Point [p1 p2]))))

;; multi-polygon
(fact
 (let [pp1 (polygon (linear-ring cseq-ring) nil)
       pp2 (polygon (linear-ring cseq-ring-inner) nil)]
   (multi-polygon [pp1 pp2]) =>
   (.createMultiPolygon geom-factory (into-array Polygon [pp1 pp2]))))



;; geometry properties
(facts
 (let [pp (polygon (linear-ring cseq-ring) nil)]
   (area pp) => (.getArea pp)
   (area (point (c 30 10))) => 0.0

   (boundary pp) => (.getBoundary pp)
   (centroid pp) => (.getCentroid pp)
   (coordinates pp) => cseq-ring
   (dimension pp) => 2
   (envelope pp) => (.getEnvelope pp)
   (interior-point pp) => (.getInteriorPoint pp)
   
   (length pp) => (.getLength pp)
   (length (point (c 30 20))) => 0.0

   ;; default srid
   (srid pp) => 0

   (n-geometries pp) => 1
   (n-points pp) => (count cseq-ring)

   (empty? pp) => false
   (simple? pp) => true))

