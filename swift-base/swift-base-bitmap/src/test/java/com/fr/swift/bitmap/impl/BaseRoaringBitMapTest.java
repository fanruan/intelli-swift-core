package com.fr.swift.bitmap.impl;

import com.fr.swift.bitmap.BitMapType;
import com.fr.swift.bitmap.roaringbitmap.buffer.MutableRoaringBitmap;
import com.fr.swift.util.IoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.DataOutputStream;
import java.io.OutputStream;

import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author anchore
 * @date 2019/4/3
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({IoUtil.class, BaseRoaringBitMap.class})
public class BaseRoaringBitMapTest {

    @Test
    public void writeBytes() throws Exception {
        BaseRoaringBitMap baseRoaringBitMap = mock(BaseRoaringBitMap.class, CALLS_REAL_METHODS);

        MutableRoaringBitmap mutableRoaringBitmap = mock(MutableRoaringBitmap.class);
        Whitebox.setInternalState(baseRoaringBitMap, "bitmap", mutableRoaringBitmap);

        OutputStream output = mock(OutputStream.class);
        DataOutputStream dataOutput = mock(DataOutputStream.class);
        whenNew(DataOutputStream.class).withArguments(output).thenReturn(dataOutput);

        when(baseRoaringBitMap.getType()).thenReturn(BitMapType.ROARING_MUTABLE);

        mockStatic(IoUtil.class);

        baseRoaringBitMap.writeBytes(output);

        verify(dataOutput).write(BitMapType.ROARING_MUTABLE.getHead());
        verify(mutableRoaringBitmap).serialize(dataOutput);

        verifyStatic(IoUtil.class);
        IoUtil.close(dataOutput);
    }
}