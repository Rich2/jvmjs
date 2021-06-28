/* Copyright 2018-21 Richard Oliver. Licensed under Apache Licence version 2.0. */
package ostrat; package geom
import Colour.Black, pWeb._

/** A mathematical closed polygon. The general case can be instantiated with [[PolygonImp]], but it provides the interface for particular sub sets of
 *  polygons such as triangles and square. Mathematically a closed polygon made up of straight line segments. The default convention is to number the
 *  vertices in a clockwise direction, with vertex 1 the first vertex that is clockwise from 12 O'Clock. Sides are numbered in a corresponding manner
 *  with then end point of side n sdn at vertex n. */
trait Polygon extends Shape with BoundedElem with Approx[Double]
{
  /** The vertices of this Polygon in an Array of [[Double]]s. */
  def vertsArray: Array[Double]

  /** The X component of the vertices of this Polygon in an Array of [[Double]]s. */
  def vertsArrayX: Array[Double]

  /** The Y component of the vertices of this Polygon in an Array of [[Double]]s. */
  def vertsArrayY: Array[Double]

  /** Performs the side effecting function on the [[Pt2]] value of each vertex. */
  def foreachVert[U](f: Pt2 => U): Unit

  /** Performs the side effecting function on the [[Pt2]] value of each vertex, excluding vertex v1. */
  def foreachVertTail[U](f: Pt2 => U): Unit

  /** Foreach vertex excluding vertex 1, perform the side effecting function on the Tuple2 of the x and y values of the vertex. */
  def foreachVertPairTail[U](f: (Double, Double) => U): Unit

  /** A function that takes a 2D geometric transformation on a [[Pt2]] as a parameter and performs the transformation on all the vertices returning a
   * new transformed Polygon */
  def vertsTrans(f: Pt2 => Pt2): Polygon = vertsMap(f).toPolygon

  def vertsMap[A, ArrT <: ArrImut[A]](f: Pt2 => A)(implicit build: ArrTBuilder[A, ArrT]): ArrT =
  { val acc = build.newBuff()
    foreachVert{ v => build.buffGrow(acc, f(v)) }
    build.buffToArr(acc)
  }

  /** flatMap with index to an immutable Arr. */
  def vertsIFlatMap[BB <: ArrImut[_]](iInit: Int = 0)(f: (Pt2, Int) => BB)(implicit build: ArrTFlatBuilder[BB]): BB =
  { val buff: build.BuffT = build.newBuff()
    var count: Int = iInit
    foreachVert { v =>
     val newElems = f(v, count)
      build.buffGrowArr(buff, newElems)
      count += 1
    }
    build.buffToArr(buff)
  }

  def vertsFoldLeft[B](initial: B)(f: (B, Pt2) => B): B =
  { var acc: B = initial
    foreachVert{ v => acc = f(acc, v) }
    acc
  }

  /** Returns the vertex of the given index. Throws if the index is out of range, if it less than 1 or greater than the number of vertices. */
  def vert(index: Int): Pt2

  @inline def side(index: Int): LineSeg = LineSeg(ife(index == 1, vLast, vert(index - 1)), vert(index))

  /** foreachs over the sides or edges of the Polygon These are of type [[LineSeg]]. */
  def sideForeach(f: LineSeg => Unit): Unit =
  { var count = 1
    while (count <= vertsNum) { f(side(count)); count += 1 }
  }

  /** foreachs over the sides or edges of the Polygon These are of type [[LineSeg]]. */
  def sideIForeach(initCount: Int = 0)(f: (LineSeg, Int) => Unit): Unit =
  { var count = 1
    while (count <= vertsNum)
    { f(side(count), count + initCount)
      count += 1
    }
  }

  /** maps over the sides or edges of the Polygon These are of type [[LineSeg]]. */
  def sidesMap[A, AA <: ArrImut[A]](f: LineSeg => A)(implicit build: ArrTBuilder[A, AA]): AA =
  { var count = 0
    val res = build.newArr(vertsNum)
    while (count < vertsNum)
    { res.unsafeSetElem(count, f(side(count + 1)))
      count += 1
    }
    res
  }

  /** maps with a integer counter over the sides or edges of the Polygon These are of type [[LineSeg]]. */
  def sidesIMap[A, AA <: ArrImut[A]](initCount: Int = 0)(f: (LineSeg, Int) => A)(implicit build: ArrTBuilder[A, AA]): AA =
  { var count = 0
    val res = build.newArr(vertsNum)
    while (count < vertsNum)
    { res.unsafeSetElem(count, f(side(count + 1), count + initCount))
      count += 1
    }
    res
  }

