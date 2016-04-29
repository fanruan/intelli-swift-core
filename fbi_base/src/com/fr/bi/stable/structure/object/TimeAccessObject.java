/**
 *
 */
package com.fr.bi.stable.structure.object;

/**
 * 表示一个带有时间属性的一个数
 *
 * @author guy
 */
public class TimeAccessObject<V> {

    private long time = System.currentTimeMillis();

    private V v;

    public TimeAccessObject(V v) {
        this.v = v;
    }

    /**
     * 获取存储的值
     *
     * @return
     */
    public V getValue() {
        return v;
    }

    public void updateTime() {
        this.time = System.currentTimeMillis();
    }

    public long getTime() {
        return time;
    }

}