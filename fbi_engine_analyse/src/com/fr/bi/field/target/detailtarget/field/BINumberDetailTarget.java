package com.fr.bi.field.target.detailtarget.field;

import com.fr.bi.field.target.detailtarget.BIAbstractDetailTarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.operation.sort.sort.NumberASCSort;
import com.fr.bi.stable.operation.sort.sort.NumberDSCSort;
import com.fr.json.JSONObject;

import java.util.Map;


public class BINumberDetailTarget extends BIAbstractDetailTarget {

    /**
     * 获取显示值
     *
     * @param value 明细表一行值
     * @return 显示值
     */
    @Override
    public Object createShowValue(Object value) {
        return value;
    }


    /**
     * 是否为计算指标
     *
     * @return 是否为计算指标
     */
    @Override
    public boolean isCalculateTarget() {
        return false;
    }

    /**
     * 可否计算，看他调用到的指标有没有算完，处理计算指标嵌套情况
     *
     * @param values 指标名和值的map
     * @return 可否计算
     */
    @Override
    public boolean isReady4Calculate(Map<String, Object> values) {
        return true;
    }


    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception{
        super.parseJSON(jo, userId);
        if(jo.has("sort")) {
            JSONObject sort = jo.optJSONObject("sort");
            int type = sort.optInt("type");
            switch (type) {
                case BIReportConstant.SORT.ASC:
                    this.sort = new NumberASCSort();
                    break;
                case BIReportConstant.SORT.DESC:
                    this.sort = new NumberDSCSort();
                    break;
            }
        }
    }
}