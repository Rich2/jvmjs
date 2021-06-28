/* Copyright 2018-21 Richard Oliver. Licensed under Apache Licence version 2.0. */
package ostrat
import collection.mutable.ArrayBuffer

trait ArrArrayDbl[A <: ArrayDblBased] extends Any with ArrImut[A]
{ def array: Array[Array[Double]]
  def elemsLen: Int = array.length
  def unsafeFromArrayArray(array: Array[Array[Double]]): ThisT
  final def unsafeNew(length: Int): ThisT = unsafeFromArrayArray(new Array[Array[Double]](length))
  def unsafeSetElem(i: Int, value: A): Unit = array(i) = value.arrayUnsafe
}

/** This is the builder for Arrays Arrays of Double. It is not the builder for Arrays of Double.  */
trait ArrArrayDblBuild[A <: ArrayDblBased, ArrT <: ArrArrayDbl[A]] extends ArrTBuilder[A, ArrT]
{ @inline def fromArray(array: Array[Array[Double]]): ArrT
  type BuffT <: ArrayDoubleBuff[A]
  @inline override def newArr(length: Int): ArrT = fromArray(new Array[Array[Double]](length))
  override def arrSet(arr: ArrT, index: Int, value: A): Unit = arr.array(index) = value.arrayUnsafe
  //override def buffNew(length: Int = 4): DblsArrayBuff[A] = new DblsArrayBuff[A](new ArrayBuffer[Array[Double]]((length)))
  override def buffToArr(buff: BuffT): ArrT = fromArray(buff.unsafeBuff.toArray)
  override def buffGrow(buff: BuffT, value: A): Unit = { buff.unsafeBuff.append(value.arrayUnsafe); () }
  override def buffGrowArr(buff: BuffT, arr: ArrT): Unit = { buff.unsafeBuff.addAll(arr.array); () }
}

class ArrArrayDblEq[A <: ArrayDblBased, ArrT <: ArrArrayDbl[A]] extends EqT[ArrT]
{
  override def eqv(a1: ArrT, a2: ArrT): Boolean = if (a1.elemsLen != a2.elemsLen) false
    else a1.iForAll((el1, i) =>  el1.arrayUnsafe equ a2(i).arrayUnsafe)
}

object ArrArrayDblEq
{
  def apply[A <: ArrayDblBased, ArrT <: ArrArrayDbl[A]]: ArrArrayDblEq[A, ArrT] = new ArrArrayDblEq[A, ArrT]
}

/** This is a buffer class for Arrays of Double. It is not a Buffer class for Arrays. */
trait ArrayDoubleBuff[A <: ArrayDblBased] extends Any with ArrayLike[A]
{ //override def apply(index: Int): AArray[Double] = unsafeBuff(index)
  def unsafeBuff: ArrayBuffer[Array[Double]]
  override def elemsLen: Int = unsafeBuff.length
}
