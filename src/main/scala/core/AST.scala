package core;

import java.util.ArrayList
import java.util.HashMap
import java.util.HashMap
import scala.collection.JavaConverters.asScalaBufferConverter

object AST {

  private var stackTillNow = 0;
  private var count = 0;

  def genTemp(): String = {
    val characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    assert(count < characters.length);
    count += 1;
    "t"+(characters.charAt(count-1))
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
          "mov x0,%d // Load number %d to x0".format(value,value)
        } 
        case Identifier(value) => {
          val offset = symbolTable.get(value)
          "ldr x0,[sp,%d] // Load identifier %s".format(offset,value)
        }
        case InfixExpression(left, op, right) => {
          val lhs = left.codegen(symbolTable);
          val rhs = right.codegen(symbolTable);
          val operation = op match {
            case InfixOp.Plus => {
              "add x0,x1,x2 // infix operation add"
            }
            case InfixOp.Minus => {
              "sub x0,x1,x2 // infix operation sub"
            }
          }
          """
          %s
          add x2,x0,0 // load rhs to x2
          %s
          add x1,x0,0 // load lhs to x1
          %s
          """.format(rhs,lhs,operation)
        }
      }

    }
    def countLocals(): Int = {
      expr match {
        case IntLiteral(_) | Identifier(_) => 1;
        case InfixExpression(left,op,right)=> left.countLocals()+right.countLocals();
      }
    }
    def codegenTAC(symbolTable: HashMap[String,Int],temp: String): String = {
      expr match {
        case IntLiteral(value) => {
          "var %s = %d".format(temp,value)
        }
        case Identifier(value) => {
          "var %s = %s".format(temp,value)
        }
        case InfixExpression(left, op, right) => {
          val lhsTemp = genTemp();
          val rhsTemp = genTemp();
          val lhs = left.codegenTAC(symbolTable,lhsTemp);
          val rhs = right.codegenTAC(symbolTable,rhsTemp);
          val operation = op match {
            case InfixOp.Plus => {
              "var %s = %s + %s".format(temp,lhsTemp,rhsTemp)
            }
            case InfixOp.Minus => {
              "var %s = %s - %s".format(temp,lhsTemp,rhsTemp)
            }
          }
          "%s \n %s \n %s".format(lhs,rhs,operation)
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
          stackTillNow = stackTillNow+16;
          val exprCode = expr.codegen(symbolTable);
          "%s\nstr x0,[sp,%s]".format(exprCode,stackTillNow-16)
        }
        case PrintStatement(expr) => {
          //assert(false, "No print while compiling:  ")
          val exprCode = expr.codegen(symbolTable)
          stackTillNow = stackTillNow+16;
          """
          %s
          add x0,x0,48
          str x0,[sp,%d]
          add x1,sp,%d
          mov x0,#1
          mov x2,#1
          mov x16,#4
          svc #0x80
          """.format(exprCode,stackTillNow-16,stackTillNow-16)
        }
      }
    }
    def countLocals(): Int = {
      stmt match {
        case AssignmentStatement(iden, expr) => 1+expr.countLocals()
        
        case PrintStatement(expr) => 1+expr.countLocals()
      }
    } 

    def codegenTAC(symbolTable: HashMap[String,Int]): String = {
      stmt match {
        case AssignmentStatement(iden, expr) => {
          val temp = genTemp();
          val exprCode = expr.codegenTAC(symbolTable,temp);
          "%s \n var %s = %s".format(exprCode,iden.value,temp);
        }
        case PrintStatement(expr) => {
          val temp = genTemp();
          val exprCode = expr.codegenTAC(symbolTable,temp);
          "%s \n print ( %s )".format(exprCode,temp);
        }
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
      result+="sub sp,sp,"+stackOffset*16+"\n";
      statements.forEach((statement: StatementNode) => {
        val fromStmt = statement.codegen(symbolTable)
        result = result + fromStmt + "\n";
      });
      result+="add sp,sp,"+stackOffset*16+"\n";
      result+="""
      mov x0,#0
      mov x16,#1
      svc #0x80
      """
      result
    }

    def codegenTAC(symbolTable: HashMap[String,Int]): String = {
      var result = ""
      statements.forEach((statement: StatementNode) => {
        val fromStmt = statement.codegenTAC(symbolTable)
        result = result + fromStmt + " \n";
      });
      result
    }
  }
}
