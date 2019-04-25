package com.fr.swift.result;

import com.fr.swift.base.meta.SwiftMetaDataEntity;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.source.ColumnTypeConstants;
import com.fr.swift.structure.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created by lyon on 2019/1/8.
 */
public class SortMultiSegmentDetailResultSetTest {

    private SortMultiSegmentDetailResultSet rs;

    @Before
    public void setUp() throws Exception {
        rs = new SortMultiSegmentDetailResultSet(100, 200, null,
                new SortedDetailResultSetMerger(100, new ArrayList<Pair<Sort, ColumnTypeConstants.ClassType>>()));
    }

    @Test
    public void getMerger() {
        assertNotNull(rs.getMerger());
    }

    @Test
    public void convert() {
        try {
            rs.convert(new SwiftMetaDataEntity());
        } catch (Exception e) {
            fail();
        }
    }
}