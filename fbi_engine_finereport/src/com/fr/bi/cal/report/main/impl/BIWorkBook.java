package com.fr.bi.cal.report.main.impl;

import com.fr.bi.cal.report.main.bianalysis.BIAnalyResultWorkBook;
import com.fr.bi.cal.report.report.cal.sheetcalculator.BIAnalyWorkBookExecutor;
import com.fr.main.impl.WorkBook;
import com.fr.web.ResourceHelper;

import java.util.Map;

/**
 * @author:ben Administrator
 * @time: 2012 2012-7-3
 * @description:此类用于executebianalyworkbook 总感觉很别扭
 */
public class BIWorkBook extends WorkBook {

    /**
     *
     */
    private static final long serialVersionUID = 6050973959387064499L;

    public BIWorkBook() {

    }

    //b:TODO 暂时不用考虑sheet间引用
    public BIAnalyResultWorkBook execute4BI(Map parameterMap) {
        this.apply4Parameters(parameterMap);

//		if (!VT4FR.WORK_BOOK.support() && this.getReportCount() > 1) {
//			return new BIAnalyResultWorkBook(parameterMap);
//		} else {
//			
        BIAnalyWorkBookExecutor bookExecuter = new BIAnalyWorkBookExecutor(this, parameterMap);
        return bookExecuter.execute();

//		}
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o)
                && o instanceof BIWorkBook;
    }
}