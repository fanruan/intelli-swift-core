package com.fr.bi.web.conf.services;

import com.finebi.cube.api.BICubeManager;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.conf.data.source.TableSourceFactory;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.data.source.ICubeTableSource;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.bi.web.conf.utils.BIWebConfUtils;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by User on 2016/1/13.
 */
public class BINumberFieldMaxMinValueAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String fieldName = WebUtils.getHTTPRequestParameter(req, "fieldName");
        ICubeTableSource source = TableSourceFactory.createTableSource(new JSONObject(WebUtils.getHTTPRequestParameter(req, "table")), userId);
        ICubeDataLoader tiLoader = BICubeManager.getInstance().fetchCubeLoader(userId);
        ICubeTableService ti = tiLoader.getTableIndex( source.fetchObjectCore());
        JSONObject jo = new JSONObject();

        if (BIWebConfUtils.checkCubeVersion(source, userId)) {
            jo.put(BIJSONConstant.JSON_KEYS.FILED_MAX_VALUE, ti != null ? ti.getMAXValue(new IndexKey(fieldName)) : 0);
            jo.put(BIJSONConstant.JSON_KEYS.FIELD_MIN_VALUE, ti != null ? ti.getMINValue(new IndexKey(fieldName)) : 0);
        } else {
            Set set = source.getFieldDistinctNewestValues(fieldName, tiLoader, userId);
            TreeSet tSet = new TreeSet(BIBaseConstant.COMPARATOR.COMPARABLE.ASC);
            Iterator it = set.iterator();
            while (it.hasNext()) {
                tSet.add(Double.parseDouble(it.next().toString()));
            }
            jo.put(BIJSONConstant.JSON_KEYS.FIELD_MIN_VALUE, tSet.first());
            jo.put(BIJSONConstant.JSON_KEYS.FILED_MAX_VALUE, tSet.last());
        }

        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "number_max_min";
    }
}