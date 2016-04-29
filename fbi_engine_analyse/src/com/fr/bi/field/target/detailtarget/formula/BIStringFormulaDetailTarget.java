package com.fr.bi.field.target.detailtarget.formula;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.utils.BIFormularUtils;
import com.fr.script.Calculator;
import com.fr.stable.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 小灰灰 on 2014/5/7.
 */
public class BIStringFormulaDetailTarget extends AbstractFormularDetailTarget {



    /**
     * 计算值
     *
     * @param values 相关值
     * @param loader loader对象
     * @return 计算出的值
     */
    @Override
    public Object createDetailValue(Long row, Map<String, Object> values, ICubeDataLoader loader, long userId) {
        Map<String, Object> v = new HashMap<String, Object>();
        Iterator<Map.Entry<String, Object>> it = values.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String, Object> entry = it.next();
            v.put(entry.getKey(), entry.getValue() instanceof String ? entry.getValue() : StringUtils.EMPTY);
        }
        return BIFormularUtils.getCalculatorValue(Calculator.createCalculator(), "=" + expression, v);
    }


}