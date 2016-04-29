/**
 *
 */
package com.fr.bi.conf.manager.singletable.data;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.base.TemplateUtils;
import com.fr.bi.conf.manager.timer.data.UpdateFrequency;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.BITable;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;
import com.fr.script.Calculator;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLReadable;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;

import java.util.*;


public class SingleTableUpdateAction implements XMLable, JSONTransform {

    public static final String XML_TAG = "single_table_update_action";
    /**
     *
     */
    private static final long serialVersionUID = 4567464822949359389L;
    private int update_type = DBConstant.SINGLE_TABLE_UPDATE_TYPE.ALL;
    private int update_schedule = DBConstant.SINGLE_TABLE_UPDATE.TOGETHER;
    private BITable tableKey;
    private List<Timer> timerList = new ArrayList<Timer>();
    private List<UpdateFrequency> update_list = new ArrayList<UpdateFrequency>();
    private String insertExpression = StringUtils.EMPTY;
    private String removeExpression = StringUtils.EMPTY;
    private String modifyExpression = StringUtils.EMPTY;
    private long userId;

    public SingleTableUpdateAction() {

    }

    public SingleTableUpdateAction(String config) throws Exception {
        JSONObject jo = new JSONObject(config);
        parseJSON(jo);
    }

    public SingleTableUpdateAction(BITable tableKey, long userId) {
        this.tableKey = tableKey;
        this.userId = userId;
    }

    private String getBlockValue(ICubeDataLoader loader, String expression) {
        Date date = loader.getTableIndex(tableKey).getLastTime();
        Calculator calculator = Calculator.createCalculator();
        calculator.set("lastdatepara", date);
        expression = TemplateUtils.renderTpl(calculator, expression);
        return expression;
    }

    public String getRemove(ICubeDataLoader loader) {
        return getBlockValue(loader, removeExpression);
    }

    public String getModify(ICubeDataLoader loader) {
        return getBlockValue(loader, modifyExpression);
    }

    public String getInsert(ICubeDataLoader loader) {
        return getBlockValue(loader, insertExpression);
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getUpdate_type() {
        return update_type;
    }

    public void setUpdate_type(int update_type) {
        this.update_type = update_type;
    }

    public int getUpdate_schedule() {
        return update_schedule;
    }

    public void setUpdate_schedule(int update_schedule) {
        this.update_schedule = update_schedule;
    }

    public void setTableKey(BITable tableKey) {
        this.tableKey = tableKey;
    }

    public String getInsertExpression() {
        return insertExpression;
    }

    public void setInsertExpression(String insertExpression) {
        this.insertExpression = insertExpression;
    }

    public String getRemoveExpression() {
        return removeExpression;
    }

    public void setRemoveExpression(String removeExpression) {
        this.removeExpression = removeExpression;
    }

    public String getModifyExpression() {
        return modifyExpression;
    }

    public void setModifyExpression(String modifyExpression) {
        this.modifyExpression = modifyExpression;
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isAttr()) {
            this.userId = reader.getAttrAsLong("userId", UserControl.getInstance().getSuperManagerID());
            this.update_type = reader.getAttrAsInt("update_type", DBConstant.SINGLE_TABLE_UPDATE_TYPE.ALL);
            this.update_schedule = reader.getAttrAsInt("update_schedule", DBConstant.SINGLE_TABLE_UPDATE.TOGETHER);
            String id = reader.getAttrAsString("id", StringUtils.EMPTY);
            this.tableKey = new BITable(id);
            this.insertExpression = reader.getAttrAsString("insert", StringUtils.EMPTY);
            this.removeExpression = reader.getAttrAsString("remove", StringUtils.EMPTY);
            this.modifyExpression = reader.getAttrAsString("modify", StringUtils.EMPTY);
        }
        if (reader.isChildNode()) {
            if (ComparatorUtils.equals("update_list", reader.getTagName())) {
                readUpdateList(reader);
            }
        }
    }

    private void readUpdateList(XMLableReader reader) {
        final java.util.List<UpdateFrequency> update_list = new java.util.ArrayList<UpdateFrequency>();
        reader.readXMLObject(new XMLReadable() {
            @Override
            public void readXML(XMLableReader reader) {
                if (reader.isChildNode()) {
                    if (ComparatorUtils.equals(reader.getTagName(), UpdateFrequency.XML_TAG)) {
                        UpdateFrequency pack = new UpdateFrequency();
//                        reader.readXMLObject(pack);
                        update_list.add(pack);
                    }
                }
            }
        });
        this.update_list.addAll(update_list);
    }


