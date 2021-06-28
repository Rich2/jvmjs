/* Copyright 2018-20 Richard Oliver. Licensed under Apache Licence version 2.0. */
package ostrat
package geom
import pCanv._, Colour.Black

/** A Simple circle based graphic. Not sure if this trait is useful. */
trait CircleGraphicSimple extends CircleGraphic with EllipseGraphicSimple
{ type ThisT <: CircleGraphicSimple
}

/** A simple single colour fill of a circle graphic. */
final case class CircleFill(shape: Circle, fill: FillFacet) extends CircleGraphicSimple with EllipseFill with CanvElem
{ override type ThisT = CircleFill
  override def ptsTrans(f: Pt2 => Pt2): ThisT = CircleFill(shape.fTrans(f), fill)
  override def rendToCanvas(cp: CanvasPlatform): Unit = cp.circleFill(this)
  override def svgElem(bounds: BoundingRect): SvgElem = ???
  override def toDraw(lineWidth: Double = 2, newColour: Colour = Black): CircleDraw = shape.draw(newColour, lineWidth)
}

/** A simple draw of a circle graphic. */
final case class CircleDraw(shape: Circle, lineWidth: Double = 2.0, lineColour: Colour = Black) extends CircleGraphicSimple with EllipseDraw
{ type ThisT = CircleDraw
  override def ptsTrans(f: Pt2 => Pt2): CircleDraw = CircleDraw(shape.fTrans(f), lineWidth, lineColour)
  override def rendToCanvas(cp: CanvasPlatform): Unit = cp.circleDraw(this)
  override def svgElem(bounds: BoundingRect): SvgElem = ???
}

case class CircleFillIcon(fillColour: Colour) extends ShapeFillIcon
{ override def reify(scale: Double, cen: Pt2): CircleFill = CircleFill(Circle(scale, cen), fillColour)
  override def reify(scale: Double, xCen: Double, yCen: Double): CircleFill = CircleFill(Circle(scale, xCen, yCen), fillColour)
}