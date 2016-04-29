package com.fr.bi.cal.report.report.poly;

import com.fr.report.core.block.PolyResultWorkSheet;
import com.fr.stable.html.Tag;
import com.fr.stable.unit.UNIT;
import com.fr.stable.web.Repository;
import com.fr.web.core.HTMLWriter;
import com.fr.web.output.html.chwriter.CellHtmlWriter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 模型控件结果block
 *
 * @author Daniel
 */
public class BIPolyGEModelResBlock extends BIPolyGEAbstractBlock implements BIAnalyPolyBlock {
    /**
     *
     */
    private static final long serialVersionUID = -5634758093143045505L;


    private List resList = new ArrayList();

    @Override
    public void writeHtml(Tag backgroundTag, HTMLWriter htmlWriter,
                          CellHtmlWriter cellWriter, int reportIndex, Repository repo) {
        int resolution = repo.getResolution();
        int offsetX = getBounds().x.toPixI(resolution);
        int offsetY = getBounds().y.toPixI(resolution);
        int width = getEffectiveWidth().toPixI(resolution);
        int height = getEffectiveHeight().toPixI(resolution);
        backgroundTag.sub(this.getTag(htmlWriter, cellWriter, reportIndex, repo));
        backgroundTag.css("width", offsetX + width + 5 + "px").css("height", offsetY + height + 5 + "px");
    }

    @Override
    public Tag getTag(HTMLWriter htmlWriter, CellHtmlWriter cellWriter,
                      int reportIndex, Repository repo) {
        return getContentTag(htmlWriter, cellWriter, reportIndex, repo);
    }

    /**
     * 将计算结果添加到list中
     *
     * @param block
     */
    public void addResBlock(BIAnalyPolyBlock block) {
        resList.add(block);
    }

    @Override
    public Tag getContentTag(HTMLWriter htmlWriter, CellHtmlWriter cellWriter,
                             int reportIndex, Repository repo) {
        int resolution = repo.getResolution();
        int height = getBounds().height.toPixI(resolution);
        Tag contentTag = new Tag("div");
        contentTag.css("height", height - 31 + "px");
        Iterator iter = resList.iterator();
        while (iter.hasNext()) {
            BIAnalyPolyBlock block = (BIAnalyPolyBlock) iter.next();
            int offsetX = block.getBounds().x.toPixI(resolution);
            int offsetY = block.getBounds().y.toPixI(resolution);
            Tag tag = new Tag("div").attr("blockName", block.getBlockName())
                    .css("position", "absolute")
                    .css("overflow", "hidden")
                    .css("left", offsetX + "px")
                    .css("top", offsetY + "px")
                    .css("height", block.getBounds().height.toPixI(resolution) + "px")
                    .css("width", block.getBounds().width.toPixI(resolution) + "px");
            tag.sub(block.getContentTag(htmlWriter, cellWriter, reportIndex, repo));
            contentTag.sub(tag);
        }
        return contentTag;
    }

    @Override
    public void addElemToSheet(ArrayList<UNIT> arrayList, ArrayList<UNIT> arrayList1, PolyResultWorkSheet polyResultWorkSheet) {

    }
}