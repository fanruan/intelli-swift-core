package com.fr.bi.tool;

import com.fr.bi.common.factory.IFactoryService;
import com.fr.bi.common.factory.annotation.BIMandatedObject;
import com.fr.bi.stable.utils.code.BILogDelegate;
import com.fr.general.ComparatorUtils;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Connery on 2016/1/26.
 */
public class BIModuleFolder {
    public static String MODLUE_FACTORY_METHOD_NAME = "registerModuleBeans";
    private String javaPackage;
    private String moduleName;
    private String moduleFactoryJavaName;
    private String moduleRootPath;
    private String srcRootPath;
    private List<String> moduleAllJavaFile;
    private BIFileScanner fileScanner;
    private BIGenerateBasicJavaFile generateBasicJavaFile;
    private Set<Class<?>> classes;

    public BIModuleFolder(String moduleName, String moduleRootPath, Set<Class<?>> classes) {
        this.moduleName = moduleName;
        this.classes = classes;
        this.moduleRootPath = moduleRootPath;
        this.srcRootPath = moduleRootPath + File.separator + "src";
        fileScanner = new BIFileScanner(srcRootPath);
        moduleAllJavaFile = fileScanner.findAllJavaFile();
        moduleFactoryJavaName = generateFactoryName(moduleName);
        generateBasicJavaFile = new BIGenerateBasicJavaFile(srcRootPath, moduleFactoryJavaName, "com.fr.bi");
    }

    public String getModuleFactoryJavaName() {
        return moduleFactoryJavaName;
    }

    public void generateFactory() {
        generateBasicJavaFile.setImportPackage("" +
                "import com.fr.bi.common.factory.BIFactory;\n" +
                "import com.fr.bi.common.factory.BIMateFactory;\n" +
                "import com.fr.bi.common.factory.BIRepeatKeyException;\n" +
                "import com.fr.bi.stable.utils.code.BILogger;");
        generateBasicJavaFile.addStaticMethod(MODLUE_FACTORY_METHOD_NAME, generateModuleBean());
        generateBasicJavaFile.writeJavaFile();
    }

    public String generateFactoryName(String moduleName) {
        String[] parts = moduleName.split("_");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].length() > 0) {
                String namePart = parts[i].toLowerCase();
                sb.append(namePart.substring(0, 1).toUpperCase() + namePart.substring(1, namePart.length()));
            }
        }
        sb.append("Factory");
        return sb.toString();
    }

    private String generateModuleBean() {
        return load();
    }

    public String load() {
        synchronized (BIFactoryAutoRegisterCode.class) {

            StringBuffer result = new StringBuffer("\r\n\ttry{\r\n\t\tBIFactory xmlFactory =((BIFactory) BIMateFactory.getInstance().getObject( BIFactory.CONF_XML , new Object[]{}));");
            Iterator<Class<?>> it = classes.iterator();
            while (it.hasNext()) {
                try {
                    Class clazz = it.next();
                    if (isClassInModule(clazz)) {
                        result.append(load(clazz));
                    }
                } catch (Exception ex) {
                    BILogDelegate.errorDelegate(ex.getMessage(), ex);
                }
            }
            result.append("}\r\n\t\tcatch(Exception ignoreE){\r\n\t\t BILogger.getLogger().error(ignoreE.getMessage(),ignoreE);\r\n\t\t}");
            return result.toString();
        }
    }

    private boolean isClassInModule(Class clazz) {
        String classFullName = clazz.getName().replace(".", File.separator);
        Iterator<String> javaFileIt = moduleAllJavaFile.iterator();
        while (javaFileIt.hasNext()) {
            String path = javaFileIt.next();
            if (path.endsWith(classFullName + ".java")) {
                return true;
            }
        }
        return false;
    }

    private String load(Class clazz) throws Exception {
        StringBuffer result = new StringBuffer();
        if (clazz.isAnnotationPresent(BIMandatedObject.class)) {
            BIMandatedObject registerObject = (BIMandatedObject) clazz.getAnnotation(BIMandatedObject.class);
            if (!ComparatorUtils.equals(registerObject.factory(), "default_factory")) {

                if (!ComparatorUtils.equals(registerObject.key(), "default_key")) {
                    if (ComparatorUtils.equals(registerObject.factory(), (IFactoryService.CONF_XML))) {
                        result.append("\r\n\t\ttry{");
                        result.append("\r\n\t\t\txmlFactory");
                        result.append(".registerClass(\"").append(registerObject.key()).append("\",").append(clazz.getName()).append(".class);");
                        result.append("}\r\n\t\tcatch(BIRepeatKeyException ignore){\r\n\t\t\t BILogger.getLogger().error(ignore.getMessage(),ignore);\r\n\t\t\t}");
                    }

                }
                if (ComparatorUtils.equals(registerObject.factory(), (IFactoryService.CONF_XML))) {
                    result.append("\r\n\t\ttry{");
                    result.append("\r\n\t\t\txmlFactory");
                    if (!ComparatorUtils.equals(registerObject.implement(), Object.class)) {
                        result.append(".registerClass(\"").append(registerObject.implement().getName()).append("\",").append(clazz.getName()).append(".class);");
                    } else {
                        result.append(".registerClass(\"").append(clazz.getName()).append("\",").append(clazz.getName()).append(".class);");
                    }
                    result.append("}\r\n\t\tcatch(BIRepeatKeyException ignore){\r\n\t\t\t BILogger.getLogger().error(ignore.getMessage(),ignore);\t\n\t\t\t}");

                }


            }
        }
        return result.toString();
    }
}