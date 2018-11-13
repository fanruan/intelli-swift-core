package com.fr.swift.adaptor.transformer.etl;

import com.finebi.conf.constant.BIConfConstants;
import com.finebi.conf.exception.FineAnalysisOperationUnSafe;
import com.finebi.conf.exception.FineEngineException;
import com.finebi.conf.internalimp.analysis.bean.operator.join.JoinBean;
import com.finebi.conf.internalimp.analysis.bean.operator.join.JoinBeanValue;
import com.finebi.conf.internalimp.analysis.bean.operator.join.JoinNameItem;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.etl.join.JoinColumn;
import com.fr.swift.source.etl.join.JoinOperator;
import com.fr.swift.source.etl.join.JoinType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2018/4/11
 */
public class JoinAdaptor {
    public static JoinOperator fromJoinBean(JoinBean jb) throws FineEngineException {
        JoinBeanValue jbv = jb.getValue();

        if (jbv.getBasis().isEmpty()) {
            throw new FineAnalysisOperationUnSafe("");
        }
        List<JoinColumn> joinColumns = new ArrayList<JoinColumn>();
        for (JoinNameItem joinName : jbv.getResult()) {
            JoinColumn jc = new JoinColumn(joinName.getName(), joinName.getIsLeft(), joinName.getColumnName());
            joinColumns.add(jc);
        }

        List<ColumnKey> leftColumns = new ArrayList<ColumnKey>();
        List<ColumnKey> rightColumns = new ArrayList<ColumnKey>();

        for (List<String> leftRight : jbv.getBasis()) {
            leftColumns.add(new ColumnKey(leftRight.get(0)));
            rightColumns.add(new ColumnKey(leftRight.get(1)));

        }

        int type = jbv.getStyle();

        return new JoinOperator(joinColumns,
                leftColumns.toArray(new ColumnKey[leftColumns.size()]),
                rightColumns.toArray(new ColumnKey[rightColumns.size()]),
                getJoinType(type));
    }

    private static JoinType getJoinType(int type) {
        switch (type) {
            case BIConfConstants.CONF.JOIN.INNER:
                return JoinType.INNER;
            case BIConfConstants.CONF.JOIN.OUTER:
                return JoinType.OUTER;
            case BIConfConstants.CONF.JOIN.RIGHT:
                return JoinType.RIGHT;
            default:
                return JoinType.LEFT;
        }
    }
}