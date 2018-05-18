package com.fr.swift.source.etl.groupsum;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/1/22 0022 14:02
 */
public class SumByGroupOperator extends AbstractOperator {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SumByGroupOperator.class);

    @CoreField
    private SumByGroupTarget[] targets;

    @CoreField
    private SumByGroupDimension[] dimensions;

    public SumByGroupOperator(SumByGroupTarget[] targets, SumByGroupDimension[] dimensions) {
        this.targets = targets;
        this.dimensions = dimensions;
    }

    public SumByGroupTarget[] getTargets() {
        return targets;
    }

    public SumByGroupDimension[] getDimensions() {
        return dimensions;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] tables) {
        List<SwiftMetaDataColumn> columns = new ArrayList<SwiftMetaDataColumn>();
        for (SwiftMetaData parent : tables) {
            try {
                if (null != dimensions) {
                    for (SumByGroupDimension dimension : this.dimensions) {
                        ColumnType columnType = ColumnTypeUtils.getColumnType(parent.getColumn(dimension.getName()));
                        switch (columnType) {
                            case DATE:
                                columns.add(new MetaDataColumn(dimension.getNameText(), getSqlType(dimension.getGroup().getGroupType())));
                                break;
                            case NUMBER:
                                SwiftMetaDataColumn singleColumn = parent.getColumn(dimension.getName());
                                columns.add(generateSumNumberGroup(dimension, singleColumn));
                                break;
                            default:
                                columns.add(new MetaDataColumn(dimension.getNameText(), parent.getColumn(dimension.getName()).getType(), parent.getColumn(dimension.getName()).getPrecision()));
                        }
                    }
                }
                if (null != targets) {
                    for (SumByGroupTarget target : this.targets) {
                        columns.add(new MetaDataColumn(target.getNameText(), ColumnTypeUtils.columnTypeToSqlType(target.getColumnType()), parent.getColumn(target.getName()).getPrecision()));
                    }
                }
            } catch (SwiftMetaDataException e) {
                LOGGER.error("getting meta's column information failed", e);
            }
        }
        return columns;
    }

    private int getSqlType(GroupType groupType) {
        switch (groupType) {
            case QUARTER:
            case MONTH:
            case WEEK:
            case WEEK_OF_YEAR:
            case DAY:
            case HOUR:
            case MINUTE:
            case SECOND:
                return Types.INTEGER;
            case YEAR:
            case Y_M_D_H_M_S:
            case Y_M_D_H_M:
            case Y_M_D_H:
            case Y_M_D:
            case Y_Q:
            case Y_M:
            case Y_W:
            case Y_D:
            case M_D:
                return Types.DATE;
            default:
                return Types.DATE;
        }
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.GROUP_SUM;
    }

    private SwiftMetaDataColumn generateSumNumberGroup(SumByGroupDimension sum, SwiftMetaDataColumn parentColumn) {
        int sqlType;
        switch (sum.getGroup().getGroupType()) {
            case CUSTOM:
            case CUSTOM_NUMBER:
            case AUTO:
                sqlType = Types.VARCHAR;
                break;
            case YEAR:
            case QUARTER:
            case MONTH:
            case WEEK:
            case WEEK_OF_YEAR:
            case DAY:
            case HOUR:
            case MINUTE:
            case SECOND:
                sqlType = Types.INTEGER;
                break;
            case Y_M_D_H_M_S:
            case Y_M_D_H_M:
            case Y_M_D_H:
            case Y_M_D:
            case Y_Q:
            case Y_M:
            case Y_W:
            case Y_D:
            case M_D:
                sqlType = Types.DATE;
                break;
            case NONE:
            default:
                sqlType = parentColumn.getType();
        }

        return new MetaDataColumn(sum.getNameText(), sum.getNameText(), sqlType, parentColumn.getPrecision(), parentColumn.getScale());
    }
}
