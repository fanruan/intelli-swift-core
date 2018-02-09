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
    private SumByGroupTarget[] targets;
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
        for(int k = 0; k < tables.length; k++) {
            SwiftMetaData parent = tables[k];
            for(int i = 0; i < this.dimensions.length; i++) {
                try {
                    int sqlType = parent.getColumn(this.dimensions[i].getName()).getType();
                    int columnSize = parent.getColumn(this.dimensions[i].getName()).getPrecision();
                    int scale = parent.getColumn(this.dimensions[i].getName()).getScale();
                    if (ColumnTypeUtils.sqlTypeToColumnType(sqlType, columnSize, scale) == ColumnType.DATE) {
                        columns.add(new MetaDataColumn(this.dimensions[i].getNameText(), convertGroupTpye(this.dimensions[i].getGroup().getGroupType()), 30));
                    } else if (ColumnTypeUtils.sqlTypeToColumnType(sqlType, columnSize, scale) == ColumnType.NUMBER) {
                        SwiftMetaDataColumn singleColumn = parent.getColumn(this.dimensions[i].getName());
                        columns.add(generateSumNumberGroup(this.dimensions[i], singleColumn));
                    } else {
                        columns.add(new MetaDataColumn(this.dimensions[i].getNameText(), parent.getColumn(this.dimensions[i].getName()).getType(), parent.getColumn(this.dimensions[i].getName()).getPrecision()));
                    }
                    for(int j = 0; j < this.targets.length; j++) {
                        columns.add(new MetaDataColumn(this.targets[i].getNameText(), ColumnTypeUtils.columnTypeToSqlType(null/*this.targets[i].getColumnType()*/), parent.getColumn(this.targets[i].getName()).getPrecision()));
                    }
                } catch(SwiftMetaDataException e) {
                    LOGGER.error("getting meta's column information failed", e);
                }
            }
        }
        return columns;
    }

    private int convertGroupTpye(GroupType groupType) {
        int type = Integer.MIN_VALUE;
        switch(groupType) {
            case YEAR:
            case QUARTER:
            case MONTH:
            case WEEK:
            case M_D:
            case HOUR:
            case MINUTE:
            case SECOND:
            case WEEK_OF_YEAR:
            case DAY:
                type = Types.INTEGER;
                break;
            case Y_M_D:
            case Y_M_D_H_M_S:
            case Y_M:
            case Y_W:
            case Y_M_D_H:
            case Y_M_D_H_M:
            case Y_Q:
                type = Types.DATE;
                break;
            default:
                type = Types.DATE;
        }
        return type;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.GROUPSUM;
    }


    private SwiftMetaDataColumn generateSumNumberGroup(SumByGroupDimension sum, SwiftMetaDataColumn parentColumn) {
        int type = ColumnTypeUtils.columnTypeToSqlType(ColumnType.STRING);
        if(sum.getGroup().getGroupType() == GroupType.NONE) {
            type = parentColumn.getType();
        }
        return new MetaDataColumn(sum.getNameText(), sum.getNameText(), type, parentColumn.getPrecision(), parentColumn.getScale());
    }
}
