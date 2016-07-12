package com.fr.bi.field.filtervalue.date.evenfilter;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.conf.report.widget.field.filtervalue.AbstractFilterValue;
import com.fr.bi.conf.report.widget.field.filtervalue.date.DateFilterValue;
import com.fr.bi.stable.data.key.date.BIDateValue;
import com.fr.bi.stable.data.key.date.BIDateValueFactory;
import com.fr.bi.stable.engine.index.key.IndexTypeKey;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.LightNode;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DateKeyTargetFilterValue extends AbstractFilterValue<Long> implements DateFilterValue {

    /**
     *
     */
    private static final long serialVersionUID = -2509778015034905186L;
    @BICoreField
    protected int group;
    @BICoreField
    protected Set<BIDateValue> valueSet;

    private JSONObject valueJo;

    public DateKeyTargetFilterValue() {

    }

    public DateKeyTargetFilterValue(int group, Set<BIDateValue> valueSet) {
        this.group = group;
        this.valueSet = valueSet;
    }

    /**
     * 获取过滤后的索引
     *
     * @param target
     * @param loader loader对象
     * @return 过滤索引
     */
    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, BusinessTable target, ICubeDataLoader loader, long userId) {
        if (valueSet == null || valueSet.isEmpty()) {
            return getGroupValueIndexWhenNull(target, loader);
        }
        ICubeColumnIndexReader getter = loader.getTableIndex(dimension.getField().getTableBelongTo().getTableSource()).loadGroup(new IndexTypeKey(dimension.getField().getFieldName(), group), dimension.getRelationList());
        Iterator<BIDateValue> it = valueSet.iterator();
        GroupValueIndex gvi = null;
        while (it.hasNext()) {
            BIDateValue dk = it.next();
            Object[] keys = getter.createKey(1);
            keys[0] = dk == null ? null : dk.getValue();
            GroupValueIndex cgvi = getter.getGroupIndex(keys)[0];
            if (gvi == null) {
                gvi = cgvi;
            } else {
                gvi = gvi.OR(cgvi);
            }
        }
        return gvi == null ? GVIFactory.createAllEmptyIndexGVI() : gvi;
    }

    private GroupValueIndex getGroupValueIndexWhenNull(BusinessTable targetKey, ICubeDataLoader loader) {
        return null;
    }

    @Override
    public boolean isAllCalculatorFilter() {
        return false;
    }

    public Set<BIDateValue> getValues() {
        return valueSet;
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
        this.valueSet = new HashSet<BIDateValue>();
        if (jo.has("filter_value")) {
            valueJo = jo.getJSONObject("filter_value");
            if(valueJo.has("group") && valueJo.has("values")){
                this.valueSet.add(BIDateValueFactory.createDateValue(valueJo.getInt("group"), valueJo.getLong("values")));
                this.group = valueJo.getInt("group");
            }
        }
    }

    /**
     * 简化
     *
     * @param subType   类型
     * @param value     值
     * @param fieldType 字段日期类型
     * @throws Exception
     */
    private void initValueSetFromValue(int subType, Object value, int fieldType) throws Exception {

    }

    /**
     * todo 代码不好
     * 创建json
     *
     * @return json对象
     * @throws Exception 报错
     */
    @Override
    public JSONObject createJSON() throws Exception {
        return valueJo;
    }

    /**
     * @param reader XML读取对象
     * @see com.fr.stable.xml.XMLableReader
     */
    @Override
    public void readXML(XMLableReader reader) {
        if (reader.getTagName().equals("filter_value")) {

            try {
                JSONObject jo = new JSONObject(reader.getAttrAsString("filter_value", StringUtils.EMPTY));
                this.parseJSON(jo, UserControl.getInstance().getSuperManagerID());
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
    }

    /**
     * @param writer XML写入对象
     */
    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);

        try {
            writer.attr("filter_value", createJSON().toString());
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        writer.end();
    }

    @Override
    public boolean canCreateFilterIndex() {
        return false;
    }

    @Override
    public boolean showNode(LightNode node, TargetGettingKey targetKey, ICubeDataLoader loader) {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DateKeyTargetFilterValue)) {
            return false;
        }

        DateKeyTargetFilterValue that = (DateKeyTargetFilterValue) o;

        if (group != that.group) {
            return false;
        }
        if (!ComparatorUtils.equals(valueSet, that.valueSet)) {
            return false;
        }
        return !(valueJo != null ? !ComparatorUtils.equals(valueJo, that.valueJo) : that.valueJo != null);

    }

    @Override
    public int hashCode() {
        int result = group;
        result = 31 * result + (valueSet != null ? valueSet.hashCode() : 0);
        result = 31 * result + (valueJo != null ? valueJo.hashCode() : 0);
        return result;
    }

	@Override
	public boolean isMatchValue(Long v) {
		if(v == null){

		}
		return false;
	}
}