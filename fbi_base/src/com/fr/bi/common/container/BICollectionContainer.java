package com.fr.bi.common.container;

import com.fr.bi.stable.utils.program.BICollectionUtils;

import java.util.Collection;
import java.util.Iterator;

/**
 * 容器类，线程安全的
 * Created by Connery on 2015/12/28.
 */
public abstract class BICollectionContainer<T> implements Cloneable {
    protected Collection<T> container;

    protected BICollectionContainer() {
        this.container = initCollection();
    }

    protected abstract Collection initCollection();

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

    protected void useContent(BICollectionContainer targetContainer) {
        synchronized (container) {
            clear();
            Iterator<T> it = targetContainer.container.iterator();
            while (it.hasNext()) {
                container.add(it.next());
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