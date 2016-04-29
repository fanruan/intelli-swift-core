package com.finebi.cube;

/**
 * This class created on 2016/3/7.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubePosition<T extends Number> {
    private T position;

    public BICubePosition(T position) {
        this.position = position;
    }

    public T getPosition() {
        return position;
    }
}
