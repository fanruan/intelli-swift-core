package com.fr.bi.conf.base.cube;

import com.finebi.cube.api.BICubeManager;
import com.finebi.cube.api.ICubeColumnDetailGetter;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableRelationPath;
import com.fr.bi.base.key.BIKey;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.stable.connection.ConnectionRowGetter;
import com.fr.bi.stable.connection.DirectTableConnectionFactory;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.traversal.TraversalAction;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.util.BIConfUtils;
import com.fr.fs.control.UserControl;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Young's on 2016/5/19.
 */
public class BICubeConfManager {
    private String cubePath;
    private String loginField;

    private long packageLastModify;

    private BIReportConstant.MULTI_PATH_STATUS multiPathCubeStatus;

    public String getCubePath() {
        return cubePath;
    }

    public void setCubePath(String cubePath) {
        this.cubePath = cubePath;
    }

    public String getLoginField() {
        return loginField;
    }

    public void setLoginField(String loginField) {
        this.loginField = loginField;
    }

    public long getPackageLastModify() {
        return packageLastModify;
    }

    public void setPackageLastModify(long packageLastModify) {
        this.packageLastModify = packageLastModify;
    }

    public BIReportConstant.MULTI_PATH_STATUS getMultiPathCubeStatus() {
        return multiPathCubeStatus;
    }

    public void setMultiPathCubeStatus(BIReportConstant.MULTI_PATH_STATUS multiPathCubeStatus) {
        this.multiPathCubeStatus = multiPathCubeStatus;
    }

    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        if (cubePath != null) {
            jo.put("cube_path", cubePath);
        }
        if (loginField != null) {
            jo.put("login_field", loginField);
        }
        return jo;
    }

    public Object[] getFieldValue(BusinessField ck, long userId) {
        try {
            ICubeDataLoader loader = BICubeManager.getInstance().fetchCubeLoader(userId);
            String userName = UserControl.getInstance().getUser(userId).getUsername();
            BusinessField field = BIModuleUtils.getBusinessFieldById(new BIFieldID(loginField));
            BITableRelationPath firstPath = BICubeConfigureCenter.getTableRelationManager().getFirstPath(userId, ck.getTableBelongTo(), field.getTableBelongTo());
            List<BITableRelation> relations = new ArrayList<BITableRelation>();
            if(firstPath != null) {
                relations = firstPath.getAllRelations();
            }
            BIKey userNameIndex = new IndexKey(field.getFieldName());
            if (userNameIndex != null) {
                final ConnectionRowGetter getter = DirectTableConnectionFactory.createConnectionRow(BIConfUtils.convert2TableSourceRelation(relations), loader);
                ICubeTableService ti = loader.getTableIndex(field.getTableBelongTo().getTableSource());
                GroupValueIndex gvi = ti.getIndexes(userNameIndex, new String[]{userName})[0];
                final ArrayList<Integer> o = new ArrayList<Integer>();
                if (gvi != null) {
                    //只取一个值
                    gvi.Traversal(new TraversalAction() {
                        @Override
                        public void actionPerformed(int[] rowIndices) {
                            for (int i = 0; i < rowIndices.length; i ++){
                                o.add(getter.getConnectedRow(rowIndices[i]));
                            }
                        }
                    });
                    if(!o.isEmpty()){

                        int[] rows = new int[o.size()];
                        Object [] values = new Object[rows.length];
                        ICubeTableService cubeTableService = loader.getTableIndex(ck.getTableBelongTo().getTableSource());
                        ICubeColumnDetailGetter detailGetter = cubeTableService.getColumnDetailReader(cubeTableService.getColumnIndex(ck.getFieldName()));
                        for (int i = 0; i < rows.length; i ++){
                            rows[i] = o.get(i);
                            values[i] = detailGetter.getValue(o.get(i));
                        }
                        return values;
                    }
                }
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return null;
    }

}
