package com.fr.bi.stable.connection;


import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.stable.engine.index.utils.TableIndexUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.structure.collection.map.CubeLinkedHashMap;

/**
 * 获取关联的值
 *
 * @author Daniel
 */
public class ConnectionRowGetter {

    private DirectTableConnection connection;

    /**
     * 构造函数
     *
     * @param
     */
    public ConnectionRowGetter(DirectTableConnection connection) {
        this.connection = connection;
    }


    /**
     * 根据起始行获取结束行 connection 是 外键->主键 这样的
     *
     * @param currentRow 当前外键的行号
     * @return 主键行的行号
     */
    public int getConnectedRow(int currentRow) {
        if (this.connection == null) {
            return currentRow;
        }
        return connection.getParentTableValue(currentRow);
    }

    public ICubeColumnIndexReader getConnectionMap(BusinessField start, BusinessField end, GroupValueIndex gvi, ICubeDataLoader loader) {
        Object[] res = getConnectedValues(start, end, gvi, loader);
        ICubeColumnIndexReader getter = loader.getTableIndex(end.getTableBelongTo().getTableSource()).loadGroup(loader.getFieldIndex(end));
        CubeLinkedHashMap map = new CubeLinkedHashMap();
        Object[] gvis = getter.getGroupIndex(res);
        for (int i = 0; i < res.length; i++) {
            map.put(res[i], gvis[i]);
        }
        return map;
    }

    /**
     * 根据起始行获取结束行
     *
     * @return 行号
     */
    public Object[] getConnectionValues(BusinessField start, BusinessField end, GroupValueIndex gvi, ICubeDataLoader loader) {
        if (this.connection == null) {
            ICubeTableService ti = loader.getTableIndex(start.getTableBelongTo().getTableSource());
            return TableIndexUtils.getValueFromGvi(ti, ti.getColumnIndex(end), new GroupValueIndex[]{gvi});
        }
        return connection.getParentTableValues(gvi, loader.getFieldIndex(end));
    }

    /**
     * 根据起始值获取主键的值
     *
     * @param value 起始值
     * @return 结束值的集合
     */
    public Object[] getConnectedValues(BusinessField start, BusinessField end, Object value, ICubeDataLoader loader) {
        if (connection == null) {
            ICubeTableService ti = loader.getTableIndex(start.getTableBelongTo().getTableSource());
            GroupValueIndex[] gvi = ti.getIndexes(ti.getColumnIndex(start), new Object[]{value});
            return TableIndexUtils.getValueFromGvi(loader.getTableIndex(end.getTableBelongTo().getTableSource()), loader.getFieldIndex(end), gvi);
        }
        return connection.getParentTableValues(start.getTableBelongTo(), value, loader.getFieldIndex(start), loader.getFieldIndex(end), loader);
    }

    public Object[] getConnectedRows(DimensionCalculator dimensionCalculator, GroupValueIndex showValue, BIKey key, ICubeDataLoader loader) {
        throw new UnsupportedOperationException();
    }
}