  /** maps with a integer counter over the sides or edges of the Polygon These are of type [[LineSeg]]. */
  def sidesIFlatMap[AA <: ArrImut[_]](initCount: Int = 0)(f: (LineSeg, Int) => AA)(implicit build: ArrTFlatBuilder[AA]): AA =
  { var count = initCount
    val buff = build.newBuff()
    sideForeach { side =>
      val newElems = f(side, count)
      build.buffGrowArr(buff, newElems)
      count += 1
    }
    build.buffToArr(buff)
  }

  override def attribs: Arr[XANumeric] = ???
  override def fill(fillColour: Colour): PolygonFill = PolygonFill(this, fillColour)
  override def fillInt(intValue: Int): PolygonFill = PolygonFill(this, Colour(intValue))
  override def draw(lineColour: Colour = Black, lineWidth: Double = 2): PolygonDraw = PolygonDraw(this, lineWidth, lineColour)

  override def fillDraw(fillColour: Colour, lineColour: Colour, lineWidth: Double): PolygonCompound =
    PolygonCompound(this, Arr(fillColour, DrawFacet(lineColour, lineWidth)))

  /** The number of vertices and also the number of sides in this Polygon. */
  def vertsNum: Int

  /** Returns the X component of the vertex of the given number. Will throw an exception if the vertex index is out of range. */
  def xVert(index: Int): Double

  /** Returns the Y component of the vertex of the given number. Will throw an exception if the vertex index is out of range. */
  def yVert(index: Int): Double

  /** The X component of the 1st vertex, will throw on a 0 vertices polygon. */
  def v1x: Double

  /** The Y component of the 1st vertex, will throw on a 0 vertices polygon. */
  def v1y: Double

  /** The 1st vertex, will throw on a 0 vertices polygon. */
  def v1: Pt2

  /** The last vertex will throw an exception on a 0 vertices polygon. */
  def vLast: Pt2 = vert(vertsNum)

  /** Currently throws, not sure if that is the correct behaviour. Creates a bounding rectangle for a collection of 2d points */
  override def boundingRect: BoundingRect =
  { var minX, maxX = v1x
    var minY, maxY = v1y
    foreachVertTail{v =>
      minX = minX.min(v.x)
      maxX = maxX.max(v.x)
      minY = minY.min(v.y)
      maxY = maxY.max(v.y)
    }
    BoundingRect(minX, maxX, minY, maxY)
  }

  @inline def polygonMap(f: Pt2 => Pt2): Polygon = vertsMap(f).toPolygon

  /** Translate geometric transformation on a Polygon returns a Polygon. The return type of this method will be narrowed  further in most descendant
   *  traits / classes. The exceptions being those classes where the centring of the geometry at the origin is part of the type. */
  override def slateXY(xDelta: Double, yDelta: Double): Polygon = polygonMap(_.addXY(xDelta, yDelta))

  /** Translate geometric transformation on a Polygon returns a Polygon. The return type of this method will be narrowed further in most descendant
   *  traits / classes. The exceptions being those classes where the centring of the geometry at the origin is part of the type. */
  def slate(offset: Vec2Like): Polygon = polygonMap(_.slate(offset))

  /** Uniform scaling against both X and Y axes transformation on a polygon returning a Polygon. Use the xyScale method for differential scaling. The
   *  return type of this method will be narrowed further in descendant traits / classes. */
  override def scale(operand: Double): Polygon = polygonMap(_.scale(operand))

  /** Mirror, reflection transformation of a Polygon across the X axis, returns a Polygon. */
  override def negY: Polygon = polygonMap(_.negY)

  /** Mirror, reflection transformation of Polygon across the Y axis, returns a Polygon. */
  override def negX: Polygon = polygonMap(_.negX)

  /** Prolign 2d transformations, similar transformations that retain alignment with the axes. */
  override def prolign(matrix: ProlignMatrix): Polygon = polygonMap(_.prolign(matrix))

  override def rotate90: Polygon = polygonMap(_.rotate90)
  override def rotate180: Polygon = polygonMap(_.rotate180)
  override def rotate270: Polygon = polygonMap(_.rotate270)

