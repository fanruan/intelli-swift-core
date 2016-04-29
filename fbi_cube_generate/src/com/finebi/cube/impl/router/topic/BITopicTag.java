package com.finebi.cube.impl.router.topic;

import com.finebi.cube.router.topic.ITopicTag;
import com.finebi.cube.router.topic.ITopicID;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public class BITopicTag implements ITopicTag {
    private ITopicID topicID;

    public BITopicTag(ITopicID topicID) {
        this.topicID = topicID;
    }

    @Override
    public String getTopicName() {
        return topicID.getIdentityValue();
    }

    @Override
    public ITopicID getTopicID() {
        return topicID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BITopicTag)) return false;

        BITopicTag that = (BITopicTag) o;

        return !(topicID != null ? !topicID.equals(that.topicID) : that.topicID != null);
    }

    @Override
    public int hashCode() {
        return topicID != null ? topicID.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BITopicTag{");
        sb.append("topicID=").append(topicID);
        sb.append('}');
        return sb.toString();
    }
}
