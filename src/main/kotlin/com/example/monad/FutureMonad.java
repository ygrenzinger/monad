package com.example.monad;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FutureMonad<T> extends Monad<FutureMonad<T>, T> {

    private Future<T> future;

    private FutureMonad(Future<T> future) {
        this.future = future;
    }

    public static <U> FutureMonad<U> instance(Future<U> future) {
        return new FutureMonad<U>(future);
    }

    public static <U> FutureMonad<U> instance(U value) {
        return new FutureMonad<U>(CompletableFuture.supplyAsync(() -> value));
    }

    @Override
    public T get() {
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
         
    }

    @Override
    protected <U> Applicative<FutureMonad<U>, U> unit(U value) {
        return instance(CompletableFuture.completedFuture(value));
    }
}
