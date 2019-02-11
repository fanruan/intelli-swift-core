package com.fr.swift.segment.event;

import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.event.SwiftEventDispatcher;
import com.fr.swift.event.SwiftEventListener;
import com.fr.swift.event.base.AbstractGlobalRpcEvent.Event;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.event.global.PushSegLocationRpcEvent;
import com.fr.swift.segment.SegmentLocationInfo;
import com.fr.swift.service.listener.RemoteSender;
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
@PrepareForTest({ProxySelector.class, SwiftEventDispatcher.class, PushSegmentLocationListener.class})
public class PushSegmentLocationListenerTest {
    @Test
    public void trigger() {
        // listen
        mockStatic(SwiftEventDispatcher.class);

        PushSegmentLocationListener.listen();

        verifyStatic(SwiftEventDispatcher.class);
        SwiftEventDispatcher.listen(eq(SyncSegmentLocationEvent.PUSH_SEG), argThat(new ArgumentMatcher<SwiftEventListener<?>>() {
            @Override
            public boolean matches(SwiftEventListener<?> argument) {
                return argument.getClass() == PushSegmentLocationListener.class;
            }
        }));

        // trigger
        mockStatic(ProxySelector.class);
        when(ProxySelector.getProxy(RemoteSender.class)).thenReturn(mock(RemoteSender.class));
        when(ProxySelector.getProxy(RemoteSender.class).trigger(ArgumentMatchers.<SwiftRpcEvent>any())).thenReturn(1).thenThrow(Exception.class);

        final SegmentLocationInfo segmentLocationInfo = mock(SegmentLocationInfo.class);
        assertEquals(1, Whitebox.newInstance(PushSegmentLocationListener.class).trigger(segmentLocationInfo));

        verify(ProxySelector.getProxy(RemoteSender.class)).trigger(argThat(new ArgumentMatcher<PushSegLocationRpcEvent>() {
            @Override
            public boolean matches(PushSegLocationRpcEvent argument) {
                return argument.subEvent() == Event.PUSH_SEG &&
                        argument.getContent().equals(segmentLocationInfo);
            }
        }));

        assertNull(Whitebox.newInstance(PushSegmentLocationListener.class).trigger(segmentLocationInfo));
    }
}