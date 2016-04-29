/**
 *
 */
package com.fr.bi.common.inter;

/**
 * 释放某个资源
 *
 * @author guy
 */
public interface ReleaseObject<K> {

    /**
     * 释放资源
     */
    public void release(K k);

}