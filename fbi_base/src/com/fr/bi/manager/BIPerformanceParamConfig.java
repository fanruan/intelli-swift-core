package com.fr.bi.manager;

import com.finebi.cube.common.log.BILoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class created on 2017/4/14.
 *
 * @author Each.Zhang
 */
public class BIPerformanceParamConfig implements BIParamConfig {

    private Properties properties;
    //加重入锁
    private ReentrantLock readLock = new ReentrantLock();
    private ReentrantLock writeLock = new ReentrantLock();

    /**
     * 读取配置参数
     * @param inputStream
     * @return Map<String, String>
     */
    @Override
    public Map<String, String> readConfig(InputStream inputStream) {
        Map<String, String> resultMap = new HashMap<String, String>();
        try {
            properties = new Properties();
            if (inputStream == null) {
                return resultMap;
            }
            properties.load(inputStream);
            readLock.lock();
            Iterator<String> it = properties.stringPropertyNames().iterator();
            while (it.hasNext()) {
                //获取参数名
                String paramName = it.next();
                //获取参数值
                String paramValue = properties.getProperty(paramName);
                resultMap.put(paramName, paramValue);
            }
            readLock.unlock();
            inputStream.close();
        } catch (Exception e) {
            //无额外参数配置，直接使用默认参数进行启动
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return resultMap;
    }

    /**
     * 将参数写入到配置文件
     * @param newParam
     * @return boolean
     */
    @Override
    public boolean writeConfig(Map<String, String> newParam, OutputStream outputStream) {
        try {
            if (outputStream == null) {
                return false;
            }
            properties = new Properties();
            PerformanceParamTools tools = new PerformanceParamTools();
            newParam = tools.convertParamKey(newParam);
            Iterator<String> it = newParam.keySet().iterator();
            writeLock.lock();
            while (it.hasNext()) {
                String paramKey = it.next();
                String paramValue = newParam.get(paramKey);
                properties.setProperty(paramKey, paramValue);
            }
            properties.store(outputStream,"paramKey - paramValue");
            writeLock.unlock();
            return true;
        } catch (Exception e){
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 在更新之前，需要做的参数优化
     * @param runParam
     * @param newParam
     * @param resultParam
     * @return
     */
    @Override
    public Map<String, String> beforeDoWrite (Map<String, String> runParam, Map<String, String> newParam, Map<String, String> resultParam) {
        Map<String, String> tempParam ;
        if (newParam.size() != 0) {
            tempParam = PerformanceParamTools.mergeMaps(newParam, resultParam);

        } else {
            if (runParam.size() == 0) {
                runParam = new HashMap<String, String>();
            }
            tempParam = PerformanceParamTools.mergeMaps(runParam, resultParam);
        }
        return tempParam;
    }
}
