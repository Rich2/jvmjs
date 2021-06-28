/* Copyright 2018-21 Richard Oliver. Licensed under Apache Licence version 2.0. */
package ostrat; package pCanv
import geom._

/** A canvas that is not divided up into panels. A panel is a portion of the canvas that has its own origin and is clipped. */
abstract class CanvasNoPanels(title: String) extends CanvasUser(title) with PanelLike
{      
  override def width = canv.width
  override def height = canv.height
   
  canv.mouseUp = (posn, button) => mouseUp(button, actives.filterToList(_.ptInside(posn)).map(_.pointerId), posn)
      
  def refresh(): Unit =
  { canv.clear(backColour)
    actives = paintObjs(canvObjs)//paintObjs paints the objects to the screen and returns a list of active objects
  }

  /** Repaints the canvas takes repeat parameters of GraphicElem. */
  def repaints(els: GraphicElem*): Unit = { canvObjs = els.toArr; refresh() }

  /** Repaints the canvas, takes a Refs collection as parameter. */
  def repaint(els: Arr[GraphicElem]): Unit = { canvObjs = els; refresh() }

  /** This function is to create a display that changes over time. So you pass in a function from the time elapsed in milliseconds to the Arr of
   * [[GraphicElem]]s that you want to display at that time point. */
  def timedRepaint(f: Integer => GraphicElems): Unit =
  { val combinedF: Integer => Unit = elapsed => repaint(f(elapsed))
    canv.startFramePermanent(combinedF)
  }

  def timedRepaint1(f: Integer => GraphicElem): Unit =
  { val combinedF: Integer => Unit = elapsed => repaints(f(elapsed))
    canv.startFramePermanent(combinedF)
  }
}