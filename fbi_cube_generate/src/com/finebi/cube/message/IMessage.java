package com.finebi.cube.message;

/**
 * Pub/Sub中的消息部分。
 * <p/>
 * 消息分成Topic，Fragment，status三种消息类型。
 * 因为存在针对Topic的订阅者，同时存在Topic下某一个分片的订阅者。
 * <p/>
 * 不同消息间 是独立的。
 * 如果是发送一个Topic下某一个分片的消息，那么该Topic的订阅者不应该收到消息。
 * <p/>
 * 详见IRouter中的注释。
 * <p/>
 * 由于Message可能被多个接收者获得。因此要避免某一个接收者更改了Message，影响其他接收者
 * 因此Message要不可更改。
 * This class created on 2016/3/17.
 *
 * @author Connery
 * @since 4.0
 */
public interface IMessage {


    IMessageTopic getTopic();

    IMessageFragment getFragment();

    IMessageStatus getStatus();

    IMessageBody getBody();

    /**
     * 如果仅有Topic存在，那么当前消息是Topic消息
     * 那么该消息只会被关注Topic的订阅者处理。
     *
     * @return 是否为Topic消息
     */
    boolean isTopicMessage();

    /**
     * 如果Status不存在，Fragment存在，那么当前消息是Fragment消息
     * 那么该消息只会被关注Fragment的订阅者处理。Fragment的
     * superTopic的订阅者不应该处理。
     *
     * @return 是否为Fragment消息
     */
    boolean isFragmentMessage();

    /**
     * 如果Status存在，那么当前消息是Status消息
     * 那么该消息只会被关注Status的订阅者处理。Status的superFragment和
     * superTopic的订阅者不应该处理。
     *
     * @return 是否为Fragment消息
     */
    boolean isStatusMessage();

    /**
     * 当前的消息是否是停机消息
     *
     * @return 是否是停机消息
     */
    boolean isStopStatus();
//    /**
//     * 由于Message可能被多个接收者获得。因此要避免某一个接收者更改了Message，影响其他接收者
//     * 因此通过
//     * 消息已经被发布，状态不可更改。
//     */
//    void publish();
}
