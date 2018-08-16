package com.fr.swift.util;

import com.fr.swift.util.function.Consumer;

import java.util.NoSuchElementException;

/**
 * @author anchore
 * @date 2018/8/16
 */
public final class Optional<T> {
    private static final Optional<?> EMPTY = new Optional();

    private boolean isPresent;

    private T value;

    private Optional() {
        this.isPresent = false;
    }

    private Optional(T value) {
        this.isPresent = true;
        this.value = value;
    }

    public static <T> Optional<T> empty() {
        return (Optional<T>) EMPTY;
    }

    public static <T> Optional<T> of(T value) {
        return new Optional<T>(value);
    }

    public T get() {
        if (isPresent()) {
            return value;
        }
        throw new NoSuchElementException("No value present");
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void ifPresent(Consumer<? super T> consumer) {
        if (isPresent()) {
            consumer.accept(value);
        }
    }

    public T orElse(T other) {
        return isPresent() ? value : other;
    }

    @Override
    public String toString() {
        return isPresent()
                ? String.format("Optional[%s]", value)
                : "Optional.empty";
    }
}