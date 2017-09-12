package com.fr.bi.stable.utils.file;

import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.data.db.excel.AbstractExcelDataModel;
import com.fr.bi.stable.data.db.excel.BIExcelDataModel;
import com.fr.bi.stable.data.db.excel.BIExcelTableData;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;

/**
 * Created by GUY on 2015/3/11.
 */
public class BIExcelUtils {

    public static long runExcel(BIExcelTableData excel, ICubeFieldSource[] columns, Traversal<BIDataValue> back) {
        BIExcelDataModel dataModel = null;
        long res = 0;
        BILoggerFactory.getLogger().info("start extracting data from the excel file");
        long start = System.currentTimeMillis();

        try {
            dataModel = excel.createDataModel();
            if (dataModel.getExcelType() == AbstractExcelDataModel.EXCEL_TYPE_CSV) {
                return runCSV(dataModel, columns, back);
            }
            String[] columnNames = dataModel.onlyGetColumnNames();
            for (int i = 0; i < dataModel.getRowCount(); i++) {
                for (int j = 0; j < columns.length; j++) {
                    ICubeFieldSource field = columns[j];
                    int index = findIndex(columnNames, field.getFieldName());
                    if (index >= 0) {
                        if (!columns[j].isUsable()) {
                            continue;
                        }
                        Object value = dataModel.getValueAt(i, index);
                        if (back != null) {
                            back.actionPerformed(new BIDataValue(i, j, value));
                        }
                    }
                }
            }
            BILoggerFactory.getLogger().info("Finish extracting data from the excel file.Time usage is：" + DateUtils.timeCostFrom(start));
            res = dataModel.getRowCount();
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        } finally {
            try {
                if (dataModel != null) {
                    dataModel.release();
                }
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);

            }
        }
        return res;
    }

    private static long runCSV(BIExcelDataModel dataModel, ICubeFieldSource[] columns, Traversal<BIDataValue> back) throws Exception {
        int row = 0;
        String[] columnNames = dataModel.onlyGetColumnNames();
        while (!dataModel.isEnd()) {
            for (int j = 0; j < columns.length; j++) {
                ICubeFieldSource field = columns[j];
                int index = findIndex(columnNames, field.getFieldName());
                if (index >= 0) {
                    if (!columns[j].isUsable()) {
                        continue;
                    }
                    Object value = dataModel.getValueAt(row, index);
                    if (dataModel.isEnd()) {
                        break;
                    }
                    if (back != null && row != 0) {
                        back.actionPerformed(new BIDataValue(row - 1, j, value));
                    }
                }
            }
            if (!dataModel.isEnd()) {
                row++;
            }
        }
        return row - 1;
    }

    private static int findIndex(String[] names, String target) {
        for (int i = 0; i < names.length; i++) {
            if (ComparatorUtils.equals(names[i], target)) {
                return i;
            }
        }
        return -1;
    }
}