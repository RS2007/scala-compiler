@main def hello(): Unit =
  val tokens = new lexer.Lexer().tokenize("var x = ( 1 + 2 )");
  println(tokens);

def msg = "I was compiled by Scala 3. :)"
