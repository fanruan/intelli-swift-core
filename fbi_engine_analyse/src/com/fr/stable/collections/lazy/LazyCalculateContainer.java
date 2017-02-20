package com.fr.stable.collections.lazy;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 小灰灰 on 2017/1/20.
 * <p>
 * 之前是自己写自己的作业。
 * 现在默认第一个写作业的人是学霸，其他人看到学霸在写作业就等着，等学霸写完了大家只抄答案。
 * size限制学霸的个数
 * 比如同时有语数外三门的作业，如果不限制学霸的个数，那么同时有三个学霸在写作业，大家等着抄，如果限制学霸只有一个，那么只能等学霸一门一门的做完了再抄。
 * <p>
 * 短期的限制容量的计算缓存。
 * 需要自己实现key，区分计算是否相同。
 * 相同的计算在一个计算周期内只计算一次，在计算进行期间，其他的计算都在等待，直到该计算完成，返回结果。
 * 同时进行的计算个数由参数size空置，小于0即不限制。
 */
public class LazyCalculateContainer<T> {
    private int size = -1;
    private ConcurrentHashMap<Object, LazyValueWaiter> map = new ConcurrentHashMap<Object, LazyValueWaiter>();

    public LazyCalculateContainer() {
    }

    /**
     * 固定容量
     *
     * @param size 容量
     */
    public LazyCalculateContainer(int size) {
        if (size < 1) {
            throw new RuntimeException("invalid size");
        }
        this.size = size;
    }

    /**
     * 获取值
     *
     * @param hashKey key
     * @param creator 构造值的接口
     * @return
     */
    public T get(Object hashKey, LazyValueCreator<T> creator) throws Exception {
        if (!map.containsKey(hashKey)) {
            synchronized (this) {
                //如果满了，并且没有相同的再执行，就等前面的执行完了
                while (isFull() && !map.containsKey(hashKey)) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                    }
                }
                //如果没有正在执行的任务，就加一个
                if (!map.containsKey(hashKey)) {
                    map.put(hashKey, new LazyValueWaiter(hashKey, creator));
                }
            }
        }
        return map.get(hashKey).get();
    }

    public boolean isFull() {
        return size > 0 && map.size() >= size;
    }

    private void remove(Object key) {
        map.remove(key);
        synchronized (this) {
            this.notifyAll();
        }
    }

    //任务分三个状态，未开始，已经开始但未完成，已经完成
    enum Status {
        NOT_STARTED, STARTED, CREATED
    }

    class LazyValueWaiter {
        private T value;
        private Object hashKey;
        private LazyValueCreator<T> creator;
        private volatile Status status = Status.NOT_STARTED;
        private Exception exception;

        public LazyValueWaiter(Object hashKey, LazyValueCreator<T> creator) {
            this.hashKey = hashKey;
            this.creator = creator;
        }

        T get() throws Exception {
            //如果已经开始但未完成就等着
            if (status == Status.STARTED) {
                synchronized (this) {
                    while (status == Status.STARTED) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                        }
                    }
                }
            } else {
                //一开始的线程肯定走的是else这里
                //可能同时有多个走到else这
                synchronized (this) {
                    //如果已经开始但未完成就等着
                    if (status == Status.STARTED) {
                        while (status == Status.STARTED) {
                            try {
                                this.wait();
                            } catch (InterruptedException e) {
                            }
                        }
                    } else {
                        //第一个线程走到else，把状态设置成STARTED，然后notifyAll，让其他synchronized的线程往下走，wait住
                        status = Status.STARTED;
                        this.notifyAll();
                    }
                }
                //第一个到这边的线程开始计算
                if (status != Status.CREATED) {
                    try {
                        this.value = this.creator.create();
                    } catch (Exception e) {
                        this.exception = e;
                    }
                    status = Status.CREATED;
                    synchronized (this) {
                        this.notifyAll();
                    }
                    remove(hashKey);
                }
            }
            if (exception != null) {
                throw exception;
            }
            return value;
        }
    }
}