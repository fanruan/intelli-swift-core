package com.fr.bi.stable.utils.file;

import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.data.db.*;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;

/**
 * Created by GUY on 2015/3/11.
 */
public class BIExcelUtils {

    public static long runExcel(BIExcelTableData excel, CubeFieldSource[] columns, Traversal<BIDataValue> back) {
        BIExcelDataModel dataModel = null;
        long res = 0;
        BILogger.getLogger().info("start extracting data from the excel file");
        long start = System.currentTimeMillis();

        try {
            dataModel = excel.createDataModel();
            String[] columnNames = dataModel.onlyGetColumnNames();
            for (int i = 0; i < dataModel.getRowCount(); i++) {
                for (int j = 0; j < columns.length; j++) {
                    CubeFieldSource field = columns[j];
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
            BILogger.getLogger().info("Finish extracting data from the excel file.Time usage is：" + DateUtils.timeCostFrom(start));
            res = dataModel.getRowCount();
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        } finally {
            try {
                if (dataModel != null) {
                    dataModel.release();
                }
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);

            }
        }
        return res;
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