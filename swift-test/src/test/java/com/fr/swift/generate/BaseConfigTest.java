package com.fr.swift.generate;

import com.fr.swift.config.SwiftCubePathConfig;
import com.fr.swift.config.TestConfDb;
import com.fr.swift.db.impl.SwiftDatabase.Schema;
import com.fr.swift.util.FileUtil;
import org.junit.Before;

/**
 * Created by pony on 2018/4/27.
 */
public abstract class BaseConfigTest {
    @Before
    public void setUp() throws Exception {
        TestConfDb.setConfDb();
        for (Schema schema : Schema.values()) {
            FileUtil.delete(SwiftCubePathConfig.getInstance().getPath() + "/" + schema.getDir());
        }
    }
}
