-- Databricks notebook source
-- MAGIC %md
-- MAGIC # Explore Weather News

-- COMMAND ----------

SELECT * from weather_json 

-- COMMAND ----------

-- MAGIC 
-- MAGIC %fs ls dbfs:/FileStore/tables

-- COMMAND ----------

-- MAGIC 
-- MAGIC %fs ls dbfs:/FileStore/df

-- COMMAND ----------



-- COMMAND ----------

-- MAGIC %scala
-- MAGIC 
-- MAGIC import org.apache.spark.sql.functions.regexp_replace
-- MAGIC import org.apache.spark.sql.Column
-- MAGIC 
-- MAGIC 
-- MAGIC   //val bch = spark.sql("SELECT date, weather_json.subject from weather_json where weather_json.from='ldm@weather.cod.edu'")
-- MAGIC   val bch = spark.sql("SELECT date, weather_json.subject from weather_json where weather_json.subject LIKE '%Tstm%'")
-- MAGIC 
-- MAGIC 
-- MAGIC bch.schema
-- MAGIC bch.count
-- MAGIC 
-- MAGIC 
-- MAGIC 
-- MAGIC //bch.withColumn("subject", regexp_replace(bch.col("subject"), " ", ""))
-- MAGIC //bch.withColumn("bch_json.subject", new Column(bch.col("bch_json.subject").toString.substring(5)))
-- MAGIC bch.show(false)

-- COMMAND ----------

-- MAGIC %scala 
-- MAGIC //create stack to collect states
-- MAGIC import scala.collection.mutable.Stack
-- MAGIC var stackStates = Stack[String]()
-- MAGIC 
-- MAGIC val r = bch.collect()
-- MAGIC val q = r.length -1
-- MAGIC 
-- MAGIC for ( i <- 1 to q){
-- MAGIC   var rowString = r(i).mkString.substring(27)
-- MAGIC   var ll = rowString.split(' ')
-- MAGIC   //for comprehension
-- MAGIC   for (p  <- ll if p.length() == 2) stackStates.push(p)
-- MAGIC  }
-- MAGIC 
-- MAGIC //re-create and confirm display frequency distribution
-- MAGIC var mapStatesWithThunderStorms = stackStates.groupBy(identity).mapValues(_.size)
-- MAGIC 
-- MAGIC for ((k,v) <- mapStatesWithThunderStorms){
-- MAGIC   printf("In State: %s, Thunderstorms: %s occured \n", k, v)
-- MAGIC } 

-- COMMAND ----------

-- MAGIC %scala
-- MAGIC import scala.collection.mutable.ListBuffer
-- MAGIC 
-- MAGIC case class Jug(state: String, occur: Int, desc: String)
-- MAGIC var s = new ListBuffer[Jug]()
-- MAGIC 
-- MAGIC for ((k,v) <- mapStatesWithThunderStorms){
-- MAGIC   var ss = "In State: %s, Thunderstorms: %s occured".format( k.toUpperCase, v)
-- MAGIC   //println(k,v)
-- MAGIC   s.append(Jug(k.toUpperCase, v, ss))
-- MAGIC   //s :+ ss
-- MAGIC } 
-- MAGIC val cocoDF = s.toDF
-- MAGIC //create datset?
-- MAGIC display(cocoDF)

-- COMMAND ----------

-- MAGIC %scala
-- MAGIC cocoDF.createOrReplaceTempView("vCoco")
-- MAGIC val orderedOutput = spark.sql("select * from vCoco ORDER BY occur DESC")
-- MAGIC display(orderedOutput)

-- COMMAND ----------

-- MAGIC %scala
-- MAGIC import org.apache.spark.sql.avro._
-- MAGIC import org.apache.avro.SchemaBuilder
-- MAGIC 
-- MAGIC orderedOutput
-- MAGIC   .select(
-- MAGIC     to_avro($"state").as("key"),
-- MAGIC     to_avro($"desc").as("value"))
-- MAGIC   .write
-- MAGIC   .format("kafka")
-- MAGIC   .option("kafka.bootstrap.servers", "52.117.28.10:9092")
-- MAGIC   .option("topic", "SenseHat001")
-- MAGIC   .save()

-- COMMAND ----------

-- MAGIC %scala
-- MAGIC import org.apache.spark.sql.avro._
-- MAGIC import org.apache.avro.SchemaBuilder
-- MAGIC 
-- MAGIC bch
-- MAGIC   .select(
-- MAGIC     to_avro($"default.weather_json.date").as("key"),
-- MAGIC     to_avro($"default.weather_json.subject").as("value"))
-- MAGIC   .write
-- MAGIC   .format("kafka")
-- MAGIC   .option("kafka.bootstrap.servers", "52.117.28.10:9092")
-- MAGIC   .option("topic", "SenseHat001")
-- MAGIC   .save()
