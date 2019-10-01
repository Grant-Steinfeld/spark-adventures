%title: Apache Spark 2.4.4
%author: Grant Steinfeld
%date: 2018-10-02

-> Apache Spark 2.4.4 <-
=========


-> Apache Foundation Open Source Analytics Cluster-computing  framework <-



-------------------------------------------------

-> # Overview <-


```
      ____              __
     / __/__  ___ _____/ /__
    _\ \/ _ \/ _ `/ __/  '_/
   /___/ .__/\_,_/_/ /_/\_\   version 2.4.4
      /_/
```

Apache Spark is a *fast* general purpose _cluster_ *compute* system
that processes *large data sets* in parallel.

The core abstraction is the resilient distributed dataset (RDD) a working set of data
that resides in memory allowing for *fast iterative processing*



-------------------------------------------------
-> # Goals <-

Origin:  out UC Berkely invented by `Matei Zaharia` 
who was frustrated with the slow disk intensive MapReduce 
options like Hadoop.


* Set out to achieve 2 main goals to provide
    - *composable* high level set of API's for _distributed processing_
    - *unified engine* for running complete apps


-------------------------------------------------
-> # What makes it so fast? <-

In-Memory datasets, that do not require i/o ( c.f. Hadoop)

Parallel execution

*_Fault tolerant_*
Checkpoints, so if job fails it can pick up where it left off

-------------------------------------------------
-> # Resilient Distributed Datasets (RDD) <-


* RDD
 - immutable
 -- MapReduce
 - Read-Only multi-set




-------------------------------------------------

-> # Spark Core <-

* Core
- Dist task dispatching
-- Scheduling
- i/o functionality
-- Java; Python; Scala; R

- Shared variables
-- Broadcast Vars
-- Accumulators


-------------------------------------------------
-> # Data Lakes v.s Streaming Data in motion <-

* Version 1.x
- Spark Streaming API 
- DSL
-- Scala
-- Java
-- Python

* Version 2.x
- Structured Streaming API



-------------------------------------------------
-> # Productivity? <-

*  Achieved by a few High Level Query APIs
- SparkSQL 
-- SQL syntax queries
-- return Data Frames

- MLlib
-- Machine Learning
-- Dist ML fwk / 9x faster hadoop
- GraphX

-------------------------------------------------
-> # GraphX Computation <-

Pregel-like bulk-synchronous message-passing API. 

`org.apache.spark.graphx.Pregel`

GraphX Pregel API 

allows for a substantially more *efficient distributed execution* while also exposing greater flexibility for *graph-based computation*





-------------------------------------------------
-> # Ingesting data <-

* options
 - read _static_ data *off disk* (object storage / S3)
  - read.textFile ( JSON, text, csv )
  - `slow`
  - Dataset
 - consume _streaming_ data in *motion*
  - Structured Streaming
  - `higher velocity`
  - RDD 




-------------------------------------------------
-> # Spark Clustering <-

* Components
    - Spark Applications
        - * Independent set of processes *
        - * Spark Context *
    - Cluster Managers
        - * Standalone* (default)
        - Mesos - Hadoop MapReduce 
        - YARN - resource manager 
        

-------------------------------------------------
-> # Applications <-

    * Running options:
        - Launch Applications
            - * bin/spark-submit *
            - * supports all cluster managers *

        - Dependencies
            - * Assembly Jar *
            


-------------------------------------------------
-> # Monitoring with Web interfaces <-

 
* Monitor
- Application information
-- Tasks
-- Memory usage
-- Running Executors
-- Environmental Variables
- REST API 
-- Port 4040 (default)


-------------------------------------------------
-> # Dev process  <-

Build ambitious applications quickly!

First use the REPL/CLI that suits your programming language

Scala users:  `spark-2.4.4/bin/spark-shell`  
Python users: `spark-2.4.4/bin/pyspark` 

Now you can interactively explore and analyze huge datasets.

Significant improvement over  batch Hive jobs running on Hadoop.

A developer can train a ML model can put the model through multiple steps in the training process
without checkpointing to disk.

-> then port key algorithms by `writing actual code` to *deploy and run* in a spark cluster


-------------------------------------------------
-> # explore data in the spark shell scala REPL   <-

First launch the Scala shell:
`bin/spark-shell`

and then go ahead and start using scala> 

```
val weather  = spark.read.json("file:///home/developer/datasets/weather.json")

