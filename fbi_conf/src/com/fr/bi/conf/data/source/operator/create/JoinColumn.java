package com.fr.bi.conf.data.source.operator.create;

import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;

/**
 * Created by 小灰灰 on 2016/5/12.
 */
public class JoinColumn implements XMLable, JSONTransform {
    public static final String XML_TAG = "JoinColumn";

    public JoinColumn(String name, boolean isLeft, String columnName) {
        this.name = name;
        this.isLeft = isLeft;
        this.columnName = columnName;
    }

    public JoinColumn() {

    }

    public String getName() {
        return name;
    }

    public boolean isLeft() {
        return isLeft;
    }

    public String getColumnName() {
        return columnName;
    }

    //etl之后的字段名
    private String name;

    private boolean isLeft;

    //父表字段名
    private String columnName;

    /**
     * 将Java对象转换成JSON对象
     *
     * @return
     * @throws Exception
     */
    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("name", name);
        jo.put("isLeft", isLeft);
        jo.put("column_name", columnName);
        return jo;
    }

    /**
     * 将JSON对象转换成java对象
     *
     * @param jo
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        columnName = jo.getString("column_name");
        name = jo.getString("name");
        isLeft = jo.getBoolean("isLeft");
    }

    /**
     * 读取子节点，应该会被XMLableReader.readXMLObject()调用多次
     *
     * @param reader XML读取对象
     * @see com.fr.stable.xml.XMLableReader
     */
    @Override
    public void readXML(XMLableReader reader) {
        name = reader.getAttrAsString("name", StringUtils.EMPTY);
        isLeft = reader.getAttrAsBoolean("isLeft", true);
        columnName = reader.getAttrAsString("columnName", StringUtils.EMPTY);
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
        writer.attr("name", name)
                .attr("isLeft", isLeft)
                .attr("columnName", columnName);
        writer.end();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("JoinColumn{");
        sb.append("name='").append(name).append('\'');
        sb.append(", isLeft=").append(isLeft);
        sb.append(", columnName='").append(columnName).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
