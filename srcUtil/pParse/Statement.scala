/* Copyright 2018-21 Richard Oliver. Licensed under Apache Licence version 2.0. */
package ostrat; package pParse

/** The top level compositional unit of Syntax in CRON: Compact Readable Object Notation. A statement can be claused consisting of comma separated
  * clauses containing a single expression. An empty statement is a special case of the UnClausedStatement where the semicolon character is the
  * expression. */
sealed trait Statement extends TextSpan
{ /** The opt semicolon token. */
  def optSemi: OptRef[SemicolonToken]

  /** The statement has semicolon as end */
  def hasSemi: Boolean = optSemi.nonEmpty

  /** The statement has no semicolon at end. */
  def noSemi: Boolean = optSemi.empty

  /** Not sure what this is meant to be doing, or whether it can be removed. */
  final def errGet[A](implicit ev: Persist[A]): EMon[A] = ???

  /** The expression value of this Statement. */
  def expr: Expr

  /** Returns the right expression if this Statement is a setting of the given name. */
  def settingExpr(settingName: String): EMon[Expr] = this match {
    case MonoStatement(AsignExpr(IdentLowerToken(_, sym), _, rightExpr), _) if sym == settingName => Good(rightExpr)
    case _ => startPosn.bad(settingName -- "not found.")
  }
}

/** Companion object for the [[Statement]] trait, contains implicit extension class for List[Statement]. */
object Statement
{
  /** Extension class for Arr[Statement]. */
  implicit class arrImplicit(statementRefs: Arr[Statement]) extends TextSpan
  { private def ifEmptyTextPosn: TextPosn = TextPosn("Empty Statement Seq", 0, 0)
    def startPosn = statementRefs.ifEmpty(ifEmptyTextPosn, statementRefs.head.startPosn)
    def endPosn = statementRefs.ifEmpty(ifEmptyTextPosn, statementRefs.last.endPosn)

    /** Finds a setting [Expr] from this Arr[Statement] extension method. */
    def findSettingExpr(settingStr: String): EMon[Expr] = statementRefs match
    { case Arr0() => TextPosn.emptyError("No Statements")
      case Arr1(st1) => st1.settingExpr(settingStr)
      case sts => sts.map(st => st.settingExpr(settingStr)).collect{ case g @ Good(_) => g } match
      { case Arr1(t) => t
        case Arr0() => sts.startPosn.bad(settingStr -- "Setting not found.")
        case s3 => sts.startPosn.bad(s3.elemsLen.toString -- "settings of" -- settingStr -- "not found.")
      }
    }

    /** Find Setting of type T from this Arr[Statement]. Extension method. */
    def findSettingT[T](settingStr: String)(implicit ev: Persist[T]): EMon[T] = ev.settingTFromStatements(statementRefs, settingStr)

    /** Find Setting of type T from this Arr[Statement] or return the default value parameter. Extension method */
    def findSettingTElse[A](settingStr: String, elseValue: A)(implicit ev: Persist[A]): A = findSettingT[A](settingStr).getElse(elseValue)

    /** Find Statement of type T, if its unique from this Arr[Statement] and return value. */
    def findUniqueT[A](implicit ev: UnShow[A]): EMon[A] = ev.findUniqueFromStatements(statementRefs)

    /** Find unique instance of type from RSON statement. The unique instance can be a plain value or setting. If no value or duplicate values found
     *  use elseValue. */
    def findTypeElse[A](elseValue: A)(implicit ev: Persist[A]): A = findUniqueT[A].getElse(elseValue)

    def findTypeIndex[A](index: Int)(implicit ev: Persist[A]): EMon[A] =
    { val list = ev.valueListFromStatements(statementRefs)
      if (list.length > index) Good(list(index))
      else TextPosn.empty.bad("Element " + index.toString -- "of" -- ev.typeStr -- "not found")
    }

    /** Find the sole [[Int]] expression from this Arr[Statement] extension method. Returns bad if absent or multiple [[Statement]]s resolve to
     * Expr[Int]. */
    def findInt: EMon[Int] = UnShow.intImplicit.findUniqueTFromStatements(statementRefs)

    /** Find the sole [[Double]] expression from this Arr[Statement] extension method. Returns bad if absent or multiple [[Statement]]s resolve to
     * Expr[Double]. */
    def findDbl: EMon[Double] = UnShow.doubleImplicit.findUniqueTFromStatements(statementRefs)

    /** Find the sole [[Boolean]] expression from this Arr[Statement] extension method. Returns bad if absent or multiple [[Statement]]s resolve to
     * Expr[Boolean]. */
    def findBool: EMon[Boolean] = UnShow.booleanImplicit.findUniqueTFromStatements(statementRefs)

    /** Find the sole [[Long]] expression from this Arr[Statement] extension method. Returns bad if absent or multiple [[Statement]]s resolve to
     * Expr[Long]. */
    def findLong: EMon[Long] = UnShow.longImplicit.findUniqueTFromStatements(statementRefs)

    /** Find the sole Array[Int] expression from this Arr[Statement] extension method. Returns bad if absent or multiple [[Statement]]s resolve to
     * Expr[Array[Int]]. */
    def findIntArray: EMon[Array[Int]] = UnShow.arrayIntImplicit.findUniqueFromStatements(statementRefs)

    /** Find Setting of the given name and type Int from this Arr[Statement] Extension method. */
    def findSettingInt(settingStr: String): EMon[Int] = ShowT.intPersistImplicit.settingTFromStatements(statementRefs, settingStr)

    /** Find Setting of the given name and type [[Double]] from this Arr[Statement] Extension method. */
    def findSettingDbl(settingStr: String): EMon[Double] = ShowT.doublePersistImplicit.settingTFromStatements(statementRefs, settingStr)

    /** Find the [[Boolean]] setting of the given name, from this Arr[Statement] extension method. Returns bad if absent or multiple [[Statement]]s
     *  resolve to Expr[Boolean]. */
    def findSettingBool(settingStr: String): EMon[Boolean] = ShowT.booleanPersistImplicit.settingTFromStatements(statementRefs, settingStr)

    /*def errFun1[A1, B](f1: A1 => B)(implicit ev1: Persist[A1]): EMon[B] = statementRefs match
    { case Arr1(h1) => h1.errGet[A1].map(f1)
      case s => bad1(s, s.elemsLen.toString -- "statements not 1")
    }*/

    /*def errFun2[A1, A2, B](f2: (A1, A2) => B)(implicit ev1: Persist[A1], ev2: Persist[A2]): EMon[B] = statementRefs match
    { case Refs2(h1, h2) => for { g1 <- h1.errGet[A1](ev1); g2 <- h2.errGet[A2](ev2) } yield f2(g1, g2)
      case s => bad1(s, s.length.toString -- "statements not 2")
    }

    def errFun3[A1, A2, A3, B](f3: (A1, A2, A3) => B)(implicit ev1: Persist[A1], ev2: Persist[A2], ev3: Persist[A3]): EMon[B] =
      statementRefs match
      { case Refs3(h1, h2, h3) => for { g1 <- h1.errGet[A1](ev1); g2 <- h2.errGet[A2](ev2); g3 <- h3.errGet[A3](ev3) } yield f3(g1, g2, g3)
      case s => bad1(s, s.length.toString -- "statements not 3")
      }*/

    /*def errFun4[A1, A2, A3, A4, B](f4: (A1, A2, A3, A4) => B)(implicit ev1: Persist[A1], ev2: Persist[A2], ev3: Persist[A3], ev4: Persist[A4]):
    EMon[B] = statementRefs match
    {
      case Refs4(h1, h2, h3, h4) => for { g1 <- h1.errGet[A1](ev1); g2 <- h2.errGet[A2](ev2); g3 <- h3.errGet[A3](ev3); g4 <-  h4.errGet[A4] }
        yield f4(g1, g2, g3, g4)
      case s => bad1(s, s.length.toString -- "statements not 4")
    }*/
  }
}

