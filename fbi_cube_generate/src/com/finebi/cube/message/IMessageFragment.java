package com.finebi.cube.message;

import com.finebi.cube.router.fragment.IFragmentTag;

/**
 * MessageTopic之下的细分。可以认为是Topic下的细分Topic。
 * 例如：Topic可能是生成索引，那么Fragment可以是生成索引这个Topic中的某一个索引
 * <p/>
 * 通过Topic和Fragment必须要确定一个独立完成任务的个体。
 * 因为，消息的监听者会依据Topic和Fragment来确定监听任务的实施者，同时根据Status来确定
 * 实施者任务的状态，从而确定自身的状态，进而依据状态来确定操作。
 * <p/>
 * This class created on 2016/3/17.
 *
 * @author Connery
 * @since 4.0
 */
public interface IMessageFragment {

    IFragmentTag getFragmentTag();
}
