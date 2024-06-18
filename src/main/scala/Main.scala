import core.*;
import java.util.HashMap;
import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets
import scala.sys.process._

@main def hello(): Unit =
  val lexer = new Lexer()
  lexer.tokenize("var x = 1 \n var y = 2 \n print ( x + y )");
  val parser = new Parser();
  val program = parser.parse(lexer);
  var scope = new HashMap[String,Int]()
  var symTab = new HashMap[String,Int]()
  println(program.evaluate(scope));

  val generated = program.codegen(symTab);
  // write to test3.s and compile with gcc (-g -o test3 test3.s)
  val filePath = Paths.get("test3.s")
  Files.write(filePath, generated.getBytes(StandardCharsets.UTF_8))
  val command = "gcc -g -o test3 test3.s" // Command to list files and directories
  command.!!

def msg = "I was compiled by Scala 3. :)"
