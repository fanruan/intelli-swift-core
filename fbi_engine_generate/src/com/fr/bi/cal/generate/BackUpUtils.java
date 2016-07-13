package com.fr.bi.cal.generate;

import com.fr.base.FRContext;
import com.fr.bi.stable.constant.BIBaseConstant;

import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.general.DateUtils;
import com.fr.stable.project.ProjectConstants;

import java.io.File;
import java.io.FileFilter;
import java.util.Date;

public class BackUpUtils {

    private static final String FOLDER = "backup";
    private static final FileFilter FILTER = new FileFilter() {

        @Override
        public boolean accept(File pathname) {
            if (pathname == null || pathname.isDirectory()) {
                return false;
            }
            String fileName = pathname.getName();
            return fileName.endsWith(".xml") || fileName.endsWith(".lic");
        }
    };

    public static void backup() {
        if (BIBaseConstant.BI_MODEL != BIBaseConstant.CLASSIC_BI) {
            return;
        }
        String resource = FRContext.getCurrentEnv().getPath() + File.separator + ProjectConstants.RESOURCES_NAME;
        String target = resource + File.separator + FOLDER + File.separator + DateUtils.DATEFORMAT5.format(new Date()) + File.separator + new Date().getTime();
        backupFiles(new File(resource), new File(target));
    }

    private static void backupFiles(File currentFolder, File targetFolder) {
        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }
        for (File f : currentFolder.listFiles(FILTER)) {
            BIFileUtils.copyFile(f.getName(), currentFolder, targetFolder);
        }
        backupBIReport(currentFolder, targetFolder);
    }

    private static void backupBIReport(File currentFolder, File targetFolder) {
        File biReport = new File(currentFolder, "biReport");
        if (biReport.exists() && biReport.isDirectory()) {
            File targetBIReport = new File(targetFolder, "biReport");
            copyFolder(biReport, targetBIReport);
        }
    }

    private static void copyFolder(File source, File dest) {
        if (!dest.exists()) {
            dest.mkdirs();
        }
        for (File f : source.listFiles()) {
            if (f.isFile()) {
                BIFileUtils.copyFile(f.getName(), source, dest);
            } else if (f.isDirectory() && (!"operation_record".equals(f.getName()))) {
                copyFolder(f, new File(dest, f.getName()));
            }
        }
    }

}