    /* (non-Javadoc)
     * @see com.fr.stable.xml.XMLWriter#writeXML(com.fr.stable.xml.XMLPrintWriter)
     */
    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(XML_TAG);
        writer.attr("userId", userId);
        writer.attr("update_schedule", this.update_schedule);
        writer.attr("id", tableKey.getID().getIdentityValue());

        writer.attr("update_type", this.update_type);
        if (this.update_type == DBConstant.SINGLE_TABLE_UPDATE_TYPE.PART) {
            writer.attr("insert", insertExpression).attr("remove", removeExpression).attr("modify", modifyExpression);
        }
        writer.startTAG("update_list");
        for (int i = 0; i < this.update_list.size(); i++) {
//            this.update_list.get(i).writeXML(writer);
        }
        writer.end();

        writer.end();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * 将JSON对象转换成java对象
     *
     * @param jo json对象
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("update_type")) {
            this.update_type = jo.getInt("update_type");

            if (this.update_type == DBConstant.SINGLE_TABLE_UPDATE_TYPE.PART) {
                this.insertExpression = jo.optString("insert", StringUtils.EMPTY);
                this.removeExpression = jo.optString("remove", StringUtils.EMPTY);
                this.modifyExpression = jo.optString("modify", StringUtils.EMPTY);
            }

        }
        if (jo.has("update_schedule")) {
            this.update_schedule = jo.getInt("update_schedule");
        }
        if (jo.has("table")) {
            BITable table = new BITable();
            table.parseJSON(jo.getJSONObject("table"));
            this.tableKey = table;
        }
        if (jo.has("update_list")) {
            JSONArray ja = jo.getJSONArray("update_list");
            List<UpdateFrequency> list = new ArrayList<UpdateFrequency>();
            for (int i = 0, len = ja.length(); i < len; i++) {
                UpdateFrequency uf = new UpdateFrequency();
                uf.parseJSON(ja.getJSONObject(i));
                list.add(uf);
            }
            this.update_list = list;
        }


    }

    /**
     * 将Java对象转换成JSON对象
     *
     * @return json对象
     * @throws Exception
     */
    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("table", tableKey.createJSON());
        jo.put("update_type", update_type);

        if (this.update_type == DBConstant.SINGLE_TABLE_UPDATE_TYPE.PART) {
            jo.put("modify", modifyExpression);
            jo.put("insert", insertExpression);
            jo.put("remove", removeExpression);
        }

        jo.put("update_schedule", update_schedule);
        JSONArray ja = new JSONArray();
        jo.put("update_list", ja);
        for (int i = 0; i < this.update_list.size(); i++) {
            ja.put(this.update_list.get(i).createJSON());
        }
        return jo;
    }

    /**
     * @return
     */
    public BITable getTableKey() {
        return tableKey;
    }

    /**
     * 清除
     */
    public void clear() {
        Iterator<Timer> iter = timerList.iterator();
        while (iter.hasNext()) {
            Timer t = iter.next();
            if (t != null) {
                t.cancel();
            }
        }
        timerList.clear();
    }

    /**
     * 开始更新
     *
     * @param creator timer创建
     */
    public void scheduleStart(BICubeTimeTaskCreator creator) {
        clear();
        timerList = new ArrayList<Timer>();
        Iterator<UpdateFrequency> iter = update_list.iterator();
        while (iter.hasNext()) {
            Timer timer = new Timer();
            UpdateFrequency uf = iter.next();
//            int hour = uf.getUpdateHour();
//            int frequency = uf.getFrequency();
//            Date startDate = BIDateUtils.createStartDate(hour, frequency);
//            long scheduleTime = BIDateUtils.createScheduleTime(hour, frequency);
//            timer.schedule(creator.createNewObject(), startDate, scheduleTime);
            timerList.add(timer);
        }
    }

    public int getUpdateType() {
        return update_type;
    }

    public int getUpdateSchedule() {
        return update_schedule;
    }

}