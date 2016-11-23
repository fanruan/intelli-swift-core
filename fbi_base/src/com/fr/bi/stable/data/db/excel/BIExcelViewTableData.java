package com.fr.bi.stable.data.db.excel;

import com.fr.general.ComparatorUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

/**
 * Created by zcf on 2016/11/18.
 */
public class BIExcelViewTableData extends AbstractExcelTableData {

    public BIExcelViewTableData(String filePath) {
        super(filePath);
    }

    public BIExcelViewTableData(String filePath, String[] columnNames, int[] columnTypes) {
        super(filePath, columnNames, columnTypes);
    }

    /**
     * 创建数据表格
     */

    public BIExcelViewDataModel createDataModel() {
        return new BIExcelViewDataModel(filePath,
                columnNames, columnTypes);
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isChildNode()) {
            String tmpTagName = reader.getTagName();
            String tmpVal;
            if ("ExcelViewTableDataAttr".equals(tmpTagName)) {
                if ((tmpVal = reader.getAttrAsString("filePath", null)) != null) {
                    this.filePath = tmpVal;
                }

                this.needColumnName = !reader.getAttrAsString("needColumnName", "").isEmpty();
            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG("ExcelViewTableDataAttr")
                .attr("needColumnName", this.needColumnName())
                .attr("filePath", this.getFilePath()).end();

    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BIExcelViewTableData
                && this.needColumnName == ((BIExcelViewTableData) obj).needColumnName
                && ComparatorUtils.equals(this.filePath,
                ((BIExcelViewTableData) obj).filePath);
    }
}
