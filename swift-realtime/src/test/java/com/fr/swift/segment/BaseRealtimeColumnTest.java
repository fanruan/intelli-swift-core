package com.fr.swift.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.Random;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * @author anchore
 * @date 2018/6/13
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftContext.class})
public abstract class BaseRealtimeColumnTest<T> {
    Random r = new Random(hashCode());
    T[] data1, data2;
    static final int BOUND = 1000;

    abstract Column<T> getColumn();

    @Before
    public void setUp() throws Exception {
        mockStatic(SwiftContext.class);
        when(SwiftContext.get()).thenReturn(mock(BeanFactory.class));
        SwiftCubePathService cubePathService = mock(SwiftCubePathService.class);
        when(SwiftContext.get().getBean(SwiftCubePathService.class)).thenReturn(cubePathService);

        when(cubePathService.getSwiftPath()).thenReturn("/");
    }

    @Test
    public void test() {
        Column<T> column = getColumn();
        for (int i = 0; i < data1.length; i++) {
            column.getDetailColumn().put(i, data1[i]);
        }

        column = getColumn();
        DictionaryEncodedColumn<T> dict = column.getDictionaryEncodedColumn();
        for (int i = 0; i < data1.length; i++) {
            Assert.assertEquals(data1[i], dict.getValue(dict.getIndexByRow(i)));
            Assert.assertTrue(column.getBitmapIndex().getBitMapIndex(dict.getIndex(data1[i])).contains(i));
        }

        column = getColumn();
        for (int i = 0; i < data2.length; i++) {
            column.getDetailColumn().put(data2.length + i, data2[i]);
        }

        column = getColumn();
        dict = column.getDictionaryEncodedColumn();
        for (int i = 0; i < data1.length; i++) {
            Assert.assertEquals(data1[i], dict.getValue(dict.getIndexByRow(i)));
            Assert.assertTrue(column.getBitmapIndex().getBitMapIndex(dict.getIndex(data1[i])).contains(i));
        }
        for (int i = 0; i < data2.length; i++) {
            Assert.assertEquals(data2[i], dict.getValue(dict.getIndexByRow(data2.length + i)));
            Assert.assertTrue(column.getBitmapIndex().getBitMapIndex(dict.getIndex(data2[i])).contains(data2.length + i));
        }

    }
}

