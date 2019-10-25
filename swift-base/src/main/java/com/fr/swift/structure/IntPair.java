package com.fr.swift.structure;

/**
 * @author anchore
 * @date 2018/2/24
 */
public class IntPair {
    private int key, value;

    public IntPair(int key, int value) {
        this.key = key;
        this.value = value;
    }

    public static IntPair of(int key, int value) {
        return new IntPair(key, value);
    }

    public int getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        IntPair intPair = (IntPair) o;

        if (key != intPair.key) {
            return false;
        }
        return value == intPair.value;
    }

    @Override
    public int hashCode() {
        int result = key;
        result = 31 * result + value;
        return result;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}