package com.fr.bi.web.conf.services;

import com.fr.base.FRContext;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.utils.excel.CSVTokenizer;
import com.fr.bi.stable.utils.file.BIPictureUtils;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.bi.web.conf.utils.BIGetImportedExcelData;
import com.fr.cache.Attachment;
import com.fr.cache.AttachmentSource;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created by sheldon on 14-8-6.
 */
public class BISaveFileGetExcelDataAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String fileId = WebUtils.getHTTPRequestParameter(req, "fileId");
        Attachment attach = AttachmentSource.getAttachment(fileId);
        if (attach != null) {
            byte[] bytes = attach.getBytes();
            String fileName = attach.getFilename();
            fileName = fileName.trim();
            fileName = fileName.toLowerCase();
            boolean isExcel = fileName.endsWith(".xls") || fileName.endsWith(".xlsx") || fileName.endsWith(".csv");
            if (isExcel) {
                File parentFile = new File(FRContext.getCurrentEnv().getPath() + BIBaseConstant.EXCELDATA.EXCEL_DATA_PATH);
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                //以attach_id+fileName作为标识
                File file = new File(parentFile, fileId + fileName);
                FileOutputStream fs = new FileOutputStream(file);
                fs.write(bytes);
                fs.flush();
                fs.close();
                String originalPath = file.getAbsolutePath();
                if (fileName.endsWith(".csv")) {
                    int l = fileName.length();
                    String name = fileName.substring(0, l - 4);
                    File xlsFile = new File(parentFile, name + ".xls");
                    Object lockCSV = BIPictureUtils.getImageLock(xlsFile.getAbsolutePath());
                    synchronized (lockCSV) {
                        FileOutputStream fs1 = new FileOutputStream(xlsFile);
                        fs1.flush();
                        fs1.close();
                        csvToExcel(originalPath, xlsFile.getAbsolutePath());
                    }
                    file = xlsFile;
                }
                BIGetImportedExcelData excelTableData = new BIGetImportedExcelData(file.getName());
                JSONObject jo = excelTableData.getFieldsAndPreviewData();
                jo.put("full_file_name" , file.getName());
                WebUtils.printAsJSON(res, jo);
            }
        }
    }

    public void csvToExcel(String csv, String excel) throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("Sheet1");
        BufferedReader r = null;

        try{
            r = new BufferedReader(new FileReader(csv));
            int i = 0;
            while (true){
                String ln = r.readLine();
                if (ln == null)
                    break;
                HSSFRow row = sheet.createRow(i++);
                int j = 0;
                for (CSVTokenizer it = new CSVTokenizer(ln); it.hasMoreTokens();){
                    try{
                        String val = it.nextToken();
                        HSSFCell cell = row.createCell((short) j++);
                        cell.setCellValue(val);
                    }catch (Exception e){
                        HSSFCell cell = row.createCell((short) j++);
                        cell.setCellValue("");
                        String val = it.getQuate();
                        System.out.println(j);
                        FRContext.getLogger().error(e.getMessage(), e);
                    }
                }
            }
        }finally {
            if (r != null)
                r.close();
        }
        FileOutputStream fileOut = null;
        try{
            fileOut = new FileOutputStream(excel);
            wb.write(fileOut);
        }finally{
            if (fileOut != null)
                fileOut.close();
        }
    }

    @Override
    public String getCMD() {
        return "save_file_get_excel_data";
    }
}