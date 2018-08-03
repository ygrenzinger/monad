package com.example.monad;

import java.util.function.Function;

@SuppressWarnings("unchecked")
public abstract class Monad<M, T> extends Applicative<M, T> {

    protected <U> Monad<?, U> yield(Function<T, U> f) {
        return (Monad<?, U>) apply((Applicative<Function<T, U>, U>) unit(f)).apply(this);
    }

    protected <U> Monad<?, U> join() {
        return get();
    }

    protected <U> Monad<?, U> fail(RuntimeException e) {
        throw e;
    }

    public <U> Function<Monad<M, T>, Monad<?, U>> fmap(final Function<T, U> f) {
        return mt -> {
            try {
                return mt.yield(f);
            } catch (RuntimeException e) {
                return mt.fail(e);
            }
        };
    }

    public <U> Monad<?, U> map(Function<T, U> f) {
        return fmap(f).apply(this);
    }

    public <U> Monad<?, U> bind(Function<T, ? extends Monad<?, U>> f) {
        return map(f).join();
    }
}