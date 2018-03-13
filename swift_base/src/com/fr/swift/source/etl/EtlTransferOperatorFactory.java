package com.fr.swift.source.etl;

import com.fr.swift.source.etl.columnfilter.ColumnFilterOperator;
import com.fr.swift.source.etl.columnfilter.ColumnFilterTransferOperator;
import com.fr.swift.source.etl.columnrowtrans.ColumnRowTransOperator;
import com.fr.swift.source.etl.columnrowtrans.ColumnRowTransferOperator;
import com.fr.swift.source.etl.datamining.DataMiningOperator;
import com.fr.swift.source.etl.datamining.DataMiningTransferOperator;
import com.fr.swift.source.etl.detail.DetailOperator;
import com.fr.swift.source.etl.detail.DetailTransferOperator;
import com.fr.swift.source.etl.formula.ColumnFormulaOperator;
import com.fr.swift.source.etl.formula.ColumnFormulaTransferOperator;
import com.fr.swift.source.etl.groupsum.SumByGroupOperator;
import com.fr.swift.source.etl.groupsum.SumByGroupTransferOperator;
import com.fr.swift.source.etl.join.JoinOperator;
import com.fr.swift.source.etl.join.JoinTransferOperator;
import com.fr.swift.source.etl.selfrelation.OneUnionRelationOperator;
import com.fr.swift.source.etl.selfrelation.OneUnionRelationTransferOperator;
import com.fr.swift.source.etl.selfrelation.TwoUnionRelationOperator;
import com.fr.swift.source.etl.selfrelation.TwoUnionRelationTransferOperator;
import com.fr.swift.source.etl.union.UnionOperator;
import com.fr.swift.source.etl.union.UnionTransferOperator;

/**
 *
 * @author pony
 * @date 2018/1/8
 */
public class EtlTransferOperatorFactory {
    public static ETLTransferOperator createTransferOperator(ETLOperator operator) {
        switch (operator.getOperatorType()) {
            case DETAIL:
                return transferDetailOperator((DetailOperator) operator);
            case UNION:
                return transferUnionOperator((UnionOperator) operator);
            case JOIN:
                return transferJoinOperator((JoinOperator) operator);
            case GROUP_SUM:
                return transferGroupSumOperator((SumByGroupOperator) operator);
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
            case DATAMINING:
                return transferDataMiningOperator((DataMiningOperator) operator);
            default:
        }
        return null;
    }

    private static ETLTransferOperator transferDetailOperator(DetailOperator operator) {
        return new DetailTransferOperator(operator.getFields());
    }

    private static ETLTransferOperator transferUnionOperator(UnionOperator operator) {
        return new UnionTransferOperator(operator.getColumnKeyList());
    }

    private static ETLTransferOperator transferJoinOperator(JoinOperator operator) {
        return new JoinTransferOperator(operator.getColumns(), operator.getlKey(), operator.getrKey(), operator.getType());
    }

    private static ETLTransferOperator transferGroupSumOperator(SumByGroupOperator operator) {
        return new SumByGroupTransferOperator(operator.getTargets(), operator.getDimensions());
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
        return new TwoUnionRelationTransferOperator(operator.getColumns(), operator.getIdColumnName(), operator.getShowColumns(), operator.getColumnType(), operator.getColumnName(), operator.getParentIdColumnName());
    }

    private static ETLTransferOperator transferColumnFormulaOperator(ColumnFormulaOperator operator) {
        return new ColumnFormulaTransferOperator(operator.getColumnType(), operator.getExpression());
    }

    private static ETLTransferOperator transferDataMiningOperator(DataMiningOperator operator) {
        return new DataMiningTransferOperator(operator.getAlgorithmBean());
    }
}
