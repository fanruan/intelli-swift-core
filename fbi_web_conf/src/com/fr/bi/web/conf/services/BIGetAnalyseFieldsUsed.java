package com.fr.bi.web.conf.services;

import com.fr.bi.web.conf.AbstractBIConfigureAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BIGetAnalyseFieldsUsed extends AbstractBIConfigureAction {
    @Override
    public String getCMD() {
        return "get_analyse_fields_used";
    }

    /**
     * 行为
     *
     * @param req 数据
     * @param res 返回值
     * @throws Exception
     */
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
//        long userId = ServiceUtils.getCurrentUserID(req);
//        String fieldsString = WebUtils.getHTTPRequestParameter(req, "fields");
//        fieldsString = fieldsString == null ? "[]" : fieldsString;
//        Set<AbstractField> set = new HashSet<AbstractField>();
//        JSONArray fieldsArray = new JSONArray(fieldsString);
//        for(int i=0;i<fieldsArray.length();i++){
//            JSONObject field = fieldsArray.optJSONObject(i);
//            boolean isAnalyse = field.optBoolean("join_analyse");
//            if(!isAnalyse){
//                AbstractField key = new BIDataColumn(userId);
//                key.parseJSON(field);
//                set.add(key);
//            }
//        }
//
//
//        JSONArray ja = new JSONArray();
//        Iterator it = set.iterator();
//        while (it.hasNext()){
//            AbstractField key = (AbstractField)it.next();
//            boolean isUsable = BIReport.isFieldInfoHasTemplateUsed(new BIFieldKey(key));
//            if(isUsable){
//                ja.put(key.createJSON());
//            }
//        }
//        WebUtils.printAsJSON(res, ja);
    }
}