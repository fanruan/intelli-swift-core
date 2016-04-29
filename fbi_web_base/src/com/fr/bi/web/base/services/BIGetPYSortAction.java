package com.fr.bi.web.base.services;

import com.fr.bi.stable.operation.sort.comp.ChinesePinyinComparator;
import com.fr.bi.web.base.AbstractBIBaseAction;
import com.fr.json.JSONArray;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.TreeSet;

public class BIGetPYSortAction extends AbstractBIBaseAction {

    @Override
    public String getCMD() {
        return "get_py_sort";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String name = WebUtils.getHTTPRequestParameter(req, "names");
        JSONArray ja = new JSONArray(name);
        TreeSet<String> tree = new TreeSet<String>(new ChinesePinyinComparator());
        for (int i = 0; i < ja.length(); i++) {
            tree.add(ja.getString(i));
        }
        JSONArray result = new JSONArray();
        Iterator it = tree.iterator();
        while (it.hasNext()) {
            result.put(it.next());
        }
        WebUtils.printAsJSON(res, result);
    }
}