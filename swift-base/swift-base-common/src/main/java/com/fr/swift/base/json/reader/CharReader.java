package com.fr.swift.base.json.reader;

import com.fr.swift.base.json.exception.JsonParseException;

import java.io.IOException;
import java.io.Reader;

/**
 * @author yee
 * @date 2018-12-13
 */
public class CharReader {
    private static final int BUFFER_SIZE = 1024;
    private final char[] buffer;
    private final Reader reader;
    /**
     * buffer position:
     */
    private int pos = 0;
    /**
     * buffer ends:
     */
    private int size = 0;

    public CharReader(Reader reader) {
        this.buffer = new char[BUFFER_SIZE];
        this.reader = reader;
    }

    /**
     * 返回 pos 下标处的字符，并返回
     *
     * @return
     * @throws IOException
     */
    public char peek() throws IOException {
        if (this.pos == this.size) {
            // fill buffer:
            fillBuffer("EOF");
        }
        return this.buffer[this.pos];
    }

    /**
     * 返回 pos 下标处的字符，并将 pos + 1，最后返回字符
     *
     * @return
     * @throws IOException
     */
    public char next() throws IOException {
        if (this.pos == this.size) {
            // fill buffer:
            fillBuffer("EOF");
        }
        char ch = this.buffer[this.pos];
        this.pos++;
        return ch;
    }

    public void back() {
        pos = Math.max(0, --pos);
    }

    public boolean hasMore() throws IOException {
        if (pos < size) {
            return true;
        }
        // try fill:
        fillBuffer(null);
        return pos < size;
    }

    void fillBuffer(String eofErrorMessage) throws IOException {
        int n = reader.read(buffer);
        if (n == (-1)) {
            if (eofErrorMessage != null) {
                throw new JsonParseException(eofErrorMessage);
            }
            return;
        }
        this.pos = 0;
        this.size = n;
    }
}
