package com.finebi.cube.impl.router.status;

import com.finebi.cube.router.fragment.IFragmentTag;
import com.finebi.cube.router.status.IStatusID;
import com.finebi.cube.router.status.IStatusTag;

/**
 * This class created on 2016/3/21.
 *
 * @author Connery
 * @since 4.0
 */
public class BIStatusTag implements IStatusTag {
    private IStatusID statusID;
    private IFragmentTag superFragmentTag;

    public static IStatusTag getStopStatusTag(IFragmentTag fragmentTag) {
        return new BIStopStatusTag(fragmentTag);
    }

    public static IStatusTag getFinishStatusTag(IFragmentTag fragmentTag) {
        return new BIFinishStatusTag(fragmentTag);
    }

    public static IStatusTag getRunningStatusTag(IFragmentTag fragmentTag) {
        return new BIRunningStatusTag(fragmentTag);
    }

    public static IStatusTag getWaitingStatusTag(IFragmentTag fragmentTag) {
        return new BIWaitingStatusTag(fragmentTag);
    }

    protected BIStatusTag(IStatusID statusID, IFragmentTag fragmentTag) {
        this.superFragmentTag = fragmentTag;
        this.statusID = statusID;
    }

    @Override
    public IFragmentTag getSuperFragmentTag() {
        return superFragmentTag;
    }

    @Override
    public IStatusID getStatusID() {
        return statusID;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BIStatusTag)) return false;

        BIStatusTag that = (BIStatusTag) o;

        if (statusID != null ? !statusID.equals(that.statusID) : that.statusID != null) return false;
        return !(superFragmentTag != null ? !superFragmentTag.equals(that.superFragmentTag) : that.superFragmentTag != null);

    }

    @Override
    public boolean isValid() {
        return getSuperFragmentTag() != null && getSuperFragmentTag().isValid();
    }

    @Override
    public int hashCode() {
        int result = statusID != null ? statusID.hashCode() : 0;
        result = 31 * result + (superFragmentTag != null ? superFragmentTag.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(statusID);
        return sb.toString();
    }
}
