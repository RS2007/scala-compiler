import core.*;

@main def hello(): Unit =
  val tokens = new Lexer().tokenize("var x = ( 1 + 2 )");
  println(tokens);

def msg = "I was compiled by Scala 3. :)"
