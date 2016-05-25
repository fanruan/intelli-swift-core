package com.fr.bi.field.target.filter.field;

import com.fr.bi.base.BICore;
import com.fr.bi.base.BICoreGenerator;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.conf.report.widget.field.filtervalue.FilterValue;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.field.filtervalue.FilterValueFactory;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

/**
 * Created by 小灰灰 on 2016/3/17.
 */
public abstract class ColumnFilter implements TargetFilter {
    /**
     *
     */
    private static final long serialVersionUID = -8910896122633672021L;
    protected JSONObject valueJo;
    @BICoreField
    protected FilterValue filterValue;

    public ColumnFilter() {
    }




    /**
     * 解析json
     *
     * @param jo json对象
     * @throws Exception 报错
     */
    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        valueJo = jo;

        filterValue = FilterValueFactory.parseFilterValue(jo, userId);
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
     * 创建json
     *
     * @return json对象
     * @throws Exception 报错
     */
    @Override
    public JSONObject createJSON() throws Exception {
        return valueJo;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean hasTopBottomFilterValue() {
        if (filterValue == null){
            return false;
        }
        return filterValue.isTopOrBottomFilterValue();
    }

    @Override
    public BICore fetchObjectCore() {
        return new BICoreGenerator(this).fetchObjectCore();
    }
}
