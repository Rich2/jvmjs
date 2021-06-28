/* Copyright 2018-21 Richard Oliver. Licensed under Apache Licence version 2.0. */
package ostrat; package geom

/** Extension methods class for Int, for the geom package. */
class IntGeomImplicit(thisInt: Int)
{
  /** Succinct syntax for creating 2 dimensional points [[Pt2]]s, from 2 numbers. Note the low precedence of this method relative to most numerical
   * operators. A third number as example {{{3.1 pp 4 pp -7.25}}} can be used to create a [[Pt3]]. */
  @inline def pp(y: Double): Pt2 = Pt2(thisInt, y)

  /** Succinct syntax for creating 2 dimensional vectors, [[Vec2]]s from 2 numbers. Note the low precedence of this method relative to most numerical
      operators. A third number as example {{{3.1 vv 4 vv -7.25}}} can be used to create a [[Vec3]]. */
  @inline def vv(y: Double): Vec2 = Vec2(thisInt, y)

  def ° : Angle = Angle(thisInt)
  def km: Metres = Metres(thisInt * 1000)
  def metre: Metres = Metres(thisInt)
  @inline def miles: Metres = Metres(thisInt * 1609.344)
  @inline def millionMiles: Metres = thisInt.miles * 1000000
  def * (operator: Metres): Metres = Metres(thisInt * operator.metres)

  /** Converts this Int into an absolute angle of the given degrees from 0 until 360 degrees. */
  def angle: Angle = Angle(thisInt)

  /** Converts this Int into an [[AngleVec]] an angle of rotation for any positive or negative value of Int. */
  def degs: AngleVec = AngleVec(thisInt)

  def ll (longDegs: Double): LatLong = LatLong.degs(thisInt, longDegs)
  def east: Longitude = Longitude.degs(thisInt)
  def west: Longitude = Longitude.degs(-thisInt)
  def north: Latitude = Latitude.apply(thisInt)
  def south: Latitude = Latitude.apply(-thisInt)
}

/** Extension methods class for [[Double]], for the geom package. */
class DoubleImplicitGeom(thisDouble: Double)
{
  /** Succinct syntax for creating 2 dimensional points [[Pt2]]s, from 2 numbers. Note the low precedence of this method relative to most numerical
   *  operators. A third number as example {{{3.1 pp 4 pp -7.25}}} can be used to create a [Pt3]. */
  @inline def pp(y: Double): Pt2 = Pt2(thisDouble, y)

  /** Succinct syntax for creating 2 dimensional vectors, [[Vec2]]s from 2 numbers. Note the low precedence of this method relative to most numerical
   *  operators. A third number as example {{{3.1 pp 4 pp -7.25}}} can be used to create a [Pt3]. */
  @inline def vv(y: Double): Vec2 = Vec2(thisDouble, y)

  def km: Metres = Metres(thisDouble * 1000)
  def metre: Metres = Metres(thisDouble)
  def * (operator: Metres): Metres = Metres(thisDouble * operator.metres)
  @inline def miles: Metres = Metres(thisDouble * 1609.344)
  @inline def millionMiles: Metres = thisDouble.miles * 1000000
  def radians: Angle = Angle.radians(thisDouble)

  /** Converts this Double into an absolute angle of the given degrees from 0 until 360 degrees. */
  def angle: Angle = Angle(thisDouble)

  /** Converts this Double into an [[AngleVec]] an angle of rotation from - infinity to + infinity. */
  def degs: AngleVec = AngleVec(thisDouble)

  /** Creates a [[LatLong]], a position of Latitude and Longitude from 2 numbers. */
  def ll (longDegs: Double): LatLong = LatLong.degs(thisDouble, longDegs)

  def east: Longitude = Longitude.degs(thisDouble)
  def west: Longitude = Longitude.degs(-thisDouble)
  def north: Latitude = Latitude.apply(thisDouble)
  def south: Latitude = Latitude.apply(-thisDouble)
  def * (operand: Pt2): Pt2 = new Pt2(thisDouble * operand.x, thisDouble * operand.y)
  def * (operand: Vec2): Vec2 = new Vec2(thisDouble * operand.x, thisDouble * operand.y)
  def metres: Metres = new Metres(thisDouble)
}