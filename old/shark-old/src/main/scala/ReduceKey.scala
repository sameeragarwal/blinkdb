package shark

import java.util.Arrays

class ReduceKey(val bytes: Array[Byte]) extends Serializable with Ordered[ReduceKey] {

  override def hashCode(): Int = {
    Arrays.hashCode(bytes)
  }

  override def equals(other: Any): Boolean  = {
    other match {
      case other: ReduceKey =>
        Arrays.equals(bytes, other.bytes)
      case _ =>
        false
    }
  }
  
  def compareBytes(a: Array[Byte], b: Array[Byte]): Int = {
    if (a eq null) {
      if (b eq null) 0
      else -1
    }
    else if (b eq null) 1
    else {
      val L = math.min(a.length, b.length)
      var i = 0
      while (i < L) {
        if (a(i) < b(i)) return -1
        else if (b(i) < a(i)) return 1
        i += 1
      }
      if (L < b.length) -1
      else if (L < a.length) 1
      else 0
    }
  }

  override def compare(that: ReduceKey): Int = {
    compareBytes(this.bytes, that.bytes)
  }
}
