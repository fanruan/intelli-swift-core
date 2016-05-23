/**
 *
 */
package com.fr.bi.field.filtervalue.string.rangefilter;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.base.BIUser;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.conf.report.widget.field.filtervalue.string.StringFilterValue;
import com.fr.bi.field.filtervalue.string.StringFilterValueUtils;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.engine.index.utils.TableIndexUtils;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.LightNode;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.List;


public abstract class StringRangeFilterValue implements StringFilterValue {
    /**
     *
     */
    private static final long serialVersionUID = -8598939332490484510L;
    private static String XML_TAG = "StringRangeFilterValue";

    protected StringValueSet valueSet = new StringValueSet();
    //TODO 这个保存着实在太J了
    protected JSONObject valueJo = new JSONObject();
    protected BIUser user;

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
	 */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = this.getClass().getName().hashCode();
        result = prime * result + ((valueSet == null) ? 0 : valueSet.hashCode());
        return result;
    }

    @Override
    public boolean isTopOrBottomFilterValue() {
        return false;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
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
        valueJo = jo;
        user = new BIUser(userId);
        if (jo.has("filter_value")) {
            JSONArray ja = null;
            try {
                JSONObject valueOb = jo.getJSONObject("filter_value");
                ja = valueOb.getJSONArray("value");
                valueSet.setType(valueOb.getInt("type"));
            } catch (JSONException e) {
                ja = new JSONArray();
            }

            for (int i = 0, len = ja.length(); i < len; i++) {
                valueSet.getValues().add(ja.getString(i));
            }
        }
    }

    /**
     * todo 代码不好
     * 创建json
     *
     * @return 创建json对象
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
        if (reader.getTagName().equals("value")) {

            try {
                JSONObject jo = new JSONObject(reader.getAttrAsString("value", StringUtils.EMPTY));
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
            writer.attr("value", createJSON().toString());
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        writer.end();
    }

    /**
     * 获取过滤后的索引
     *
     * @param target
     * @param loader loader对象
     * @return 过滤索引
     */
    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, Table target, ICubeDataLoader loader, long userId) {
        if (valueSet.getValues().isEmpty()) {
            return null;
        }
        return createFilterIndexByRelations(dimension.getRelationList(), dimension.createNoneSortGroupValueMapGetter(target, loader),
                loader.getTableIndex(dimension.getField()), loader.getTableIndex(target), dimension.createKey());
    }

    private GroupValueIndex createFilterIndexByRelations(List<BITableSourceRelation> relations, ICubeColumnIndexReader sgm, ICubeTableService cti, ICubeTableService eti, BIKey ckey) {
        if (relations == null) {
            return null;
        }
        String[] value = valueSet.getValues().toArray(new String[valueSet.getValues().size()]);
        boolean hasNull = valueSet.getValues().contains(StringUtils.EMPTY);
        GroupValueIndex sgvi = GVIFactory.createAllEmptyIndexGVI();
        Object[] indexs = sgm.getGroupIndex(value);
        boolean hasValue = value.length > 0;
        for (int i = 0, len = indexs.length; i < len; i++) {
            GroupValueIndex gvi = (GroupValueIndex) indexs[i];
            if (gvi != null) {
                sgvi.or(gvi);
            }
        }
        if (hasNull) {
            GroupValueIndex nullres;
            if (!relations.isEmpty()) {
                nullres = TableIndexUtils.createLinkNullGVI(cti, eti, relations);
            } else {
                nullres = cti.getNullGroupValueIndex(ckey);
                if (nullres == null) {
                    nullres = GVIFactory.createAllEmptyIndexGVI();
                }
            }
            if (sgvi != null) {
                sgvi.or(nullres);
            }
        }
        return hasValue ? (valueSet.isContains() ? sgvi : sgvi.NOT(eti.getRowCount())) : null;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean canCreateFilterIndex() {
        return true;
    }
    
    /**
     * 是否显示记录
     *
     * @param node 节点
     * @return 是否显示
     */
    @Override
    public boolean showNode(LightNode node, TargetGettingKey targetKey, ICubeDataLoader loader) {
        String value = StringFilterValueUtils.toString(node.getShowValue());
        if (valueSet.getValues() == null || valueSet.getValues().isEmpty()) {
            return true;
        }
        return isMatchValue(value);
    }

}