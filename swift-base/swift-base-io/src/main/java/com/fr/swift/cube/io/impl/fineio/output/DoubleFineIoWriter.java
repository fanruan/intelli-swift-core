package com.fr.swift.cube.io.impl.fineio.output;

import com.fineio.accessor.FineIOAccessor;
import com.fineio.accessor.buffer.DoubleBuf;
import com.fineio.accessor.file.IAppendFile;
import com.fineio.accessor.file.IWriteFile;
import com.fineio.accessor.impl.BaseModel;
import com.fineio.storage.Connector;
import com.fr.swift.cube.io.impl.fineio.connector.ConnectorManager;
import com.fr.swift.cube.io.output.DoubleWriter;

import java.net.URI;

/**
 * @author anchore
 */
public class DoubleFineIoWriter extends BaseFineIoWriter<DoubleBuf> implements DoubleWriter {
    private DoubleFineIoWriter(URI uri, Connector connector, boolean isOverwrite) {
        super(isOverwrite);
        if (isOverwrite) {
            writeFile = (IWriteFile<DoubleBuf>) FineIOAccessor.INSTANCE.createFile(connector, uri, BaseModel.ofDouble().asWrite());
        } else {
            appendFile = (IAppendFile<DoubleBuf>) FineIOAccessor.INSTANCE.createFile(connector, uri, BaseModel.ofDouble().asAppend());
        }
    }

    public static DoubleWriter build(URI location, boolean isOverwrite) {
        return new DoubleFineIoWriter(
                location,
                ConnectorManager.getInstance().getConnector(),
                isOverwrite
        );
    }

    @Override
    public void put(long pos, double val) {
        if (isOverwrite) {
            FineIOAccessor.INSTANCE.put(writeFile, (int) pos, val);
        } else {
            FineIOAccessor.INSTANCE.put(appendFile, val);
        }
    }
}