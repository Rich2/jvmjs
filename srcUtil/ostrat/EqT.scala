/* Copyright 2018-21 Richard Oliver. Licensed under Apache Licence version 2.0. */
package ostrat

/** Equals type class trait. */
trait EqT[A]
{ def eqv(a1: A, a2: A): Boolean
}

/** The campanion object for the EqT type class, containing instances for common types. This does not currently use a functor instance for a number of
 * reasons. */
object EqT
{
  implicit val intImplicit: EqT[Int] = (a1, a2) => a1 == a2
  implicit val doubleImplicit: EqT[Double] =  (a1, a2) => a1 == a2
  implicit val booleanImplicit: EqT[Boolean] = (a1, a2) => a1 == a2
  implicit val stringImplicit: EqT[String] = (a1, a2) => a1 == a2
  implicit val charImplicit: EqT[Char] = (a1, a2) => a1 == a2
  implicit val noneImplicit: EqT[None.type] = (_, _) => true
  implicit def someImplicit[A](implicit ev: EqT[A]): EqT[Some[A]]= (s1, s2) => ev.eqv(s1.value, s2.value)

  implicit def optionImplicit[A](implicit ev: EqT[A]): EqT[Option[A]] = (a1, a2) => (a1, a2) match
  { case (None, None) => true
    case (Some(v1), Some(v2)) => ev.eqv(v1, v2)
    case _ => false
  }

  implicit def listImplicit[A](implicit ev: EqT[A]): EqT[List[A]] = (l1, l2) =>
  { def loop(rem1: List[A], rem2: List[A]): Boolean = (rem1, rem2) match
    { case (Nil, Nil) => true
      case (::(h1, t1) , ::(h2, t2)) if ev.eqv(h1, h2) => loop(t1, t2)
      case _ => false
    }
    loop(l1, l2)
  }

  implicit def arrayImplicit[A](implicit ev: EqT[A]): EqT[Array[A]] = (a1, a2) =>
    if(a1.length != a2.length) false
    else
    { var count = 0
      var acc = true
      var continue = true

      while (count < a1.length & continue)
      { if (ev.eqv(a1(count), a2(count))) count += 1
        else {acc = false; continue = false}
      }
      acc
    }

  implicit def seqImplicit[A](implicit ev: EqT[A]): EqT[Seq[A]] = (s1, s2) => (s1.length == s2.length) & s1.iForall((el, i) => ev.eqv(el, s2(i)))

  implicit def vectorImplicit[A](implicit ev: EqT[A]): EqT[Vector[A]] = (s1, s2) => (s1.length == s2.length) & s1.iForall((el, i) => ev.eqv(el, s2(i)))

  implicit def tuple2Implicit[A1, A2](implicit eq1: EqT[A1], eq2: EqT[A2]): EqT[(A1, A2)] = (p1, p2) => eq1.eqv(p1._1, p2._1) & eq2.eqv(p1._2, p2._2)
}

class Eq1T[A1, R](val fArg1: R => A1)(implicit eq1: EqT[A1]) extends EqT[R]
{ override def eqv(r1: R, r2: R): Boolean = eq1.eqv(fArg1(r1), fArg1(r2))
}

/** Equality type class trait for Product 2. */
trait Eq2T[A1, A2, R] extends EqT[R]
{ def fArg1: R => A1
  def fArg2: R => A2
  implicit def eq1: EqT[A1]
  implicit def eq2: EqT[A2]
  override def eqv(r1: R, r2: R): Boolean = eq1.eqv(fArg1(r1), fArg1(r2)) & eq2.eqv(fArg2(r1), fArg2(r2))
}

object Eq2T
{
  def apply[A1, A2, R](fArg1In: R => A1, fArg2In: R => A2)(implicit eq1In: EqT[A1], eq2In: EqT[A2]): Eq2T[A1, A2, R] = new Eq2T[A1, A2, R]
  { override def fArg1: R => A1 = fArg1In
    override def fArg2: R => A2 = fArg2In
    override implicit def eq1: EqT[A1] = eq1In
    override implicit def eq2: EqT[A2] = eq2In
  }
}

case class Eq2DblsT[R](fArg1: R => Double, fArg2: R => Double) extends Eq2T[Double, Double, R]
{ override implicit def eq1: EqT[Double] = EqT.doubleImplicit
  override implicit def eq2: EqT[Double] = EqT.doubleImplicit
}

