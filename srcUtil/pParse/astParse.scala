/* Copyright 2018-21 Richard Oliver. Licensed under Apache Licence version 2.0. */
package ostrat; package pParse

/** Function object for getting an EMon of Statements from Tokens. */
object astParse
{
  /** Gets Statements from Tokens. All other methods in this object are private. */
  def apply(implicit tokens: Arr[Token]): ERefs[Statement] =
  {
    val acc: Buff[BlockMember] = Buff()

    /** The top level loop takes a token sequence input usually from a single source file stripping out the brackets and replacing them and the
     * intervening tokens with a Bracket Block. */
    def loop(rem: ArrOff[Token]): ERefs[Statement] = rem match
    {
      case ArrOff0() => statementsParse(acc.toArr)
      case ArrOff1Tail(bo: BracketOpen, tail) => bracesParse(tail, bo).flatMap { (bracketBlock, remTokens) =>
        acc.append(bracketBlock)
        loop(remTokens)
      }

      case ArrOffHead(bc: BracketCloseToken) => bc.startPosn.bad("Unexpected Closing Brace at top syntax level")
      case ArrOff1Tail(bm: BlockMember, tail) => { acc.append(bm); loop(tail) }
      case _ => excep("Case not implemented")
    }

    loop(tokens.offset0)
  }
}