package token;

enum Token {
  case Identifier(name: String)
  case IntLiteral(value: Int)
  case StringLiteral(value: String)
  case Plus
  case Minus
  case LParen
  case RParen
  case Print
  case EOF
};
