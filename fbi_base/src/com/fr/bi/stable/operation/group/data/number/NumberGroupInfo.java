package com.fr.bi.stable.operation.group.data.number;

import com.fr.bi.base.BIName;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.general.ComparatorUtils;

/**
 * Created by 小灰灰 on 2014/9/29.
 */
public class NumberGroupInfo extends BIName {
    private static final long serialVersionUID = -5100637398294318934L;

    @BICoreField
    public double max = Double.POSITIVE_INFINITY;
    @BICoreField
    public double min = Double.NEGATIVE_INFINITY;
    @BICoreField
    public boolean closemax;
    @BICoreField
    public boolean closemin;

    public NumberGroupInfo() {
    }

    protected NumberGroupInfo(String groupName, double max, boolean closemax, double min, boolean closemin) {
        super.setValue(groupName);
        this.max = max;
        this.closemax = closemax;
        this.min = min;
        this.closemin = closemin;
    }

    /**
     * 创建一个分组信息
     *
     * @param v 值
     * @return 分组信息
     */
    public static NumberGroupInfo createOneValueGroup(Number v) {
        return new NumberGroupInfo("", v.doubleValue(), true, v.doubleValue(), true);
    }

    /**
     * 创建一个分组信息
     *
     * @param start    开始值
     * @param closemin 是否闭合
     * @param end      结束值
     * @param closemax 结束是否闭合
     * @return 分组信息
     */
    public static NumberGroupInfo createGroupInfo(double start, boolean closemin, double end, boolean closemax) {
        return new NumberGroupInfo("", end, closemax, start, closemin);
    }

    /**
     * 创建一个分组信息
     *
     * @param groupName 组名
     * @param start     开始值
     * @param closemin  是否闭合
     * @param end       结束值
     * @param closemax  结束是否闭合
     * @return 分组信息
     */
    public static NumberGroupInfo createGroupInfo(String groupName, double start, boolean closemin, double end, boolean closemax) {
        return new NumberGroupInfo(groupName, end, closemax, start, closemin);
    }

    /**
     * 重写code
     *
     * @return 数值
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (closemax ? 1231 : 1237);
        result = prime * result + (closemin ? 1231 : 1237);
        result = prime * result
                + super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(max);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(min);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        NumberGroupInfo other = (NumberGroupInfo) obj;
        if (closemax != other.closemax) {
            return false;
        }

        if (closemin != other.closemin) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }

        if (!ComparatorUtils.equals(Double.doubleToLongBits(max), Double
                .doubleToLongBits(other.max))) {
            return false;
        }

        if (Double.doubleToLongBits(min) != Double
                .doubleToLongBits(other.min)) {
            return false;
        }
        return true;
    }

    /**
     * 可以取等号
     *
     * @return 是否
     */
    public boolean isCloseMax() {
        return closemax;
    }

    /**
     * 可以取等号
     *
     * @return 是否
     */
    public boolean isCloseMin() {
        return closemin;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    /**
     * 是否小于最小
     *
     * @param value 标准
     * @return 大小
     */
    private boolean isSmallThanMin(double value) {
        return closemin ? value < min : value <= min;
    }

    /**
     * 属于区间
     *
     * @param value 标准
     * @return 包含
     */
    public boolean contains(double value) {
        return (closemin ? value >= min : value > min)
                && (closemax ? value <= max : value < max);
    }

    /**
     * 是否大于
     *
     * @param value 标准
     * @return 大小
     */
    private boolean isLargeThanMax(double value) {
        return closemax ? value > max : value >= max;
    }

    /**
     * 是否在其他
     *
     * @return 其他
     */
    public boolean isOtherGroup() {
        return false;
    }
}