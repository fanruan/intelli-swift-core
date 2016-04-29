package com.finebi.cube.pubsub;

/**
 * 订阅者当订阅的的条件被满足时候，
 * 该对象的process函数被调用。
 * This class created on 2016/3/17.
 *
 * @author Connery
 * @since 4.0
 */
public interface IProcessor<R> {
    void process();

    void setPublish(IPublish publish);

    R getResult();
}
