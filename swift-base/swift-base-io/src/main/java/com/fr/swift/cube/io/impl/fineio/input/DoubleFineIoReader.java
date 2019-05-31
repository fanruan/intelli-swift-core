package com.fr.swift.cube.io.impl.fineio.input;

import com.fineio.accessor.FineIOAccessor;
import com.fineio.accessor.buffer.DoubleBuf;
import com.fineio.accessor.file.IReadFile;
import com.fineio.accessor.impl.BaseModel;
import com.fineio.storage.Connector;
import com.fr.swift.cube.io.impl.fineio.connector.ConnectorManager;
import com.fr.swift.cube.io.input.DoubleReader;

import java.net.URI;

/**
 * @author anchore
 */
public class DoubleFineIoReader extends BaseFineIoReader<DoubleBuf> implements DoubleReader {
    private DoubleFineIoReader(URI uri, Connector connector) {
        readFile = (IReadFile<DoubleBuf>) FineIOAccessor.INSTANCE.createFile(connector, uri, BaseModel.ofDouble().asRead());
    }

    public static DoubleReader build(URI location) {
        return new DoubleFineIoReader(
                location,
                ConnectorManager.getInstance().getConnector()
        );

    }

    @Override
    public double get(long pos) {
        return FineIOAccessor.INSTANCE.getDouble(readFile, (int) pos);
    }
}