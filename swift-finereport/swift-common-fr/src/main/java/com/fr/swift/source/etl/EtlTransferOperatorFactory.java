package com.fr.swift.source.etl;

import com.fr.swift.source.etl.columnfilter.ColumnFilterOperator;
import com.fr.swift.source.etl.columnfilter.ColumnFilterTransferOperator;
import com.fr.swift.source.etl.columnrowtrans.ColumnRowTransOperator;
import com.fr.swift.source.etl.columnrowtrans.ColumnRowTransferOperator;
import com.fr.swift.source.etl.date.GetFromDateOperator;
import com.fr.swift.source.etl.date.GetFromDateTransferOperator;
import com.fr.swift.source.etl.datediff.DateDiffOperator;
import com.fr.swift.source.etl.datediff.DateDiffTransferOperator;
import com.fr.swift.source.etl.detail.DetailOperator;
import com.fr.swift.source.etl.detail.DetailTransferOperator;
import com.fr.swift.source.etl.formula.ColumnFormulaOperator;
import com.fr.swift.source.etl.formula.ColumnFormulaTransferOperator;
import com.fr.swift.source.etl.group.GroupAssignmentOperator;
import com.fr.swift.source.etl.group.GroupAssignmentTransferOperator;
import com.fr.swift.source.etl.group.GroupNumericOperator;
import com.fr.swift.source.etl.group.GroupNumericTransferOperator;
import com.fr.swift.source.etl.groupsum.GroupSumOperator;
import com.fr.swift.source.etl.groupsum.GroupSumTransferOperator;
import com.fr.swift.source.etl.join.JoinOperator;
import com.fr.swift.source.etl.join.JoinTransferOperator;
import com.fr.swift.source.etl.rowcal.accumulate.AccumulateRowOperator;
import com.fr.swift.source.etl.rowcal.accumulate.AccumulateRowTransferOperator;
import com.fr.swift.source.etl.rowcal.alldata.AllDataRowCalculatorOperator;
import com.fr.swift.source.etl.rowcal.alldata.AllDataTransferOperator;
import com.fr.swift.source.etl.rowcal.rank.RankRowOperator;
import com.fr.swift.source.etl.rowcal.rank.RankRowTransferOperator;
import com.fr.swift.source.etl.selfrelation.OneUnionRelationOperator;
import com.fr.swift.source.etl.selfrelation.OneUnionRelationTransferOperator;
import com.fr.swift.source.etl.selfrelation.TwoUnionRelationOperator;
import com.fr.swift.source.etl.selfrelation.TwoUnionRelationTransferOperator;
import com.fr.swift.source.etl.union.UnionOperator;
import com.fr.swift.source.etl.union.UnionTransferOperator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author pony
 * @date 2018/1/8
 */
public class EtlTransferOperatorFactory {
    private static Map<String, ETLTransferCreator> extra = new ConcurrentHashMap<String, ETLTransferCreator>();

    public static ETLTransferOperator createTransferOperator(ETLOperator operator) {
        switch (operator.getOperatorType()) {
            case DETAIL:
                return transferDetailOperator((DetailOperator) operator);
            case UNION:
                return transferUnionOperator((UnionOperator) operator);
            case JOIN:
                return transferJoinOperator((JoinOperator) operator);
            case GROUP_SUM:
                return transferGroupSumOperator((GroupSumOperator) operator);
            case COLUMN_ROW_TRANS:
                return transferColumnRowTransOperator((ColumnRowTransOperator) operator);
            case FILTER:
                return transferColumnFilterOperator((ColumnFilterOperator) operator);
            case ONE_UNION_RELATION:
                return transferOneUnionRelationOperator((OneUnionRelationOperator) operator);
            case TWO_UNION_RELATION:
                return transferTwoUnionRelationOperator((TwoUnionRelationOperator) operator);
            case COLUMN_FORMULA:
                return transferColumnFormulaOperator((ColumnFormulaOperator) operator);
            case ACCUMULATE:
                return transferAccumulateRowOperator((AccumulateRowOperator) operator);
            case ALL_DATA:
                return transferAllDataCalculatorOperator((AllDataRowCalculatorOperator) operator);
            case RANK:
                return transferRankRowOperator((RankRowOperator) operator);
            case GET_DATE:
                return transferGetDateOperator((GetFromDateOperator) operator);
            case DATE_DIFF:
                return transferDateDiffOperator((DateDiffOperator) operator);
            case GROUP_STRING:
                return transferGroupStringOperator((GroupAssignmentOperator) operator);
            case GROUP_NUM:
                return transferGroupNumOperator((GroupNumericOperator) operator);
            default:
        }
        ETLTransferCreator creator = extra.get(operator.getClass().getName());
        return creator == null ? null : creator.createTransferOperator(operator);
    }