/** Equality type class trait for Product 3. */
class Eq3T[A1, A2, A3, R](val fArg1: R => A1, val fArg2: R => A2, val fArg3: R => A3)(implicit eq1: EqT[A1], eq2: EqT[A2], eq3: EqT[A3]) extends
  EqT[R]
{ override def eqv(r1: R, r2: R): Boolean = eq1.eqv(fArg1(r1), fArg1(r2)) & eq2.eqv(fArg2(r1), fArg2(r2)) & eq3.eqv(fArg3(r1), fArg3(r2))
}

object Eq3T
{
  def apply[A1, A2, A3, R](fArg1: R => A1, fArg2: R => A2, fArg3: R => A3)(implicit eq1: EqT[A1], eq2: EqT[A2], eq3: EqT[A3]): Eq3T[A1, A2, A3, R] =
    new Eq3T(fArg1, fArg2, fArg3)
}

/** Equality type class trait for Product 4. */
class Eq4T[A1, A2, A3, A4, R](val fArg1: R => A1, val fArg2: R => A2, val fArg3: R => A3, val fArg4: R => A4)(implicit eq1: EqT[A1], eq2: EqT[A2],
                                                                                                              eq3: EqT[A3], eq4: EqT[A4]) extends EqT[R]
{
  override def eqv(r1: R, r2: R): Boolean = eq1.eqv(fArg1(r1), fArg1(r2)) & eq2.eqv(fArg2(r1), fArg2(r2)) & eq3.eqv(fArg3(r1), fArg3(r2)) &
    eq4.eqv(fArg4(r1), fArg4(r2))
}

/*class EqCase5[A1, A2, A3, A4, A5, R](val fArg1: R => A1, val fArg2: R => A2, val fArg3: R => A3, val fArg4: R => A4, val fArg5: R => A5)(implicit
  eq1: Eq[A1], eq2: Eq[A2], eq3: Eq[A3], eq4: Eq[A4], eq5: Eq[A5]) extends Eq[R]
{ override def eqv(r1: R, r2: R): Boolean =
    eq1.eqv(fArg1(r1), fArg1(r2)) & eq2.eqv(fArg2(r1), fArg2(r2)) & eq3.eqv(fArg3(r1), fArg3(r2)) & eq4.eqv(fArg4(r1), fArg4(r2)) &
    eq5.eqv(fArg5(r1), fArg5(r2))
}*/

/*
trait Eq6T[A1, A2, A3, A4, A5, A6, R] extends Eq[R]
{
  def fArg1: R => A1
  def fArg2: R => A2
  def fArg3: R => A3
  def fArg4: R => A4
  def fArg5: R => A5
  def fArg6: R => A6
  implicit def eq1: Eq[A1]
  implicit def eq2: Eq[A2]
  implicit def eq3: Eq[A3]
  implicit def eq4: Eq[A4]
  implicit def eq5: Eq[A5]
  implicit def eq6: Eq[A6]
  override def eqv(r1: R, r2: R): Boolean =
  eq1.eqv(fArg1(r1), fArg1(r2)) & eq2.eqv(fArg2(r1), fArg2(r2)) & eq3.eqv(fArg3(r1), fArg3(r2)) & eq4.eqv(fArg4(r1), fArg4(r2)) &
  eq5.eqv(fArg5(r1), fArg5(r2)) & eq6.eqv(fArg6(r1), fArg6(r2) )
}

object Eq6T
{
  def apply[A1, A2, A3, A4, A5, A6, R](fArg1In: R => A1, fArg2in: R => A2, fArg3In: R => A3, fArg4In: R => A4, fArg5In: R => A5, fArg6In: R => A6)(
    implicit eq1: Eq[A1], eq2: Eq[A2], eq3: Eq[A3], eq4: Eq[A4], eq5: Eq[A5], eq6: Eq[A6])

  (val fArg1: R => A1, val fArg2: R => A2, val fArg3: R => A3, val fArg4: R => A4, val fArg5: R => A5,
  val fArg6: R => A6)(implicit eq1: Eq[A1], eq2: Eq[A2], eq3: Eq[A3], eq4: Eq[A4], eq5: Eq[A5], eq6: Eq[A6])
}*/
