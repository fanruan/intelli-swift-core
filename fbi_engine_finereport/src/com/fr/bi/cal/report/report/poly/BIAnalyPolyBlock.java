package com.fr.bi.cal.report.report.poly;

import com.fr.report.block.AnalyPolyBlock;
import com.fr.stable.html.Tag;
import com.fr.stable.web.Repository;
import com.fr.web.core.HTMLWriter;
import com.fr.web.output.html.chwriter.CellHtmlWriter;


/**
 * BI的res block
 *
 * @author Daniel
 */
public interface BIAnalyPolyBlock extends AnalyPolyBlock {

    /**
     * 获取content内容的tag
     *
     * @param htmlWriter
     * @param cellWriter
     * @param reportIndex
     * @param repo
     * @return
     */
    public Tag getContentTag(HTMLWriter htmlWriter, CellHtmlWriter cellWriter, int reportIndex, Repository repo);


}