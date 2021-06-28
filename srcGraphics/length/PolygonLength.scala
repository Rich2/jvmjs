/* Copyright 2018-21 Richard Oliver. Licensed under Apache Licence version 2.0. */
package ostrat; package geom

trait PolygonLength extends Any

/* A polygon using distances measured in metres rather than scalars. */
final class PolygonM(val arrayUnsafe: Array[Double]) extends AnyVal with PolygonLength with Dbl2sArr[Pt2M]
{ type ThisT = PolygonM
  def unsafeFromArray(array: Array[Double]): PolygonM = new PolygonM(array)
  override def typeStr: String = "PolygonMs"
  override def elemBuilder(d1: Double, d2: Double): Pt2M = new Pt2M(d1, d2)
  override def fElemStr: Pt2M => String = _.str
}

/** The companion object for PolygonDist. Provides an implicit builder. */
object PolygonM extends Dbl2sArrCompanion[Pt2M, PolygonM]
{ override def fromArrayDbl(array: Array[Double]): PolygonM = new PolygonM(array)

  implicit val persistImplicit: Dbl2sArrPersist[Pt2M, PolygonM] = new Dbl2sArrPersist[Pt2M, PolygonM]("PolygonMs")
  { override def fromArray(value: Array[Double]): PolygonM = new PolygonM(value)
  }
}

/* A polygon using distances measured in metres rather than scalars. */
final class PolygonKMs(val arrayUnsafe: Array[Double]) extends AnyVal with PolygonLength