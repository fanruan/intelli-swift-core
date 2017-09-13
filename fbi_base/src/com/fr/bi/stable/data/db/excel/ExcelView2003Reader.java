package com.fr.bi.stable.data.db.excel;

import com.fr.stable.StringUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zcf on 2016/11/23.
 */
public class ExcelView2003Reader extends AbstractExcel2003Reader {
    public ExcelView2003Reader(String filename) throws IOException {
        super(filename);
    }

    public ExcelView2003Reader(String filePath, boolean preview) throws Exception {
        super(filePath, preview);
    }

    protected void initFieldNames() {
        Object[] firstRow = rowDataList.get(0);
        columnNames = new String[firstRow.length];
        //如果是首行含有空值或特殊字符
        for (int i = 0; i < firstRow.length; i++) {
            //特殊字符替换
            String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~\\s]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(firstRow[i].toString());
            columnNames[i] = m.replaceAll(StringUtils.EMPTY).trim();
        }
    }
}
