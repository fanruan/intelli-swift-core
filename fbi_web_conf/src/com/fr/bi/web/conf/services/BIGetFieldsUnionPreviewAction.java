package com.fr.bi.web.conf.services;

import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.general.data.DataModel;
import com.fr.general.data.TableDataException;
import com.fr.json.JSONArray;
import com.fr.stable.Primitive;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BIGetFieldsUnionPreviewAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "get_fields_union_preview";
    }

    //获取自循环列的具体信息
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
//		String unionRelation = WebUtils.getHTTPRequestParameter(req, "relation");
//		JSONObject unionJo = new JSONObject(unionRelation);
//		String connectionName = unionJo.getString("connection_name");
//		long userId = ServiceUtils.getCurrentUserID(req);
//		Connection conn = null;
//        ResultSet result = null;
//        Statement statement = null;
//		try {
//			com.fr.data.impl.Connection dbc = DatasourceManager.getInstance().getConnection(connectionName);
//			conn = dbc.createConnection();
//			JSONArray ja;
//            if (ComparatorUtils.equals(connectionName, BIBaseConstant.CUBEINDEX.ETL_CONNECTION) || ComparatorUtils.equals(conn, BIBaseConstant.CUBEINDEX.EXCEL_CONNECTION)){
//                String tableName = unionJo.getString("table_name");
//                String idFieldName = unionJo.getString("id_field_name");
//                String parentIdFieldName = unionJo.getString("parentid_field_name");
//                if(unionJo.has("parentid_field_name")) {
//                    ja = getFloorJaFromIdAndParentIdAndCube(connectionName, tableName, idFieldName, parentIdFieldName, userId);
//                } else {
//                    BIOneFieldUnionRelation oneUnion;
//                    if(!unionJo.has("field_length")) {
//                        oneUnion = BIOneFieldUnionRelation.parseJson(unionJo);
//                    } else {
//                        oneUnion = BIOneFieldIsometricUnionRelation.parseJSON(unionJo);
//                    }
//                    ja = getFloorJaFromIdAndCube(connectionName, tableName, idFieldName, oneUnion, userId);
//                }
//            } else {
//                if(unionJo.has("parentid_field_name")) {
//                    BITwoFieldUnionRelation twoUnion = BITwoFieldUnionRelation.parseJson(unionJo);
//                    statement = conn.createStatement();
//                    result = statement.executeQuery(twoUnion.createQueryPattern());
//                    ja = getFloorJaFromIdAndParentId(result);
//                } else {
//                    BIOneFieldUnionRelation oneUnion;
//                    if(!unionJo.has("field_length")) {
//                        oneUnion = BIOneFieldUnionRelation.parseJson(unionJo);
//                    } else {
//                        oneUnion = BIOneFieldIsometricUnionRelation.parseJSON(unionJo);
//                    }
//                    statement = conn.createStatement();
//                    result = statement.executeQuery(oneUnion.createQueryPattern());
//                    ja = getFloorJaFromId(result, oneUnion);
//                }
//            }
//			WebUtils.printAsJSON(res, ja);
//		} catch (Exception e) {
//			FRContext.getLogger().log(Level.WARNING, e.getMessage(), e);
//		} finally {
//            DBUtils.closeResultSet(result);
//            DBUtils.closeStatement(statement);
//			DBUtils.closeConnection(conn);
//		}
    }

    private JSONArray parseDataMode(DataModel dm, int type) throws TableDataException {
        int columnSize = dm.getColumnCount();
        int rowSize = dm.getRowCount();
        JSONArray ja = new JSONArray();
        for (int i = (type == 0 ? 2 : 0); i < columnSize; i++) {
            JSONArray column = new JSONArray();
            Set set = new HashSet();
            for (int j = 0; j < rowSize; j++) {
                Object obj = dm.getValueAt(j, i);
                if (obj == null || obj == Primitive.NULL) {
                    continue;
                }
                set.add(obj);
            }
            Iterator iter = set.iterator();
            while (iter.hasNext()) {
                column.put(iter.next());
            }
            ja.put(column);
        }
        return ja;
    }

//	private JSONArray getFloorJaFromIdAndParentId(ResultSet result) throws SQLException, TableDataException {
//		DataModel dm = BITwoFieldUnionRelation.createPreviewTableData(result, String.class);
//		return parseDataMode(dm, 0);
//	}
//
//
//	private JSONArray getFloorJaFromId(ResultSet result, BIOneFieldUnionRelation relation) throws SQLException, TableDataException {
//		DataModel dm = relation.createPreviewTableData(result, relation.createColumnLengthArray());
//		return parseDataMode(dm, 1);
//	}
//
//    private JSONArray getFloorJaFromIdAndParentIdAndCube(String dbName, String tableName, String id, String pid, long userId) throws SQLException, TableDataException {
//        BITableIndex ti = CubeReadingTableIndexLoader.getInstance(userId).getTableIndex(new BITableKey(dbName, null, tableName, null));
//        DataModel dm = BITwoFieldUnionRelation.createPreviewTableData(ti, id, pid,  String.class);
//        return parseDataMode(dm, 0);
//    }
//
//
//    private JSONArray getFloorJaFromIdAndCube(String dbName, String tableName, String id,BIOneFieldUnionRelation relation, long userId) throws Exception {
//        BITableIndex ti = CubeReadingTableIndexLoader.getInstance(userId).getTableIndex(new BITableKey(dbName, null, tableName, null));
//        DataModel dm = relation.createPreviewTableData(ti, id, relation.createColumnLengthArray());
//        return parseDataMode(dm, 1);
//    }
}