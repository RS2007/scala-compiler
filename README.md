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

- multiply by 7\*16

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

- Simple linked list as of now(no control flow)

#### Liveness Analysis

```scala
    var x = 3 // L1
    var y = 2 // L2
    var t1 = y - 5 // L3
    var z = x + t1 // L4
    var t3 = y - 5 // L5
    var t2 = x + t3 // L6
    print(t3) // L7
    print(z) // L8
```

- Dataflow solver
- Start from the bottom

- Maintain a queue
- Two hashmaps: in and out
- start at the bottom(push bottom most node to queue)

```
queue = {L8}
in = {}
out = {}

Process L8:
queue = {L7}
in = {L8:(z)}
out = {L8:()}}
L8 has changed push to the queue
queue = {L7,L8}

Process L7:
queue = {L6}
in = {L8:(z),L7:(t3,z)}
out = {L8:(),L7:(z)}
L7 has changed push to the queue
queue = {L8,L7}

Process L6:
queue = {L5}
in = {L8:(z),L7:(t3,z),L6: (x,t3,z)}
out = {L8:(),L7:(z),L6:(t3,z),L6:(t3,z)}
L6 has changed push to the queue
queue = {L8,L7,L6}

Process L5:
queue = {L4}
in = {L8:(z),L7:(t3,z),L6: (x,t3,z),L5: (z,x,y) }
out = {L8:(),L7:(z),L6:(t3,z),L6:(t3,z),L5:(t3,z,x)}
L5 has changed push to the queue
queue = {L8,L7,L6,L5}

Process L4:
queue = {L3}
in = {L8:(z),L7:(t3,z),L6: (x,t3,z),L5: (z,x,y), L4: (t1,x,y) }
out = {L8:(),L7:(z),L6:(t3,z),L6:(t3,z),L5:(t3,z,x), L4: (z,x,y)}
L4 has changed push to the queue
queue = {L8,L7,L6,L5,L4}

Process L3:
queue = {L2}
in = {L8:(z),L7:(t3,z),L6: (x,t3,z),L5: (z,x,y), L4: (t1,x,y), L3: (x,y) }
out = {L8:(),L7:(z),L6:(t3,z),L6:(t3,z),L5:(t3,z,x), L4: (z,x,y), L3: (t1,x,y)}

L3 has changed push to the queue
queue = {L8,L7,L6,L5,L4,L3}

Process L2:
queue = {L1}
in = {L8:(z),L7:(t3,z),L6: (x,t3,z),L5: (z,x,y), L4: (t1,x,y), L3: (x,y), L2: (x) }
out = {L8:(),L7:(z),L6:(t3,z),L6:(t3,z),L5:(t3,z,x), L4: (z,x,y), L3: (t1,x,y), L2: (x,y)}


L2 has changed push to the queue
queue = {L8,L7,L6,L5,L4,L3,L2}

Process L1:
queue = {L1}
in = {L8:(z),L7:(t3,z),L6: (x,t3,z),L5: (z,x,y), L4: (t1,x,y), L3: (x,y), L2: (x) }
out = {L8:(),L7:(z),L6:(t3,z),L6:(t3,z),L5:(t3,z,x), L4: (z,x,y), L3: (t1,x,y), L2: (x,y)}

L1 has changed push to the queue
queue = {L8,L7,L6,L5,L4,L3,L2,L1}

These queue on popping, will give the same results (this is cause there is no control flow as of now :) )

```

#### Construction of interference graph

![Interference graph](./test.svg)

- There is an optimized algorithm for this, but I am dumb
- Go $n^2$
- for each element in the liveout hashmap, create edges between all the elements

> [!WARNING]
> I am not following the book exactly (about to realize why thats a bad idea)
