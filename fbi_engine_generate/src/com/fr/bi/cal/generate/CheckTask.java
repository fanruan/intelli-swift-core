package com.fr.bi.cal.generate;

import com.fr.bi.cal.generate.index.IndexGenerator;
import com.fr.bi.cal.stable.cube.file.TableCubeFile;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.bi.stable.engine.CubeTaskType;
import com.fr.bi.stable.utils.file.BIPathUtils;
import com.fr.json.JSONObject;

public class CheckTask extends AllTask {


    public CheckTask(long userId) {
        super(userId);
    }

    @Override
    protected boolean checkCubeVersion(TableCubeFile cube) {
        return cube.checkCubeVersion();
    }

    @Override
    public JSONObject createJSON() throws Exception {
        return super.createJSON();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return true;
    }

    @Override
    public CubeTaskType getTaskType() {
        return CubeTaskType.CHECK;
    }

    @Override
    protected IndexGenerator createGenerator(ITableSource source) {
        TableCubeFile cube = new TableCubeFile(BIPathUtils.createTablePath(source.fetchObjectCore().getID().getIdentityValue(), biUser.getUserId()));
        if (!checkCubeVersion(cube)) {
            return new IndexGenerator(source, biUser.getUserId(), cube.getTableVersion() + 1);
        }
        return null;
    }
}