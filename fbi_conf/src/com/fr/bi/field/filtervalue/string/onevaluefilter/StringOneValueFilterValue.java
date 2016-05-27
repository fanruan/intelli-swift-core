package com.fr.bi.field.filtervalue.string.onevaluefilter;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.conf.report.widget.field.filtervalue.AbstractFilterValue;
import com.fr.bi.conf.report.widget.field.filtervalue.string.StringFilterValue;
import com.fr.bi.field.filtervalue.string.StringFilterValueUtils;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.report.result.LightNode;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.Iterator;
import java.util.Map;

public abstract class StringOneValueFilterValue extends AbstractFilterValue<String> implements StringFilterValue {
    /**
     *
     */
    private static final long serialVersionUID = 360509300932967000L;

    private static String XML_TAG = "StringOneValueFilterValue";

    protected String value;

    /**
     * 获取过滤后的索引
     *
     * @return 过滤索引
     */
    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, CubeTableSource target, ICubeDataLoader loader, long userId) {
        ICubeTableService ti = loader.getTableIndex(target.getTableSource());
        if (value == null || value.isEmpty()) {
            return ti.getAllShowIndex();
        }

        ICubeColumnIndexReader sgm = dimension.createNoneSortGroupValueMapGetter(target, loader);
        Iterator iter = sgm.iterator();

        //利用SmallGroupValueIndex加快效率
        long rowCount = ti.getRowCount();
        GroupValueIndex gvi = GVIFactory.createAllEmptyIndexGVI();

        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = entry.getKey().toString();

            if (isMatchValue(key)) {
                GroupValueIndex v = (GroupValueIndex) entry.getValue();
                gvi.or(v);
            }
        }
        return gvi;
    }

    /**
     * 重写code
     *
     * @return 数值
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = this.getClass().getName().hashCode();
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
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
        StringOneValueFilterValue other = (StringOneValueFilterValue) obj;

        if (value == null) {

            if (other.value != null) {
                return false;
            }
        } else if (!ComparatorUtils.equals(value, other.value)) {
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
        if (jo.has("filter_value")) {
            value = jo.getString("filter_value");
        }
    }

    /**
     * 创建json
     *
     * @return jo json对象
     * @throws Exception 报错
     */
    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();

        jo.put("filter_value", value);

        return jo;
    }

    /**
     * @param reader XML读取对象
     * @see com.fr.stable.xml.XMLableReader
     */
    @Override
    public void readXML(XMLableReader reader) {
        reader.getAttrAsString("value", "");
    }

    /**
     * @param writer XML写入对象
     */
    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        writer.attr("filter_value", this.value);
        writer.end();
    }

    @Override
    public boolean canCreateFilterIndex() {
        return true;
    }
    
    @Override
    public boolean showNode(LightNode node, TargetGettingKey targetKey, ICubeDataLoader loader) {
        String value = StringFilterValueUtils.toString(node.getShowValue());
        if (value == null && this.value == null) {
            return true;
        }
        if (value == null || this.value == null) {
            return false;
        }
        return isMatchValue(value);
    }

    @Override
    public boolean isTopOrBottomFilterValue() {
        return false;
    }

}