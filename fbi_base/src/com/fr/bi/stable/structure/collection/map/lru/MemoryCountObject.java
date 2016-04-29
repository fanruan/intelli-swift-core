/**
 *
 */
package com.fr.bi.stable.structure.collection.map.lru;

/**
 * @author Daniel
 *         TODO 优化需要将count变成最近单位时间内的访问次数
 *         比如可以保存每次访问的时间
 *         然后判断是否加入内存时
 *         使用单位时间的个数
 */
public class MemoryCountObject<V> {

    private int count = 0;

    private V v;

    public MemoryCountObject(V v) {
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

    /**
     * 访问量+1
     */
    public void access_plus() {
        count++;
    }

    /**
     * 访问计数
     *
     * @return
     */
    public int access_count() {
        return count;
    }

    /**
     * toString方法
     */
    @Override
    public String toString() {
        return "count:" + count + " object:" + v == null ? "" : v.toString();
    }

}