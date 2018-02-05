package com.fr.swift.cube.io;

/**
 * A <tt>Flushable</tt> is a destination of data that can be flushed.  The
 * flush method is invoked to write any buffered output to the underlying
 * stream.
 *
 * @author anchore
 * @date 2018/1/22
 */
public interface Flushable {
    /**
     * Flushes this by writing any buffered output to the underlying
     * stream.
     */
    void flush();
}