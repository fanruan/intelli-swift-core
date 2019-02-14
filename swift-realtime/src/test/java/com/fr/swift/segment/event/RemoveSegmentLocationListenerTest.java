package com.fr.swift.segment.event;

import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.SwiftEventListener;
import com.fr.swift.event.base.AbstractGlobalRpcEvent.Event;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.event.global.RemoveSegLocationRpcEvent;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.service.listener.RemoteSender;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/1/15
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ProxySelector.class, SwiftEventDispatcher.class, RemoveSegmentLocationListener.class, SwiftProperty.class})
public class RemoveSegmentLocationListenerTest {
    @Before
    public void setUp() throws Exception {
        mockStatic(SwiftProperty.class);
        when(SwiftProperty.getProperty()).thenReturn(mock(SwiftProperty.class));
        when(SwiftProperty.getProperty().getClusterId()).thenReturn("1");
    }

    @Test
    public void trigger() {
        // listen
        mockStatic(SwiftEventDispatcher.class);

        RemoveSegmentLocationListener.listen();

        verifyStatic(SwiftEventDispatcher.class);
        SwiftEventDispatcher.listen(eq(SyncSegmentLocationEvent.REMOVE_SEG), argThat(new ArgumentMatcher<SwiftEventListener<?>>() {
            @Override
            public boolean matches(SwiftEventListener<?> argument) {
                return argument.getClass() == RemoveSegmentLocationListener.class;
            }
        }));

        // trigger
        mockStatic(ProxySelector.class);
        when(ProxySelector.getProxy(RemoteSender.class)).thenReturn(mock(RemoteSender.class));
        when(ProxySelector.getProxy(RemoteSender.class).trigger(ArgumentMatchers.<SwiftRpcEvent>any())).thenReturn(1).thenThrow(Exception.class);

        final SegmentLocationInfo segmentLocationInfo = mock(SegmentLocationInfo.class);
        assertEquals(1, Whitebox.newInstance(RemoveSegmentLocationListener.class).trigger(segmentLocationInfo));

        verify(ProxySelector.getProxy(RemoteSender.class)).trigger(argThat(new ArgumentMatcher<RemoveSegLocationRpcEvent>() {
            @Override
            public boolean matches(RemoveSegLocationRpcEvent argument) {
                return argument.subEvent() == Event.REMOVE_SEG &&
                        argument.getClusterId().equals("1") &&
                        argument.getContent().equals(segmentLocationInfo);
            }
        }));

        assertNull(Whitebox.newInstance(RemoveSegmentLocationListener.class).trigger(segmentLocationInfo));
    }
}