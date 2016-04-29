package com.finebi.cube.impl.pubsub;

import com.finebi.cube.exception.BITagDuplicateException;
import com.finebi.cube.exception.BIRegisterIsForbiddenException;
import com.finebi.cube.exception.BIThresholdIsOffException;
import com.finebi.cube.message.IMessage;

import java.util.HashSet;
import java.util.Set;

/**
 * 阀值管理器，默认关闭状态，注册Tag后自动开启。
 * 只有在开启状态下，处理Message才有意义。
 * 接受消息后，变不再准许注册
 * This class created on 2016/3/24.
 *
 * @author Connery
 * @since 4.0
 */
public abstract class BIBasicThreshold<T> {
    final private Set<T> registerTag;
    /**
     * 标志当前阀值器是否被打开。只有打开状态才处理消息
     * 同时标志该阀值器是否能够进行达到阀值判断。
     * 只有处于开启状态，进行达到阀值判断才有意义。
     */
    private boolean thresholdSwitch;

    private boolean isMeetThreshold;
    /**
     * 接受消息后，当前阀值的注册器便关闭，不准许在注册。
     */
    private boolean registerClosed;

    public BIBasicThreshold() {
        registerTag = new HashSet<T>();
        turnOffSwitch();
        isMeetThreshold = false;
        registerClosed = false;
    }

    private void turnOnSwitch() {
        thresholdSwitch = true;
    }

    private void turnOffSwitch() {
        thresholdSwitch = false;
    }

    protected boolean isSwitchOn() {
        return thresholdSwitch;
    }

    protected boolean isMeetThreshold() throws BIThresholdIsOffException {
        if (!isSwitchOn()) {
            throw new BIThresholdIsOffException();
        }
        return isMeetThreshold;
    }

    private void meetThreshold() {
        isMeetThreshold = true;
    }

    protected boolean isRegisterClosed() {
        return registerClosed;
    }

    private void closeRegister() {
        if (!registerClosed) {
            registerClosed = true;
        }
    }

    public void registerThresholdTag(T tag) throws BITagDuplicateException, BIRegisterIsForbiddenException {
        if (!isRegisterClosed()) {
            if (!registerTag.contains(tag)) {
                registerTag.add(tag);
                /**
                 * 注册一个标签到阀值器中，那么打开开关。
                 */
                turnOnSwitch();
            } else {
                throw new BITagDuplicateException(tag.toString());
            }
        } else {
            throw new BIRegisterIsForbiddenException("this threshold is under the status of receiving message ,you can't register tag" +
                    "anymore");
        }
    }

    public abstract T getTargetTag(IMessage message);

    /**
     * 检查当前接受的消息，是否达到阀值。
     *
     * @param message 消息
     * @return 是否达到阀值
     */
    public void handleMessage(IMessage message) throws BIThresholdIsOffException {

        /**
         * 开关是否在打开状态
         */
        if (isSwitchOn()) {
            /**
             * 接受消息，那么关闭注册器.不准许再注册
             */
            closeRegister();
            if (handleOrNot(message)) {
                T tag = getTargetTag(message);
                if (registerTag.contains(tag)) {
                    registerTag.remove(tag);
                }
                /**
                 * 如果触发，那么关闭threshold开关,同时设置为满足阀值
                 */
                if (isTriggered()) {

                    /**
                     * 这里不能把关，
                     */
                    meetThreshold();
                } else {

                }
            }
        } else {
            throw new BIThresholdIsOffException("This threshold is off nor.You should try to register tag" +
                    "firstly to turn on it");
        }
    }

    protected abstract boolean handleOrNot(IMessage message);

    /**
     * 只要为空就可以触发。但是不是达到阀值。
     * 达到阀值还需要更严格的条件，阀值由关闭到打开，注册器由打开到关闭。
     * 标志着有阀值注册，并且处理了消息，从而达到阀值。
     *
     * @return
     */
    protected boolean isTriggered() {
        return registerTag.isEmpty();
    }
}
