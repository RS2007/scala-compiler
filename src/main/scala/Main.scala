import core.*;
import java.util.HashMap;
import java.nio.file.{Files, Paths}
import java.nio.charset.StandardCharsets
import scala.sys.process._

@main def hello(): Unit =
  val lexer = new Lexer()
  lexer.tokenize("var ta = 3 \n var x = ta \n var tb = 2 \n var y = tb \n var td = x \n var tf = y \n var tg = 5 \n var te = tf - tg \n var tc = td + te \n print ( tc )");

  val parser = new Parser();
  val program = parser.parse(lexer);
  var scope = new HashMap[String,Int]()
  var symTab = new HashMap[String,Int]()
  println("Interpreter output: "+program.evaluate(scope));

  val lexerTAC = new Lexer()
  lexerTAC.tokenize(program.codegenTAC(symTab))
  val programTAC = parser.parse(lexerTAC)

  val generated = programTAC.codegen(symTab);
  val filePath = Paths.get("test3.s")
  Files.write(filePath, generated.getBytes(StandardCharsets.UTF_8))
  val command = "gcc -g -o test3 test3.s" // Command to list files and directories
  command.!!

def msg = "I was compiled by Scala 3. :)"
