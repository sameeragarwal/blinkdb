package shark

import spark._

import org.apache.hadoop.conf.Configuration

import java.text.SimpleDateFormat
import java.util.Calendar


object SharkEnv {
  
  System.setProperty("spark.serializer", "spark.KryoSerializer")
  System.setProperty("spark.kryo.registrator", classOf[SharkKryoRegistrator].getName)
  
  val contextIdStr = java.net.InetAddress.getLocalHost.getHostName

  val sc: SparkContext = new SparkContext(
      if (System.getenv("MASTER") == null) "local" else System.getenv("MASTER"),
      "Shark::" + contextIdStr)

  val conf: Configuration = new Configuration()

}

