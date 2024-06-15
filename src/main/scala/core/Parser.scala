package core;

import java.util.ArrayList
import scala.util.control.Breaks._

sealed trait Expression
case class IntLiteral(value: Int) extends Expression

sealed trait Node
case class Var() extends Node
case class ExpressionNode(expr: Expression) extends Node
case class Statement(variable: Var, expr: ExpressionNode) extends Node
case class Program(statements: ArrayList[Statement]) extends Node

class Parser {
  def parse(lexer: Lexer): Program = {
    var program = new Program(ArrayList());
    lexer.advance();
    while (lexer.hasNext()) {
      val statement = lexer.nextToken match {
        case Token.Var => {
          val variable = new Var();
          lexer.advance();
          lexer.nextToken match {
            case Token.Identifier(name) => {
              lexer.advance();
              lexer.nextToken match {
                case Token.Assign => {
                  lexer.advance();
                  val expr = parseExpression(lexer);
                  new Statement(variable, expr)
                }
                case _ => throw new Exception("Expected '='")
              }
            }
            case Token.EOF => break;
            case _         => throw new Exception("Expected identifier")
          }
        }
      }
      program.statements.add(statement);
    }
    program
  }
  def parseExpression(lexer: Lexer): ExpressionNode = {
    val expr = lexer.nextToken match {
      case Token.IntLiteral(value) => {
        lexer.advance();
        new ExpressionNode(IntLiteral(value))
      }
      case _ => throw new Exception("Expected expression")
    }
    expr
  }
}
