package com.fr.swift.cube.io.impl.fineio.output;

import com.fineio.accessor.FineIOAccessor;
import com.fineio.accessor.buffer.LongBuf;
import com.fineio.accessor.file.IAppendFile;
import com.fineio.accessor.file.IWriteFile;
import com.fineio.accessor.impl.BaseModel;
import com.fineio.storage.Connector;
import com.fr.swift.cube.io.impl.fineio.connector.ConnectorManager;
import com.fr.swift.cube.io.output.LongWriter;

import java.net.URI;

/**
 * @author anchore
 */
public class LongFineIoWriter extends BaseFineIoWriter<LongBuf> implements LongWriter {
    private LongFineIoWriter(URI uri, Connector connector, boolean isOverwrite) {
        super(isOverwrite);
        if (isOverwrite) {
            writeFile = (IWriteFile<LongBuf>) FineIOAccessor.INSTANCE.createFile(connector, uri, BaseModel.ofLong().asWrite());
        } else {
            appendFile = (IAppendFile<LongBuf>) FineIOAccessor.INSTANCE.createFile(connector, uri, BaseModel.ofLong().asAppend());
        }
    }

    public static LongWriter build(URI location, boolean isOverwrite) {
        return new LongFineIoWriter(
                location,
                ConnectorManager.getInstance().getConnector(),
                isOverwrite
        );
    }

    @Override
    public void put(long pos, long val) {
        if (isOverwrite) {
            FineIOAccessor.INSTANCE.put(writeFile, (int) pos, val);
        } else {
            FineIOAccessor.INSTANCE.put(appendFile, (int) pos, val);
        }
    }
}