package com.fr.swift.cloud.cube.io.impl.fineio.output;

import com.fineio.accessor.FineIOAccessor;
import com.fineio.accessor.buffer.IntBuf;
import com.fineio.accessor.file.IAppendFile;
import com.fineio.accessor.file.IWriteFile;
import com.fineio.accessor.impl.BaseModel;
import com.fineio.storage.Connector;
import com.fr.swift.cloud.cube.io.impl.fineio.connector.ConnectorManager;
import com.fr.swift.cloud.cube.io.output.IntWriter;

import java.net.URI;

/**
 * @author anchore
 */
public class IntFineIoWriter extends BaseFineIoWriter<IntBuf> implements IntWriter {
    private IntFineIoWriter(URI uri, Connector connector, boolean isOverwrite) {
        super(isOverwrite);
        if (isOverwrite) {
            writeFile = (IWriteFile<IntBuf>) FineIOAccessor.INSTANCE.createFile(connector, uri, BaseModel.ofInt().asWrite());
        } else {
            appendFile = (IAppendFile<IntBuf>) FineIOAccessor.INSTANCE.createFile(connector, uri, BaseModel.ofInt().asAppend());
        }
    }

    public static IntWriter build(URI location, boolean isOverwrite) {
        return new IntFineIoWriter(
                location,
                ConnectorManager.getInstance().getConnector(),
                isOverwrite
        );
    }

    @Override
    public void put(long pos, int val) {
        if (isOverwrite) {
            FineIOAccessor.INSTANCE.put(writeFile, pos, val);
        } else {
            FineIOAccessor.INSTANCE.put(appendFile, pos, val);
        }
    }
}