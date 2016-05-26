package com.fr.bi.conf.data.source.operator.add.datediff;

import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.operator.add.AbstractAddColumnOperator;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.constant.DateConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.time.BIDateUtils;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

/**
 * Created by GUY on 2015/3/5.
 */
public class DateDiffOperator extends AbstractAddColumnOperator {


    public static final String XML_TAG = "DateDiffOperator";
    private static final long serialVersionUID = -1675716963282566541L;
    @BICoreField
    private String field1;
    @BICoreField
    private String field2;
    @BICoreField
    private int unit;

    public DateDiffOperator(long userId) {
        super(userId);
    }

    public DateDiffOperator() {
    }

    @Override
    public String xmlTag() {
        return XML_TAG;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = super.createJSON();
        JSONObject item = new JSONObject();
        jo.put("item", item);
        item.put("firstField", field1);
        item.put("secondField", field2);
        item.put("type", unit);
        jo.put("add_column_type", BIJSONConstant.ETL_ADD_COLUMN_TYPE.DATE_DIFF);
        return jo;
    }

    /**
     * 将JSON对象转换成java对象
     *
     * @param jo json对象
     * @throws Exception 报错
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        super.parseJSON(jo);
        if (jo.has("item")) {
            JSONObject jsonObject = jo.getJSONObject("item");
            if (jsonObject.has("firstField")) {
                field1 = jsonObject.getString("firstField");
            }
            if (jsonObject.has("secondField")) {
                field2 = jsonObject.getString("secondField");
            }
            if (jsonObject.has("type")) {
                unit = jsonObject.getInt("type");
            }
        }
    }

    private interface ValueGetter {
        long getTime(int row);

        void check();
    }

    private class SystemTimeValueGetter implements ValueGetter {

        long t;

        SystemTimeValueGetter(long t) {
            this.t = t;
        }

        public long getTime(int row) {
            return t;
        }

        public void check() {
        }
    }

    private class DataValueGetter implements ValueGetter {
        ICubeTableService ti;
        BIKey key;

        DataValueGetter(ICubeTableService ti, BIKey key) {
            this.ti = ti;
            this.key = key;
        }

        public long getTime(int row) {
            return (Long) ti.getRow(key, row);
        }

        public void check() {
            BIDateUtils.checkDateFieldType(ti.getColumns(), key);
        }
    }


    private ValueGetter createValueGetter(String field, ICubeTableService ti, long systemTime) {
        ValueGetter vg = null;
        if (ComparatorUtils.equals(field, BIReportConstant.SYSTEM_TIME)) {
            vg = new SystemTimeValueGetter(systemTime);
        } else {
            vg = new DataValueGetter(ti, new IndexKey(field));
        }
        vg.check();
        return vg;
    }


    @Override
    protected int write(Traversal<BIDataValue> travel, ICubeTableService ti, int startCol) {
        int rowCount = ti.getRowCount();
        DateDiffCalculator dc = getDiffCalculator(unit);
        long systemTime = System.currentTimeMillis();
        ValueGetter g1 = createValueGetter(field1, ti, systemTime);
        ValueGetter g2 = createValueGetter(field2, ti, systemTime);
        for (int row = 0; row < rowCount; row++) {
            int value = dc.get(g1.getTime(row), g2.getTime(row));
            try {
                travel.actionPerformed(new BIDataValue(row, startCol, value));
            } catch (Exception e) {
                BILogger.getLogger().error("incorrect formular");
                travel.actionPerformed(new BIDataValue(row, startCol, null));
            }
        }
        return rowCount;
    }

    @Override
    protected int getClassType() {
        return DBConstant.CLASS.INTEGER;
    }

    @Override
    public void readXML(XMLableReader reader) {
        super.readXML(reader);
        if (reader.isAttr()) {
            try {
                this.field1 = reader.getAttrAsString("field1", StringUtils.EMPTY);
                this.field2 = reader.getAttrAsString("field2", StringUtils.EMPTY);
                this.unit = reader.getAttrAsInt("unit", DateConstant.DATE.DAY);
            } catch (Exception e) {
            }
        }

    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        super.writeXML(writer);
        try {
            writer.attr("field1", field1);
            writer.attr("field2", field2);
            writer.attr("unit", unit);
        } catch (Exception e) {
        }
        writer.end();
    }

    private static DateDiffCalculator getDiffCalculator(int unit) {
        switch (unit) {
            case DateConstant.DATE.YEAR:
                return YearDiff.INSTANCE;
            case DateConstant.DATE.MONTH:
                return MonthDiff.INSTANCE;
            case DateConstant.DATE.SEASON:
                return SeasonDiff.INSTANCE;
            case DateConstant.DATE.DAY:
                return DayDiff.INSTANCE;
            default: {
                return getDiffCalculator(DateConstant.DATE.DAY);
            }
        }
    }
}