//prep for SQL queries
weather.createOrReplaceTempView("vWeatherLDM")

//Create new dataset with subjects that have the word Tstem (Thunderstorm) 
val thunder = spark.sql("SELECT subject FROM vWeatherLDM WHERE subject LIKE '%Tstm%'")

//Convert to Arra
val r = thunder.limit(60).collect()

//inspect first row
var firstValue = r(0)
var a = firstValue.mkString
var rowString = a.substring(a.indexOf("Tstm")+4).toUpperCase

//create stack to collect states
import scala.collection.mutable.Stack
var stackStates = Stack[String]()

//parse out States where thunderstorms occured
var ll = rowString.split(' ')
//for comprehension
for (p <- ll if p.length() == 2) stackStates.push(p)

//create and display frequency distribution
var mapStatesWithThunderStorms = stackStates.groupBy(identity).mapValues(_.size)
for ((k,v) <- mapStatesWithThunderStorms) printf("In State: %s, Thunderstorms: %s occured \n", k, v)

//add some extra state data to test count groupBy
stackStates.push("NY")
stackStates.push("NY")
stackStates.push("NY")

```


-------------------------------------------------
-> # Thunderstorms by state <-



> In  State: MA, Thunderstorms: 1 occured
> In  State: ME, Thunderstorms: 1 occured
> In  State: CW, Thunderstorms: 1 occured
> In  State: CT, Thunderstorms: 1 occured
> In  State: NH, Thunderstorms: 1 occured
> In  State: NY, Thunderstorms: 7 occured
> In  State: RI, Thunderstorms: 1 occured

So after eploring the nature and shape of the data about,
it is now possible to write programs:

a) To create and save the newly curated dataset to disk to use later on.

b) To run periodically on unstructured data to perform a)

c) Deploy to and take advantage of *big datasets*  to take full advantage of _Spark clustering_


-------------------------------------------------
-> # DEMO Scala thunderstorm.scala <-

 tbd


-------------------------------------------------
-> # DEMO Scala read csv file <-
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
-> # DEMO: Running and deploying a Scala application <-



```
/*
    first compile with mvn
*/
mvn package

/*
    produces a jar file
*/
/home/developer/dev/scala-hw/target/scala-2.12/hello-spark_2.12-0.1.0-SNAPSHOT.jar

/*
    deploy and run jar in the scala unified engine
*/
/opt/spark-2.4.4/bin/spark-submit 
    --class example.Hello 
    --master local[*] 
    /home/developer/dev/scala-hw/target/scala-2.12/hello-spark_2.12-0.1.0-SNAPSHOT.jar
```

Simplify: use a *config file* in cases where there are multiple Dependencies/Jars



-------------------------------------------------
-> # Resources <-

spark sql tutorial
https://www.edureka.co/blog/spark-sql-tutorial/

scala for list comprehensions
https://alvinalexander.com/scala/iterating-scala-lists-foreach-for-comprehension

Pregel optimization algorithm
https://spark.apache.org/docs/1.6.0/api/java/org/apache/spark/graphx/Pregel.html


-------------------------------------------------
-> # The End <-


-------------------------------------------------
-> # installing Apache Spark <-

_platforms_
    ubuntu
    mac
    linux

_prerequisites_

java 8 / openJDK
https://www.digitalocean.com/community/tutorials/how-to-install-java-on-centos-and-fedora

this demo is built on CentOS 7 from soruce using mvn 

download spark 2.4.4 (stable latest)
https://www-us.apache.org/dist/spark/spark-2.4.4/spark-2.4.4.tgz






