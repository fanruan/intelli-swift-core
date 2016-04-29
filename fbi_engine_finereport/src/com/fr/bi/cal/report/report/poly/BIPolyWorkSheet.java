package com.fr.bi.cal.report.report.poly;

import com.fr.bi.cal.report.report.BIAnalyReport;
import com.fr.bi.cal.report.report.BITemplateReport;
import com.fr.bi.cal.report.report.cal.BIBlockSequenceExecutor;
import com.fr.main.TemplateWorkBook;
import com.fr.report.poly.AbstractPolyReport;
import com.fr.report.poly.PolyAcessUnsupportedException;
import com.fr.report.report.ResultReport;
import com.fr.report.report.TemplateReport;

import java.util.Map;

/**
 * @author:ben Administrator
 * @time: 2012 2012-7-3
 * @description:此类用于
 */
public class BIPolyWorkSheet extends AbstractPolyReport implements BITemplateReport, TemplateReport {
    /**
     *
     */
    private static final long serialVersionUID = 6166844469638502357L;

    public BIPolyWorkSheet() {
    }


    //b:TODO 无需调整位置，only one block
    @Override
    public BIAnalyReport execute4BI(Map parameterMap) {
        BIBlockSequenceExecutor blockExecutor = new BIBlockSequenceExecutor(this, parameterMap);
        BIAnalyReport resSheet = blockExecutor.execute4BI();
        return resSheet;
    }

    @Override
    public TemplateWorkBook getTemplateWorkBook() {
        return (TemplateWorkBook) super.getBook();
    }

    @Override
    public void setTemplateWorkBook(TemplateWorkBook templateWorkBook) {
        super.setBook(templateWorkBook);
    }

    @Override
    public ResultReport execute(Map parameterMap, com.fr.report.stable.fun.Actor actor) {
        if (!actor.supportPolyExecute()) {
            throw new PolyAcessUnsupportedException(actor.description());
        }
        BIBlockSequenceExecutor blockExecuter = new BIBlockSequenceExecutor(this, parameterMap);
        return blockExecuter.execute(actor);
    }
}