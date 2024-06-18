# A Compiler from Essentials of Compilation

Interesting book and interesting language, hence this.

### Usage

This is a normal sbt project. You can compile code with `sbt compile`, run it with `sbt run`, and `sbt console` will start a Scala 3 REPL.


### Notes

- How to generate first stack offset(TAC would have been very nice):
    - Calculate all locals, function arguments and returns  

Lets consider an example:
```scala
    var x = 3
    var y = 2
    var z = x + y
    print(x+y)
    print(z)
```
- countLocals:
    - 1: 1
    - 2: 1
    - 3: 2
    - 4: 2
    - 5: 1

- total locals = 7

- multiply by 7*16

- assembly generated:

```arm
sub sp,sp, 112
mov x0,3
str x0,[sp]
mov x0,2
str x0,[sp,16]
ldr x1,[sp]
ldr x2,[sp,16]
add x0,x1,x2
str x0,[sp,32]
ldr x1,[sp]
ldr x2,[sp,16]
add x0,x1,x2
str x0,[sp,48]
add sp,sp,112

```


#### Conversion to TAC

- assign all locals to temp

```scala
    var x = 3
    var y = 2
    var z = x + y - 5
    print(x+y - 5)
    print(z)
```

- Simple tree rewrite(can use the same parser,not a good practice though)

```scala
    var x = 3
    var y = 2
    // all infix expressions assigned to a temp
    var t1 = y - 5
    var z = x + t1
    var t3 = y - 5
    var t2 = x + t3
    print(t3)
    print(z)
```

#### Constructing a dataflow graph



#### Construction of interference graph



