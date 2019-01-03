package com.fr.swift.source.etl.join;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/1/16 0016 11:46
 */
public class JoinOperator extends AbstractOperator {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(JoinOperator.class);

    @CoreField
    private List<JoinColumn> columns;
    @CoreField
    private ColumnKey[] lKey;
    @CoreField
    private ColumnKey[] rKey;
    @CoreField
    private JoinType type;

    public JoinOperator(List<JoinColumn> columns, ColumnKey[] lKey, ColumnKey[] rKey, JoinType type) {
        this.columns = columns;
        this.lKey = lKey;
        this.rKey = rKey;
        this.type = type;
    }

    public List<JoinColumn> getColumns() {
        return columns;
    }

    public ColumnKey[] getlKey() {
        return lKey;
    }

    public ColumnKey[] getrKey() {
        return rKey;
    }

    public JoinType getType() {
        return type;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] tables) {

        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        SwiftMetaData leftT = tables[0];
        SwiftMetaData rightT = tables[1];
        for (JoinColumn joinColumn : this.columns) {
            try {
                SwiftMetaDataColumn originColumn = joinColumn.isLeft() ? leftT.getColumn(joinColumn.getColumnName()) : rightT.getColumn(joinColumn.getColumnName());
                columnList.add(new MetaDataColumnBean(joinColumn.getName(), originColumn.getRemark(), originColumn.getType(), originColumn.getPrecision(), originColumn.getScale(), joinColumn.getName()));
            } catch (SwiftMetaDataException e) {
                LOGGER.error("the field " + joinColumn.getColumnName() + " get meta failed", e);
            }
        }
        return columnList;
    }


    @Override
    public OperatorType getOperatorType() {
        return OperatorType.JOIN;
    }
}
