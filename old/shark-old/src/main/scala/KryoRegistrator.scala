package shark

import spark.KryoRegistrator
import com.esotericsoftware.kryo._

class SharkKryoRegistrator extends KryoRegistrator {
  def registerClasses(kryo: Kryo) {
    kryo.register(classOf[ReduceKey])
  }
}
