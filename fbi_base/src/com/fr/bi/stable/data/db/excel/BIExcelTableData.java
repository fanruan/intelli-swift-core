package com.fr.bi.stable.data.db.excel;

import com.fr.general.ComparatorUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

/**
 * Created by sheldon on 14-8-8.
 */
public class BIExcelTableData extends AbstractExcelTableData {

    public BIExcelTableData(String filePath) {
        super(filePath);
    }

    public BIExcelTableData(String filePath, String[] columnNames, int[] columnTypes) {
        super(filePath, columnNames, columnTypes);
    }

    /**
     * 创建数据表格
     */
    public BIExcelDataModel createDataModel() {
        return new BIExcelDataModel(filePath,
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
        return obj instanceof BIExcelTableData
                && this.needColumnName == ((BIExcelTableData) obj).needColumnName
                && ComparatorUtils.equals(this.filePath,
                ((BIExcelTableData) obj).filePath);
    }
}