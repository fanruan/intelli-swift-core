package com.fr.swift.cube.io.impl.fineio.input;

import com.fineio.FineIO;
import com.fineio.io.ByteBuffer;
import com.fineio.storage.Connector;
import com.fr.swift.cube.io.impl.fineio.connector.ConnectorManager;
import com.fr.swift.cube.io.input.ByteReader;

import java.net.URI;

/**
 * @author anchore
 */
public class ByteFineIoReader extends BaseFineIoReader<ByteBuffer> implements ByteReader {
    private ByteFineIoReader(URI uri, Connector connector) {
        ioFile = FineIO.createIOFile(connector, uri, FineIO.MODEL.READ_BYTE);
    }

    public static ByteReader build(URI location) {
        return new ByteFineIoReader(
                location,
                ConnectorManager.getInstance().getConnector()
        );
    }

    @Override
    public byte get(long pos) {
        return FineIO.getByte(ioFile, pos);
    }
}