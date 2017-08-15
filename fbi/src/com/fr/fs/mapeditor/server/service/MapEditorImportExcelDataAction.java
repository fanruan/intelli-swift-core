package com.fr.fs.mapeditor.server.service;

import com.fr.data.impl.excelplus.ExcelDataModelPlus;
import com.fr.general.GeneralUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.StableUtils;
import com.fr.web.core.ActionNoSessionCMD;
import com.fr.web.core.upload.SmartFile;
import com.fr.web.core.upload.SmartFiles;
import com.fr.web.core.upload.SmartUpload;
import com.fr.web.utils.WebUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

public class MapEditorImportExcelDataAction extends ActionNoSessionCMD {
    private static final int COLUMN_COUNT = 3;


    public String getCMD() {
        return "import_excel_data";
    }

    /**
     * 执行Action
     * @param req http请求
     * @param res http应答
     * @throws Exception 异常
     */
    public void actionCMD(HttpServletRequest req, HttpServletResponse res) throws Exception {

        ServletContext servletContext = req.getSession().getServletContext();
        SmartUpload smartUpload = new SmartUpload();
        smartUpload.initialize(servletContext, req, res);
        smartUpload.upload();
        SmartFiles uploadFiles = smartUpload.getFiles();

        if(uploadFiles.getCount() == 0){
            return;
        }

        JSONArray points = JSONArray.create();

        SmartFile smartFile = uploadFiles.getFile(0);
        InputStream is = new ByteArrayInputStream(smartFile.getBytes());
        //兼容.XLS .xls
        String name = smartFile.getFileName().toLowerCase();

        List<List<Object[]>> multiSheetDataList = ExcelDataModelPlus.createMutiSheetData(is, name);

        for(int sheetIndex = 0, sheetCount = multiSheetDataList.size(); sheetIndex < sheetCount; sheetIndex++){
            List<Object[]> sheet = multiSheetDataList.get(sheetIndex);

            for(int rowIndex = 0, rowCount = sheet.size(); rowIndex < rowCount; rowIndex++){
                Object[] singleRow = sheet.get(rowIndex);

                if(singleRow.length == COLUMN_COUNT){

                    if(rowIndex == 0 && StableUtils.string2Number(GeneralUtils.objectToString(singleRow[1])) == null){
                        continue;
                    }

                    points.put(
                            JSONObject.create()
                                    .put("name", GeneralUtils.objectToString(singleRow[0]))
                                    .put("lng", GeneralUtils.objectToString(singleRow[1]))
                                    .put("lat", GeneralUtils.objectToString(singleRow[2]))
                    );
                }
            }
        }
        is.close();

        PrintWriter writer = WebUtils.createPrintWriter(res);
        writer.print(points.toString());
        writer.flush();
        writer.close();
    }
}