    public static void register(Class c, ETLTransferCreator creator) {
        extra.put(c.getName(), creator);
    }

    public static ETLTransferOperator transferGroupNumOperator(GroupNumericOperator operator) {
        return new GroupNumericTransferOperator(operator.getColumnKey(), operator.getMax(),
                operator.getMin(), operator.getUseOther(), operator.getNodes());
    }

    public static ETLTransferOperator transferGroupStringOperator(GroupAssignmentOperator operator) {
        return new GroupAssignmentTransferOperator(operator.getOtherName(), operator.getColumnKey(), operator.getGroup());
    }

    private static ETLTransferOperator transferDateDiffOperator(DateDiffOperator operator) {
        return new DateDiffTransferOperator(operator.getField1(), operator.getField2(), operator.getGroupType());
    }

    private static ETLTransferOperator transferGetDateOperator(GetFromDateOperator operator) {
        return new GetFromDateTransferOperator(operator.getField(), operator.getType());
    }

    private static ETLTransferOperator transferRankRowOperator(RankRowOperator operator) {
        return new RankRowTransferOperator(operator.getType(), operator.getColumnKey(), operator.getDimension());
    }

    private static ETLTransferOperator transferAllDataCalculatorOperator(AllDataRowCalculatorOperator operator) {
        return new AllDataTransferOperator(operator.getCalculatorType(), operator.getColumnName(), operator.getDimension());
    }

    private static ETLTransferOperator transferDetailOperator(DetailOperator operator) {
        return new DetailTransferOperator(operator.getFields());
    }

    private static ETLTransferOperator transferUnionOperator(UnionOperator operator) {
        return new UnionTransferOperator(operator.getColumnNameList());
    }

    private static ETLTransferOperator transferJoinOperator(JoinOperator operator) {
        return new JoinTransferOperator(operator.getColumns(), operator.getlKey(), operator.getrKey(), operator.getType());
    }

    private static ETLTransferOperator transferGroupSumOperator(GroupSumOperator operator) {
        return new GroupSumTransferOperator(operator.getTargets(), operator.getDimensions());
    }

    private static ETLTransferOperator transferColumnRowTransOperator(ColumnRowTransOperator operator) {
        return new ColumnRowTransferOperator(operator.getGroupName(), operator.getLcName(), operator.getColumns(), operator.getLcValue(), operator.getOtherColumnNames());
    }

    private static ETLTransferOperator transferColumnFilterOperator(ColumnFilterOperator operator) {
        return new ColumnFilterTransferOperator(operator.getFilterInfo());
    }

    private static ETLTransferOperator transferOneUnionRelationOperator(OneUnionRelationOperator operator) {
        return new OneUnionRelationTransferOperator(operator.getColumnName(), operator.getShowColumns(), operator.getIdColumnName(), operator.getColumnType(), operator.getColumns());
    }

    private static ETLTransferOperator transferTwoUnionRelationOperator(TwoUnionRelationOperator operator) {
        return new TwoUnionRelationTransferOperator(operator.getColumns(), operator.getIdColumnName(), operator.getShowColumns(), operator.getAddedName(), operator.getParentIdColumnName());
    }

    private static ETLTransferOperator transferColumnFormulaOperator(ColumnFormulaOperator operator) {
        return new ColumnFormulaTransferOperator(operator.getColumnType(), operator.getExpression());
    }

    private static ETLTransferOperator transferAccumulateRowOperator(AccumulateRowOperator operator) {
        return new AccumulateRowTransferOperator(operator.getColumnKey(), operator.getDimension());
    }

    public interface ETLTransferCreator {
        ETLTransferOperator createTransferOperator(ETLOperator operator);
    }
}
