/**
 *
 */
package com.fr.bi.stable.data.key.date;

import com.fr.general.ComparatorUtils;
import com.fr.json.JSONCreator;
import com.fr.json.JSONObject;
import com.fr.json.JSONParser;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;


public class DateRange implements JSONParser, JSONCreator, XMLable {

    private BIDay start;

    private BIDay end;

    public DateRange() {
    }

    public DateRange(BIDay start, BIDay end) {
        this.start = start;
        this.end = end;
    }



    /**
     * 解析json
     *
     * @param jo json对象
     * @throws Exception 报错
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("start")){
            long start = jo.getLong("start");
            this.start = new BIDay(start);
        }
        if (jo.has("end")){
            long end = jo.getLong("end");
            this.end = new BIDay(end);
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
        return null;
    }

    /**
     * 读取子节点，应该会被XMLableReader.readXMLObject()调用多次
     *
     * @param reader XML读取对象
     * @see com.fr.stable.xml.XMLableReader
     */
    @Override
    public void readXML(XMLableReader reader) {

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

    }

    public final BIDay getStart() {
        return start;
    }

    public final BIDay getEnd() {
        return end;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * 重写code
     *
     * @return 数值
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        result = prime * result + ((start == null) ? 0 : start.hashCode());
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
        DateRange other = (DateRange) obj;

        if (end == null) {
            if (other.end != null) {
                return false;
            }

        } else if (!ComparatorUtils.equals(end, other.end)) {
            return false;
        }

        if (start == null) {

            if (other.start != null) {
                return false;
            }
        } else if (!ComparatorUtils.equals(start, other.start)) {

            return false;
        }

        return true;
    }
}