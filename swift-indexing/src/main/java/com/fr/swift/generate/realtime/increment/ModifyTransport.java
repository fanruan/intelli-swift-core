package com.fr.swift.generate.realtime.increment;

import com.fr.swift.flow.FlowRuleController;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.source.db.TableDBSource;

import java.util.List;

/**
 * This class created on 2018-1-5 14:43:29
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class ModifyTransport implements IncrementTransport {

    private DataSource dataSource;
    private DataSource modifyDataSource;
    private SwiftMetaData swiftMetaData;
    private FlowRuleController flowRuleController;

    public ModifyTransport(DataSource dataSource, DataSource modifyDataSource, SwiftMetaData swiftMetaData, FlowRuleController flowRuleController) {
        this.dataSource = dataSource;
        this.modifyDataSource = modifyDataSource;
        this.swiftMetaData = swiftMetaData;
        this.flowRuleController = flowRuleController;

    }

    @Override
    public void doIncrementTransport() throws Exception {
        IncrementTransport decreaseTransport = new DecreaseTransport(dataSource, modifyDataSource, swiftMetaData);
        String query = ((QueryDBSource) modifyDataSource).getQuery();
        String sql = getModifySql(query, (QueryDBSource) modifyDataSource);
        QueryDBSource queryDBSource = new QueryDBSource(sql, ((QueryDBSource) modifyDataSource).getConnectionName());
        IncrementTransport increaseTransport = new IncreaseTransport(dataSource, queryDBSource, swiftMetaData, flowRuleController);
        decreaseTransport.doIncrementTransport();
        increaseTransport.doIncrementTransport();
    }

    private String getModifySql(String sql, QueryDBSource modifyDataSource) {
        String finalSql = null;
        if (dataSource instanceof TableDBSource) {
            List<String> fieldNames = dataSource.getMetadata().getFieldNames();
            StringBuilder fieldsBuilder = new StringBuilder();
            for (int i = 0; i < fieldNames.size(); ) {
                fieldsBuilder.append("t1.").append(fieldNames.get(i)).append(" as ").append(fieldNames.get(i));
                i++;
                if (i != fieldNames.size()) {
                    fieldsBuilder.append(",");
                }
            }
            String fields = fieldsBuilder.toString();

            List<String> whereFieldNames = modifyDataSource.getMetadata().getFieldNames();
            String selectSql = "SELECT " + fields + " FROM " + ((TableDBSource) dataSource).getDBTableName() + " as t1,(" + sql + ") as t2" + " WHERE ";
            StringBuilder whereFieldsBuilder = new StringBuilder();
            for (int i = 0; i < whereFieldNames.size(); ) {
                whereFieldsBuilder.append("t1.").append(whereFieldNames.get(i)).append(" = t2.").append(whereFieldNames.get(i));
                i++;
                if (i != whereFieldNames.size()) {
                    whereFieldsBuilder.append(" and ");
                }
            }
            String whereSql = whereFieldsBuilder.toString();
            finalSql = selectSql + whereSql;
        }
        return finalSql;
    }
}
