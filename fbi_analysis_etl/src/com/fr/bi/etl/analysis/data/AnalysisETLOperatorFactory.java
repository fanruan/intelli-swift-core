package com.fr.bi.etl.analysis.data;

import com.fr.bi.conf.data.source.operator.IETLOperator;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 小灰灰 on 2016/4/8.
 */
public class AnalysisETLOperatorFactory {

    public static List<IETLOperator> createOperatorsByJSON(JSONObject jo, long userId) {
        return new ArrayList<IETLOperator>();
    }

}
