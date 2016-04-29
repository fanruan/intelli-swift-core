package com.finebi.cube;

/**
 * 位置数据的封装。
 * 可以是排序数据的位置信息，或者其他队列类型的位置信息。
 * 这里和UI里面的Position肯能存在歧义。这里只有一维，暂时不好率多维的。
 * 不采用JDK中的Index表示的原因是，index在BI中
 * 已经表示为索引了。
 * This class created on 2016/3/5.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeLongTypePosition extends BICubePosition<Long> {
    public BICubeLongTypePosition(long position) {
        super(position);
    }
}
