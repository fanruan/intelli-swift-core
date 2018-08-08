package com.fr.swift.http.servlet;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class created on 2018/6/14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class DelegatingServletOutputStream extends ServletOutputStream {

    private final OutputStream targetStream;


    /**
     * Create a DelegatingServletOutputStream for the given target stream.
     * @param targetStream the target stream (never <code>null</code>)
     */
    public DelegatingServletOutputStream(OutputStream targetStream) {
        this.targetStream = targetStream;
    }

    /**
     * Return the underlying target stream (never <code>null</code>).
     */
    public final OutputStream getTargetStream() {
        return this.targetStream;
    }


    public void write(int b) throws IOException {
        this.targetStream.write(b);
    }

    public void flush() throws IOException {
        super.flush();
        this.targetStream.flush();
    }

    public void close() throws IOException {
        super.close();
        this.targetStream.close();
    }
}