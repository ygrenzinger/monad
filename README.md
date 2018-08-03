# Functor -> Applicative -> Monad typeclasses

*Most of this content comes from [Haskell from the first principle](http://haskellbook.com/)*

## Some definitions

A __typeclass__ is a sort of interface that defines some behavior. If a type is a part of a typeclass, that means that it supports and implements the behavior the typeclass describes. 

Typeclasses correspond to sets of types which have certain operations defined for them, and type class polymorphic functions work only for types which are instances of the type class(es) in question. Typeclasses can have __laws__.

```haskell
class Eq a where
  (==) :: a -> a -> Bool
  (/=) :: a -> a -> Bool
  x /= y = not (x == y)

(==) :: Eq a => a -> a -> Bool
```

*Higher-kinded polymorphism* is polymorphism which has a type variable abstracting over types of a higher kind. Functor is an example of higher kinded polymorphism because the kind of the __f__ parameter to Functor is * -> *.

## Functor

A functor maps a function over some structure (commonly called __f__) to apply. Another way to see it is we __lift__ a function into a context.

```haskell
class Functor f where 
    fmap::(a->b)->fa->fb
```

Laws
- Identity : fmap id == id
- Composition : fmap (f . g) == fmap f . fmap g

http://www.haskellforall.com/2012/09/the-functor-design-pattern.html
https://en.wikibooks.org/wiki/Haskell/The_Functor_class


## Applicative
Applicative is a monoidal functor. The Applicative typeclass allows for function application lifted over structure (like Functor) but with Applicative the function we’re applying is also embedded in some structure. Because the function and the value it’s being applied to both have structure, we have to smash those structures together. So, Applicative involves monoids and functors.

Another explanation : an applicative maps a function that is contained over some structure over some structure and then mappends the two bits of structure.

```haskell
class Functor f => Applicative f where 
    pure :: a -> f a
    (<*>) :: f (a -> b) -> f a -> f b
```

Laws :
- Identity : pure id <\*> v = v - ex: (pure id) <\*> (Just "abc") = Just "abc"
- Composition : pure (.) <\*> u <\*> v <\*> w = u <\*> (v <\*> w) - ex: pure (.) <\*> (Just head) <\*> (Just head) <\*> (Just [[1]]) = (Just head) <\*> ((Just head) <\*> (Just [[1]]))
- Homomorphism : pure f <\*> pure x = pure (f x) - A homomorphism is a structure-preserving map between two categories. The effect of applying a function that is embedded in some structure to a value that is embedded in some structure should be the same as applying a function to a value without affecting any outside structure - ex: pure (+1) <\*> pure 1 = pure ((+1) 1)
- Interchange : u <\*> pure y = pure ($ y) <\*> u - ex: Just (+2) <\*> pure 2 = pure ($ 2) <\*> Just (+2)

Use case example:

```haskell
validateLength :: Int -> String -> Maybe String 
validateLength maxLen s =
    if (length s) > maxLen 
    then Nothing
    else Just s
    
newtype Name = Name String deriving (Eq, Show) 
newtype Address = Address String deriving (Eq, Show)

mkName :: String -> Maybe Name
mkName s = fmap Name $ validateLength 25 s 

mkAddress :: String -> Maybe Address
mkAddress a = fmap Address $ validateLength 100 a

data Person = Person Name Address deriving (Eq, Show)

mkPerson :: String -> String -> Maybe Person 
mkPerson n a =
    case mkName n of 
        Nothing -> Nothing 
        Just n' ->
            case mkAddress a of 
                Nothing -> Nothing 
                Just a' ->
                    Just $ Person n' a'

mkPerson' :: String -> String -> Maybe Person 
mkPerson' n a = Person <$> mkName n <*> mkAddress a
```

Another useful function:
Control.Applicative defines a function that's called liftA2, which has a type of liftA2 :: (Applicative f) => (a -> b -> c) -> f a -> f b -> f c

## Monad

"Monad" refers to a particular pattern of composition that can be implemented on types with certain higher-kinded type constructors. The entirety of the concept is tied up in the types of a couple operations and the rules for how those operations must interact with themselves and each other.

> Monads in Haskell can be thought of as composable computation descriptions. The essence of monad is thus separation of composition timeline from the composed computation's execution timeline, as well as the ability of computation to implicitly carry extra data, as pertaining to the computation itself, in addition to its one (hence the name) output, that it will produce when run (or queried, or called upon). This lends monads to supplementing pure calculations with features like I/O, common environment, updatable state, etc.

> Monads in Haskell are used as a mechanism for scheduling evaluation, thus turning a language with a hard-to-predict evaluation strategy into a language with predictable, sequential, interactions. This makes it possible to add interactive computations such as state and input-output to the language, noting that benign (non-interactive) computations are already a part of the language, transparent to the type system.

- (>>=) :: m a -> (a -> m b) -> m b 
- (>>) :: m a -> m b -> m b
- return :: a -> m a

Monads are applicative functors : Functor -> Applicative -> Monad

- fmap :: Functor f      =>   (a -> b) -> f a        -> f b 
- <\*>  :: Applicative f => f (a -> b) -> f a        -> f b 
- \>\>=  :: Monad f      => f a        -> (a -> f b) -> f b

![Functor -> Applicative -> Monad](https://i.stack.imgur.com/KifMX.png)

Some example in the REPL:
```haskell
putStrLn "Hello, " >> putStrLn "World!"

binding :: IO ()
binding = do
    name <- getLine 
    putStrLn name

binding' :: IO ()
binding' = getLine >>= putStrLn

:t putStrLn <$> getLine
import Control.Monad
join $ putStrLn <$> getLine

bindingAndSequencing :: IO ()
bindingAndSequencing = do
    putStrLn "name pls:"
    name <- getLine
    putStrLn ("y helo thar: " ++ name)

bindingAndSequencing' :: IO ()
bindingAndSequencing' = putStrLn "name pls:" >> getLine >>= \name -> putStrLn ("hello: " ++ name)
```

Laws
- right identity : m >>= return = m
- left identity : return x >>= f = fx
- associativity : (m >>= f) >>= g = m >>= (\x -> f x >>= g)

```haskell
data Cow = Cow {
    name :: String
    , age :: Int
    , weight :: Int
} deriving (Eq, Show)

noEmpty :: String -> Maybe String 
noEmpty "" = Nothing
noEmpty str = Just str

noNegative :: Int -> Maybe Int 
noNegative n 
    | n >= 0 = Just n
    | otherwise = Nothing

-- if Cow's name is Bess, must be under 500
weightCheck :: Cow -> Maybe Cow 
weightCheck c =
    let w = weight c n = name c
    in if n == "Bess" && w > 499 
        then Nothing
        else Just c

mkSphericalCow :: String -> Int -> Int -> Maybe Cow 
mkSphericalCow name' age' weight' =
    case noEmpty name' of 
        Nothing -> Nothing 
        Just nammy ->
            case noNegative age' of 
                Nothing -> Nothing 
                Just agey ->
                    case noNegative weight' of 
                        Nothing -> Nothing
                        Just weighty ->
                            weightCheck (Cow nammy agey weighty)

-- Prelude> mkSphericalCow "Bess" 5 499
-- Just (Cow {name = "Bess", age = 5, weight = 499})
-- Prelude> mkSphericalCow "Bess" 5 500
-- Nothing

mkSphericalCow' :: String -> Int -> Int -> Maybe Cow 
mkSphericalCow' name' age' weight' = do
    nammy <- noEmpty name'
    agey <- noNegative age'
    weighty <- noNegative weight' 
    weightCheck (Cow nammy agey weighty)
```

http://learnyouahaskell.com/a-fistful-of-monads
https://en.wikibooks.org/wiki/Haskell/Understanding_monads
http://dev.stephendiehl.com/hask/#monads
http://www.stephendiehl.com/posts/monads.html
https://www.schoolofhaskell.com/user/mutjida/order-of-evaluation
http://jelv.is/blog/Haskell-Monads-and-Purity/
http://danghica.blogspot.com/2018/07/haskell-if-monads-are-solution-what-is.html
https://github.com/frankiesardo/monads

## Arrow Kotlin lib defs

Datatypes
A datatype is a class that encapsulates one reusable coding pattern. These solutions have a canonical implementation that is generalised for all possible uses.

Typeclasses
Typeclasses define a set of functions associated to one type. This behavior is checked by a test suite called the “laws” for that typeclass.
You can use typeclasses as a DSL to add new free functionality to an existing type or treat them as an abstraction placeholder for any one type that can implement the typeclass.
Examples of these behaviors are: comparability (Eq), composability (Monoid), its contents can be mapped from one type to another (Functor), or error recovery (MonadError).

Instances
A single implementation of a typeclass for a specific datatype or class. Because typeclasses require generic parameters each implementation is meant to be unique for that parameter.

Higher Kinds
In a Higher Kind with the shape Kind<F, A>, if A is the type of the content then F has to be the type of the container.
