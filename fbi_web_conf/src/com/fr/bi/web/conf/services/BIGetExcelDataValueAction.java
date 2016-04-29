package com.fr.bi.web.conf.services;

import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.bi.web.conf.utils.BIGetImportedExcelData;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by sheldon on 14-8-6.
 */

/**
 * 获取excel表格前200行数据，和默认字段名
 */
public class BIGetExcelDataValueAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String fileName = WebUtils.getHTTPRequestParameter(req, "fileName");

        if( StringUtils.isNotBlank(fileName) ) {
            BIGetImportedExcelData excelData = new BIGetImportedExcelData( fileName );
            WebUtils.printAsJSON(res, excelData.getFieldsAndPreviewData());
        }
    }
    @Override
    public String getCMD() {
        return "get_excel_data_by_file_name";
    }
}