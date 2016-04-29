package com.fr.bi.cal.analyze.session;

import com.fr.data.TableDataSource;
import com.fr.general.LogDuration;
import com.fr.main.FineBook;
import com.fr.main.TemplateWorkBook;
import com.fr.main.workbook.AnalyRWorkBook;
import com.fr.main.workbook.ResultWorkBook;
import com.fr.report.core.SimpleColumnRowNameSpace;
import com.fr.report.elementcase.ElementCase;
import com.fr.report.poly.AnalyPolySheet;
import com.fr.report.report.ResultReport;
import com.fr.report.stable.fun.Actor;
import com.fr.script.Calculator;
import com.fr.script.CalculatorMap;
import com.fr.stable.ActorConstants;
import com.fr.stable.ActorFactory;
import com.fr.stable.script.CalculatorProvider;
import com.fr.stable.script.ColumnRowRange;
import com.fr.web.core.ReportSession;
import com.fr.web.core.SessionIDInfor;


/**
 * Created by GUY on 2015/4/8.
 */
public abstract class AbstractSession extends SessionIDInfor implements ReportSession, LogDuration {
    protected ResultWorkBook book2Show;
    protected Object renameWidgetLock = new Object();
    /**
     * 生成结果报表
     */
    private Object initBook2ShowLock = new Object();


    public AbstractSession(String remoteAddress) {
        this.remoteAddress = remoteAddress;
        this.parameterMap4Execute = CalculatorMap.createEmptyMap();
        updateTime();
    }

    @Override
    public String getDurationPrefix() {
        return this.getWebTitle();
    }

    @Override
    public FineBook getContextBook() {
        synchronized (renameWidgetLock) {
            if (book2Show == null || book2Show.getReportCount() == 0) {
                initBook2Show();
            }
        }
        return book2Show;
    }

    @Override
    public Actor getActor() {
        return ActorFactory.getActor(ActorConstants.TYPE_BI);
    }

    @Override
    public ResultReport getReport2Show(int i) {
        return this.getWorkBook2Show().getResultReport(i);
    }

    @Override
    public int getReportCount() {
        return this.getContextBook().getReportCount();
    }

    @Override
    public String getReportName(int i) {
        return this.getContextBook().getReportName(i);
    }

    @Override
    public TemplateWorkBook getWorkBookDefine() {
        return null;
    }

    @Override
    public TableDataSource getTableDataSource() {
        return null;
    }

    @Override
    protected Object resolveVariable(Object var, CalculatorProvider co) {
        if (var instanceof ColumnRowRange) {
            ResultWorkBook rwb = this.getWorkBook2Show();
            ResultReport rp = rwb.getResultReport(0);
            java.util.Iterator caseIt = rp.iteratorOfElementCase();
            if (caseIt.hasNext()) {
                ElementCase ec = (ElementCase) caseIt.next();
                return SimpleColumnRowNameSpace.resolveColumnRowRange((ColumnRowRange) var, ec);
            }
        }
        return null;
    }

    @Override
    public String getWebTitle() {
        return "FBI-NEW";
    }

    @Override
    public void release() {
        Calculator.putThreadSavedNameSpace(null);
        this.releaseShowBook();
    }

    /**
     * 释放
     */
    public void releaseShowBook() {
        if (this.book2Show != null) {
            this.book2Show.clearAllTableData();

            for (int i = 0, len = this.book2Show.getReportCount(); i < len; i++) {
                this.book2Show.removeReport(i);
            }
            this.book2Show = null;
        }
    }

    /**
     * 获取ResultWorkBook
     *
     * @return workbook对象
     */
    public ResultWorkBook getWorkBook2Show() {
        synchronized (renameWidgetLock) {
            if (book2Show == null || book2Show.getReportCount() == 0) {
                initBook2Show();
            }
        }
        return book2Show;
    }

    private void initBook2Show() {
        synchronized (initBook2ShowLock) {
            if (book2Show == null) {
                AnalyRWorkBook book = new AnalyRWorkBook(getParameterMap4Execute());
                book.addReport("bi", new AnalyPolySheet());
                book2Show = book;
            }
        }
    }
}