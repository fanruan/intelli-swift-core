package com.fr.swift.cube.io.impl.fineio.connector;

import com.fineio.FineIO;
import com.fineio.FineIO.MODEL;
import com.fineio.io.file.IOFile;
import com.fineio.io.file.ReadIOFile;
import com.fineio.storage.Connector;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.Util;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yee
 * @date 2017/8/15
 */
public class FineIODeleter {
    private static final int DEFAULT_POOL_SIZE = 5;
    private ExecutorService deleter;
    private Connector connector;

    public FineIODeleter() {
        try {
            connector = ConnectorManager.getInstance().getConnector();
            deleter = Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
            Crasher.crash(e);
        }
    }

    public void delete(final String path) {
        Util.requireNonNull(path);
        deleter.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    IOFile ioFile = FineIO.createIOFile(connector, URI.create(path), MODEL.READ_BYTE);
                    ((ReadIOFile) ioFile).delete();
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e);
                    Crasher.crash(e);
                }
            }
        });
    }

    public void clear() {
        deleter.shutdown();
    }
}
