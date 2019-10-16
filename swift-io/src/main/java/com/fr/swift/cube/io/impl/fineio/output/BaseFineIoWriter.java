package com.fr.swift.cube.io.impl.fineio.output;

import com.fineio.accessor.buffer.Buf;
import com.fineio.accessor.file.IAppendFile;
import com.fineio.accessor.file.IWriteFile;
import com.fr.swift.cube.io.output.Writer;
import com.fr.swift.util.IoUtil;

/**
 * @author anchore
 */
abstract class BaseFineIoWriter<B extends Buf> implements Writer {
    IWriteFile<B> writeFile;
    IAppendFile<B> appendFile;

    final boolean isOverwrite;

    BaseFineIoWriter(boolean isOverwrite) {
        this.isOverwrite = isOverwrite;
    }

    @Override
    public void release() {
        IoUtil.close(writeFile, appendFile);
    }
}