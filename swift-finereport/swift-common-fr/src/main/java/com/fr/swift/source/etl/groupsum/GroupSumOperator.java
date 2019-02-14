package com.fr.swift.source.etl.groupsum;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.ColumnTypeUtils;
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
public class GroupSumOperator extends AbstractOperator {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(GroupSumOperator.class);

    @CoreField
    private GroupSumTarget[] targets;

    @CoreField
    private GroupSumDimension[] dimensions;

    public GroupSumOperator(GroupSumTarget[] targets, GroupSumDimension[] dimensions) {
        this.targets = targets;
        this.dimensions = dimensions;
    }

    public GroupSumTarget[] getTargets() {
        return targets;
    }

    public GroupSumDimension[] getDimensions() {
        return dimensions;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] tables) {
        List<SwiftMetaDataColumn> columns = new ArrayList<SwiftMetaDataColumn>();
        for (SwiftMetaData parent : tables) {
            try {
                if (null != dimensions) {
                    for (GroupSumDimension dimension : this.dimensions) {
                        String columnName = dimension.getName();
                        SwiftMetaDataColumn columnMeta = parent.getColumn(columnName);
                        ColumnType columnType = ColumnTypeUtils.getColumnType(columnMeta);
                        switch (columnType) {
                            case DATE:
                                columns.add(new MetaDataColumnBean(columnName, getSqlType(dimension.getGroup().getGroupType()), columnMeta.getPrecision(), columnMeta.getScale()));
                                break;
                            case NUMBER:
                                columns.add(getNumberGroupSumMeta(dimension, columnMeta));
                                break;
                            default:
                                columns.add(new MetaDataColumnBean(columnName, columnMeta.getType(), columnMeta.getPrecision(), columnMeta.getScale()));
                        }
                    }
                }
                if (null != targets) {
                    for (GroupSumTarget target : this.targets) {
                        String columnName = target.getName();
                        SwiftMetaDataColumn columnMeta = parent.getColumn(columnName);
                        if (target.getColumnType() == ColumnType.NUMBER) {
                            columns.add(new MetaDataColumnBean(columnName, ColumnTypeUtils.columnTypeToSqlType(target.getColumnType())));
                        } else {
                            columns.add(new MetaDataColumnBean(columnName, ColumnTypeUtils.columnTypeToSqlType(target.getColumnType()), columnMeta.getPrecision(), columnMeta.getScale()));
                        }
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

    private SwiftMetaDataColumn getNumberGroupSumMeta(GroupSumDimension sum, SwiftMetaDataColumn parentColumn) {
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

        return new MetaDataColumnBean(sum.getName(), sum.getName(), sqlType, parentColumn.getPrecision(), parentColumn.getScale());
    }
}