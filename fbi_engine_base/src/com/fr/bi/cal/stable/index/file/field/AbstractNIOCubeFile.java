package com.fr.bi.cal.stable.index.file.field;

import com.fr.bi.cal.stable.index.file.AbstractCubeFile;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.io.newio.NIOWriter;
import com.fr.bi.stable.io.newio.SingleUserNIOReadManager;

public abstract class AbstractNIOCubeFile<K> extends AbstractCubeFile implements NIOCubeFile<K> {

    private final static RuntimeException NULL_MANAGER = new RuntimeException("NIOReader must create by SingleUserNIOReadManager");
    protected NIOWriter<K> list;

    protected AbstractNIOCubeFile(String path) {
        super(path);
    }

    protected void checkManager(SingleUserNIOReadManager manager) {
        if (manager == null) {
            throw NULL_MANAGER;
        }
    }

    protected <T extends NIOWriter<K>> NIOWriter<K> createObject(Class<T> clazz) {
        if (list != null) {
            return list;
        }
        synchronized (this) {
            if (list == null) {
                try {
                    list = clazz.getConstructor(String.class).newInstance(getPath());
                } catch (Exception e) {
                    BILogger.getLogger().error(e.getMessage(), e);
                }
            }
            return list;
        }
    }

    @Override
    public void clearWriter() {
        if (list != null) {
            list.releaseResource();
        }
    }

    public boolean exists() {
        return createFile().exists();
    }

}