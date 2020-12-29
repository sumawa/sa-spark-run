# SPARKRUN LEARNINGS

### FP and effects: Major theme in FP is separating the
* Description of the program: pure description of the program "I give you the names, you give me back IDs"
* Execution of the program: running the actual code and asking it to go to the DB.

The code that does not have effect is pure. FP is about writing pure functions.

#### Benefit or writing this way is to achieve separation of concerns between the IO parts and rest of our code.

### Effect is the way (the recipe) to produce the Output (i.e go to the DB and get me a record back.
IO value allows separation of two concerns:
* Description: IO is a simple immutable value, it’s a recipe to get a type A by doing some kind of IO(network/filesystem/…)
* Execution: in order for IO to do something, you need to execute/run it using io.unsafeRunSync

#### The code that does not have effect is pure. FP is about writing pure functions.

### Pure functions characteristics
* Like Mathematical functions : y = f(x)  where y is a set of outputs for each set of inputs x
* Deterministic: Given the same input x returns the same output y.
* Referentially transparent: An expression is referentially transparent if it can be replaced with its corresponding value without changing the program’s behaviour.
* Example, if f(x) = x * x, we can replace f(6) with 36 in the program. 

### The kinds of things that break these properties are side effects
* directly accessing or changing mutable state (e.g. maintaining a var in a class or using a legacy API that is impure)
* communicating with external resources (e.g. files or network lookup)
* or throwing and catching exceptions.

#### Using cats effect libraries We can define a simple safe F[_] execution context
For example from the source code of cats.effect.IO

```
object IO extends IOInstances {

  /**
    * Suspends a synchronous side effect in `IO`.
    *
    * Alias for `IO.delay(body)`.
    */
  def apply[A](body: => A): IO[A] =
    delay(body)
   
}
```
It lazily evaluates a thunk (body: => A), the concept is derived from Haskell's call by name. 

(Since, most of the Scala and Cats etc is copied from Haskell) 
Haskell uses call-by-name evaluation, or lazy-evaluation to evaluate expressions

<a href="https://wiki.haskell.org/Thunk" target="_blank">Haskell Wiki: Thunk</a>




	
