package com.fr.bi.report.key;

/**
 * Created by 小灰灰 on 2017/7/7.
 */
public class XTargetGettingKey extends TargetGettingKey {
    private int subIndex;
    public XTargetGettingKey(int targetIndex, int subIndex, String targetName) {
        super(targetIndex, targetName);
        this.subIndex = subIndex;
    }

    public int getSubIndex() {
        return subIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        XTargetGettingKey that = (XTargetGettingKey) o;

        return subIndex == that.subIndex;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + subIndex;
        return result;
    }
}
