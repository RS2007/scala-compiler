package core;

import core.AST
import scala.collection.mutable.ListBuffer

object CFG {


  class Node: 
    var astNode: Option[AST.Node] = None
    var successors: ListBuffer[Node] = new ListBuffer()
    var _start: Boolean = false
    var _end: Boolean = false

  def create(astNode: AST.Node): Node = {
    val node = new Node()
    node.astNode = Some(astNode)
    node
  }

  def start(): Node = {
    val node = new Node()
    node._start = true
    node
  }

  def end(): Node = {
    val node = new Node()
    node._end = true;
    node
  }

  def prettyPrint(_cfg: Node): Unit = {
    var cfg = _cfg;
    assert(cfg._start,"Call pretty print only on start nodes");
    println("start");
    while(cfg.successors.length > 0){
      cfg.successors.foreach((node: CFG.Node)=>{
        if(node._start)
          println("start")
        else if(node._end)
          println("end")
        else
          println(node.astNode)
      })
      cfg = cfg.successors(0)
    }
  }

}
