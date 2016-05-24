/**
 *
 */
package com.fr.bi.field.filtervalue.string.nfilter;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.conf.report.widget.field.filtervalue.string.StringFilterValue;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;


public abstract class StringNFilterValue implements StringFilterValue {
    /**
	 * 
	 */
	private static final long serialVersionUID = 5589013488276270481L;

	private static String XML_TAG = "StringNFilterValue";

    /**
     * default 10
     */
    protected int N = 10;

    /**
     * 获取过滤后的索引
     *
     * @param ck       字段
     * @param tableKey 表格
     * @param target
     * @return 过滤索引
     */
    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, BusinessTable target, ICubeDataLoader loader, long userId) {
        return null;
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
        result = prime * result + N;
        return result;
    }

    @Override
    public boolean isTopOrBottomFilterValue() {
        return true;
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
        StringNFilterValue other = (StringNFilterValue) obj;

        if (N != other.N) {
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
            this.N = jo.getInt("filter_value");
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
        JSONObject jo = new JSONObject();
        jo.put("filter_value", this.N);

        return jo;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * @param reader XML读取对象
     * @see com.fr.stable.xml.XMLableReader
     */
    @Override
    public void readXML(XMLableReader reader) {
        this.N = reader.getAttrAsInt("filter_value", 10);
    }

    /**
     * @param writer XML写入对象
     */
    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        writer.attr("filter_value", this.N);
        writer.end();
    }

    @Override
    public boolean canCreateFilterIndex() {
        return false;
    }
    
	@Override
	@Deprecated
	public boolean isMatchValue(String v) {
		return false;
	}

}