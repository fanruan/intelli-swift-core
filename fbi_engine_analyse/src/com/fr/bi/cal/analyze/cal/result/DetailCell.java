package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.report.result.BIDetailCell;

/**
 * Created by andrew_asa on 2017/8/2.
 * 明细表单元格
 */
public class DetailCell implements BIDetailCell {

    Object value;

    @Override
    public Object getData() {

        return value;
    }

    @Override
    public void setData(Object data) {

        this.value = data;
    }
}
