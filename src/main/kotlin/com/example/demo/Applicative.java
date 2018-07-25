package com.example.demo;

import java.util.function.Function;

public abstract class Applicative<F, T> implements Functor<F, T> {

    protected abstract <U> Applicative<?, U> unit(U value);

    protected final <U> Function<Applicative<F, T>, Applicative<?, U>> apply(final Applicative<Function<T, U>, U> fa) {
        return ft -> {
            Function<T, U> f = fa.get();
            T t = ft.get();
            return unit(f.apply(t));
        };
    }
}