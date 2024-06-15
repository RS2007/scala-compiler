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
      new Statement(new Var, new ExpressionNode(IntLiteral(1)));
    assertEquals(
      program.statements.get(0),
      statementExpected
    );

  }
}
