package com.fr.bi.resource;

import com.fr.base.FRContext;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BIConstructorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.EncodeConstants;
import com.fr.stable.OperatingSystem;
import com.fr.stable.StableUtils;
import com.fr.stable.StringUtils;
import com.fr.stable.script.Function;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 从后台获取所有的公式名字
 */
public class FormulaCollections {


    /**
     * 公式名字组成的列表
     *
     * @return 列表
     */

    public static JSONArray getAllFormulaObject() {
        List<String> names = new ArrayList<String>();
        JSONArray formulaJSONs = new JSONArray();
        names.addAll(findFunction());
        for (String formulaName : names) {
            String className = "com.fr.function." + formulaName;
            Class formulaClass = null;
            Function formula = null;
            try {
                formulaClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
            try {
                if (!BIConstructorUtils.isAbstract(formulaClass)) {
                    formula = (Function) formulaClass.newInstance();

                } else {
                    continue;
                }

            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
                continue;
            }
            JSONObject formulaJo = new JSONObject();
            try {
                formulaJo.put("def", formula.getCN());
                formulaJo.put("type", getFunctionType(formula.getType()));
                formulaJo.put("name", formulaName);
            } catch (JSONException e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
            formulaJSONs.put(formulaJo);
        }
        return formulaJSONs;

    }

    private static String getFunctionType(Function.Type type) {
        if (type == Function.ARRAY) {
            return "ARRAY";
        } else if (type == Function.LOGIC) {
            return "LOGIC";
        } else if (type == Function.DATETIME) {
            return "DATETIME";
        } else if (type == Function.DELETE) {
            return "DELETE";
        } else if (type == Function.HA) {
            return "HA";
        } else if (type == Function.MATH) {
            return "MATH";
        } else if (type == Function.OTHER) {
            return "OTHER";
        } else if (type == Function.REPORT) {
            return "REPORT";
        } else {
            return "TEXT";
        }
    }

    private static List<String> findFunction() {
        List<String> names = new ArrayList<String>();
        String pkgName = "com.fr.function";
        Class<Function> iface = Function.class;
        ClassLoader classloader = iface.getClassLoader();
        URL url = classloader.getResource(pkgName.replace('.', '/'));
        String classFilePath = url.getFile();
        try {
            classFilePath = URLDecoder.decode(classFilePath, EncodeConstants.ENCODING_UTF_8);
        } catch (UnsupportedEncodingException e1) {
            BILogger.getLogger().error(e1.getMessage(), e1);
        }
        for (String fileName : findClassNamesUnderFilePath(classFilePath)) {
            try {
                String name = fileName.substring(0, fileName.length() - ".class".length());
                Class<?> cls = Class.forName(pkgName + "." + name);
                if (StableUtils.classInstanceOf(cls, iface)) {
                    names.add(name);
                }
            } catch (ClassNotFoundException ignore) {
            }
        }
        return names;
    }


    private static String[] findClassNamesUnderFilePath(String filePath) {
        java.util.List<String> classNameList = new ArrayList<String>();

        if (filePath.contains("!/")) {
            String[] arr = filePath.split("!/");
            String jarPath = arr[0].substring(6); // alex:substring(6)去掉前面的file:/这六个字符
            String classPath = arr[1];
            if (OperatingSystem.isMacOS()) {
                //windows里substring后是d:\123\456, mac下substring后是Application/123/456
                jarPath = StringUtils.perfectStart(jarPath, "/");
            }

            ZipFile zip;
            try {
                zip = new ZipFile(jarPath);
                Enumeration entries = zip.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = (ZipEntry) entries.nextElement();
                    if (entry.isDirectory()) {
                        continue;
                    }

                    String entryName = entry.getName();
                    if (!entryName.contains(classPath) || !entryName.endsWith(".class")) {
                        continue;
                    }
                    classPath = StringUtils.perfectEnd(classPath, "/");

                    classNameList.add(entryName.substring(classPath.length()));
                }
            } catch (IOException e) {
                FRContext.getLogger().error(e.getMessage(), e);
            }
        } else {
            File dir = new File(filePath);
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    String fileName = f.getName();
                    if (fileName.endsWith(".class")) {
                        classNameList.add(fileName);
                    }
                }
            }
        }

        return classNameList.toArray(new String[classNameList.size()]);
    }
}