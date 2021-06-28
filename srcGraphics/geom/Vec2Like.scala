/* Copyright 2018-21 Richard Oliver. Licensed under Apache Licence version 2.0. */
package ostrat; package geom

/** A common trait for [[Vec2]] and [[Pt2]] it would be good if the methods on this trait could be reduced.  */
trait Vec2Like extends Any with Show2Dbls with ApproxDbl
{ /** The x component of this 2D vector / point. */
  def x: Double

  /** The y component of this 2D vector / point. */
  def y: Double

  override def name1: String = "x"
  override def name2: String = "y"
  @inline override def show1: Double = x
  @inline override def show2: Double = y
  def yScale(factor: Double): Vec2Like
  def xScale(factor: Double): Vec2Like
  def xyScale(xOperand: Double, yOperand: Double): Vec2Like
  
  @inline def scale(factor: Double): Vec2Like

  /** rotates the vector 90 degrees or Pi/2 radians, anticlockwise. */
  @inline def rotate90: Vec2Like

  /** Rotates the vector 180 degrees or Pi radians. */
  @inline def rotate180: Vec2Like

  /** rotates the vector 90 degrees or Pi/2 radians, clockwise. */
  @inline def rotate270: Vec2Like

  /** Rotates this vector through the given angle around the origin. */
  def rotate(a: AngleVec): Vec2Like

  /** The dot product of this and the operand vector. */
  @inline def dot(operand: Vec2Like): Double = x * operand.x + y * operand.y

  def doublesSeq = Seq(x, y)
  def toPair: (Double, Double) = (x, y)
}