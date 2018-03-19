package com.fr.swift.source.etl.groupsum;

import com.fr.swift.query.group.Group;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.utils.GroupValueIterator;
import com.fr.swift.source.etl.utils.SwiftValuesAndGVI;
import com.fr.swift.structure.iterator.RowTraversal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/1/22 0022 11:38
 */
public class SumByGroupOperatorResultSet implements SwiftResultSet {

    private SumByGroupTarget[] targets;
    private SumByGroupDimension[] dimensions;
    private ListRow listRow = new ListRow();
    private Segment[] segments;
    private GroupValueIterator iterator;
    private SwiftMetaData metaData;
    private boolean hasDimension;
    private boolean noNext;

    public SumByGroupOperatorResultSet(SumByGroupTarget[] targets, SumByGroupDimension[] dimensions, Segment[] segments, SwiftMetaData metaData) {
        this.targets = targets;
        this.dimensions = dimensions;
        this.segments = segments;
        this.metaData = metaData;
        init();
    }

    private void init() {
        int len = getDimensions().length;
        if(len > 0) {
            hasDimension = true;
            ColumnKey[] keys = new ColumnKey[len];
            // TODO
            Group[] groups = new Group[len];
            for (int i = 0; i < groups.length; i++) {
                keys[i] = dimensions[i].createKey();
                groups[i] = dimensions[i].getGroup();
            }
            iterator = new GroupValueIterator(segments, keys, groups);
        }
    }


    @Override
    public void close() throws SQLException {

    }

    @Override
    public boolean next() throws SQLException {
        if(hasDimension) {
            if(iterator.hasNext()) {
                SwiftValuesAndGVI valuesAndGVI = iterator.next();
                write(valuesAndGVI);
                return true;
            }
            return false;
        } else {
            if(!noNext) {
                writeNoDimension(segments);
                noNext = true;
                return true;
            }
            return false;
        }

    }

    private ListRow writeNoDimension(Segment[] segment){
        List valueList = new ArrayList();
        RowTraversal[] rowTraversal = new RowTraversal[segment.length];
        for(int i = 0; i < segment.length; i++) {
            rowTraversal[i] = segment[i].getAllShowIndex();
        }
        for(int i = 0; i < getTargets().length; i++) {
            valueList.add(getTargets()[i].getSumValue(segment, rowTraversal));
        }
        ListBasedRow basedRow = new ListBasedRow(valueList);
        listRow.setBasedRow(basedRow);
        return listRow;
    }

    private ListRow write(SwiftValuesAndGVI valuesAndGVI) {
        List valueList = new ArrayList();
        for(int i = 0; i < valuesAndGVI.getValues().length; i++) {
            valueList.add(dimensions[i].getKeyValue(valuesAndGVI.getValues()[i]));
        }
        List<Segment> segmentList = new ArrayList<Segment>();
        List<RowTraversal> rowTraversalList = new ArrayList<RowTraversal>();
        for(int i = 0; i < valuesAndGVI.getAggreator().size(); i++) {
            RowTraversal traversal = valuesAndGVI.getAggreator().get(i).getBitMap();
            if(traversal != null) {
                segmentList.add(valuesAndGVI.getAggreator().get(i).getSegment());
                rowTraversalList.add(traversal);
            }
        }
        for(int i = 0; i < getTargets().length; i++) {
            valueList.add(getTargets()[i].getSumValue(segmentList.toArray(new Segment[segmentList.size()]), rowTraversalList.toArray(new RowTraversal[rowTraversalList.size()])));
        }
        ListBasedRow basedRow = new ListBasedRow(valueList);
        listRow.setBasedRow(basedRow);
        return listRow;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;

    }

    @Override
    public Row getRowData() throws SQLException {
        return listRow.getBasedRow();
    }

    private SumByGroupDimension[] getDimensions() {
        if (this.dimensions == null) {
            return new SumByGroupDimension[0];
        }
        return this.dimensions;
    }

    private SumByGroupTarget[] getTargets() {
        if (this.targets == null) {
            return new SumByGroupTarget[0];
        }
        return this.targets;
    }

    private class ListRow {
        ListBasedRow basedRow;

        public ListBasedRow getBasedRow() {
            return basedRow;
        }

        public void setBasedRow(ListBasedRow basedRow) {
            this.basedRow = basedRow;
        }

    }
}
