/* Copyright 2018-21 Richard Oliver. Licensed under Apache Licence version 2.0. */
package ostrat

/** A sub trait of the [[ShowT]] sub class where the type parameter of ShowT extends Show. This allows the ShowT type class to delegate to the Show
 * class for the implementation of its strT and ShowT methods. It is better to use [[Show]] and ShowElemT for types you control than have the toString
 * method delegate to the [[ShowT]] type class instance in the companion object. Potentially that can create initialisation order problems, but at the
 * very least it can increase compile times. */
trait ShowElemT[R <: Show] extends ShowT[R]
{
  /** Provides the standard string representation for the object. Its called ShowT to indicate this is a type class method that acts upon an object
   * rather than a method on the object being shown. */
  override def strT(obj: R): String = obj.str

  override def showT(obj: R, way: Show.Way, maxPlaces: Int, minPlaces: Int): String = obj.show(way, maxPlaces, 0)

  /** Simple values such as Int, String, Double have a syntax depth of one. A Tuple3[String, Int, Double] has a depth of 2. Not clear whether this
   * should always be determined at compile time or if sometimes it should be determined at runtime. */
  override def syntaxDepthT(obj: R): Int = obj.syntaxdepth
}

object ShowElemT
{
  def apply[R <: Show](typeStrIn: String): ShowElemT[R] = new ShowElemT[R]
  { override def typeStr: String = typeStrIn
  }
}