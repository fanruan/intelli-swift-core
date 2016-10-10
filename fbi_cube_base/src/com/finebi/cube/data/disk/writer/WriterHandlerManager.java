package com.finebi.cube.data.disk.writer;

import com.finebi.cube.data.disk.NIOHandlerManager;
import com.finebi.cube.data.output.primitive.ICubePrimitiveWriter;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by wang on 2016/9/30.
 * 应该只能有一个线程在操作写，
 */
public class WriterHandlerManager implements NIOHandlerManager<ICubePrimitiveWriter> {
    ICubePrimitiveWriter writer = null;
    protected AtomicInteger countOfWriters = new AtomicInteger(0);
    final ReentrantLock lock = new ReentrantLock();
    protected boolean isForceReleased = false;

    public WriterHandlerManager(ICubePrimitiveWriter writer) {
        this.writer = writer;
    }
    @Override
    public ICubePrimitiveWriter queryHandler() {
        synchronized (this) {
            countOfWriters.getAndIncrement();
            return writer;
        }
    }

    @Override
    public void releaseHandler() {
        synchronized (this){
            int currentCountOfWriters = countOfWriters.decrementAndGet();
            if (currentCountOfWriters==0) {
                writer.releaseSource();
                countOfWriters.set(0);
            }else if (currentCountOfWriters < 0){
                countOfWriters.set(0);
            }
        }
    }
    @Override
    public void forceReleaseHandler(){
        synchronized (this) {
            isForceReleased = true;
            writer.releaseSource();
            countOfWriters.set(0);
        }
    }
    @Override
    public boolean isForceReleased() {
        return isForceReleased;
    }

    public void setForceReleased(boolean isForce){
        this.isForceReleased = isForce;
    }
}
