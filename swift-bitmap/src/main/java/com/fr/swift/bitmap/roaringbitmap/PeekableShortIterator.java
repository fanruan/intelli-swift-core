/*
 * (c) the authors Licensed under the Apache License, Version 2.0.
 */
package com.fr.swift.bitmap.roaringbitmap;


/**
 * Simple extension to the ShortIterator interface
 */
public interface PeekableShortIterator extends ShortIterator {
    /**
     * If needed, advance as long as the next value is smaller than minval (as an unsigned
     * short)
     *
     * @param minval threshold
     */
    void advanceIfNeeded(short minval);

    /**
     * Look at the next value without advancing
     *
     * @return next value
     */
    short peekNext();

    /**
     * Creates a copy of the iterator.
     *
     * @return a clone of the current iterator
     */
    @Override
    PeekableShortIterator clone();
}
