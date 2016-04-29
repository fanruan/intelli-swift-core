package com.finebi.cube.router.topic;

import com.finebi.cube.router.IMessageDeliver;

/**
 * ITopic内部的接口分配到tag和Manager中的原因记录一下。
 * 由于MessageTopic要继承Topic，但是ITopic 的数据结构是ID+Fragment和Subscribe的集合。
 * 在MessageTopic的数据结构只有ID，所以将ITopic拆分成Tag和Manager两部分，分别对应ID和
 * Fragment和Subscribe的集合。那么MessageTopic继承Tag。
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public interface ITopic extends ITopicService, IMessageDeliver {

    ITopicTag getTopicTag();

}
