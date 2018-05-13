package com.fr.swift.generate.history.transport;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.task.TaskResult.Type;
import com.fr.swift.cube.task.impl.BaseWorker;
import com.fr.swift.cube.task.impl.TaskResultImpl;
import com.fr.swift.generate.Transporter;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;

import java.util.List;

/**
 * This class created on 2017-12-25 13:55:12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class TableTransporter extends BaseWorker implements Transporter {
    private DataSource dataSource;

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(TableTransporter.class);

    private List<String> indexFieldsList;

    public TableTransporter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void work() {
        try {
            transport();
            workOver(new TaskResultImpl(Type.SUCCEEDED));
        } catch (Exception e) {
            LOGGER.error("Datasource:" + dataSource.getSourceKey().getId() + " transport failed", e);
            workOver(new TaskResultImpl(Type.FAILED, e));
        }
    }

    @Override
    public void transport() throws Exception {
        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourceTransfer(dataSource);
        SwiftResultSet resultSet = transfer.createResultSet();
        Inserter inserter = SwiftContext.getInstance().getSwiftDataOperatorProvider().getHistoryBlockSwiftInserter(dataSource);
        indexFieldsList = inserter.getFields();
        inserter.insertData(resultSet);
    }

    @Override
    public List<String> getIndexFieldsList() {
        return indexFieldsList;
    }
}
