package core;

import java.util.ArrayList
import scala.util.control.Breaks._

class Parser:
  def parse(lexer: Lexer): AST.Program = {
    var program = new AST.Program(ArrayList());
    lexer.advance();
    while (lexer.hasNext()) {
      val statement: Option[AST.Statement] = lexer.nextToken match {
        case Token.Print => {
          lexer.advance();
          if (lexer.nextToken != Token.LParen)
            throw new Exception("Expected '('")
          lexer.advance()
          val expr = parseExpression(lexer);
          if (lexer.nextToken != Token.RParen)
            throw new Exception("Expected ')'")
          lexer.advance()
          Some(new AST.PrintStatement(expr))
        }
        case Token.Var => {
          val variable = new AST.Var();
          lexer.advance();
          lexer.nextToken match {
            case Token.Identifier(name) => {
              lexer.advance();
              lexer.nextToken match {
                case Token.Assign => {
                  lexer.advance();
                  val expr = parseExpression(lexer);
                  Some(
                    new AST.AssignmentStatement(
                      new AST.Identifier(name),
                      expr
                    )
                  )
                }
                case _ => throw new Exception("Expected '='")
              }
            }
            case _ => throw new Exception("Expected identifier")
          }
        }
        case Token.EOF => {
          lexer.advance();
          None
        };
        case _ => throw new Exception("Expected statement")
      }
      if (statement.isDefined)
        program.statements.add(new AST.StatementNode(statement.get));

    }
    program
  }

  def parsePrefix(lexer: Lexer): AST.ExpressionNode = {

    val expr = lexer.nextToken match {
      case Token.IntLiteral(value) => {
        lexer.advance();
        new AST.ExpressionNode(AST.IntLiteral(value))
      }
      case Token.Identifier(value) => {
        lexer.advance();
        new AST.ExpressionNode(AST.Identifier(value))
      }
      case _ => throw new Exception("Expected expression")
    }
    expr
  }

  def getPrecedence(token: Token): Int = {
    token match {
      case Token.Plus | Token.Minus => 1;
      case _                        => 0;
    }
  }

  def parseExpression(
      lexer: Lexer
  ): AST.ExpressionNode = {
    var lhs = parsePrefix(lexer);
    lexer.nextToken match {
      case Token.Plus | Token.Minus => {
        val op = lexer.nextToken match {
          case Token.Plus => AST.InfixOp.Plus;
          case Token.Minus => AST.InfixOp.Minus;
          case _ => return lhs;
          
        }
        lexer.advance();
        val rhs = parseExpression(lexer);
        new AST.ExpressionNode(
          AST.InfixExpression(
            lhs,
            op,
            rhs
          )
        )
      };
      case _ => lhs;
    }
  }
