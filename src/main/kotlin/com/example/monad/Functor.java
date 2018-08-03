package com.example.monad;

import java.util.function.Function;

public interface Functor<F, T> {
    <U> Function<? extends Functor<F, T>, ? extends Functor<?, U>> fmap(Function<T, U> f);
    <U> U get();
}
