package com.example.monad;

import java.util.NoSuchElementException;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public abstract class Maybe<T> extends Monad<Maybe<T>, T> {

    public static <U> Maybe<U> instance(U value) {
        return (Maybe<U>) (value != null ? Just.instance(value) : Nothing.instance());
    }

    @Override public <U> Maybe<U> bind(Function<T, ? extends Monad<?, U>> f) {
        return (Maybe<U>) super.bind(f);
    }

    @Override protected <U> Maybe<U> fail(RuntimeException e) {
        return Nothing.instance();
    }

    public abstract boolean isJust();

    public abstract boolean isNothing();
}

@SuppressWarnings("unchecked")
class Just<T> extends Maybe<T> {

    public static <U> Just<U> instance(U value) {
        return new Just<>(value);
    }

    private T value;

    private Just(T value) {
        this.value = value;
    }

    @Override public <U> U get() {
        return (U) value;
    }

    @Override protected <U> Maybe<U> unit(U value) {
        return Just.instance(value);
    }

    @Override public boolean isJust() {
        return true;
    }

    @Override public boolean isNothing() {
        return false;
    }

    @Override public String toString() {
        return "Just(" + get() + ")";
    }
}

@SuppressWarnings("unchecked")
class Nothing<T> extends Maybe<T> {

    private static final Nothing NOTHING = new Nothing();

    public static <T> Nothing<T> instance() {
        return NOTHING;
    }

    public static <T> Nothing<T> instance(Class<T> clazz) {
        return NOTHING;
    }

    @Override public <U> U get() {
        throw new NoSuchElementException("Nothing.get");
    }

    @Override protected <U> Maybe<U> unit(U value) {
        return NOTHING;
    }

    @Override protected <U> Maybe<U> yield(Function<T, U> f) {
        return NOTHING;
    }

    @Override protected <U> Maybe<U> join() {
        return NOTHING;
    }

    @Override public boolean isJust() {
        return false;
    }

    @Override public boolean isNothing() {
        return true;
    }

    @Override public String toString() {
        return "Nothing";
    }
}