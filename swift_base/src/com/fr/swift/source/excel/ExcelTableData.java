package com.fr.swift.source.excel;

import com.fr.general.ComparatorUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;

/**
 * Created by sheldon on 14-8-8.
 */
public class ExcelTableData extends AbstractExcelTableData {

    public ExcelTableData(String filePath) {
        super(filePath);
    }

    public ExcelTableData(String filePath, String[] columnNames, ColumnType[] columnTypes) {
        super(filePath, columnNames, columnTypes);
    }

    /**
     * 创建数据表格
     */
    @Override
    public ExcelDataModel createDataModel() {
        return new ExcelDataModel(filePath,
                columnNames, columnTypes);
    }

    /**
     * 是否需要列名
     *
     * @return 第一行是列名
     */
    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isChildNode()) {
            String tmpTagName = reader.getTagName();
            String tmpVal;
            if ("ExcelTableDataAttr".equals(tmpTagName)) {
                if ((tmpVal = reader.getAttrAsString("filePath", null)) != null) {
                    this.filePath = tmpVal;
                }

                this.needColumnName = !reader.getAttrAsString("needColumnName", "").isEmpty();
            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG("ExcelTableDataAttr")
                .attr("needColumnName", this.needColumnName())
                .attr("filePath", this.getFilePath()).end();

    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ExcelTableData
                && this.needColumnName == ((ExcelTableData) obj).needColumnName
                && ComparatorUtils.equals(this.filePath,
                ((ExcelTableData) obj).filePath);
    }
}