  /** Rotation 2D geometric transformation on a Polygon, taking the rotation as a scalar measured in radians, returns a Polygon. The Return type will
   *  be narrowed in some but not all sub traits / classes. */
  override def rotate(angle: AngleVec): Polygon = polygonMap(_.rotate(angle))

  /** Reflect 2D geometric transformation across a line, line segment or ray on a polygon, returns a Polygon. The Return type will be narrowed in sub
   *  traits / classes. */
  override def reflect(lineLike: LineLike): Polygon = polygonMap(_.reflect(lineLike))

  /** XY scaling 2D geometric transformation on a Polygon returns a Polygon. This allows different scaling factors across X and Y dimensions. The
   *  return type will be narrowed in some, but not all descendant Polygon types. */
  override def scaleXY(xOperand: Double, yOperand: Double): Polygon = polygonMap(_.xyScale(xOperand, yOperand))

  /** Shear 2D geometric transformation along the X Axis on a Polygon, returns a Polygon. The return type will be narrowed in some but not all sub
   *  classes and traits. */
  override def shearX(operand: Double): Polygon = polygonMap(_.xShear(operand))

  /** Shear 2D geometric transformation along the Y Axis on a Polygon, returns a Polygon. The return type will be narrowed in sub classes and
   *  traits. */
  override def shearY(operand: Double): Polygon = polygonMap(_.xShear(operand))

  /** Converts this closed Polygon to LineSegs. The LineSegs collection is empty of there are less than 2 vertices. */
  def toLineSegs: LineSegs = if (vertsNum > 1)
  { val res: LineSegs = LineSegs(vertsNum)
    res.unsafeSetElem(0, LineSeg(vert(vertsNum), vert(1)))
    iUntilForeach(1, vertsNum){i => res.unsafeSetElem(i, LineSeg(vert(i), vert(i + 1))) }
    res
  }
  else LineSegs()

  /** Determines if the parameter point lies inside this Polygon. */
  def ptInside(pt: Pt2): Boolean = toLineSegs.ptInPolygon(pt)

  /** The centre point of this Polygon. The default centre for Polygons is the centre of the bounding rectangle. */
  def cenPt: Pt2 = boundingRect.cen
  def cenVec: Vec2 = boundingRect.cenVec

  def sline(index: Int): LineSeg =
  { val startVertNum: Int = ife(index == 1, vertsNum, index - 1)
    LineSeg(vert(startVertNum), vert(index))
  }

  def active(id: Any): PolygonActive = PolygonActive(this, id)
  def activeChildren(id: Any, children: GraphicElems): PolygonCompound = PolygonCompound(this, Arr(), active(id) +: children)

  def fillActive(fillColour: Colour, pointerID: Any): PolygonCompound =
    PolygonCompound(this, Arr(fillColour), Arr(PolygonActive(this, pointerID)))

  def drawActive(lineColour: Colour = Black, lineWidth: Double = 2, pointerID: Any): PolygonCompound =
    PolygonCompound(this, Arr(DrawFacet(lineColour, lineWidth)), Arr(PolygonActive(this, pointerID)))

  /** Creates a PolygonCompound graphic that is active with a simple 1 colour fill and has a draw graphic for the Polygon. The default values for the
   * draw area line width of 2 and a colour of Black. */
  def fillActiveDraw(fillColour: Colour, pointerID: Any, lineColour: Colour = Black, lineWidth: Double = 2): PolygonCompound =
    PolygonCompound(this, Arr(fillColour, DrawFacet(lineColour, lineWidth)), Arr(PolygonActive(this, pointerID)))

  def fillDrawActive(fillColour: Colour, pointerID: Any, lineWidth: Double, lineColour: Colour = Black): PolygonCompound =
    PolygonCompound(this, Arr(fillColour, DrawFacet(lineColour, lineWidth)), Arr(PolygonActive(this, pointerID)))

  def fillDrawText(fillColour: Colour, str: String, fontSize: Int = 24, lineColour: Colour = Black, lineWidth: Double = 2.0): PolygonCompound =
    PolygonCompound(this, Arr(fillColour, DrawFacet(lineColour, lineWidth)), Arr(TextGraphic(str, fontSize, cenDefault)))

  def parentFillText(pointerID: Any, fillColour: Colour, str: String, fontSize: Int = 10, textColour: Colour = Black, align: TextAlign = CenAlign):
  PolygonCompound = PolygonCompound(this, Arr(fillColour, TextFacet(str, textColour)), Arr())

