# BlinkDB: Queries with Bounded Errors and Bounded Response Times on Very Large Data

BlinkDB is a large-scale data warehouse system built on Shark and Spark and is designed to be
compatible with Apache Hive. It can answer HiveQL queries up to 200-300 times faster than Hive
by executing them on user-specified samples of data and providing approximate answers with meaningful
error bars.

BlinkDB requires:
* Shark 0.7.0
* Scala 2.9.3
* Hive 0.9
* Spark 0.7.1
* OpenJDK 7 or Oracle HotSpot JDK 7 or Oracle HotSpot JDK 6u23+

## For current documentation, see the [BlinkDB Website](http://blinkdb.cs.berkeley.edu)
