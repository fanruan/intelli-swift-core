package com.fr.swift.segment.operator.delete;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.db.Database;
import com.fr.swift.db.Table;
import com.fr.swift.db.Where;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.exception.TableNotExistException;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.SourceKey;

/**
 * This class created on 2018/3/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftWhereDeleter implements WhereDeleter {
    private SourceKey tableKey;

    protected Segment segment;

    protected final Database database = SwiftDatabase.getInstance();

    public SwiftWhereDeleter(SourceKey tableKey, Segment segment) {
        this.tableKey = tableKey;
        this.segment = segment;
    }

    @Override
    public ImmutableBitMap delete(Where where) throws Exception {
        if (!database.existsTable(tableKey)) {
            throw new TableNotExistException(tableKey);
        }
        Table table = database.getTable(tableKey);
        ImmutableBitMap originAllShowIndex = segment.getAllShowIndex();
        ImmutableBitMap indexAfterFilter = where.createWhereIndex(table, segment);

        ImmutableBitMap allShowIndex = originAllShowIndex.getAndNot(indexAfterFilter);
        segment.putAllShowIndex(allShowIndex);

        release();

        return allShowIndex;
    }

    public void release() {
        if (segment.isHistory()) {
            segment.release();
        }
    }
}
