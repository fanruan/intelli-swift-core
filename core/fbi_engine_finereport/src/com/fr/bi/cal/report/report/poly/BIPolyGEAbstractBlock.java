package com.fr.bi.cal.report.report.poly;

import com.fr.report.stable.PolyBlockAttr;
import com.fr.stable.StringUtils;
import com.fr.stable.unit.UNIT;
import com.fr.stable.unit.UnitRectangle;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;


public class BIPolyGEAbstractBlock {
    private String blockName;
    private UnitRectangle bounds;
    private PolyBlockAttr blockAttr;

    /**
     * 是否含有单元格
     *
     * @return
     */
    public boolean isCells() {
        return false;
    }

    /**
     * 获取block的名字
     *
     * @return
     */
    public String getBlockName() {
        return blockName;
    }

    /**
     * 设置block的名字
     *
     * @param name
     */
    public void setBlockName(String name) {
        this.blockName = name;
    }

    /**
     * 获取block的属性
     *
     * @return
     */
    public PolyBlockAttr getBlockAttr() {
        if (blockAttr == null) {
            blockAttr = new PolyBlockAttr();
        }
        return blockAttr;
    }

    /**
     * 设置block的属性
     *
     * @param blockAttr
     */
    public void setBlockAttr(PolyBlockAttr blockAttr) {
        this.blockAttr = blockAttr;
    }

    /**
     * 获取大小位置
     *
     * @return
     */
    public UnitRectangle getBounds() {
        return bounds == null ? null : new UnitRectangle(bounds);
    }

    /**
     * 设置大小位置
     *
     * @param bounds
     */
    public void setBounds(UnitRectangle bounds) {
        if (bounds.x.less_than_zero()) {
            bounds.x = UNIT.ZERO;
        }
        if (bounds.y.less_than_zero()) {
            bounds.y = UNIT.ZERO;
        }
        this.bounds = bounds;
        firePropertyChange();
    }

    /**
     * 发送大小改变事件
     */
    public void firePropertyChange() {
    }

    /**
     * @see com.fr.report.block.ResultBlock#getEffectiveHeight()
     */
    public UNIT getEffectiveHeight() {
        return getBounds().height;
    }

    /**
     * @see com.fr.report.block.ResultBlock#getEffectiveWidth()
     */
    public UNIT getEffectiveWidth() {
        return getBounds().width;
    }

    /**
     * @see com.fr.report.block.ResultBlock#writeCommonXML(com.fr.stable.xml.XMLPrintWriter)
     */
    public void writeCommonXML(XMLPrintWriter writer) {
    }

    /**
     * @see com.fr.stable.xml.XMLReadable#readXML(com.fr.stable.xml.XMLableReader)
     */
    public void readXML(XMLableReader reader) {
    }

    /**
     * @see com.fr.stable.xml.XMLWriter#writeXML(com.fr.stable.xml.XMLPrintWriter)
     */
    public void writeXML(XMLPrintWriter writer) {
    }

    /**
     * 克隆方法
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        BIPolyGEAbstractBlock cloned = (BIPolyGEAbstractBlock) super.clone();
        if (StringUtils.isNotEmpty(blockName)) {
            cloned.blockName = blockName;
        }
        if (bounds != null) {
            cloned.bounds = (UnitRectangle) bounds.clone();
        }
        if (blockAttr != null) {
            cloned.blockAttr = (PolyBlockAttr) blockAttr.clone();
        }
        return cloned;
    }
}