  def fillDrawTextActive(fillColour: Colour, pointerID: Any, str: String, fontSize: Int = 24, lineWidth: Double, lineColour: Colour = Black,
    align: TextAlign = CenAlign): PolygonCompound = PolygonCompound(this, Arr(fillColour, DrawFacet(lineColour, lineWidth)),
    Arr(TextGraphic(str, fontSize, cenDefault, Black, align), PolygonActive(this, pointerID)))

  def fillText(fillColour: Colour, str: String, fontSize: Int = 10, textColour: Colour = Black, layer: Int = 0): PolygonCompound =
    PolygonCompound(this, Arr(fillColour), Arr(TextGraphic(str, fontSize, cenDefault, textColour)))

  def fillTextActive(fillColour: Colour, pointerEv: Any, str: String, fontSize: Int = 24, fontColour: Colour = Black, align: TextAlign = CenAlign):
    PolygonCompound = PolygonCompound(this, Arr(fillColour), Arr(PolygonActive(this, pointerEv), TextGraphic(str, fontSize, cenDefault, fontColour, align)))

  /** Insert vertex. */
  def insVert(insertionPoint: Int, newVec: Pt2): Polygon =
  { val res = PolygonImp.uninitialised(vertsNum + 1)
    (0 until insertionPoint).foreach(i => res.unsafeSetElem(i, vert(i)))
    res.unsafeSetElem(insertionPoint, newVec)
    (insertionPoint until vertsNum).foreach(i => res.unsafeSetElem(i + 1, vert(i)))
    res
  }

  /** Insert vertices before the specified insertion vertex. */
  def insVerts(insertionPoint: Int, newVecs: Pt2 *): Polygon =
  { val res = PolygonImp.uninitialised(vertsNum + newVecs.length)
    (1 until insertionPoint).foreach(i => res.unsafeSetElem(i - 1, vert(i)))
    newVecs.iForeach((elem, i) => res.unsafeSetElem(insertionPoint + i -1, elem))
    (insertionPoint until vertsNum + 1).foreach(i => res.unsafeSetElem(i + newVecs.length -1, vert(i)))
    res
  }

  def precisionDefault: Double = ???

  override def approx(that: Any, precision: Double): Boolean = ???
}

/** Companion object for the Polygon trait, contains factory apply methods and implicit instances for all 2D affine geometric transformations. */
object Polygon
{
  def apply(pts: Pt2 *): Polygon = PolygonImp(pts: _*)

  def uninitialised(length: Int): Polygon = new PolygonImp(new Array[Double](length * 2))

  implicit val eqImplicit: EqT[Polygon] = (p1, p2) => EqT.arrayImplicit[Double].eqv(p1.vertsArray, p2.vertsArray)

  implicit val slateImplicit: Slate[Polygon] = (obj: Polygon, dx: Double, dy: Double) => obj.slateXY(dx, dy)
  implicit val scaleImplicit: Scale[Polygon] = (obj: Polygon, operand: Double) => obj.scale(operand)
  implicit val rotateImplicit: Rotate[Polygon] = (obj: Polygon, angle: AngleVec) => obj.rotate(angle)
  implicit val prolignImplicit: Prolign[Polygon] = (obj, matrix) => obj.prolign(matrix)
  implicit val XYScaleImplicit: ScaleXY[Polygon] = (obj, xOperand, yOperand) => obj.scaleXY(xOperand, yOperand)
  implicit val reflectImplicit: Reflect[Polygon] = (obj: Polygon, lineLike: LineLike) => obj.reflect(lineLike)

  implicit val reflectAxesImplicit: TransAxes[Polygon] = new TransAxes[Polygon]
  { override def negYT(obj: Polygon): Polygon = obj.negY
    override def negXT(obj: Polygon): Polygon = obj.negX
    override def rotate90(obj: Polygon): Polygon = obj.rotate90
    override def rotate180(obj: Polygon): Polygon = obj.rotate180
    override def rotate270(obj: Polygon): Polygon = obj.rotate270
  }
  
  implicit val shearImplicit: Shear[Polygon] = new Shear[Polygon]
  { override def shearXT(obj: Polygon, yFactor: Double): Polygon = obj.shearX(yFactor)
    override def shearYT(obj: Polygon, xFactor: Double): Polygon = obj.shearY(xFactor)
  }
}