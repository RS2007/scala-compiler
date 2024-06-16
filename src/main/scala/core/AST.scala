package core;

import java.util.ArrayList
import java.util.HashMap
import java.util.HashMap
import scala.collection.JavaConverters.asScalaBufferConverter

object AST {

  private var stackTillNow = 0;

  def genTemp(): String = {
    "sup"
  }

  enum InfixOp {
    case Plus;
    case Minus;
  }

  sealed trait Statement
  case class AssignmentStatement(iden: Identifier, expr: ExpressionNode)
      extends Statement
  case class PrintStatement(expr: ExpressionNode) extends Statement

  sealed trait Expression
  case class IntLiteral(value: Int) extends Expression
  case class Identifier(value: String) extends Expression
  case class InfixExpression(left: ExpressionNode, op: InfixOp, right: ExpressionNode) extends Expression

  sealed trait Node { 
    def evaluate(symbolTable: HashMap[String, Int]): Int 
    def codegen(symbolTable: HashMap[String,Int]): String
  }
  case class Var() extends Node {
    override def evaluate(symbolTable: HashMap[String, Int]): Int = {
      throw new Exception("Cannot evaluate variable")
    }
    override def codegen(symbolTable: HashMap[String,Int]): String = {
      throw new Exception("Cannot codegen variable")
    }
  }
  case class ExpressionNode(expr: Expression) extends Node {
    override def evaluate(symbolTable: HashMap[String, Int]): Int = {
      expr match {
        case IntLiteral(value) => {
          value
        }
        case Identifier(value) => symbolTable.getOrDefault(value, 0)
        case InfixExpression(left, op, right) => {
          val lhs = left.evaluate(symbolTable);
          val rhs = right.evaluate(symbolTable);
          op match {
            case InfixOp.Plus => lhs + rhs;
            case InfixOp.Minus => lhs - rhs;
          }
        }
      }
    }
    override def codegen(symbolTable: HashMap[String,Int]): String = {
      expr match {
        case IntLiteral(value) => {
          """
          %s
          """.format("this is a literal")

        } 
        case Identifier(value) => ""
        case InfixExpression(left, op, right) => {
          ""
        }
      }
    }
  }

  case class StatementNode(stmt: Statement) extends Node {
    override def evaluate(symbolTable: HashMap[String, Int]): Int = {
      stmt match {
        case AssignmentStatement(iden, expr) => {
          val value = expr.evaluate(symbolTable);
          symbolTable.put(iden.value, value);
          value
        }
        case PrintStatement(expr) => {
          val value = expr.evaluate(symbolTable);
          value
        }
      }
    }
    override def codegen(symbolTable: HashMap[String,Int]): String = {
      stmt match {
        case AssignmentStatement(iden, expr) => {
          symbolTable.put(iden.value, stackTillNow);
          stackTillNow = stackTillNow+8;
          "str x0,[sp,%s]".format(stackTillNow-8)
        }
        case PrintStatement(expr) => {
          //assert(false, "No print while compiling:  ")
          "# This is a comment\n"
        }
      }
    }
    def countLocals(): Int = {
      stmt match {
        case AssignmentStatement(iden, expr) => {
          1
        }
        case _ => 1
      }
    } 
  }

  case class Program(statements: ArrayList[StatementNode]) extends Node {
    override def evaluate(symbolTable: HashMap[String, Int]): Int = {
      var result = 0;
      statements.forEach((statement: StatementNode) => {
        result = statement.evaluate(symbolTable);
      });
      result
    }
    override def codegen(symbolTable: HashMap[String,Int]): String = {
      stackTillNow = 0;
      var result="""
.global _main;
.align 2;
_main:
""";

  
      val stackOffset = statements.asScala.map(_.countLocals()).sum
      result+="sp,sp,-"+stackOffset*8+"\n";
      statements.forEach((statement: StatementNode) => {
        val fromStmt = statement.codegen(symbolTable)
        result = result + fromStmt + "\n";
      });
      result
    }
  }
}
