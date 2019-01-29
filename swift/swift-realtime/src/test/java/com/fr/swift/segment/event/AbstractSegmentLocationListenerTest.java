package com.fr.swift.segment.event;

import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.segment.impl.RealTimeSegDestImpl;
import com.fr.swift.segment.impl.SegmentDestinationImpl;
import com.fr.swift.service.ServiceType;
import com.fr.swift.source.SourceKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/1/15
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftProperty.class})
public class AbstractSegmentLocationListenerTest {

    @Before
    public void setUp() throws Exception {
        mockStatic(SwiftProperty.class);
        when(SwiftProperty.getProperty()).thenReturn(mock(SwiftProperty.class));
        when(SwiftProperty.getProperty().isCluster()).thenReturn(false, true);
        when(SwiftProperty.getProperty().getClusterId()).thenReturn("1");
    }

    @Test
    public void on() {
        final SegmentKey[] segKeys = {mock(SegmentKey.class), mock(SegmentKey.class), mock(SegmentKey.class)};
        when(segKeys[0].getTable()).thenReturn(new SourceKey("t"));
        when(segKeys[1].getTable()).thenReturn(new SourceKey("t1"));
        when(segKeys[2].getTable()).thenReturn(new SourceKey("t"));

        when(segKeys[0].getStoreType()).thenReturn(StoreType.MEMORY);
        when(segKeys[1].getStoreType()).thenReturn(StoreType.FINE_IO);
        when(segKeys[2].getStoreType()).thenReturn(StoreType.FINE_IO);

        when(segKeys[0].getId()).thenReturn("t@MEMORY@0");
        when(segKeys[1].getId()).thenReturn("t1@FINE_IO@1");
        when(segKeys[2].getId()).thenReturn("t@FINE_IO@2");

        when(segKeys[0].getOrder()).thenReturn(0);
        when(segKeys[1].getOrder()).thenReturn(1);
        when(segKeys[2].getOrder()).thenReturn(2);

        SegmentLocationListener listener = spy(new SegmentLocationListener());
        listener.on(Arrays.asList(segKeys));
        // standalone mode, trigger nothing
        verify(listener, never()).trigger(ArgumentMatchers.<SegmentLocationInfo>any());
        // 计算结果为空则不trigger
        listener.on(Collections.<SegmentKey>emptyList());
        verify(listener, never()).trigger(ArgumentMatchers.<SegmentLocationInfo>any());

        listener.on(Arrays.asList(segKeys));

        verify(listener).trigger(argThat(new ArgumentMatcher<SegmentLocationInfo>() {
            @Override
            public boolean matches(SegmentLocationInfo argument) {
                Map<SourceKey, List<SegmentDestination>> historyDsts = new HashMap<SourceKey, List<SegmentDestination>>();
                historyDsts.put(segKeys[1].getTable(),
                        Collections.<SegmentDestination>singletonList(new SegmentDestinationImpl("1", "t1@FINE_IO@1", 1)));
                historyDsts.put(segKeys[0].getTable(),
                        Collections.<SegmentDestination>singletonList(new SegmentDestinationImpl("1", "t@FINE_IO@2", 2)));

                return argument.serviceType() == ServiceType.HISTORY &&
                        argument.getDestinations().equals(historyDsts);
            }
        }));
        verify(listener).trigger(argThat(new ArgumentMatcher<SegmentLocationInfo>() {
            @Override
            public boolean matches(SegmentLocationInfo argument) {
                return argument.serviceType() == ServiceType.REAL_TIME &&
                        argument.getDestinations().equals(singletonMap(
                                segKeys[0].getTable(),
                                Collections.<SegmentDestination>singletonList(new RealTimeSegDestImpl("1", "t@MEMORY@0", 0))));
            }
        }));
    }

    class SegmentLocationListener extends BaseSegmentLocationListener {
        @Override
        Serializable trigger(SegmentLocationInfo segLocations) {
            return 1;
        }
    }
}