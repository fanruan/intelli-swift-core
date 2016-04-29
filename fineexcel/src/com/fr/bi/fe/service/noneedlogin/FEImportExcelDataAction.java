package com.fr.bi.fe.service.noneedlogin;

import com.fr.base.FRContext;
import com.fr.bi.excel.BIGetImportExcelDataValue;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.web.services.conf.AbstractBIConfigureAction;
import com.fr.cache.Attachment;
import com.fr.cache.AttachmentSource;
import com.fr.fs.control.UserControl;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.DateUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by young on 2014/12/15.
 */
public class FEImportExcelDataAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String id = WebUtils.getHTTPRequestParameter(req, "file_id");
        long userId = ServiceUtils.getCurrentUserID(req);
        final String userName = UserControl.getInstance().getUser(userId).getUsername();
        String[] id_array = id.split("\\.");
        if (id_array.length == 1) {
            Attachment attach = AttachmentSource.getAttachment(id);

            if (attach != null) {
                byte[] bytes = attach.getBytes();
                String reportName = attach.getFilename();
                reportName = reportName.trim();

                reportName = reportName.toLowerCase();
                if (reportName.endsWith(".xls") || reportName.endsWith(".xlsx") || reportName.endsWith(".csv")) {

                    //生成excel文件到指定目录下
//                    BIImportExcelForTable biImportExcel = new BIImportExcelForTable(reportName, jo);
//                    File file = biImportExcel.getFile();

                    File parentFile = new File(FRContext.getCurrentEnv().getPath() + BIBaseConstant.EXCELDATA.EXCEL_DATA_PATH);
                    if (!parentFile.exists()) {
                        parentFile.mkdirs();
                    }

                    File file = new File(parentFile, DateUtils.getDate2AllIncludeSSS(new Date()) + "_" + reportName);
//                    File file = new File( parentFile, reportName );
                    File file_xls = null;

                    String fileName = file.getName();

                    FileOutputStream fs = new FileOutputStream(file);
                    fs.write(bytes);
                    fs.flush();
                    fs.close();

                    final File parentFileAdmin = new File("C:\\FineExcel\\uploadFiles\\" + userName + File.separator + "feedback_file");
                    final byte[] bytesAdmin = attach.getBytes();
                    final String fileNameAdmin = reportName;
                    if (!parentFileAdmin.exists()) {
                        parentFileAdmin.mkdirs();
                    }
                    new Thread() {
                        @Override
						public void run() {
                            File file = new File(parentFileAdmin, DateUtils.getDate2AllIncludeSSS(new Date()) + "_" + fileNameAdmin);
                            FileOutputStream fs;
                            try {
                                fs = new FileOutputStream(file);
                                fs.write(bytesAdmin);
                                fs.flush();
                                fs.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                   /*
                    * 上面是生成xls或者csv
                    *当生成的是csv的时候，在以下部分进行转换
                    *原理是通过一个工具类将csv文件格式转换为Excel，即又生成了一个文件
                    *使用的时候其实还是操作Excel格式文件
                    *
                    */
                    if (fileName.endsWith(".csv")) {
                        int l = file.getName().length();
                        String name = file.getName().substring(0, l - 4);
                        file_xls = new File(parentFile, DateUtils.getDate2AllIncludeSSS(new Date()) + "_" + name + ".xls");
                        fileName = file_xls.getName();
                        FileOutputStream fs1 = new FileOutputStream(file_xls);
                        fs1.flush();
                        fs1.close();
                        BIGetImportExcelDataValue convertUtil = new BIGetImportExcelDataValue(file.getName());
                        convertUtil.CsvToExcel(file.getAbsolutePath(), file_xls.getAbsolutePath());
                    }

                    //将关系写入Connection
                    WebUtils.printAsString(res, fileName);
                }
            }

        }
    }

    @Override
    public String getCMD() {
        return "import_excel_data";
    }
}