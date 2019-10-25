package com.fr.swift.test;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author anchore
 * @date 2019/5/20
 * <p>
 * 当你需要mock final时，通过@Rule的方式开启
 * <p>
 * 谨慎开启，可能会影响mock其他正常的功能，所以每次都会删除掉配置文件
 */
public class MockFinal extends Statement implements TestRule {

    @Override
    public void evaluate() throws Throwable {
        File dir = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "mockito-extensions");
        File path = new File(dir, "/org.mockito.plugins.MockMaker");
        try {
            if (!dir.exists() && dir.mkdirs()) {
                FileWriter writer = null;
                try {
                    writer = new FileWriter(path);
                    writer.write("mock-maker-inline");
                } finally {
                    if (writer != null) {
                        writer.close();
                    }
                }
            } else if (dir.exists() && !path.exists()) {
                FileWriter writer = null;
                try {
                    writer = new FileWriter(path);
                    writer.write("mock-maker-inline");
                } finally {
                    if (writer != null) {
                        writer.close();
                    }
                }
            }
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(Level.WARNING, "enable mock final failed", e);
        } finally {
            try {
                base.evaluate();
            } finally {
                if (path.delete()) {
                    dir.delete();
                }
            }
        }
    }

    private Statement base;

    @Override
    public Statement apply(Statement base, Description description) {
        this.base = base;
        return this;
    }
}