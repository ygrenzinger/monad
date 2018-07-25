package com.example.demo;

import org.junit.Assert;
import org.junit.Test;

public class MonadDemoTest {

    //Link : https://github.com/frankiesardo/monads

    @Test
    public void maybe_monad() {
        Monad<?, String> stringMonad = Maybe.instance("string")
                .map(String::toUpperCase)
                .bind(s -> Maybe.instance(s.concat(" bind")));
        Assert.assertEquals("STRING bind", stringMonad.get());
    }

    @Test
    public void maybe_monad_with_nothing() {
        Nothing<String> instance = Nothing.instance(String.class);
        Maybe<String> stringMonad = Maybe.instance("string")
                .bind(s -> instance)
                .bind(s -> Maybe.instance(s.concat(" bind")));
        Assert.assertEquals(instance, stringMonad);
    }
}
