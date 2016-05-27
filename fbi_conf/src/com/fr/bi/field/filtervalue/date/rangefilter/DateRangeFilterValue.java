/**
 *
 */
package com.fr.bi.field.filtervalue.date.rangefilter;

import com.finebi.cube.api.ICubeColumnIndexReader;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.conf.report.filter.NullFilterDealer;
import com.fr.bi.conf.report.widget.field.filtervalue.AbstractFilterValue;
import com.fr.bi.conf.report.widget.field.filtervalue.date.DateFilterValue;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.key.date.BIDay;
import com.fr.bi.stable.data.key.date.DateRange;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexTypeKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.report.result.DimensionCalculator;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.time.BIDateUtils;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;


public abstract class DateRangeFilterValue extends AbstractFilterValue<Long> implements DateFilterValue, NullFilterDealer {

    /**
	 *
	 */
	private static final long serialVersionUID = 924801123261384205L;
    @BICoreField
	protected DateRange range;

    /**
     * 获取过滤后的索引
     *
     * @param target
     * @param loader loader对象
     * @return 过滤索引
     * FIXME 需要实现
     */
    @Override
    public GroupValueIndex createFilterIndex(DimensionCalculator dimension, CubeTableSource target, ICubeDataLoader loader, long userId) {

        if (range == null || (range.getEnd() == null && range.getStart() == null)) {
            return getGroupValueIndexWhenNull(target, loader);
        }
        ICubeColumnIndexReader yearMap = loader.getTableIndex(dimension.getField().getTableBelongTo().getTableSource()).loadGroup(new IndexTypeKey(dimension.getField().getFieldName(), BIReportConstant.GROUP.Y), dimension.getRelationList());
        ICubeColumnIndexReader monthMap = loader.getTableIndex(dimension.getField().getTableBelongTo().getTableSource()).loadGroup(new IndexTypeKey(dimension.getField().getFieldName(), BIReportConstant.GROUP.M), dimension.getRelationList());
        ICubeColumnIndexReader dayMapMap = loader.getTableIndex(dimension.getField().getTableBelongTo().getTableSource()).loadGroup(new IndexTypeKey(dimension.getField().getFieldName(), BIReportConstant.GROUP.MD), dimension.getRelationList());
        return BIDateUtils.createFilterIndex(yearMap, monthMap, dayMapMap, range.getStart(), range.getEnd());

    }

    private GroupValueIndex getGroupValueIndexWhenNull(BusinessTable targetKey, ICubeDataLoader loader) {
        return loader.getTableIndex(targetKey.getTableSource()).getAllShowIndex();
    }

    @Override
    public boolean isTopOrBottomFilterValue() {
        return false;
    }

    public DateRange getRange() {
        return range;
    }

    public void setRange(DateRange range) {
        this.range = range;
    }


    /**
     * 解析json
     *
     * @param jo     json对象
     * @param userId 用户id
     * @throws Exception 报错
     */
    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {

        if (jo.has("filter_value") && (jo.get("filter_value") instanceof JSONObject)) {
            range = new DateRange();
            range.parseJSON(jo.getJSONObject("filter_value"));
        }
    }


    /**
     * todo 代码不好
     * 创建json
     *
     * @return json对象
     * @throws Exception 报错
     */
    @Override
    public JSONObject createJSON() throws Exception {
		JSONObject jo = range.createJSON();
        JSONObject resjo = new JSONObject();
        resjo.put("filter_value", jo);
        return resjo;
    }

    /**
     * @param reader XML读取对象
     * @see com.fr.stable.xml.XMLableReader
     */
    @Override
    public void readXML(XMLableReader reader) {
        if (reader.getTagName().equals("value")) {

            try {
                JSONObject jo = new JSONObject(reader.getAttrAsString("value", StringUtils.EMPTY));
                this.parseJSON(jo, UserControl.getInstance().getSuperManagerID());
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
        }
    }

    /**
     * @param writer XML写入对象
     */
    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);

        try {
            writer.attr("value", createJSON().toString());
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
        writer.end();
    }

    /**
     * 重写code
     *
     * @return 数值
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = this.getClass().getName().hashCode();
        result = prime * result + ((range == null) ? 0 : range.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        DateRangeFilterValue other = (DateRangeFilterValue) obj;
        if (range == null) {

            if (other.range != null) {
                return false;
            }

        } else if (!ComparatorUtils.equals(range, other.range)) {
            return false;
        }

        return true;
    }


    @Override
    public boolean canCreateFilterIndex() {
        return false;
    }

	@Override
	public boolean isMatchValue(Long v) {
		if(v == null){
			return dealWithNullValue();
		}
        BIDay key = new BIDay(v);
		return inRange(key);
	}

	 protected abstract boolean inRange(BIDay key);
}