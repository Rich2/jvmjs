/* Copyright 2018-20 Richard Oliver. Licensed under Apache Licence version 2.0. */
package ostrat
package geom
import pWeb._

/** Compound Circle Graphic class. */
case class CircleCompound(shape: Circle, facets: Arr[GraphicFacet], children: Arr[GraphicElem] = Arr()) extends EllipseCompound with
  CircleGraphic with AxisFree
{
  override type ThisT = CircleCompound
  override def attribs: Arr[XmlAtt] = ???

  override def rendToCanvas(cp: pCanv.CanvasPlatform): Unit = facets.foreach {
    case c: Colour => cp.circleFill(CircleFill(shape, c))
    case DrawFacet(c, w) => cp.circleDraw(shape.draw(c, w))
    case fr: FillRadial => cp.circleFillRadial(shape, fr)  
    case sf =>
  }

  override def svgElem(bounds: BoundingRect): SvgCircle = SvgCircle(shape.negY.slateXY(0, bounds.minY + bounds.maxY).
    attribs ++ facets.flatMap(_.attribs))

  /** Translate geometric transformation. */
  override def slateXY(xDelta: Double, yDelta: Double): CircleCompound =
    CircleCompound(shape.slateXY(xDelta, yDelta), facets, children.SlateXY(xDelta, yDelta))

  /** Uniform scaling transformation. The scale name was chosen for this operation as it is normally the desired operation and preserves Circles and
   * Squares. Use the xyScale method for differential scaling. */
  override def scale(operand: Double): CircleCompound = CircleCompound(shape.scale(operand), facets, children.scale(operand))

  override def prolign(matrix: ProlignMatrix): CircleCompound = CircleCompound(shape.prolign(matrix), facets, children.prolign(matrix))

  override def rotate(angle: AngleVec): CircleCompound = CircleCompound(shape.rotate(angle), facets, children.rotate(angle))

  override def reflect(lineLike: LineLike): CircleCompound = ??? //CircleCompound(shape.reflect(lineLike), facets, children.reflect(lineLike))

  override def scaleXY(xOperand: Double, yOperand: Double): EllipseCompound = ???

  override def shearX(operand: Double): EllipseCompound = ???

  override def shearY(operand: Double): EllipseCompound = ???

 // override def slateTo(newCen: Pt2): EllipseCompound = ???
}