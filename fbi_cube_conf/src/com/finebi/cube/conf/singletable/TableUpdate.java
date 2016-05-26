/**
 *
 */
package com.finebi.cube.conf.singletable;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.timer.UpdateFrequency;
import com.fr.base.TemplateUtils;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.json.JSONTransform;
import com.fr.script.Calculator;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLReadable;
import com.fr.stable.xml.XMLableReader;

import java.util.*;

/**
 * This class created on 2016/5/23.
 *
 * @author Connery
 * @since 4.0
 */
public class TableUpdate implements JSONTransform {

    public static final String XML_TAG = "single_table_update_action";
    /**
     *
     */
    private static final long serialVersionUID = 4567464822949359389L;
    private int update_type = DBConstant.SINGLE_TABLE_UPDATE_TYPE.ALL;
    private int update_schedule = DBConstant.SINGLE_TABLE_UPDATE.TOGETHER;
    private BusinessTable tableKey;
    private List<Timer> timerList = new ArrayList<Timer>();
    private List<UpdateFrequency> update_list = new ArrayList<UpdateFrequency>();
    private String insertExpression = StringUtils.EMPTY;
    private String removeExpression = StringUtils.EMPTY;
    private String modifyExpression = StringUtils.EMPTY;
    private long userId;

    public TableUpdate() {

    }

    public TableUpdate(String config) throws Exception {
        JSONObject jo = new JSONObject(config);
        parseJSON(jo);
    }

    public TableUpdate(BusinessTable tableKey, long userId) {
        this.tableKey = tableKey;
        this.userId = userId;
    }

    private String getBlockValue(ICubeDataLoader loader, String expression) {
        Date date = loader.getTableIndex(tableKey.getTableSource()).getLastTime();
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

    public void setTableKey(BusinessTable tableKey) {
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


    private void readUpdateList(XMLableReader reader) {
        final List<UpdateFrequency> update_list = new ArrayList<UpdateFrequency>();
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
            BusinessTable table = new BIBusinessTable(null);
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
    public BusinessTable getTableKey() {
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