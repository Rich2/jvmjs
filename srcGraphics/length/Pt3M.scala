/* Copyright 2018-21 Richard Oliver. Licensed under Apache Licence version 2.0. */
package ostrat; package geom
import math._, collection.mutable.ArrayBuffer

/** 3 dimensional point specified using metres as units rather than pure numbers. */
final class Pt3M(val xMetres: Double, val yMetres: Double, val zMetres: Double) extends Dbl3Elem
{ def typeStr: String = "Metres3"
  override def toString: String = typeStr.appendParenthSemis(xMetres.toString, yMetres.toString, zMetres.toString)
  //override def canEqual(other: Any): Boolean = other.isInstanceOf[Metres3]
  def dbl1 = xMetres
  def dbl2 = yMetres
  def dbl3 = zMetres
  def x: Metres = Metres(xMetres)
  def y: Metres = Metres(yMetres)
  def z: Metres = Metres(zMetres)

  /** Produces the dot product of this 2 dimensional distance Vector and the operand. */
  @inline def dot(operand: Pt3M): Area = x * operand.x + y * operand.y + z * operand.z
  def xy: Pt2M = new Pt2M(xMetres, yMetres)
  def xPos: Boolean = x.pos
  def xNeg: Boolean = x.neg
  def yPos: Boolean = y.pos
  def yNeg: Boolean = y.neg
  def zPos: Boolean = z.pos
  def zNeg: Boolean = z.neg
  def ifZPos[A](vPos: => A, vNeg: => A): A = ife(zPos, vPos, vNeg)
  def / (operator: Metres): Pt3 = Pt3(x / operator, y / operator, z / operator)

  /** Converts this Metres3 point to a Some[Metres2] point of the X and Y values, returns None if the Z value is negative. */
  def toXYIfZPositive: Option[Pt2M] = ifZPos(Some(Pt2M(x, y)), None)

  /** Rotate this 3D point defined in metres around the X Axis by the given parameter given in radians. Returns a new [[Pt3M]] point. */
  def xRotateRadians(rotationRadians: Double): Pt3M =
  { val scalar: Metres = Metres(sqrt(y.metres * y.metres + z.metres * z.metres))
    if(scalar > EarthEquatorialRadius * 1.05) throw excep("scalar: " + scalar.toString)

    val ang0 = ife2(//As y and z are both negative, the atan will give a positive value added to -Pi gives range -Pi / 2 to - Pi
      z.neg && y.neg, atan(y / z) - Pi,
      z.neg,          Pi + atan(y / z), //The atan will give a negative value. Added to Pi gives a range Pi/2 to Pi
                      atan(y / z))//This operates on the standard atan range -Pi/2 to pi/2

    val ang1 = ang0 + rotationRadians
    Pt3M(x, sin(ang1) * scalar, cos(ang1) * scalar)
  }
}

/** Companion object for the Metres3 class. */
object Pt3M
{
  def metres(xMetres: Double, yMetres: Double, zMetres: Double): Pt3M = new Pt3M(xMetres, yMetres, zMetres)
  def apply(x: Metres, y: Metres, z: Metres): Pt3M = new Pt3M(x.metres, y.metres, z.metres)
  //implicit object Metres3Persist extends Persist3[Metres, Metres, Metres, Metres3]("Metres3", "x", _.x, "y", _.y, "z", _.z, apply)
  var counter = 0

  implicit val builderImplicit: Dbl3sArrBuilder[Pt3M, Pt3MArr] = new Dbl3sArrBuilder[Pt3M, Pt3MArr]
  { type BuffT = Pt3MBuff
    override def fromDblArray(array: Array[Double]): Pt3MArr = new Pt3MArr(array)
    def fromDblBuffer(inp: ArrayBuffer[Double]): Pt3MBuff = new Pt3MBuff(inp)
  }
}
