package com.staberinde.sscript.util;

import java.util.Objects;

public final class Pair<F,S> {
    private final F firstValue;
    private final S secondValue;

    private int hash = Integer.MAX_VALUE;

    public Pair(final F first, final S second) {
        this.firstValue = first;
        this.secondValue = second;
    }

    public F getFirstValue() {
        return firstValue;
    }

    public S getSecondValue() {
        return secondValue;
    }

    private int createHash(F firstValue, S secondValue) {
        return Objects.hash(firstValue, secondValue);
    }

    @Override
    public int hashCode() {
        if (hash == Integer.MAX_VALUE) {
            this.hash = createHash(firstValue, secondValue);
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Pair<?,?>) {
            Pair<?,?> pair = (Pair<?, ?>)obj;
            return getFirstValue().equals(pair.getFirstValue()) && getSecondValue().equals(pair.getSecondValue());
        }
        return false;
    }

    @Override
    public String toString() {
        return "Pair [ " + getFirstValue() + ", " + getSecondValue() + "]";
    }

    public static <A,B> Pair<A,B> of(A firstValue, B secondValue) {
        return new Pair<>(firstValue, secondValue);
    }
}
