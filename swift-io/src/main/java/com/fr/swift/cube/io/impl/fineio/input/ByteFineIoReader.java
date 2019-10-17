package com.fr.swift.cube.io.impl.fineio.input;

import com.fineio.accessor.FineIOAccessor;
import com.fineio.accessor.buffer.ByteBuf;
import com.fineio.accessor.file.IReadFile;
import com.fineio.accessor.impl.BaseModel;
import com.fineio.storage.Connector;
import com.fr.swift.cube.io.impl.fineio.connector.ConnectorManager;
import com.fr.swift.cube.io.input.ByteReader;

import java.net.URI;

/**
 * @author anchore
 */
public class ByteFineIoReader extends BaseFineIoReader<ByteBuf> implements ByteReader {
    private ByteFineIoReader(URI uri, Connector connector) {
        readFile = (IReadFile<ByteBuf>) FineIOAccessor.INSTANCE.createFile(connector, uri, BaseModel.ofByte().asRead());
    }

    public static ByteReader build(URI location) {
        return new ByteFineIoReader(
                location,
                ConnectorManager.getInstance().getConnector()
        );
    }

    @Override
    public byte get(long pos) {
        return FineIOAccessor.INSTANCE.getByte(readFile, (int) pos);
    }
}