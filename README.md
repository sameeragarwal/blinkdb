# BlinkDB
#### Queries with Bounded Errors and Bounded Response Times on Very Large Data

BlinkDB is a large-scale data warehouse system built on Shark and Spark and is designed to be
compatible with Apache Hive. It can answer HiveQL queries up to 200-300 times faster than Hive
by executing them on user-specified samples of data and providing approximate answers that are
augmented with meaningful error bars. BlinkDB 0.1.0 is an alpha developer release that supports
creating/deleting samples on any input table and/or materialized view and executing approximate
HiveQL queries with those aggregates that have statistical closed forms (i.e., AVG, SUM, COUNT,
VAR and STDEV).

#### BlinkDB requires:
* Scala 2.9.3
* Spark 0.8.x
* OpenJDK 7 or Oracle HotSpot JDK 7 or Oracle HotSpot JDK 6u23+

#### Key Features:
* Allows users to create/delete samples on any input table and/or materialized view.
* Provides approximate answers with error bars for queries with a single AVG, SUM, COUNT, VAR and STDEV operator on any user specified sample.
* Complete support for GROUP BYs and FILTERs

## For current documentation, see the [BlinkDB Website](http://blinkdb.cs.berkeley.edu)
