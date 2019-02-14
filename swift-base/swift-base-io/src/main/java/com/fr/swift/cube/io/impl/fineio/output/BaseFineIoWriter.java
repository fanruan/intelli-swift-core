package com.fr.swift.cube.io.impl.fineio.output;

import com.fineio.io.Buffer;
import com.fineio.io.file.IOFile;
import com.fr.swift.cube.io.output.Writer;

/**
 * @author anchore
 */
abstract class BaseFineIoWriter<Buf extends Buffer> implements Writer {
    IOFile<Buf> ioFile;

    @Override
    public void flush() {
        ioFile.close();
    }

    @Override
    public void release() {
        if (ioFile != null) {
            flush();
            ioFile = null;
        }
    }
}