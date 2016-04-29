package com.fr.bi.cal.report.report.poly;

import com.fr.general.DateUtils;
import com.fr.page.ClippedECPage.EmptyClippedReportPage;
import com.fr.page.ClippedPageProvider;
import com.fr.page.ReportPageProvider;
import com.fr.report.poly.AnalyECBlock;
import com.fr.stable.StringUtils;
import com.fr.stable.html.Tag;
import com.fr.stable.web.Repository;
import com.fr.web.core.HTMLWriter;
import com.fr.web.core.reportcase.WebPageReportCase;
import com.fr.web.output.html.chwriter.CellHtmlWriter;


/**
 * @author:ben Administrator
 * @time: 2012 2012-7-12
 * @description:此类用于BI分页block，暂时把分页内容存此处，移除时一定要清除
 */
public class BIAnalyResultBlock extends AnalyECBlock {
    /**
     *
     */
    private static final long serialVersionUID = -1005858371768823299L;
    // b:TODO 放这里显得有些不妥，但是在BI结构下block <--> pages
    private ReportPageProvider[][] reportPages = null;
//	private int width = 0;

    public void setReportPages(ReportPageProvider[][] reportPages) {
        this.reportPages = reportPages;
    }

    /**
     * 取页方式已改，下面不再适用
     * b:TODO 不同的触发加载方式反馈的方式也有所不同，table的位置需直接计算得出
     *
     * @param pagecolumn ,pagerow page页面上开始加载的最后一页，一次可能加载多page而对应同一pagecolumn,pagerow
     * @param vertical   触发方向
     * @param index      加载一次多张中的第几张
     * @return
     */
    public Tag getTableTag(HTMLWriter htmlWriter, CellHtmlWriter cellWriter,
                           Repository repo, int pagecolumn, int pagerow) {
        ReportPageProvider reportPage = this.reportPages[pagecolumn][pagerow]
                .deriveResolution(repo.getResolution());
        ClippedPageProvider cpp = null;
        ClippedPageProvider[] cp = reportPage.getPages();
        if (cp.length == 0) {
            cpp = new EmptyClippedReportPage();
        } else {
            cpp = cp[0];
        }
        Tag table = htmlWriter.writeReportToHtml(new WebPageReportCase(
                cpp), 0, cellWriter, repo, StringUtils.EMPTY);
        setPlaceAndCheckBorder(pagecolumn, pagerow, table);

        return table;
    }

    // b:默认取第一页
    @Override
    public Tag getTag(HTMLWriter htmlWriter, CellHtmlWriter cellWriter,
                      int reportIndex, Repository repo) {
        if (this.reportPages == null) {
            return null;
        }
        return getTag(htmlWriter, cellWriter, reportIndex, repo,
                this.reportPages[0][0]);
    }

    // b:-1标示边界
    private void setPlaceAndCheckBorder(int x, int y, Tag tableTag) {
        tableTag.attr("horizontalend", String.valueOf(this.reportPages.length));
        tableTag.attr("verticalend", String.valueOf(this.reportPages[0].length));

        tableTag.attr("place", new StringBuffer(String.valueOf(x + 1)).append(",")
                .append(String.valueOf(y + 1)).toString());
    }

    // b:TODO 输出时需要根据自身大小来决定页数,这边做合并的工作？以后要考虑多页输出&frozen
    private Tag getTag(HTMLWriter htmlWriter, CellHtmlWriter cellWriter,
                       int reportIndex, Repository repo, ReportPageProvider reportpage) {
        reportpage = reportpage.deriveResolution(repo.getResolution());
        long s = System.currentTimeMillis();
        ClippedPageProvider cpp = null;
        ClippedPageProvider[] cp = reportpage.getPages();
        if (cp.length == 0) {
            cpp = new EmptyClippedReportPage();
        } else {
            cpp = cp[0];
        }
        Tag reportTableTag = htmlWriter.writeReportToHtml(
                new WebPageReportCase(cpp),
                reportIndex, cellWriter, repo, "");
        setPlaceAndCheckBorder(0, 0, reportTableTag);
        System.out.println(DateUtils.timeCostFrom(s)
                + " table write time.");
        return reportTableTag;
    }

//	}

    //b:TODO
    public void clear() {
        if (this.reportPages != null) {
            this.reportPages = null;
        }
    }
}