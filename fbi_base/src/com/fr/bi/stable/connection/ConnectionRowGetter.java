package com.fr.bi.stable.connection;


import com.fr.bi.stable.gvi.array.ICubeTableIndexReader;

/**
 * 获取关联的值
 *
 * @author Daniel
 */
public class ConnectionRowGetter {
    private ICubeTableIndexReader reader;

    /**
     * 构造函数
     *
     * @param
     */
    protected ConnectionRowGetter(ICubeTableIndexReader reader) {
        this.reader = reader;
    }



    /**
     * 根据起始行获取结束行 connection 是 外键->主键 这样的
     *
     * @param currentRow 当前外键的行号
     * @return 主键行的行号
     */
    public Integer getConnectedRow(final int currentRow) {
        return reader == null ? currentRow : reader.getReverse(currentRow);
    }

}