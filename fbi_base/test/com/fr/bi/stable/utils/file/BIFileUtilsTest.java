package com.fr.bi.stable.utils.file;

import junit.framework.TestCase;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by kary on 2016/7/19.
 */
public class BIFileUtilsTest extends TestCase{


    public void testRenameFolder() throws Exception {
        File oldFile = new File(File.listRoots()[0]+File.separator+"oldFileDir");
        oldFile.mkdirs();
        oldFile.list();
        File newFile = new File(File.listRoots()[0]+File.separator+"newFileDir");
        if (newFile.exists()) {
            newFile.delete();
        }
        BIFileUtils.renameFolder(oldFile, newFile);
        assert (newFile.exists());
        newFile.delete();
    }


    public void testDeleteFiles() throws Exception {
        File dir = new File(File.listRoots()[0]+File.separator+"fileDir");
        dir.mkdirs();
        File child1 = new File(File.listRoots()[0]+File.separator+"fileDir"+File.separator+"child1");
        File child2 = new File(File.listRoots()[0]+File.separator+"fileDir"+File.separator+"child2");
        child1.createNewFile();
        child2.createNewFile();
        InputStream f = new FileInputStream(child1);
        List list = BIFileUtils.deleteFiles(dir);
        assert (list.size() != 0);
        f.close();
        list = BIFileUtils.deleteFiles(dir);
        assert (list.size() == 0);
        assert(!new File(File.listRoots()[0]+File.separator+"fileDir").exists());
    }

}