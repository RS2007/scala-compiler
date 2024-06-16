import core.*;
import munit.Clue.generate;

import java.util.ArrayList

class ParserTests extends munit.FunSuite {
  test("simple parser example") {
    val lexer = new Lexer()
    lexer.tokenize("var x = 1\n");
    val parser = new Parser();
    val program = parser.parse(lexer);
    val statementExpected =
      new AST.AssignmentStatement(
        new AST.Identifier("x"),
        new AST.ExpressionNode(AST.IntLiteral(1))
      );
    assertEquals(
      program.statements.get(0).stmt,
      statementExpected
    );

  }
  test("two statement example") {
    val lexer = new Lexer()
    lexer.tokenize("var x = 1 \n print ( x )");
    val parser = new Parser();
    val program = parser.parse(lexer);
    val assStatementExpected =
      new AST.AssignmentStatement(
        new AST.Identifier("x"),
        new AST.ExpressionNode(new AST.IntLiteral(1))
      );
    val printStatementExpected =
      new AST.PrintStatement(new AST.ExpressionNode(AST.Identifier("x")));
    assertEquals(
      program.statements.get(0).stmt,
      assStatementExpected
    );
    assertEquals(
      program.statements.get(1).stmt,
      printStatementExpected
    );

  }
}
