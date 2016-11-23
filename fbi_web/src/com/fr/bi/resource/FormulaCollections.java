package com.fr.bi.resource;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.utils.program.BIClassUtils;
import com.fr.bi.stable.utils.program.BIConstructorUtils;
import com.fr.file.FunctionManager;
import com.fr.file.FunctionManagerProvider;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.plugin.ExtraClassManager;
import com.fr.stable.StableUtils;
import com.fr.stable.script.Function;
import com.fr.stable.script.FunctionDef;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            } catch (Throwable throwable) {
                BILoggerFactory.getLogger().error(throwable.getMessage());
            }
            try {
                if (!BIConstructorUtils.isAbstract(formulaClass)) {
                    formula = (Function) formulaClass.newInstance();

                } else {
                    continue;
                }

            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
                continue;
            }
            JSONObject formulaJo = new JSONObject();
            try {
                formulaJo.put("def", formula.getCN());
                formulaJo.put("type", getFunctionType(formula.getType()));
                formulaJo.put("name", formulaName);
            } catch (JSONException e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
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

    private static Set<String> findFunction() {
        Set<String> names = new HashSet<String>();
        String pkgName = "com.fr.function";
        Class<Function> iface = Function.class;
        Set<Class<?>> classSet = BIClassUtils.getClasses(pkgName);
        for (Class<?> cls : classSet) {
            if (StableUtils.classInstanceOf(cls, iface)) {
                names.add(cls.getName().substring(cls.getName().lastIndexOf('.') + 1));
            }
        }
        FunctionManagerProvider funtionManager = FunctionManager.getProviderInstance();
        if (funtionManager != null) {
            int functionDefCount = funtionManager.getFunctionDefCount();

            for (int i = 0; i < functionDefCount; i++) {
                try{
                    names.add(funtionManager.getFunctionDef(i).getName());
                }catch (Throwable throwable){
                    BILoggerFactory.getLogger().info(throwable.getMessage());
                }
            }
        }
        FunctionDef[] fs = new FunctionDef[0];
        try{
            fs = ExtraClassManager.getInstance().getFunctionDef();
        } catch (Throwable throwable){
            BILoggerFactory.getLogger().info(throwable.getMessage());
        }
        int count = fs.length;
        for (int i = 0; i < count; i++) {
            names.add(fs[i].getName());
        }

        return names;
    }
}