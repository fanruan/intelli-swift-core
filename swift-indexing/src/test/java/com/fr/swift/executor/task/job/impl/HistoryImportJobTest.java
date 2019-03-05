package com.fr.swift.executor.task.job.impl;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.insert.HistoryBlockImporter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author anchore
 * @date 2019/2/28
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({HistoryImportJob.class})
public class HistoryImportJobTest {

    @Test
    public void call() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        SwiftSourceAlloter alloter = mock(SwiftSourceAlloter.class);
        SwiftResultSet resultSet = mock(SwiftResultSet.class);

        HistoryBlockImporter importer = mock(HistoryBlockImporter.class);
        whenNew(HistoryBlockImporter.class).withAnyArguments().thenReturn(importer);

        HistoryImportJob job = new HistoryImportJob(dataSource, alloter, resultSet);

        assertTrue(job.call());
        verifyNew(HistoryBlockImporter.class).withArguments(dataSource, alloter);
        verify(importer).importData(resultSet);

        doThrow(Exception.class).when(importer).importData(ArgumentMatchers.<SwiftResultSet>any());

        assertFalse(job.call());
    }
}