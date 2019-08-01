package com.fr.swift.segment.operator.insert;

import com.fr.swift.segment.MutableCacheColumnSegment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.source.SwiftMetaData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.Arrays;

/**
 * @author lucifer
 * @date 2019-07-31
 * @description
 * @since advanced swift 1.0
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({})
public class MutableInserterTest {

    @Mock
    MutableCacheColumnSegment segment;

    @Mock
    SwiftMetaData oldMetaData;

    @Mock
    SwiftMetaData newMetaData;

    @Mock
    Column columnd;
    @Mock
    DetailColumn detailColumnd;
    @Mock
    Column columne;
    @Mock
    DetailColumn detailColumne;

    @Test
    public void testRefresh() {
        Mockito.when(segment.getMetaData()).thenReturn(oldMetaData);
        Mockito.when(oldMetaData.getFieldNames()).thenReturn(Arrays.asList("a", "b", "c"));
        Mockito.when(newMetaData.getFieldNames()).thenReturn(Arrays.asList("a", "b", "c", "d", "e"));

        Mockito.when(segment.getColumn(new ColumnKey("d"))).thenReturn(columnd);
        Mockito.when(columnd.getDetailColumn()).thenReturn(detailColumnd);

        Mockito.when(segment.getColumn(new ColumnKey("e"))).thenReturn(columne);
        Mockito.when(columne.getDetailColumn()).thenReturn(detailColumne);

        MutableInserter mutableInserter = MutableInserter.ofOverwriteMode(segment);
        mutableInserter.cursor = 1;
        mutableInserter.refreshMetadata(newMetaData);
        Mockito.verify(segment).refreshMetadata(newMetaData);
        Mockito.verify(detailColumnd).put(0, null);
        Mockito.verify(detailColumne).put(0, null);


    }
}