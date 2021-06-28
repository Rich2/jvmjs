/* Copyright 2018-21 Richard Oliver. Licensed under Apache Licence version 2.0. */
package ostrat
import collection.mutable.ArrayBuffer

/** Immutable Array based class for [[Int]]s. There are no concat methods, as Ints has no type parameter and can not be widened. */
final class Ints(val arrayUnsafe: Array[Int]) extends AnyVal with ArrImut[Int]
{ type ThisT = Ints

  /** Copy's the backing Array[[Int]] to a new Array[Int]. End users should rarely have to use this method. */
  override def unsafeNew(length: Int): Ints = new Ints(new Array[Int](length))

  override def typeStr: String = "Ints"
  override def elemsLen: Int = arrayUnsafe.length
  override def apply(index: Int): Int = arrayUnsafe(index)
  override def unsafeSetElem(i: Int, value: Int): Unit = arrayUnsafe(i) = value
  def unsafeArrayCopy(operand: Array[Int], offset: Int, copyLength: Int): Unit = { arrayUnsafe.copyToArray(arrayUnsafe, offset, copyLength); () }
  override def fElemStr: Int => String = _.toString
  /** Alias for appendInts. Functionally appends the operand Ints. */
  @inline def ++ (op: Ints): Ints = appendInts(op)
  /** Functionally appends the operand Ints. Aliased by the ++ operator. */
  def appendInts(op: Ints): Ints =
  { val newArray = new Array[Int](elemsLen + op.elemsLen)
    arrayUnsafe.copyToArray(newArray)
    op.arrayUnsafe.copyToArray(newArray, elemsLen)
    new Ints(newArray)
  }

  /** Alias for append. Functionally appends the operand Int. */
  @inline def :+(op: Int): Ints = append(op)
  /** Functionally appends the operand Int. This method by the :+ operator, rather than the +- operator alias used for append on Refs to avoid
   *  confusion with arithmetic operations. */
  def append(op: Int): Ints =
  { val newArray = new Array[Int](elemsLen + 1)
    arrayUnsafe.copyToArray(newArray)
    newArray(elemsLen) = op
    new Ints(newArray)
  }

  /** Alias for prepend. Functionally appends the operand Int. */
  @inline def +:(op: Int): Ints = prepend(op)
  /** Functionally prepends the operand Int. This alphanumeric method is not aliased with an operator to avoid confusion with numeric operators. */
  def prepend(op: Int): Ints =
  { val newArray = new Array[Int](elemsLen + 1)
    newArray(0) = op
    arrayUnsafe.copyToArray(newArray, 1)
    new Ints(newArray)
  }
}

object Ints
{ def apply(input: Int*): Ints = new Ints(input.toArray)

  implicit val showImplicit: ShowT[Ints] = ArrayLikeShow[Int, Ints](ShowT.intPersistImplicit)

  implicit val EqImplicit: EqT[Ints] = (a1, a2) =>
    if(a1.elemsLen != a2.elemsLen) false
    else
    { var count = 0
      var acc = true
      var continue = true

      while (count < a1.elemsLen & continue)
      { if (a1(count) == a2(count)) count += 1
      else {acc = false; continue = false}
      }
      acc
    }
}

object IntsBuild extends ArrTBuilder[Int, Ints] with ArrTFlatBuilder[Ints]
{ type BuffT = IntBuff
  override def newArr(length: Int): Ints = new Ints(new Array[Int](length))
  override def arrSet(arr: Ints, index: Int, value: Int): Unit = arr.arrayUnsafe(index) = value
  override def newBuff(length: Int = 4): IntBuff = new IntBuff(new ArrayBuffer[Int](length))
  override def buffGrow(buff: IntBuff, value: Int): Unit = buff.unsafeBuff.append(value)
  override def buffGrowArr(buff: IntBuff, arr: Ints): Unit = buff.unsafeBuff.addAll(arr.arrayUnsafe)
  override def buffToArr(buff: IntBuff): Ints = new Ints(buff.unsafeBuff.toArray)
}

class IntBuff(val unsafeBuff: ArrayBuffer[Int]) extends AnyVal with ArrayLike[Int]
{ override def apply(index: Int): Int = unsafeBuff(index)
  override def elemsLen: Int = unsafeBuff.length
}