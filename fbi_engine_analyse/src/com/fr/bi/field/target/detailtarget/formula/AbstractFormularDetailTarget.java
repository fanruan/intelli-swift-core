package com.fr.bi.field.target.detailtarget.formula;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.field.target.detailtarget.BIAbstractDetailTarget;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeColumnIndexReader;
import com.fr.bi.stable.utils.BIFormularUtils;
import com.fr.json.JSONObject;
import com.fr.script.Calculator;
import com.fr.stable.StringUtils;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by 小灰灰 on 2015/9/24.
 */
public abstract class AbstractFormularDetailTarget extends BIAbstractDetailTarget {
    protected String expression = StringUtils.EMPTY;

    /**
     * 将JSON对象转换成java对象
     *
     * @param jo     json对象
     * @param userId 用户id
     * @throws Exception
     */
    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);

        expression = jo.optJSONObject("_src").optJSONObject("expression").optString("formula_value");
    }


    /**
     * 计算值
     *
     * @param values 相关值
     * @param loader loader对象
     * @return 计算出的值
     */
    @Override
    public Object createDetailValue(Long row, Map<String, Object> values, ICubeDataLoader loader, long userId) {
        Object ob =  BIFormularUtils.getCalculatorValue(Calculator.createCalculator(), "=" + expression, values);
        if (ob instanceof Number){
            return ((Number)ob).doubleValue();
        }
        return ob;
    }

    @Override
    public BusinessTable createTableKey() {
        return null;
    }

    /**
     * 是否为计算指标
     *
     * @return 是
     */
    @Override
    public boolean isCalculateTarget() {
        return true;
    }


    /**
     * 是否可计算
     *
     * @param values 参数
     * @return 是否可计算
     */
    @Override
    public boolean isReady4Calculate(Map<String, Object> values) {
        boolean isReady = true;
        Iterator<String> it = BIFormularUtils.createColumnIndexMap(expression).values().iterator();
        while (it.hasNext()) {
            if (values.get(it.next()) == null) {
                isReady = false;
                break;
            }
        }
        return isReady;
    }

    /**
     * 创建分组map
     *
     * @param target 指标
     * @param loader loader对象
     * @return 空
     */
    @Override
    public ICubeColumnIndexReader createGroupValueMapGetter(BusinessTable target, ICubeDataLoader loader, long userId) {
        return null;
    }


    /**
     * 获得显示值
     *
     * @return 显示值
     */
    @Override
    public Object createShowValue(Object value) {
        return value;
    }
}