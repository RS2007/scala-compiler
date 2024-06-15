package core;

enum Token {
  case Identifier(name: String)
  case IntLiteral(value: Int)
  case StringLiteral(value: String)
  case Var
  case Assign
  case Plus
  case Minus
  case LParen
  case RParen
  case Print
  case Invalid
  case EOF
};
