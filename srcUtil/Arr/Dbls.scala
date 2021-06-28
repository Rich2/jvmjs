/* Copyright 2018-21 Richard Oliver. Licensed under Apache Licence version 2.0. */
package ostrat
import scala.collection.mutable.ArrayBuffer

/** An immutable Array based class for Doubles. */
class Dbls(val arrayUnsafe: Array[Double]) extends AnyVal with ArrImut[Double]
{ type ThisT = Dbls
  override def typeStr: String = "Doubles"
  override def unsafeNew(length: Int): Dbls = new Dbls(new Array[Double](length))
  override def elemsLen: Int = arrayUnsafe.length
  override def apply(index: Int): Double = arrayUnsafe(index)
  override def unsafeSetElem(i: Int, value: Double): Unit = arrayUnsafe(i) = value
  def unsafeArrayCopy(operand: Array[Double], offset: Int, copyLength: Int): Unit = { arrayUnsafe.copyToArray(arrayUnsafe, offset, copyLength); () }
  override def fElemStr: Double => String = _.toString
  def ++ (op: Dbls): Dbls =
  { val newArray = new Array[Double](elemsLen + op.elemsLen)
    arrayUnsafe.copyToArray(newArray)
    op.arrayUnsafe.copyToArray(newArray, elemsLen)
    new Dbls(newArray)
  }
}

/** Companion object for the Dbls Array based class for Doubles, contains a repeat parameter factory method. */
object Dbls
{ def apply(input: Double*): Dbls = new Dbls(input.toArray)
}

object DblsBuild extends ArrTBuilder[Double, Dbls] with ArrTFlatBuilder[Dbls]
{ type BuffT = DblsBuff
  override def newArr(length: Int): Dbls = new Dbls(new Array[Double](length))
  override def arrSet(arr: Dbls, index: Int, value: Double): Unit = arr.arrayUnsafe(index) = value
  override def newBuff(length: Int = 4): DblsBuff = new DblsBuff(ArrayBuffer[Double](length))
  override def buffGrow(buff: DblsBuff, value: Double): Unit = buff.unsafeBuff.append(value)
  override def buffGrowArr(buff: DblsBuff, arr: Dbls): Unit = buff.unsafeBuff.addAll(arr.arrayUnsafe)
  override def buffToArr(buff: DblsBuff): Dbls = new Dbls(buff.unsafeBuff.toArray)
}

class DblsBuff(val unsafeBuff: ArrayBuffer[Double]) extends AnyVal with ArrayLike[Double]
{ override def apply(index: Int): Double = unsafeBuff(index)
  override def elemsLen: Int = unsafeBuff.length
}