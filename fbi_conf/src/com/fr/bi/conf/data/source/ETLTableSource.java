package com.fr.bi.conf.data.source;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.operator.IETLOperator;
import com.fr.bi.conf.data.source.operator.OperatorFactory;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by GUY on 2015/2/28.
 */
public class ETLTableSource extends AbstractETLTableSource<IETLOperator, CubeTableSource> {

    public static final String XML_TAG = "ETLTableSource";
    /**
     *
     */
    private static final long serialVersionUID = -4709748792691267870L;

    public ETLTableSource() {
    }

    public ETLTableSource(List<IETLOperator> operators, List<CubeTableSource> parents) {
        super(operators, parents);
    }

    @Override
    public JSONObject createJSON() throws Exception {

        JSONObject jo = super.createJSON();
        jo.put("connection_name", DBConstant.CONNECTION.ETL_CONNECTION);
        JSONArray tables = new JSONArray();
        for (int i = 0; i < parents.size(); i++) {
            tables.put(parents.get(i).createJSON());
        }

        jo.put("tables", tables);
        OperatorFactory.createJSONByOperators(jo, oprators);
        return jo;
    }

    @Override
    public void parseJSON(JSONObject jo, long userId) throws Exception {
        super.parseJSON(jo, userId);
        oprators = OperatorFactory.createOperatorsByJSON(jo, userId);
        JSONArray tables = jo.getJSONArray("tables");
        for(int i = 0; i < tables.length(); i++) {
            parents.add(TableSourceFactory.createTableSource(tables.getJSONObject(i), userId));
        }
    }

    @Override
    public int getType() {
        return BIBaseConstant.TABLETYPE.ETL;
    }

    @Override
    /**
     * FIXME 需要实现
     */
    public long read(final Traversal<BIDataValue> travel, ICubeFieldSource[] fields, ICubeDataLoader loader) {
        Iterator<IETLOperator> it = oprators.iterator();
        long index = 0;
        while (it.hasNext()) {
            IETLOperator op = it.next();
            index = op.writeSimpleIndex(travel, parents, loader);
        }
        return index;
    }

    @Override
    public long read4Part(Traversal<BIDataValue> traversal, ICubeFieldSource[] cubeFieldSources, String sql, long rowCount) {
        return 0;
    }

    @Override
    protected Set<CubeTableSource> createSourceSet() {
        Set<CubeTableSource> set = new HashSet<CubeTableSource>();
        if (oprators != null) {
            for (IETLOperator op : oprators) {
                set.add(new SingleOperatorETLTableSource(parents, op));
            }
        } else {
            set.add(this);
        }
        return set;
    }

}
