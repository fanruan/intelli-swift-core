package com.fr.bi.web.conf.services;

import com.fr.base.FRContext;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.excel.CSVTokenizer;
import com.fr.bi.stable.utils.file.BIPictureUtils;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.bi.web.conf.utils.BIGetImportedExcelData;
import com.fr.cache.Attachment;
import com.fr.cache.AttachmentSource;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
                    String fullFileName = file.getName();
                    int l = fullFileName.length();
                    String name = fullFileName.substring(0, l - 4);
                    File xlsxFile = new File(parentFile, name + ".xlsx");
                    Object lockCSV = BIPictureUtils.getImageLock(xlsxFile.getAbsolutePath());
                    synchronized (lockCSV) {
                        FileOutputStream fs1 = new FileOutputStream(xlsxFile);
                        fs1.flush();
                        fs1.close();
                        csvToExcel(originalPath, xlsxFile.getAbsolutePath());
                    }
                    file = xlsxFile;
                }
                BIGetImportedExcelData excelTableData = new BIGetImportedExcelData(file.getName());
                JSONObject jo = excelTableData.getFieldsAndPreviewData();
                jo.put("full_file_name" , file.getName());
                WebUtils.printAsJSON(res, jo);
            }
        }
    }

    public void csvToExcel(String csv, String excel) throws IOException {
        BufferedReader r = null;
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sheet1");

        try{
            r = new BufferedReader(new FileReader(csv));
            int i = 0;
            while (true){
                String ln = r.readLine();
                if (ln == null)
                    break;
                XSSFRow row = sheet.createRow(i++);
                int j = 0;
                for (CSVTokenizer it = new CSVTokenizer(ln); it.hasMoreTokens();){
                    try{
                        String val = it.nextToken();
                        XSSFCell cell = row.createCell((short) j++);
                        cell.setCellValue(val);
                    }catch (Exception e){
                        XSSFCell cell = row.createCell((short) j++);
                        cell.setCellValue("");
                        BILogger.getLogger().error(e.getMessage());
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
            workbook.write(fileOut);
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