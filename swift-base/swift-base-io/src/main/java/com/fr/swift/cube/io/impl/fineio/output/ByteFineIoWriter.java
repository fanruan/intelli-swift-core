package com.fr.swift.cube.io.impl.fineio.output;

import com.fineio.accessor.FineIOAccessor;
import com.fineio.accessor.buffer.ByteBuf;
import com.fineio.accessor.file.IAppendFile;
import com.fineio.accessor.file.IWriteFile;
import com.fineio.accessor.impl.BaseModel;
import com.fineio.storage.Connector;
import com.fr.swift.cube.io.impl.fineio.connector.ConnectorManager;
import com.fr.swift.cube.io.output.ByteWriter;

import java.net.URI;

/**
 * @author anchore
 */
public class ByteFineIoWriter extends BaseFineIoWriter<ByteBuf> implements ByteWriter {
    private ByteFineIoWriter(URI uri, Connector connector, boolean isOverwrite) {
        super(isOverwrite);
        if (isOverwrite) {
            writeFile = (IWriteFile<ByteBuf>) FineIOAccessor.INSTANCE.createFile(connector, uri, BaseModel.ofByte().asWrite());
        } else {
            appendFile = (IAppendFile<ByteBuf>) FineIOAccessor.INSTANCE.createFile(connector, uri, BaseModel.ofByte().asAppend());
        }
    }

    public static ByteWriter build(URI location, boolean isOverwrite) {
        return new ByteFineIoWriter(
                location,
                ConnectorManager.getInstance().getConnector(),
                isOverwrite
        );
    }

    @Override
    public void put(long pos, byte val) {
        if (isOverwrite) {
            FineIOAccessor.INSTANCE.put(writeFile, pos, val);
        } else {
            FineIOAccessor.INSTANCE.put(appendFile, pos, val);
        }
    }
}