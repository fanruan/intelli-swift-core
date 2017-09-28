package com.fr.bi.common.container;

import com.fr.bi.stable.utils.program.BICollectionUtils;

import java.io.Serializable;
import java.util.Collection;

/**
 * 容器类，线程安全的
 * Created by Connery on 2015/12/28.
 */
public abstract class BICollectionContainer<T> implements Cloneable, Serializable {
    private static final long serialVersionUID = 953366154679673459L;
    protected Collection<T> container;

    protected BICollectionContainer() {
        this.container = initCollection();
    }

    protected abstract Collection<T> initCollection();

    protected Collection<T> getContainer() {
        return BICollectionUtils.unmodifiedCollection(container);
    }

    /**
     * 添加对象
     * 注意不一定是对象唯一的。
     *
     * @param element
     */
    protected void add(T element) {
        synchronized (container) {
            container.add(element);
        }
    }

    protected Boolean contain(T element) {
        return container.contains(element);
    }

    protected void setContainer(Collection<T> container) {
        this.container = container;
    }

    protected void useContent(BICollectionContainer<T> targetContainer) {
        synchronized (container) {
            clear();
            for (T t : targetContainer.container) {
                container.add(t);
            }
        }
    }

    protected void remove(T element) {
        synchronized (container) {
            if (container.contains(element)) {
                container.remove(element);
            }
        }
    }

    protected void clear() {
        synchronized (container) {
            container.clear();
        }
    }

    protected boolean isEmpty() {
        synchronized (container) {
            return container.isEmpty();
        }
    }

    protected int size() {
        synchronized (container) {
            return container.size();
        }
    }


}