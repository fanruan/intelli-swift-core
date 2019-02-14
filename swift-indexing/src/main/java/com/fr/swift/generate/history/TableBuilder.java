package com.fr.swift.generate.history;

import com.fr.swift.generate.BaseTableBuilder;
import com.fr.swift.generate.history.transport.TableTransporter;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.DataSource;

/**
 * This class created on 2017-12-28 10:53:49
 *
 * @author Lucifer
 * @description 完整表进行取数、索引。
 * @since Advanced FineBI Analysis 1.0
 */
public class TableBuilder extends BaseTableBuilder {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(TableBuilder.class);

    public TableBuilder(int round, DataSource dataSource) {
        super(round, dataSource);
        this.transporter = new TableTransporter(dataSource);
    }
}