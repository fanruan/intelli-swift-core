package com.fr.bi.tool;

import com.fr.bi.stable.utils.program.BIClassUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Connery on 2016/1/23.
 */
public class BIFactoryAutoRegisterCode {
    private static List<String> BLACK_FILE_NAME = new ArrayList<String>();

    static {
//        BLACK_FILE_NAME.add("fbi_analysis_etl");
        BLACK_FILE_NAME.add("fbi_engine_cache");
        BLACK_FILE_NAME.add("fbi_engine_support");
        BLACK_FILE_NAME.add("fbi_engine_temp");
        BLACK_FILE_NAME.add("fbi_engine_cache");
        BLACK_FILE_NAME.add("fbi_fs");
        BLACK_FILE_NAME.add("fbi_starter");
    }

    private String projectPath;
    private List<BIModuleFolder> modules;
    private BIGenerateBasicJavaFile systemRegisterCenter;

    public BIFactoryAutoRegisterCode(String projectPath) {
        this.projectPath = projectPath;
        modules = new ArrayList<BIModuleFolder>();
        File file = new File(projectPath);
        File[] files = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("fbi_") && !BLACK_FILE_NAME.contains(name);
            }
        });
        Set<Class<?>> set = BIClassUtils.getClasses("com.fr.bi");
        for (int i = 0; i < files.length; i++) {
            modules.add(new BIModuleFolder(files[i].getName(), files[i].getAbsolutePath(), set));
        }
        systemRegisterCenter = new BIGenerateBasicJavaFile(projectPath + File.separator + "fbi" + File.separator + "src", "SystemFactoryRegister", "com.fr.bi");
    }

    private String generateSysFactoryMethodContent() {
        Iterator<BIModuleFolder> it = modules.iterator();
        StringBuffer sb = new StringBuffer();
        sb.append("\r\n\ttry{");
        while (it.hasNext()) {
            BIModuleFolder folder = it.next();
            String factoryName = folder.getModuleFactoryJavaName();
            sb.append(factoryName).append(".").append(BIModuleFolder.MODLUE_FACTORY_METHOD_NAME).append("();\r\n\t");
        }
        sb.append("\r\n\t}catch(Exception ignore){\r\n\tBILogger.getLogger().error(ignore.getMessage(),ignore);\r\n\t}");
        return sb.toString();
    }

    private void generateSysFactory() {
        systemRegisterCenter.setImportPackage("" +
                "import com.fr.bi.stable.utils.code.BILogger;");
        systemRegisterCenter.addStaticMethod("systemRegister", generateSysFactoryMethodContent());
        systemRegisterCenter.writeJavaFile();
    }

    public void generateRegisterCode() {
        Iterator<BIModuleFolder> it = modules.iterator();
        while (it.hasNext()) {
            BIModuleFolder folder = it.next();
            folder.generateFactory();
        }
    }

    /**
     * 生成所有注册的Bean
     *
     * @param args
     */
    public static void main(String[] args) {
        BIFactoryAutoRegisterCode code = new BIFactoryAutoRegisterCode("D:\\FineBI\\Git\\workHouse\\project");
        code.generateRegisterCode();
        code.generateSysFactory();
    }
}