package com.fr.swift.exception.inspect;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 9/3/2019
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ComponentHealthCheck.class})
public class ComponentHealthCheckTest {

    ComponentHealthInspector inspector;
    ComponentHealthCheck checker;

    @Test
    public void testCheck() throws Exception {
        PowerMockito.mockStatic(ComponentHealthCheck.class);
        inspector = PowerMockito.mock(ComponentHealthInspector.class);
        PowerMockito.when(inspector.inspect(null)).thenReturn(true);
        checker = new ComponentHealthCheck(inspector, 30000);
        checker.isHealthy();
        Mockito.verify(inspector).inspect(null);
    }
}
