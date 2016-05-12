package com.finebi.cube.impl.router.fragment;

import com.finebi.cube.router.fragment.IFragmentID;
import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.topic.ITopicTag;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public class BIFragmentTag implements IFragmentTag {
    private IFragmentID fragmentID;
    private ITopicTag superTopicTag;

    public BIFragmentTag(IFragmentID fragmentID, ITopicTag topicTag) {
        this.fragmentID = fragmentID;
        this.superTopicTag = topicTag;
    }

    @Override
    public ITopicTag getSuperTopicTag() {
        return superTopicTag;
    }

    @Override
    public IFragmentID getFragmentID() {
        return fragmentID;
    }

    @Override
    public boolean isValid() {
        return getSuperTopicTag() != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BIFragmentTag)) return false;

        BIFragmentTag that = (BIFragmentTag) o;

        if (fragmentID != null ? !fragmentID.equals(that.fragmentID) : that.fragmentID != null) return false;
        return !(superTopicTag != null ? !superTopicTag.equals(that.superTopicTag) : that.superTopicTag != null);

    }

    @Override
    public int hashCode() {
        int result = fragmentID != null ? fragmentID.hashCode() : 0;
        result = 31 * result + (superTopicTag != null ? superTopicTag.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(fragmentID);
        return sb.toString();
    }
}
