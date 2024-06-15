import core.*;

class LexerTests extends munit.FunSuite {
  test("simple lexer example") {
    val lexer = new Lexer()
    lexer.tokenize("var x = ( 1 + 2 )");
    val expected = List(
      Token.Var,
      Token.Identifier("x"),
      Token.Assign,
      Token.LParen,
      Token.IntLiteral(1),
      Token.Plus,
      Token.IntLiteral(2),
      Token.RParen,
      Token.EOF
    );
    lexer.tokens.zip(expected).foreach { case (t, e) =>
      assertEquals(t, e)
    }
  }
}
