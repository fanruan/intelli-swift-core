/**
 *
 */
package com.fr.bi.cal.analyze.cal.store;

import com.fr.json.JSONCreator;

/**
 * @author Administrator
 */
public interface PageProvider extends JSONCreator {

    public int getPage();

    /**
     * 判断是否结束
     *
     * @return 是否
     */
    public boolean isFinished();

    /**
     * @param groupKey
     * @return
     */
    public NodePageTraveller get(GroupKey groupKey);

    /**
     * @return
     */
    public NodePageTraveller getFirst();

    /**
     * 获取总共的行数
     *
     * @return 行数
     */
    public int getRowCount();

}