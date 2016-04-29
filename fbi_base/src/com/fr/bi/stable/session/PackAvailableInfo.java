package com.fr.bi.stable.session;

/**
 * Created by 小灰灰 on 2014/10/8.
 */
public interface PackAvailableInfo {
    /**
     * 包是否可用
     *
     * @param packageName 包名
     * @return true或false
     */
    boolean isPackAvailableForDesign(String packageName);
}