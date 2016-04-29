package com.fr.bi.field.target.detailtarget.formula;



import java.util.Calendar;
import java.util.Date;

/**
 * Created by 小灰灰 on 2014/5/7.
 */
public class BIDateFormulaDetaiTarget extends BIStringFormulaDetailTarget {


    @Override
    public Object createShowValue(Object value) {
        Date date = (Date) value;
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);


        return c.get(Calendar.YEAR) + "/"
                + (c.get(Calendar.MONTH) + 1) + "/"
                + c.get(Calendar.DAY_OF_MONTH) + "/ "
                + c.get(Calendar.HOUR_OF_DAY) + ":"
                + c.get(Calendar.MINUTE) + ":"
                + c.get(Calendar.SECOND);
    }




}