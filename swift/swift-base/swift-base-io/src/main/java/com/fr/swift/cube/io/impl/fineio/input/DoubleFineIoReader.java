package com.fr.swift.cube.io.impl.fineio.input;

import com.fineio.FineIO;
import com.fineio.io.DoubleBuffer;
import com.fineio.storage.Connector;
import com.fr.swift.cube.io.impl.fineio.connector.ConnectorManager;
import com.fr.swift.cube.io.input.DoubleReader;

import java.net.URI;

/**
 * @author anchore
 */
public class DoubleFineIoReader extends BaseFineIoReader<DoubleBuffer> implements DoubleReader {
    private DoubleFineIoReader(URI uri, Connector connector) {
        this.ioFile = FineIO.createIOFile(connector, uri, FineIO.MODEL.READ_DOUBLE);
    }

    public static DoubleReader build(URI location) {
        return new DoubleFineIoReader(
                location,
                ConnectorManager.getInstance().getConnector()
        );

    }

    @Override
    public double get(long pos) {
        return FineIO.getDouble(ioFile, pos);
    }
}