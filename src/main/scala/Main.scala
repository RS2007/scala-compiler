import core.*;
import java.util.HashMap;

@main def hello(): Unit =
  val lexer = new Lexer()
  lexer.tokenize("var x = 1 \n var y = 2 \n print ( x + y - 5)");
  val parser = new Parser();
  val program = parser.parse(lexer);
  var scope = new HashMap[String,Int]()
  var symTab = new HashMap[String,Int]()
  println(program.evaluate(scope));
  println(program.codegen(symTab));

def msg = "I was compiled by Scala 3. :)"
