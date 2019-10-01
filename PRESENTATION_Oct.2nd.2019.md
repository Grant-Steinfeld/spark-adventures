%title: Apache Spark 2.4.4
%author: Grant Steinfeld
%date: 2018-10-02

-> Apache Spark 2.4.4 <-
=========


-> Apache Foundation Open Source Analytics Cluster-computing  framework <-



-------------------------------------------------
RDD Resilient Distributed Datasets
* Spark 1.x

Read-Only multiset
* cluster of machines
* Fault tolerant

Dataset API
* Spark 2.x


-------------------------------------------------

dev 2012 map / reduce - RAM

Iterative algo

SQL like queries

Interactive data analysis

Requires Cluster Manager 
* Cluster Manager
** Hadoop YARN, Apache Mesos
* Distributed storage
** HDFS, S3, Kudu

-------------------------------------------------
### Spark Core
Dist task dispatching
Scheduling

i/o functionality
 * Java; Python; Scala; R

 * Shared variables
    ** Broadcast Vars
    ** Accumulators

-------------------------------------------------
## Spark SQL

Data Frames

-------------------------------------------------
Structured or Semi-Structed data
DSL
Scala; Java; Python

* SQL Language support
cli
odbc/jdbc

-------------------------------------------------
ML
MLlib


-------------------------------------------------

Dist ML fwk / 9x faster hadoop
large scale ML
SparkML - 

GraphX
dist ML fwk
RDDs
 immutalbe
 Pregel Abstraction
 MapReduce

-------------------------------------------------
 Streaming
 analytics
  core scheduling

-------------------------------------------------
-> # Ingesting <-

 mini batching
 higher velcodity
 RDD 
 Lamda arch

 Structured Streaming
 spark 2.x


read.textFile
Dataset



-------------------------------------------------
-> # Clusters <-

* Components
    - Spark Applications
        - * Independent set of processes *
        - * Spark Context *
            -- Driver Program
    - Cluster Managers
        - * Standalone *
        - * Mesos - Hadoop MapReduce *
        - * YARN - resource manager *
        - * K8s - experimental *

-------------------------------------------------
-> # Applications <-

    * Running options:
        - Launch Applications
            - * bin/spark-submit *
            - * supports all cluster managers *

        - Dependencies
            - * Assembly Jar *
            
        - Master URL
        - can also use a config file to 
        - Dependancy management with Maven


-------------------------------------------------

-> # Running Scala application/code <-

    first compile with mvn

    ```
    mvn package
    #produces a jar file
    /home/developer/dev/scala-hw/target/scala-2.12/hello-spark_2.12-0.1.0-SNAPSHOT.jar
    #run jar in scala engine
    /opt/spark-2.4.4/bin/spark-submit --class example.Hello --master local[*] /home/developer/dev/scala-hw/target/scala-2.12/hello-spark_2.12-0.1.0-SNAPSHOT.jar
    ```
    


-------------------------------------------------
-> # Monitoring with Web interfaces <-

> Port 4040 (default)

>> Application information
> > Tasks
> > Memory usage
> > Environmental
> > Running Executors

>> Environmental Variables

>> REST API
> > gets JSON

    
-------------------------------------------------
-> # Scheduling <-
    
    
    * Resource allocation
        - Across applications
            -- Dynamic resource allocation
            -- Multiple applications within Spark cluster 
        - *Within* applications in pools
            -- Fair scheduler 
            -- Equal share (default FiFo) 
            -- Pool properties
                --- * schedulingMode 
                --- * weight [1] - (3) would get 3x resources
                --- * minShare [0] number cores 0 let's spark decide


-------------------------------------------------

-> # installation options <-

ubuntu
mac

linux
 this demo is built on CentOS 7 from soruce using mvn 

src using Maven and Java 1.8 or later.


spark 2.4.4 (stable latest)
https://www-us.apache.org/dist/spark/spark-2.4.4/spark-2.4.4.tgz



-------------------------------------------------
-> # prerequisites <-

java 8 openJDK
https://www.digitalocean.com/community/tutorials/how-to-install-java-on-centos-and-fedora



-------------------------------------------------
-> # spark sql tutorial <-
https://www.edureka.co/blog/spark-sql-tutorial/

https://alvinalexander.com/scala/iterating-scala-lists-foreach-for-comprehension

-------------------------------------------------
-> # Scala read csv file <-
```
package example

import org.apache.spark.sql.SparkSession
import org.apache.log4j.{Logger,Level}


object Hello {
        def main(args: Array[String]) {
         Logger.getLogger("org").setLevel(Level.OFF)
         println("hello spark")
         val spark = SparkSession.builder().getOrCreate()
         val medals = spark.read.option("header",true).csv("file:///home/developer/datasets/most-medals-won.csv")
         println(medals)
         medals.printSchema()
         spark.stop()
        }
}

```

-------------------------------------------------
-> # spark shell explorations   <-

bin/spark-shell

```
val weather  = spark.read.json("file:///home/developer/datasets/weather.json")

//prep for SQL queries
weather.createOrReplaceTempView("vWeatherLDM")

//Grab just subjects with Tstem in subject
val thunder = spark.sql("SELECT subject FROM vWeatherLDM WHERE subject LIKE '%Tstm%'")
val r = thunder.limit(60).collect()
var firstValue = r(0)
var a = firstValue.mkString
var rowString = a.substring(a.indexOf("Tstm")+4).toUpperCase
import scala.collection.mutable.Stack

var ll = rowString.split(' ')
for (p <- ll if p.length() == 2) stackStates.push(p)
stackStates.groupBy(identity).mapValues(_.size)
var mapStatesWithThunderStorms = stackStates.groupBy(identity).mapValues(_.size)
for ((k,v) <- mapStatesWithThunderStorms) printf("In State: %s, Thunderstorms: %s occured \n", k, v)

```


-------------------------------------------------
-> # Thunderstorms by state <-



> In State: MA, Thunderstorms: 1 occured
> In State: In, Thunderstorms: 1 occured
> In State: ME, Thunderstorms: 1 occured
> In State: CW, Thunderstorms: 1 occured
> In State: CT, Thunderstorms: 1 occured
> In State: Ww, Thunderstorms: 1 occured
> In State: NH, Thunderstorms: 1 occured
> In State: CA, Thunderstorms: 4 occured
> In State: RI, Thunderstorms: 1 occured

-------------------------------------------------
-> # The End <-

-------------------------------------------------
-> # misc. <-
val states = spark.read.option("header",true).csv("file:///home/developer/datasets/state-abbr.csv")

