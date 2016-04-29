package com.fr.bi.cal.analyze.base;

import com.fr.bi.cal.stable.loader.CubeReadingTableIndexLoader;
import com.fr.bi.stable.constant.Status;
import com.fr.general.GeneralContext;
import com.fr.stable.EnvChangedListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 管理索引
 *
 * @author Daniel
 */
public class CubeIndexManager {

    private static final int MEMORY_LIMIT = 64;
    private static CubeIndexManager loader;
    private volatile Status status = Status.UNLOAD;

    /**
     * TODO 暂时先用这个，后面再优化
     */
    private List<Object> indexCheckList = new ArrayList<Object>();

    protected CubeIndexManager() {
    }

    /**
     * 环境改变
     */
    public static void envChanged() {
        if (loader != null) {
            loader.releaseAll();
        }
        loader = null;
    }

    public synchronized static CubeIndexManager getInstance() {
        if (loader == null) {
            loader = new CubeIndexManager();
        }
        return loader;
    }

    public Status getTableStatus() {
        return Status.LOADED;
    }

    public void setStatus(Status status) {
        this.status = status;
        if (status == Status.LOADED) {
            notifyCheckList();
        }
    }

    /**
     * 注册索引检查
     *
     * @param o 索引
     */
    public void regeistIndexCheck(Object o) {
        indexCheckList.add(o);
        synchronized (o) {
            try {
                o.wait();
            } catch (InterruptedException e) {
            }
        }
    }

    private void notifyCheckList() {
        Iterator iter = indexCheckList.iterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o != null) {
                synchronized (o) {
                    o.notifyAll();
                }
            }
            iter.remove();
        }
    }
    static {
        GeneralContext.addEnvChangedListener(new EnvChangedListener() {

            @Override
            public void envChanged() {
                //FIXME 释放所有的
                CubeReadingTableIndexLoader.envChanged();
            }
        });
    }

    /**
     * 释放
     */
    public void releaseIndexes() {
        CubeReadingTableIndexLoader.envChanged();
    }

    /**
     * 释放
     */
    public void releaseAll() {
        releaseIndexes();
        notifyCheckList();
    }
}