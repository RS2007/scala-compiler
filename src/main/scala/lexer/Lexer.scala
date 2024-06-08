package lexer;

import token.*;

//exp ::= stmt ::= exp ::= stmt ::= LVar ::=
//int | input_int() | -exp | exp+exp | exp-exp | (exp) print(exp) | exp
//var
//var = exp
//stmt∗
import scala.collection.mutable.ListBuffer

class Lexer {
  def tokenize(input: String): List[Token] = {
    val tokens = new ListBuffer[Token]
    var i = 0
    while (i < input.length) {
      val c = input(i)
      if (c.isDigit) {
        val tillSpace = input.substring(i).indexWhere(!_.isDigit)
        val value = input.substring(i, i + tillSpace).toInt
        tokens += Token.IntLiteral(value)
        i += value.toString.length
      } else if (c == '+') {
        tokens += Token.Plus
        i += 1
      } else if (c == '-') {
        tokens += Token.Minus
        i += 1
      } else if (c == '(') {
        tokens += Token.LParen
        i += 1
      } else if (c == ')') {
        tokens += Token.RParen
        i += 1
      } else if (c == '\n') {
        tokens += Token.EOF
        i += 1
      } else if (c == ' ') {
        i += 1;
      } else {
        val startOfSpace = input.substring(i).indexWhere(_.isSpaceChar);
        val name = input.substring(i, i + startOfSpace);
        tokens += Token.Identifier(name)
        i += name.length
      }
    }
    tokens.toList
  }
}
