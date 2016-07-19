package com.fr.bi.stable.utils.file;

import org.junit.Test;

import java.io.File;

/**
 * Created by wuk on 2016/7/19.
 */
public class BIFileUtilsTest {
    @Test
    public void createFile() throws Exception {

    }

    @Test
    public void delete() throws Exception {

    }

    @Test
    public void createDirs() throws Exception {

    }

    @Test
    public void createFile1() throws Exception {

    }

    @Test
    public void checkDir() throws Exception {

    }

    @Test
    public void ensureDir() throws Exception {

    }

    @Test
    public void readFileByLines() throws Exception {

    }

    @Test
    public void copyFile() throws Exception {

    }

    @Test
    public void moveFile() throws Exception {

    }

    @Test
    public void getListFiles() throws Exception {

    }

    @Test
    public void copyFile1() throws Exception {

    }

    @Test
    public void writeFile() throws Exception {

    }

    @Test
    public void readFile() throws Exception {

    }

    @Test
    public void copyFolder() throws Exception {

    }

    @Test
    public void renameFolder() throws Exception {
        File oldFile=new File("c:\\oldFile");
        oldFile.mkdirs();
        oldFile.list();
        File newFile=new File("c:\\newFile");
        if (newFile.exists()){
            newFile.delete();
        }
        BIFileUtils.renameFolder(oldFile,newFile);
        assert(newFile.exists());
        newFile.delete();
    }

}