package com.fr.swift.cloud.cube.io.impl.fineio.input;

import com.fineio.accessor.FineIOAccessor;
import com.fineio.accessor.buffer.IntBuf;
import com.fineio.accessor.file.IReadFile;
import com.fineio.accessor.impl.BaseModel;
import com.fineio.storage.Connector;
import com.fr.swift.cloud.cube.io.impl.fineio.connector.ConnectorManager;
import com.fr.swift.cloud.cube.io.input.IntReader;

import java.net.URI;

/**
 * @author anchore
 */
public class IntFineIoReader extends BaseFineIoReader<IntBuf> implements IntReader {
    private IntFineIoReader(URI uri, Connector connector) {
        readFile = (IReadFile<IntBuf>) FineIOAccessor.INSTANCE.createFile(connector, uri, BaseModel.ofInt().asRead());
    }

    public static IntReader build(URI location) {
        return new IntFineIoReader(
                location,
                ConnectorManager.getInstance().getConnector()
        );
    }

    @Override
    public int get(long pos) {
        return FineIOAccessor.INSTANCE.getInt(readFile, pos);
    }
}