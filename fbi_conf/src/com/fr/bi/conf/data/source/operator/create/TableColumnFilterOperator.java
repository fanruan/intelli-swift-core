package com.fr.bi.conf.data.source.operator.create;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.field.target.filter.TargetFilterFactory;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.data.BITable;
import com.fr.bi.stable.data.source.ICubeTableSource;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.List;

/**
 * Created by GUY on 2015/3/5.
 */
public class TableColumnFilterOperator extends AbstractTableColumnFilterOperator {

    public static final String XML_TAG = "TableColumnFilterOperator";
    private static final long serialVersionUID = 1177276899686204275L;
    @BICoreField
    private TargetFilter filter;

    public TableColumnFilterOperator(long userId) {
        super(userId);
    }

    public TableColumnFilterOperator() {

    }

    @Override
    public String xmlTag() {
        return XML_TAG;
    }

    /**
     * 将Java对象转换成JSON对象
     *
     * @return json对象
     * @throws Exception
     */
    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        if (filter != null) {
            jo.put("filter_value", filter.createJSON());
        }
        return jo;
    }


    protected GroupValueIndex createFilterIndex(List<? extends ICubeTableSource> parents, ICubeDataLoader loader){
        if (filter == null){
            return loader.getTableIndex(getSingleParentMD5(parents)).getAllShowIndex();
        }
        GroupValueIndex gvi = null;
        for (ICubeTableSource parent : parents){
            GroupValueIndex temp = filter.createFilterIndex(new BITable(parent.fetchObjectCore().getID().getIdentityValue()), loader, loader.getUserId());
            if (gvi == null){
                gvi = temp;
            } else {
                gvi = gvi.AND(temp);
            }
        }
        return gvi;
    }

    @Override
    protected boolean hasTopBottomFilter() {
        return true;
    }


    /**
     * 将JSON对象转换成java对象
     *
     * @param jo json对象
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has(BIJSONConstant.JSON_KEYS.FILTER_VALUE)) {
            filter = TargetFilterFactory.parseFilter(jo.getJSONObject(BIJSONConstant.JSON_KEYS.FILTER_VALUE), user.getUserId());
        }
    }

    /**
     * 读取子节点，应该会被XMLableReader.readXMLObject()调用多次
     *
     * @param reader XML读取对象
     * @see com.fr.stable.xml.XMLableReader
     */
    @Override
    public void readXML(XMLableReader reader) {
        super.readXML(reader);
        try {
            JSONObject jo = new JSONObject(reader.getAttrAsString("filter", StringUtils.EMPTY));
            this.parseJSON(jo);
        } catch (Exception e) {

        }
    }

    /**
     * Write XML.<br>
     * The method will be invoked when save data to XML file.<br>
     * May override the method to save your own data.
     * 从性能上面考虑，大家用writer.print(), 而不是writer.println()
     *
     * @param writer XML写入对象
     */
    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        super.writeXML(writer);
        try {
            writer.attr("filter", this.createJSON().toString());
        } catch (Exception e) {
        }
        writer.end();
    }
}