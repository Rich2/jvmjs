/* Copyright 2018-20 Richard Oliver. Licensed under Apache Licence version 2.0. */
package ostrat
package geom
import pCanv._, Colour.Black

/** An Ellipse based Graphic. The Ellipse can be defined as a circle. */
trait EllipseGraphic extends ShapeGraphicCentred
{ override def shape: Ellipse
}

/** A Simple circle based graphic. Not sure if this trait is useful. */
trait EllipseGraphicSimple extends EllipseGraphic with ShapeGraphicSimple with SimilarAffPreserve
{ type ThisT <: EllipseGraphicSimple
  type ThisT2 <: EllipseGraphicSimple
}

/** A simple single colour fill of a circle graphic. */
trait EllipseFill extends EllipseGraphicSimple with ShapeFill with CanvElem
{ type ThisT <: EllipseFill
  type ThisT2 = EllipseFill
  override def fTrans2(f: Pt2 => Pt2): ThisT2 = EllipseFill(shape.fTrans(f), fill)
  override def rendToCanvas(cp: CanvasPlatform): Unit = cp.ellipseFill(this)

  override def toDraw(lineWidth: Double = 2, newColour: Colour = Black): EllipseDraw = shape.draw(newColour, lineWidth)
}

/** Companion object for the EllipseFill class. */
object EllipseFill
{
  def apply(shape: Ellipse, fillFacet: FillFacet): EllipseFill = EllipseFillImp(shape, fillFacet)

  /** A simple single colour fill of an ellipse graphic. */
  final case class EllipseFillImp(shape: Ellipse, fill: FillFacet) extends EllipseFill
  { type ThisT = EllipseFill

    override def ptsTrans(f: Pt2 => Pt2): ThisT = EllipseFill(shape.fTrans(f), fill)
    override def rendToCanvas(cp: CanvasPlatform): Unit = cp.ellipseFill(this)
    override def svgElem(bounds: BoundingRect): SvgElem = ???
    override def svgStr: String = ???

  }
}

trait EllipseDraw extends EllipseGraphicSimple with ShapeDraw with CanvElem
{
  type ThisT <: EllipseDraw
  type ThisT2 = EllipseDraw
  override def fTrans2(f: Pt2 => Pt2): EllipseDraw = EllipseDraw(shape.fTrans(f), lineColour, lineWidth)
}

object EllipseDraw
{
  def apply(shape: Ellipse, lineColour: Colour = Black, lineWidth: Double = 2.0): EllipseDraw = EllipseDrawImp(shape, lineColour, lineWidth)

  /** Implementation class for [[EllipseDraw]]. */
  final case class EllipseDrawImp(shape: Ellipse, lineColour: Colour = Black, lineWidth: Double = 2.0) extends EllipseDraw
  { type ThisT = EllipseDraw

    override def ptsTrans(f: Pt2 => Pt2): EllipseDraw = EllipseDrawImp(shape.fTrans(f), lineColour, lineWidth)

    override def rendToCanvas(cp: CanvasPlatform): Unit = cp.ellipseDraw(this)

    override def svgElem(bounds: BoundingRect): SvgElem = ???

    override def svgStr: String = ???

  }
}