package com.fr.swift.cube.io.impl.fineio.input;

import com.fineio.FineIO;
import com.fineio.io.IntBuffer;
import com.fineio.storage.Connector;
import com.fr.swift.cube.io.impl.fineio.connector.ConnectorManager;
import com.fr.swift.cube.io.input.IntReader;

import java.net.URI;

/**
 * @author anchore
 */
public class IntFineIoReader extends BaseFineIoReader<IntBuffer> implements IntReader {
    private IntFineIoReader(URI uri, Connector connector) {
        this.ioFile = FineIO.createIOFile(connector, uri, FineIO.MODEL.READ_INT);
    }

    public static IntReader build(URI location) {
        return new IntFineIoReader(
                location,
                ConnectorManager.getInstance().getConnector()
        );
    }

    @Override
    public int get(long pos) {
        return FineIO.getInt(ioFile, pos);
    }
}