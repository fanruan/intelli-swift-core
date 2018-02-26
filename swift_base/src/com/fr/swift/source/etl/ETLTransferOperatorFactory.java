package com.fr.swift.source.etl;

import com.fr.swift.source.etl.columnfilter.ColumnFilterOperator;
import com.fr.swift.source.etl.columnfilter.ColumnFilterTransferOperator;
import com.fr.swift.source.etl.columnrowtrans.ColumnRowTransOperator;
import com.fr.swift.source.etl.columnrowtrans.ColumnRowTransferOperator;
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
import com.fr.swift.source.etl.sort.ColumnSortOperator;
import com.fr.swift.source.etl.sort.ColumnSortTransferOperator;
import com.fr.swift.source.etl.union.UnionOperator;
import com.fr.swift.source.etl.union.UnionTransferOperator;

/**
 * Created by pony on 2018/1/8.
 */
public class ETLTransferOperatorFactory {
    public static ETLTransferOperator createTransferOperator(ETLOperator operator) {
        switch (operator.getOperatorType()) {
            case DETAIL:
                return transferDetailOperator((DetailOperator) operator);
            case UNION:
                return transferUnionOperator((UnionOperator) operator);
            case JOIN:
                return transferJoinOperator((JoinOperator) operator);
            case GROUPSUM:
                return transferGroupSumOperator((SumByGroupOperator) operator);
            case COLUMNROWTRANS:
                return transferColumnRowTransOperator((ColumnRowTransOperator) operator);
            case FILTER:
                return transferColumnFilterOperator((ColumnFilterOperator) operator);
            case ONEUNIONRELATION:
                return transferOneUnionRelationOperator((OneUnionRelationOperator) operator);
            case TWOUNIONRELATION:
                return transferTwoUnionRelationOperator((TwoUnionRelationOperator) operator);
            case SORT:
                return transferColumnSortOperator((ColumnSortOperator) operator);
            case COLUMNFORMULA:
                return transferColumnFormulaOperator((ColumnFormulaOperator) operator);
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
        return new ColumnRowTransferOperator(operator.getGroupName(), operator.getLcName(), operator.getColumns(), operator.getLc_value(), operator.getOtherColumnNames());
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

    private static ETLTransferOperator transferColumnSortOperator(ColumnSortOperator operator) {
        return new ColumnSortTransferOperator(operator.getFieldsSortedMap());
    }

    private static ETLTransferOperator transferColumnFormulaOperator(ColumnFormulaOperator operator) {
        return new ColumnFormulaTransferOperator(operator.getColumnType(), operator.getExpression());
    }
}
