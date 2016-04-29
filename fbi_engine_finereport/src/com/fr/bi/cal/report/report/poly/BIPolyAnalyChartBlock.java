package com.fr.bi.cal.report.report.poly;

import com.fr.base.Painter;
import com.fr.base.Style;
import com.fr.base.chart.BaseChartPainter;
import com.fr.report.core.PaintUtils;
import com.fr.report.poly.AnalyChartBlock;
import com.fr.stable.html.Tag;
import com.fr.stable.web.Repository;
import com.fr.web.core.HTMLWriter;
import com.fr.web.output.html.chwriter.CellHtmlWriter;


/**
 * BI聚合图表分析结果.
 *
 * @author kunsnat E-mail:kunsnat@gmail.com
 * @version 创建时间：2013-3-27 下午02:47:18
 */
public class BIPolyAnalyChartBlock extends AnalyChartBlock implements BIAnalyPolyBlock {

    private static final long serialVersionUID = 4360357140623863575L;
    private static final int WIDTH_OFFSET = 20;
    private static final int HEIGHT_OFFSET = 47;

    public BIPolyAnalyChartBlock(BaseChartPainter chartPainter) {
        super(chartPainter);
    }

    /**
     * 返回Tag标签.
     */
    @Override
    public Tag getTag(HTMLWriter htmlWriter, CellHtmlWriter cellWriter, int reportIndex, Repository repo) {
        return getContentTag(htmlWriter, cellWriter, reportIndex, repo);
    }

    /**
     * 返回内容Tag标签
     *
     * @see com.fr.bi.cal.report.report.poly.BIAnalyPolyBlock#getContentTag(com.fr.web.core.HTMLWriter, com.fr.web.core.chwriter.CellHtmlWriter, int, com.fr.stable.web.Repository)
     */
    @Override
    public Tag getContentTag(HTMLWriter htmlWriter, CellHtmlWriter cellWriter,
                             int reportIndex, Repository repo) {
        int resolution = repo.getResolution();
        int width = getEffectiveWidth().toPixI(resolution);
        int height = getEffectiveHeight().toPixI(resolution);
        Tag tdTag = new Tag("td");
        PaintUtils.paintTag((Painter) chartPainter, repo, width - WIDTH_OFFSET, height - HEIGHT_OFFSET, Style.NULL_STYLE, tdTag);
        return new Tag("table").cls("x-chart-table").sub(new Tag("tr").sub(tdTag));
    }
}