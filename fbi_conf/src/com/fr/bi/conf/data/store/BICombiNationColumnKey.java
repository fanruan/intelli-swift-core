package com.fr.bi.conf.data.store;


import com.fr.bi.stable.data.Table;

/**
 * Created by 小灰灰 on 2015/2/15.
 */
public interface BICombiNationColumnKey {
    /**
     * 获取合并的表
     *
     * @return
     */
    public Table getCombiNationTable();

    /**
     * 设置合并表
     *
     * @param table
     */
    public void setCombiNationTable(Table table);
}