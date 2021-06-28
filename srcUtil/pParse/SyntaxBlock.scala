/* Copyright 2018-21 Richard Oliver. Licensed under Apache Licence version 2.0. */
package ostrat; package pParse

/** This trait includes all the tokens except braces plus the Bracket syntactic blocks. The Block in the name block member is either the top level
 *  statements in the file or the statements with in a bracket block. Conceptually the source file is considered a special case of bracket block where 
 *  the beginning of substitutes for the opening bracket and the end of file substitutes for the closing bracket. BlockMember has only two sub traits
 *  StatementMember and SemiToken. So a sequence of TokenOrBlocks is simply a series of tokens which has been parsed into Blocks. */
trait BlockMember extends TextSpan

/** A statement member is a Token except the semi colon, which is the statement delimiter and the braces, plus it includes blocks, As blocks contain a
 *  sequence of Statements but can themselves be part of a statement. */
trait StatementMember extends BlockMember

/** I think its helpful to have an expression member trait for syntax that is not expressions. So I don't think it would be helpful if say an opening
 *  brace was an expression. All Expressions are Expression members. */
trait ClauseMember extends StatementMember

sealed trait BracketToken extends Token
{ def braces: Braces
}

/** An opening Brace, '{', '[' or '('.  */
sealed trait BracketOpen extends BracketToken
{ final override def tokenTypeStr = "Open" + braces.name + "BraceToken"
  final override def srcStr: String = braces.openChar.toString
}

/** A closing bracket Token, The ']', ']' or ')' characters are BracketCloseTokens. */
sealed trait BracketCloseToken extends BracketToken
{ final override def tokenTypeStr = "Close" + braces.name + "BraceToken"
  final override def srcStr: String = braces.closeChar.toString
}

case class ParenthOpenToken(startPosn: TextPosn) extends BracketOpen
{ override def braces: Braces = Parenthesis
}

case class ParenthCloseToken(startPosn: TextPosn) extends BracketCloseToken
{ override def braces: Braces = Parenthesis
}

case class SquareOpenToken(startPosn: TextPosn) extends BracketOpen
{ override def braces: Braces = SquareBraces
}

case class SquareCloseToken(startPosn: TextPosn) extends BracketCloseToken
{ override def braces: Braces = SquareBraces
}

case class CurlyOpenToken(startPosn: TextPosn) extends BracketOpen
{ override def braces: Braces = CurlyBraces
}

case class CurlyCloseToken(startPosn: TextPosn) extends BracketCloseToken
{ override def braces: Braces = CurlyBraces
}

sealed trait Braces
{ def openChar: Char
  def closeChar: Char
  def name: String
}
case object Parenthesis extends Braces
{ override def openChar: Char = '('
  override def closeChar: Char = ')'
  override def name: String =  "Round"
}

case object SquareBraces extends Braces
{ override def openChar: Char = '['
  override def closeChar: Char = ']'
  override def name: String = "Square"
}

case object CurlyBraces extends Braces
{ override def openChar: Char = '{'
  override def closeChar: Char = '}'
  override def name: String = "Curly"
}

case class BracketedRaws(statements: Arr[TextSpan], braces: Braces, startBracket: TextPosn, endBracket: TextPosn)// extends StatementsHolder
{ def exprName: String = braces.name + "BlockExpr"
}

case class BracketedStatements(statements: Statements, braces: Braces, startBracket: TextPosn, endBracket: TextPosn) extends BlockStatements
{ def exprName: String = braces.name + "BlockExpr"
  def memExprs: Arr[Expr] = statements.map(_.expr)
}

object ParenthBlock
{ def unapply(inp: AnyRef): Option[(Statements, TextPosn, TextPosn)] = inp match
  { case BracketedStatements(sts, Parenthesis, sp, ep) => Some((sts, sp, ep))
    case _ => None
  }
}

object SquareBlock
{ def unapply(inp: AnyRef): Option[(Statements, TextPosn, TextPosn)] = inp match
  { case BracketedStatements(sts, SquareBraces, sp, ep) => Some((sts, sp, ep))
    case _ => None
  }
}

object CurlyBlock
{ def unapply(inp: AnyRef): Option[(Statements, TextPosn, TextPosn)] = inp match
  { case BracketedStatements(sts, CurlyBraces, sp, ep) => Some((sts, sp, ep))
    case _ => None
  }
}