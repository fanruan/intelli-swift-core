package com.fr.bi.cal.analyze.cal.multithread;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 双缓冲的多线程增加单线程取的容器。不是严格按照添加的顺序执行的。
 * 缓冲buffer太小了性能不好，建议至少比线程数量高一个量级。
 * Created by 小灰灰 on 2017/6/21.
 */
public class TwinBufferStreamIterator<T> implements Iterator{
    private BufferArray consumer;
    private BufferArray current;
    private BufferArray next;
    private BufferArray tNext;
    //是不是正在消费
    private volatile boolean isConsuming = false;
    private int size;
    private volatile boolean isEnd;
    public TwinBufferStreamIterator(int size) {
        this.size = size;
        current = new BufferArray(size);
        next = new BufferArray(size);
        tNext = new BufferArray(size);
        consumer = current;
    }

    public void add(T t){
        //把当前的current传过去，避免current发生替换的时候出发了add到当前的tNext
        add(t, current);
        //没有在消费，并且consumer有足量的缓冲，就唤醒下
        if (!isConsuming && consumer.isPlenty()){
            wakeUp();
        }
    }

    private void add(T t, BufferArray current) {
        //第一个数组满了，就放第二个数组，同时触发数组的替换。
        if (!current.add(t)){
            if (current.next != null && current.next.add(t)){
                addBuffer();
            } else {
                //如果第二个也满了，就需要同步的执行下数组的替换，以确保下次递归的时候能add成功。
                // 这边也可以不同步，递归执行add，也占cpu。考虑到执行到这边的概率很小，只是为了避免bug加上的，这边不是很重要。
                synchronized (this){
                    addBuffer();
                }
                add(t);
            }
        }
    }

    private void addBuffer(){
        //避免频繁增加，current满了才进行替换
        if (current.ifFullThenAddOnce(next)){
            current = next;
            next = tNext;
            tNext = new BufferArray(size);
        }
    }

    @Override
    public boolean hasNext() {
        waitIfNeeded();
        return consumer.hasNext();
    }

    private void waitIfNeeded() {
        if (!realHasNext()) {
            synchronized (this) {
                while (!realHasNext() && (!isEnd)) {
                    try {
                        isConsuming = false;
                        this.wait();
                    } catch (Exception e) {
                    }
                }
                isConsuming = true;
            }
        }
    }

    private boolean realHasNext(){
        if (consumer.hasNext()){
            return true;
        }
        if (consumer.isOver() && consumer.next != null){
            consumer = consumer.next;
            if (consumer != null && consumer.hasNext()){
                consumer.previous.next = null;
                consumer.previous = null;
                return true;
            }
        }
        return false;
    }


    @Override
    public T next() {
        return consumer.next();
    }

    @Override
    public void remove() {

    }

    public void wakeUp() {
        synchronized (this){
            if (!isConsuming){
                this.notify();
            }
        }
    }

    public void finish() {
        isEnd = true;
        synchronized (this){
            this.notify();
        }
    }

    class BufferArray{
        Object[] array;
        AtomicInteger count;
        AtomicInteger addFlag;
        BufferArray previous;
        BufferArray next;
        int index;

        BufferArray(int size) {
            array = new Object[size];
            count = new AtomicInteger();
            addFlag = new AtomicInteger(0);
        }

        boolean isFull(){
            return count.get() >= array.length;
        }

        //满了就不能加了，返回false
        boolean add(T t){
            int index = count.getAndIncrement();
            if (index < array.length){
                array[index] = t;
                return true;
            }
            return false;
        }

        public boolean ifFullThenAddOnce(BufferArray next) {
            if (isFull()){
                //不能用count.get()==array.length来判断，因为可能会有多个线程进行add，只有一个add成功了，但是count被加了很多次，导致count超过array.length，造成替换失败
                //也不能在add失败的时候减少count,
                if (addFlag.getAndIncrement() == 0){
                    next.previous = this;
                    this.next = next;
                    return true;
                }
            }
            return false;
        }

        boolean hasNext() {
            return index < array.length && array[index] != null;
        }

        boolean isOver() {
            return index >= array.length;
        }

        T next() {
            int i = index++;
            T t = (T) array[i];
            array[i] = null;
            return t;
        }

        public boolean isPlenty() {
            return count.get() - index > size / 2;
        }
    }

}
