package core;

//exp ::= stmt ::= exp ::= stmt ::= LVar ::=
//int | input_int() | -exp | exp+exp | exp-exp | (exp) print(exp) | exp
//var
//var = exp
//stmt∗
import scala.collection.mutable.ListBuffer

class Lexer:

  var tokens: List[Token] = List()
  var currentToken: Token = Token.Invalid
  var nextToken: Token = Token.Invalid
  private var i = 0

  def tokenize(input: String): Unit = {
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
      } else if (c == '=') {
        tokens += Token.Assign
        i += 1;
      } else if (c == ' ') {
        i += 1;
      } else {
        val startOfSpace = input.substring(i).indexWhere(_.isSpaceChar);
        val name = input.substring(i, i + startOfSpace);
        name match {
          case "var" => tokens += Token.Var;
          case "print" => tokens += Token.Print;
          case _     => tokens += Token.Identifier(name);
        }
        i += name.length
      }
    }
    this.tokens = tokens.toList
  }

  def hasNext(): Boolean = {
    i < tokens.length
  }

  def advance(): Unit = {
    currentToken = nextToken;
    if i < tokens.length then 
      nextToken = tokens(i);
    i += 1;
  }

