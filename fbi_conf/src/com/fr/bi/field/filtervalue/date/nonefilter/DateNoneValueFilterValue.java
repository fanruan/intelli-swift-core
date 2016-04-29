package com.fr.bi.field.filtervalue.date.nonefilter;

import com.fr.bi.conf.report.widget.field.filtervalue.date.DateFilterValue;
import com.fr.json.JSONObject;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;


/**
 * Created by sheldon on 14-8-14.
 */
public abstract class DateNoneValueFilterValue implements DateFilterValue {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8750135868147524428L;

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
     * 创建json
     *
     * @return json对象
     * @throws Exception 报错
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean canCreateFilterIndex() {
        return false;
    }

}