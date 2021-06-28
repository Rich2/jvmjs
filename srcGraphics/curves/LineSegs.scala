/* Copyright 2018-21 Richard Oliver. Licensed under Apache Licence version 2.0. */
package ostrat; package geom
import collection.mutable.ArrayBuffer

/** Compact immutable Array[Double] based collection class for [[LineSeg]]s. LineSeg is the library's term for a mathematical straight line segment, but what in
 *  common parlance is often just referred to as a line. */
class LineSegs(val arrayUnsafe: Array[Double]) extends Dbl4sArr[LineSeg] with AffinePreserve
{ type ThisT = LineSegs
  def unsafeFromArray(array: Array[Double]): LineSegs = new LineSegs(array)
  override def typeStr: String = "Line2s"
  override def fElemStr: LineSeg => String = _.str
  //override def toString: String = Line2s.PersistImplict.show(this)
  override def newElem(d1: Double, d2: Double, d3: Double, d4: Double): LineSeg = new LineSeg(d1, d2, d3, d4)
  override def ptsTrans(f: Pt2 => Pt2): LineSegs = map(orig => LineSeg(f(orig.pStart), f(orig.pEnd)))

  def ptInPolygon(pt: Pt2): Boolean =
  { val num = foldLeft(0)((acc, line) => acc + ife(line.rayIntersection(pt), 1, 0))
    num.isOdd
  }

  def draw(lineWidth: Double, colour: Colour = Colour.Black): LinesDraw = LinesDraw(this, lineWidth, colour)
}

/** Companion object for the LineSegs class. */
object LineSegs extends Dbl4sArrCompanion[LineSeg, LineSegs]
{
  implicit val factory: Int => LineSegs = i => new LineSegs(new Array[Double](i * 4))

  implicit val persistImplicit: ArrProdDbl4Persist[LineSeg, LineSegs] = new ArrProdDbl4Persist[LineSeg, LineSegs]("Line2s")
  { override def fromArray(value: Array[Double]): LineSegs = new LineSegs(value)

    override def showT(obj: LineSegs, way: Show.Way, maxPlaces: Int, minPlaces: Int): String = ???
  }

  implicit val arrArrBuildImplicit: ArrTFlatBuilder[LineSegs] = new Dbl4sArrFlatBuilder[LineSeg, LineSegs]
  { type BuffT = Line2sBuff
    override def fromDblArray(array: Array[Double]): LineSegs = new LineSegs(array)
    def fromDblBuffer(inp: ArrayBuffer[Double]): Line2sBuff = new Line2sBuff(inp)
  }

  implicit val transImplicit: AffineTrans[LineSegs] = (obj, f) => obj.map(_.ptsTrans(f))
}

/** Efficient expandable buffer for Line2s. */
class Line2sBuff(val buffer: ArrayBuffer[Double]) extends AnyVal with Dbl4sBuffer[LineSeg]
{ override def dblsToT(d1: Double, d2: Double, d3: Double, d4: Double): LineSeg = new LineSeg(d1, d2, d3, d4)
}