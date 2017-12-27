package com.finebi.base.container;


import java.io.Serializable;
import java.util.Collection;

/**
 * This class created on 2017/9/30.
 *
 * @author Each.Zhang
 */
public abstract class CollectionContainer<T> implements Cloneable, Serializable {

    protected Collection<T> container;

    protected CollectionContainer() {
        this.container = initCollection();
    }

    protected abstract Collection<T> initCollection();

    protected Collection<T> getContainer() {
        //return BICollectionUtils.unmodifiedCollection(container);
        return null;
    }

    /**
     * 添加对象
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

    protected void useContent(CollectionContainer<T> targetContainer) {
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
