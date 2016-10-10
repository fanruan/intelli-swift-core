package com.finebi.cube.data.disk.reader;

import com.finebi.cube.data.disk.NIOHandlerManager;
import com.finebi.cube.data.input.primitive.ICubePrimitiveReader;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by wang on 2016/9/30.
 */
public class ReaderHandlerManager implements NIOHandlerManager<ICubePrimitiveReader> {
    ICubePrimitiveReader reader = null;
    protected AtomicInteger countOfReaders = new AtomicInteger(0);
    protected final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    protected boolean isForceReleased = false;

    public ReaderHandlerManager(ICubePrimitiveReader reader){
        this.reader = reader;
    }
    @Override
    public ICubePrimitiveReader queryHandler() {
        try {
            readWriteLock.readLock().lock();
            countOfReaders.incrementAndGet();
            return reader;
        }catch (Exception e){
            throw BINonValueUtils.beyondControl(e);
        }finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public void releaseHandler() {
        try{
            readWriteLock.writeLock().lock();
            int currentCount = countOfReaders.decrementAndGet();
            if (currentCount == 0) {
                reader.releaseSource();
                countOfReaders.set(0);
            }else if (currentCount < 0){
                countOfReaders.set(0);
            }
        }catch (Exception e){
            throw BINonValueUtils.beyondControl(e);
        }finally {
            readWriteLock.writeLock().unlock();
        }
    }
    @Override
    public void forceReleaseHandler(){

        try{
            readWriteLock.writeLock().lock();
            isForceReleased = true;
            reader.releaseSource();
            countOfReaders.set(0);
        }catch (Exception e){
            throw BINonValueUtils.beyondControl(e);
        }finally {
            readWriteLock.writeLock().unlock();
        }
    }


    @Override
    public boolean isForceReleased() {
        return isForceReleased;
    }

    public void setForceReleased(boolean isForce){
        isForceReleased = isForce;
    }
}
