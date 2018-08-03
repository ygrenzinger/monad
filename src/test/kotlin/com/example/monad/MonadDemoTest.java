package com.example.monad;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

public class MonadDemoTest {

    //Link : https://github.com/frankiesardo/monads

    @Test
    public void maybe_monad() {
        Monad<?, String> stringMonad = Maybe.instance("monad")
                .map(String::toUpperCase)
                .bind(s -> Maybe.instance(s.concat(" bind")));
        Assert.assertEquals("MONAD bind", stringMonad.get());
    }

    @Test
    public void maybe_monad_with_nothing() {
        Nothing<String> instance = Nothing.instance(String.class);
        Monad<?, String>  stringMonad = Maybe.instance("string")
                .bind(s -> instance)
                .bind((String s) -> Maybe.instance(s.concat(" bind")));
        Assert.assertEquals(instance, stringMonad);                   
    }

    private static <U> CompletableFuture<U> futureValue(U value) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Supplying value " + value);
            return value;
        });
    }

    @Test
    public void future_monad() {
        Monad<?, String> futureMonad = FutureMonad.instance(futureValue("MONAD"))
                .map(String::toUpperCase)
                .bind((String s) -> FutureMonad.instance(futureValue(s.concat(" bind"))));
        Assert.assertEquals("MONAD bind", futureMonad.get());
    }
}
