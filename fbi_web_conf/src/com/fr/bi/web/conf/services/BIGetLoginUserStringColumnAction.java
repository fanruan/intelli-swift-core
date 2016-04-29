package com.fr.bi.web.conf.services;

import com.fr.bi.web.conf.AbstractBIConfigureAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BIGetLoginUserStringColumnAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "get_login_user_field";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
//		JSONArray ja = new JSONArray();
//		boolean fromSourceDB = Boolean.valueOf(WebUtils.getHTTPRequestParameter(req, "fromDBSource")).booleanValue();
//		BILoginUserInfo info = BIConnectionManager.getInstance().settingUseGetLoginUserInfo();
//		if(info != null && info.getTableKey() != null){
//			Set result = new HashSet();
//			//这里生成好了才能看到过滤条件
//			BIConfUtils.analysisUseGetKeyTablesByEnd(result, info.getTableKey());
//			if(fromSourceDB){
//				//FIXME 这里若数据没生成不能取到主表，如何设置过滤条件
//				BIDataSource td = BIAbstractDataSource.createBIDataSource(info.getTableKey());
//				BITable t = td.getBaseBITable();
//				Set tableSet = new HashSet();
//				tableSet.add(info.getTableKey());
//				if(t != null){
//					Iterator iter = t.getBIColumnIterator();
//					while(iter.hasNext()){
//						BIColumn col = (BIColumn) iter.next();
//						BIDataColumn dataColumn = new BIDataColumn(info.getTableKey().getDbName(), info.getTableKey().getSchema(), info.getTableKey().getTableName(), col.getColumnName(), BIBaseUtils.checkColumnClassTypeFromSQL(col.getType()), col.getColumnSize());
//						if(dataColumn.getType() == BIBaseConstant.COLUMN.STRING
//								&& !ComparatorUtils.equals(info.getUserNameColumn(), dataColumn.getFieldName())){
//							ja.put(dataColumn.createJSON());
//						}
//					}
//				}
//				Iterator iter = result.iterator();
//				while(iter.hasNext()){
//					BIAbstractFieldDefine field = (BIAbstractFieldDefine) iter.next();
//					if(!tableSet.contains(field.createKey())){
//						tableSet.add(field.createKey());
//						BIDataSource source = BIAbstractDataSource.createBIDataSource(field);
//						BITable table = source.getBaseBITable();
//						if(table != null){
//							BITableRelation[][] relation = BIConfUtils.createNewRelationList(info.getTableKey(), field.createKey());
//							if(relation == null || relation.length != 1){
//								//FIXME 多路径暂不支持
//								continue;
//							}
//							Iterator it = table.getBIColumnIterator();
//							while(it.hasNext()){
//								BIColumn col = (BIColumn) it.next();
//								BIDataColumn dataColumn = new BIDataColumn(field.getDbName(), field.getSchema(), field.getTableName(), col.getColumnName(), BIBaseUtils.checkColumnClassTypeFromSQL(col.getType()), col.getColumnSize());
//								dataColumn.setRelation(relation[0]);
//								if(dataColumn.getType() == BIBaseConstant.COLUMN.STRING){
//									ja.put(dataColumn.createJSON());
//								}
//							}
//						}
//					}
//				}
//			} else {
//				Set tableSet = new HashSet();
//				tableSet.add(info.getTableKey());
//				BITableKey td = info.getTableKey();
//				BITableIndex ti = CubeReadingTableIndexLoader.getInstance().getTableIndex(td);
//				BIAbstractFieldDefine[] c = ti.getColumn();
//				for(int i = 0, len = c.length; i < len; i++){
//                    BIAbstractFieldDefine dataColumn = c[i];
//					if(dataColumn.getType() == BIBaseConstant.COLUMN.STRING
//							&& !ComparatorUtils.equals(info.getUserNameColumn(), dataColumn.getFieldName())){
//						ja.put(dataColumn.createJSON(BITableTranslater.UNCONFIGURED));
//					}
//				}
//				Iterator iter = result.iterator();
//				while(iter.hasNext()){
//					BIAbstractFieldDefine field = (BIAbstractFieldDefine) iter.next();
//					if(!tableSet.contains(field.createKey())){
//						tableSet.add(field.createKey());
//
//						BITableIndex tis =CubeReadingTableIndexLoader.getInstance().getTableIndex(field);
//						BITableRelation[][] relation = BIConfUtils.createNewRelationList(info.getTableKey(), field.createKey());
//						if(relation == null || relation.length != 1){
//							//FIXME 多路径暂不支持
//							continue;
//						}
//						BIAbstractFieldDefine[] column = tis.getColumn();
//						for(int i = 0, len = column.length; i < len; i++){
//							BIDataColumn dataColumn = (BIDataColumn) column[i].clone();
//							dataColumn.setRelation(relation[0]);
//							if(dataColumn.getType() == BIBaseConstant.COLUMN.STRING){
//								ja.put(dataColumn.createJSON());
//							}
//						}
//					}
//				}
//			}
//		}
//		WebUtils.printAsJSON(res, ja);
    }
}