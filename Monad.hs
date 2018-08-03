-- Two datatypes
data Optional a = Some a | Empty deriving (Eq, Show)
data Or a b = A a | B b deriving (Eq, Show)

-- functor instances
instance Functor Optional where
  fmap f (Some a) = Some (f a)
  fmap _ Empty = Empty

instance Functor (Or a) where
    fmap _ (A a) = A a
    fmap f (B b) = B (f b)

-- applicative instances
instance Applicative Optional where
    pure = Some
    (Some f) <*> (Some a) = Some (f a)
    Empty <*> _ = Empty
    _ <*> Empty = Empty

instance Applicative (Or e) where
    pure          = B
    A  e <*> _ = A e
    B f <*> r = fmap f r

-- monad instances
instance Monad Optional where
    return x = Some x
    (Some x) >>= f = f x
    Empty >>= _ = Empty

instance Monad (Or e) where
    A l >>= _ = A l
    B r >>= f = f r

-- import Data.Char
-- pure (map toUpper) :: Optional ([Char] -> [Char])
