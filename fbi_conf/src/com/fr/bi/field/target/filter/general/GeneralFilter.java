package com.fr.bi.field.target.filter.general;

import com.fr.bi.field.target.filter.AbstractTargetFilter;
import com.fr.bi.field.target.filter.TargetFilterFactory;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.Arrays;

public abstract class GeneralFilter extends AbstractTargetFilter {
    private static String XML_TAG = "GeneralFilter";

    protected TargetFilter[] childs;

    public void setChilds(TargetFilter[] childs) {
        this.childs = childs;
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
        result = prime * result + Arrays.hashCode(childs);
        return result;
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

        GeneralFilter other = (GeneralFilter) obj;
        if (!ComparatorUtils.equals(childs, other.childs)) {
            return false;
        }
        return true;
    }

    /**
     * 解析json
     *
     * @param jo     json对象
     * @param userid 用户id
     * @throws Exception 报错
     */
    @Override
    public void parseJSON(JSONObject jo, long userid) throws Exception {
        if (jo.has("filter_value")) {
            JSONArray ja = jo.getJSONArray("filter_value");
            childs = new TargetFilter[ja.length()];
            for (int i = 0, len = childs.length; i < len; i++) {
                childs[i] = TargetFilterFactory.parseFilter(ja.getJSONObject(i), userid);
            }
        }
    }


    /**
     * 创建json
     *
     * @return json对象
     * @throws Exception 报错
     */
    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();

        JSONArray childJa = new JSONArray();
        for (int i = 0, len = childs.length; i < len; i++) {
            childJa.put(childs[i].createJSON());
        }
        jo.put("filter_value", childJa);
        return jo;
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

    public TargetFilter[] getChilds() {
        return childs;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}