/**
 *
 */
package com.fr.bi.field.filtervalue.number.nfilter;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.conf.report.widget.field.filtervalue.AbstractFilterValue;
import com.fr.bi.conf.report.widget.field.filtervalue.number.NumberFilterValue;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;


public abstract class NumberNFilterValue extends AbstractFilterValue<Number> implements NumberFilterValue {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2964751763689110973L;
	public static String XML_TAG = "NumberNFilterValue";
    /**
     * default 10
     */
    @BICoreField
    protected int n = 10;

    /**
     * 重写code
     *
     * @return 数值
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = this.getClass().getName().hashCode();
        result = prime * result + n;
        return result;
    }
    @Override
    public boolean isTopOrBottomFilterValue() {
        return false;
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

        NumberNFilterValue other = (NumberNFilterValue) obj;
        if (n != other.n) {

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
            this.n = jo.getInt("filter_value");
        }
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
        jo.put("filter_value", this.n);

        return jo;
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

    /**
     * 获取过滤后的索引
     *
     * @return 过滤索引
     */
    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, CubeTableSource target, ICubeDataLoader loader, long userId) {
        return null;
    }

    /**
     * 获取过滤后的索引
     *
     * @param value 值
     * @return 过滤索引
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