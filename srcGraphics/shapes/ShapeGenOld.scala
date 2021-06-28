/* Copyright 2018-21 Richard Oliver. Licensed under Apache Licence version 2.0. */
package ostrat; package geom
import Colour.Black

/** The generalised implementation of a [[Shape]]. A closed sequence of curve segments. An Array[Double] based collection for a sequence of CurveSegs,
 *  similar to a Polygon which is an Array[Double based collection of just LineSegs. It Uses 6 Doubles for each CurveSeg. The first Double of each
 *  curveSeg is set to Negative Infinity for a LineSeg positive infinity for an ArcSeg, but represents the x component of the first control point for
 *  a BezierSeg. */
class ShapeGenOld(val arrayUnsafe: Array[Double]) extends Dbl7sArr[CurveTail] with AffinePreserve
{ type ThisT = ShapeGenOld
  def unsafeFromArray(array: Array[Double]): ShapeGenOld = new ShapeGenOld(array)
  override def typeStr = "Shape"
  override def fElemStr: CurveTail => String = _.toString
  override def newElem(iMatch: Double, d1: Double, d2: Double, d3: Double, d4: Double, d5: Double, d6: Double): CurveTail =
    CurveTail(iMatch, d1, d2, d3, d4, d5, d6)

  def ptsTrans(f: Pt2 => Pt2): ShapeGenOld =
  { val newArray = new Array[Double](elemsLen * 7)
    def setMiddle(offset: Int): Unit =
    { val newMiddle: Pt2 = f(arrayUnsafe(offset + 3) pp arrayUnsafe(offset + 4))
      newArray(offset + 3) = newMiddle.x
      newArray(offset + 4) = newMiddle.y
    }

    def setEnd(offset: Int): Unit =
    { val newEnd: Pt2 = f(arrayUnsafe(offset + 5) pp arrayUnsafe(offset + 6))
      newArray(offset + 5) = newEnd.x
      newArray(offset + 6) = newEnd.y
    }

    (0 until elemsLen).foreach{ index =>
      val offset = index * 7
      arrayUnsafe(offset) match
      {
        case 10 =>
        { newArray(offset) = 10
          setEnd(offset)
        }

        case 11 =>
        { newArray(offset) = 11
          setMiddle(offset)
          setEnd(offset)
        }

        case 12 =>
        { newArray(offset) = 12
          val newControl1: Pt2 = f(arrayUnsafe(offset + 1) pp arrayUnsafe(offset + 2))
          newArray(offset + 1) = newControl1.x
          newArray(offset + 2) = newControl1.y
          setMiddle(offset)
          setEnd(offset)
        }

        case n => excep("iMatch in LineSeg has value: " + n.toString + " Must be 10, 11 0r 12.")
      }
    }
    new ShapeGenOld(newArray)
  }

  def fill(colour: Colour): PolyCurveFill = PolyCurveFill(this, colour)
  def draw(lineColour: Colour = Black, lineWidth: Double = 2.0): PolyCurveDraw = PolyCurveDraw(this, lineColour, lineWidth)

  def shapeAll(shape: ShapeGenOld, evObj: AnyRef, fillColour: Colour, str: String, fontSize: Int = 24, lineWidth: Double = 2, lineColour: Colour = Black):
    PolyCurveAll = PolyCurveAll(shape, evObj, str, fillColour, fontSize, lineColour, lineWidth)

  def fillScale(colour: Colour, factor: Double): PolyCurveFill = PolyCurveFill(this.scale(factor), colour)
  def fillScaleSlate(colour: Colour, factor: Double, offset: Pt2): PolyCurveFill = PolyCurveFill(this.scale(factor).slate(offset), colour)

  /** Not sure if this method should be a member of Transable */
  def boundingRect =
  { //val t = Arc()
    var minX, maxX, minY, maxY = 0.0
    var i = 0
    this.foreach {ss =>
      val v = ss.pEnd
      if (i == 0)
      { minX = v.x
        maxX = v.x
        minY = v.y
        maxY = v.y
      }
      else
      { minX = minX.min(v.x)
        maxX = maxX.max(v.x)
        minY = minY.min(v.y)
        maxY = maxY.max(v.y)
      }
      i += 1
    }
    if (i == 0) throw new Exception("boundingRect method called on empty Vec2 collection") else {}
    BoundingRect(minX, maxX, minY, maxY)
  }
  def ptInShape: Pt2 => Boolean = pt =>  pMap[Pt2, PolygonImp](_.pEnd).ptInside(pt)

  /** Not sure if this is useful */
  def tailForeach(fLineSeg: CurveTail => Unit, fArcSeg: CurveTail => Unit, fBezierSeg: CurveTail => Unit): Unit =
    foreach(_.segDo(fLineSeg, fArcSeg, fBezierSeg))
}

object ShapeGenOld extends Dbl7sArrCompanion[CurveTail, ShapeGenOld]
{ implicit val factory: Int => ShapeGenOld = i => new ShapeGenOld(new Array[Double](i * 7))
}