/** This statement has 1 or more comma separated clauses. If there is only 1 Clause, it must be terminated by a comma, otherwise the trailing comma
 *  on the last Clauses is optional. */
case class ClausedStatement(clauses: Arr[Clause], optSemi: OptRef[SemicolonToken]) extends Statement with TextSpanCompound
{ def expr: Expr = ??? //ClausesExpr(clauses.map(_.expr))
  def startMem: TextSpan = clauses.head
  def endMem: TextSpan = optSemi.fld[TextSpan](clauses.last, st => st)
  //override def errGet[A](implicit ev: Persist[A]): EMon[A] = ev.fromClauses(clauses)
}

/** An unclaused Statement has a single expression. */
sealed trait UnClausedStatement extends Statement
{ def expr: Expr
  def optSemi: OptRef[SemicolonToken]
 // override def errGet[A](implicit ev: Persist[A]): EMon[A] = ev.fromExpr(expr)
}

/** An un-claused Statement that is not the empty statement. */
case class MonoStatement(expr: Expr, optSemi: OptRef[SemicolonToken]) extends UnClausedStatement with TextSpanCompound
{ def startMem: TextSpan = expr
  def endMem: TextSpan = optSemi.fld(expr, sc => sc)
}

/** The Semicolon of the Empty statement is the expression of this special case of the unclaused statement */
case class EmptyStatement(st: SemicolonToken) extends UnClausedStatement with TextSpanCompound
{ override def expr: Expr = st
  override def optSemi: OptRef[SemicolonToken] = OptRef(st)
  override def startMem: TextSpan = st
  override def endMem: TextSpan = st
  def asError[A]: Bad[A] = st.startPosn.bad("Empty Statement")
}

object EmptyStatement
{ def apply(st: SemicolonToken): EmptyStatement = new EmptyStatement(st)
}