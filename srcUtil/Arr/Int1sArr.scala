/* Copyright 2018-21 Richard Oliver. Licensed under Apache Licence version 2.0. */
package ostrat

/** An object that can be constructed from a single [[Int]]. These are used in [[Int1sArr]] Array[Int] based collections. */
trait Int1Elem extends Any with IntNElem
{ def intValue: Int
  @inline def int1 : Int = intValue
}

/** A specialised immutable, flat Array[Int] based collection of a type of [[Int1Elem]]s. */
trait Int1sArr[A <: Int1Elem] extends Any with IntNsArr[A]
{
  final override def elemProductNum: Int = 1
  def newElem(intValue: Int): A
  final override def apply(index: Int): A = newElem(arrayUnsafe(index))
  final override def unsafeSetElem(index: Int, elem: A): Unit = arrayUnsafe(index) = elem.intValue

  /** This method could be made more general. */
  def findIndex(value: A): OptInt =
  {
    var count = 0
    var acc: OptInt = NoInt
    var continue = true
    while (continue == true & count < elemsLen)
    {
      if (value.intValue == arrayUnsafe(count))
      { acc = SomeInt(count)
        continue = false
      }
      count += 1
    }
    acc
  }

  /** Functionally appends the operand of type A. This alphanumeric method is not aliased by the ++ operator, to avoid confusion with numeric operators. */
  def append(op: A): ThisT =
  { val newArray = new Array[Int](elemsLen + 1)
    arrayUnsafe.copyToArray(newArray)
    newArray(elemsLen) = op.int1
    unsafeFromArray(newArray)
  }
}

/** Trait for creating the ArrTBuilder type class instances for [[Int1Arr]] final classes. Instances for the [[ArrTBuilder]] type
 *  class, for classes / traits you control, should go in the companion object of B. The first type parameter is called B, because to corresponds to
 *  the B in ```map(f: A => B): ArrB``` function. */
trait Int1sArrBuilder[A <: Int1Elem, ArrT <: Int1sArr[A]] extends IntNsArrBuilder[A, ArrT]
{ type BuffT <: Int1sBuffer[A, ArrT]

  final override def elemSize: Int = 1
  def newArray(length: Int): Array[Int] = new Array[Int](length)
  final override def arrSet(arr: ArrT, index: Int, value: A): Unit =  arr.arrayUnsafe(index) = value.int1
  override def buffGrow(buff: BuffT, value: A): Unit = { buff.buffer.append(value.int1); () }
}

/** Trait for creating the ArrTBuilder and ArrTFlatBuilder type class instances for [[Int1Arr]] final classes. Instances for the [[ArrTBuilder]] type
 *  class, for classes / traits you control, should go in the companion object of B. Instances for [[ArrTFlatBuilder] should go in the companion
 *  object the ArrT final class. The first type parameter is called B, because to corresponds to the B in ```map(f: A => B): ArrB``` function. */
trait Int1sArrFlatBuilder[A <: Int1Elem, ArrT <: Int1sArr[A]] extends IntNsArrFlatBuilder[A, ArrT]
{ type BuffT <: Int1sBuffer[A, ArrT]

  final override def elemSize: Int = 1
  def newArray(length: Int): Array[Int] = new Array[Int](length)
  //final override def arrSet(arr: ArrT, index: Int, value: A): Unit =  arr.arrayUnsafe(index) = value.int1
  //override def buffGrow(buff: BuffT, value: A): Unit = { buff.buffer.append(value.int1); () }
}

/** A specialised flat ArrayBuffer[Int] based trait for [[Int1Elem]]s collections. */
trait Int1sBuffer[A <: Int1Elem, M <: Int1sArr[A]] extends Any with IntNsBuffer[A]
{ type ArrT <: Int1sArr[A]
  def intToT(value: Int): A
  def apply(i1: Int): A = intToT(buffer(i1))
  override def elemSize: Int = 1
  override def grow(newElem: A): Unit = { buffer.append(newElem.int1); () }
}