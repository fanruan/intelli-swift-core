package com.fr.bi.field.filtervalue.string.nonevaluefilter;

import com.fr.bi.conf.report.widget.field.filtervalue.AbstractFilterValue;
import com.fr.bi.field.filtervalue.string.StringFilterValueUtils;
import com.fr.bi.conf.report.widget.field.filtervalue.string.StringFilterValue;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.LightNode;
import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

/**
 * Created by sheldon on 14-8-14.
 */
public abstract class StringNoneValueFilterValue extends AbstractFilterValue<String> implements StringFilterValue {

    private static final long serialVersionUID = -7727949200202027961L;

    /**
     * 重写code
     *
     * @return 数值
     */
    @Override
    public int hashCode() {
        return getClass().getName().hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return true;
    }

    /**
     * 将Java对象转换成JSON对象
     *
     * @return json对象
     * @throws Exception
     */
    @Override
    public JSONObject createJSON() throws Exception {
        return new JSONObject();
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
        //FIXME 这里之前将空值保存为空字符串
        return isMatchValue(value);
    }

    @Override
    public boolean isTopOrBottomFilterValue() {
        return false;
    }
}