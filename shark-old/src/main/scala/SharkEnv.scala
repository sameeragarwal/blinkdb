package shark

import spark._

import org.apache.hadoop.conf.Configuration

object SharkEnv {
  
  System.setProperty("spark.serializer", "spark.KryoSerializer")
  System.setProperty("spark.kryo.registrator", classOf[SharkKryoRegistrator].getName)
  
  var sc: SparkContext = new SparkContext(if (System.getenv("MASTER") == null) "local" else System.getenv("MASTER"), "Shark")
  val conf: Configuration = new Configuration()

}

