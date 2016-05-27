/**
 *
 */
package com.fr.bi.field.filtervalue.number.nonefilter;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.conf.report.widget.field.filtervalue.AbstractFilterValue;
import com.fr.bi.conf.report.widget.field.filtervalue.number.NumberFilterValue;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.index.utils.TableIndexUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;


public abstract class NumberNoneValueFilterValue extends AbstractFilterValue<Number> implements NumberFilterValue {

    /**
     *
     */
    private static final long serialVersionUID = 3099851820106421738L;

    @Override
    public boolean isTopOrBottomFilterValue() {
        return false;
    }

    /**
     * 重写code
     *
     * @return 数值
     */
    @Override
    public int hashCode() {
        return this.getClass().getName().hashCode();
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
        return true;
    }

    /**
     * 解析json
     *
     * @param jo     json对象
     * @param userId 用户id
     * @throws Exception 报错
     */
    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
    }

    /**
     * 创建json对象
     *
     * @return json对象
     * @throws Exception
     */
    @Override
    public JSONObject createJSON() throws Exception {
        return new JSONObject();
    }

    /**
     * @param reader XML读取对象
     * @see com.fr.stable.xml.XMLableReader
     */
    @Override
    public void readXML(XMLableReader reader) {

    }

    /**
     * @param writer XML写入对象
     */
    @Override
    public void writeXML(XMLPrintWriter writer) {

    }

    /**
     * 获取基本索引
     *
     * @return 分组索引
     */
    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, CubeTableSource target, ICubeDataLoader loader, long userId) {
        ICubeTableService ti = loader.getTableIndex(dimension.getField().getTableBelongTo());
        GroupValueIndex gvi = TableIndexUtils.createLinkNullGVI(ti, dimension.getRelationList(), loader);
        return gvi == null
                ? ti.getNullGroupValueIndex(dimension.createKey()) : gvi;
    }

    /**
     * 是否符合条件
     *
     * @param value 值
     * @return true或false
     */
    @Override
    public boolean isMatchValue(Number value) {
        return false;
    }


    @Override
    public boolean canCreateFilterIndex() {
        return false;
    }

}