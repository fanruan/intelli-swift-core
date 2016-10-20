package com.finebi.cube.data.disk;


import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.data.ICubePrimitiveResourceDiscovery;
import com.finebi.cube.data.disk.reader.ReaderHandlerManager;
import com.finebi.cube.data.disk.writer.WriterHandlerManager;
import com.finebi.cube.data.disk.reader.primitive.BIPrimitiveNIOReaderManager;
import com.finebi.cube.data.disk.writer.primitive.BIPrimitiveNIOWriterManager;
import com.finebi.cube.data.input.primitive.ICubePrimitiveReader;
import com.finebi.cube.data.output.primitive.ICubePrimitiveWriter;
import com.finebi.cube.exception.BIBuildReaderException;
import com.finebi.cube.exception.BIBuildWriterException;
import com.finebi.cube.exception.IllegalCubeResourceLocationException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by wang on 2016/9/30.
 */
public class NIOResourceManager implements ICubePrimitiveResourceDiscovery {
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private BIPrimitiveNIOWriterManager writerManager = null;
    private BIPrimitiveNIOReaderManager readerManager = null;
    private ReaderHandlerManager readerHandlerManager;
    private WriterHandlerManager writerHandlerManager;

    public NIOResourceManager() {
        writerManager = BIPrimitiveNIOWriterManager.getInstance();
        readerManager = BIPrimitiveNIOReaderManager.getInstance();
    }

    public ReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }

    @Override
    public ICubePrimitiveReader getCubeReader(ICubeResourceLocation resourceLocation) throws IllegalCubeResourceLocationException, BIBuildReaderException {
        synchronized (this) {
            if (!isAvailable(readerHandlerManager) || (isAvailable(readerHandlerManager) && (readerHandlerManager.isForceReleased()))) {
                ICubePrimitiveReader reader = readerManager.buildCubeReader(resourceLocation);
//                readerHandlerManager = new ReaderHandlerManager(reader);
                readerHandlerManager = new ReaderHandlerManager(reader, resourceLocation);
                reader.setHandlerReleaseHelper(readerHandlerManager);
            }
            if (canGetReader()) {
                return readerHandlerManager.queryHandler();
            } else {
                BILoggerFactory.getLogger().warn("can't get reader: " + resourceLocation.getAbsolutePath());
                throw new RuntimeException("Writing,.Current can't get the resource reader");
            }
        }
    }

    @Override
    public ICubePrimitiveWriter getCubeWriter(ICubeResourceLocation resourceLocation) throws IllegalCubeResourceLocationException, BIBuildWriterException {
        synchronized (this) {
            if (!isAvailable(writerHandlerManager) || (isAvailable(writerHandlerManager) && writerHandlerManager.isForceReleased())) {
                ICubePrimitiveWriter writer = writerManager.buildCubeWriter(resourceLocation);
//              writerHandlerManager = new WriterHandlerManager(writer);
                writerHandlerManager = new WriterHandlerManager(writer, resourceLocation);
                writer.setHandlerReleaseHelper(writerHandlerManager);
            }
            if (canGetWriter()) {
                return writerHandlerManager.queryHandler();
            } else {
                BILoggerFactory.getLogger().warn("can't get writer: " + resourceLocation.getAbsolutePath());
                BILoggerFactory.getLogger().warn("force release reader: " + resourceLocation.getAbsolutePath());
//                throw new RuntimeException("Writing or reading ,Current can't get the resource writer");
                readerHandlerManager.forceReleaseHandler();
                return writerHandlerManager.queryHandler();
            }
        }
    }

    private boolean isAvailable(NIOHandlerManager nioHandler) {
        return nioHandler != null;
    }

    private boolean canGetReader() {
        return !isAvailable(writerHandlerManager) || writerHandlerManager.isHandlerEmpty();
//        return true;
    }

    private boolean canGetWriter() {
        return (!isAvailable(readerHandlerManager) || (readerHandlerManager.isHandlerEmpty()))
                &&
                (!isAvailable(writerHandlerManager) || writerHandlerManager.isHandlerEmpty());
//        return true;
    }

    //只在替换tcube时调用，
    public void forceRelease() {
        if (isAvailable(readerHandlerManager)) {
            readerHandlerManager.forceReleaseHandler();
        }
        if (isAvailable(writerHandlerManager)) {
            writerHandlerManager.forceReleaseHandler();
        }
    }

    public void printCountOfHandler() {
        if (isAvailable(readerHandlerManager)) {
            readerHandlerManager.printCountOfHandler();
        }
        if (isAvailable(writerHandlerManager)) {
            writerHandlerManager.printCountOfHandler();
        }
    }

    public void reSetHandlerValid() {
        if (isAvailable(readerHandlerManager)) {
//            reader use isValid
            readerHandlerManager.reSetHandlerValid(true);
            readerHandlerManager.setForceReleased(false);
        }
    }
}
