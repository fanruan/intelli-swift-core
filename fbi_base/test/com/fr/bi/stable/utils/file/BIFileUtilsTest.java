package com.fr.bi.stable.utils.file;

import junit.framework.TestCase;

import java.io.*;
import java.util.List;

/**
 * Created by kary on 2016/7/19.
 */
public class BIFileUtilsTest extends TestCase {


    public void testRenameFolder() throws Exception {
        File oldFile = new File(File.listRoots()[0] + File.separator + "oldFileDir");
        oldFile.mkdirs();
        oldFile.list();
        File newFile = new File(File.listRoots()[0] + File.separator + "newFileDir");
        if (newFile.exists()) {
            newFile.delete();
        }
        BIFileUtils.renameFolder(oldFile, newFile);
        assert (newFile.exists());
        newFile.delete();
    }


    public void testDeleteFiles() throws Exception {
        File dir = new File(File.listRoots()[0] + File.separator + "fileDir");
        dir.mkdirs();
        File child1 = new File(File.listRoots()[0] + File.separator + "fileDir" + File.separator + "child1");
        File child2 = new File(File.listRoots()[0] + File.separator + "fileDir" + File.separator + "child2");
        child1.createNewFile();
        child2.createNewFile();
        InputStream f = new FileInputStream(child1);
        List list = BIFileUtils.deleteFiles(dir);
        assert (list.size() != 0);
        f.close();
        list = BIFileUtils.deleteFiles(dir);
        assert (list.size() == 0);
        assert (!new File(File.listRoots()[0] + File.separator + "fileDir").exists());
    }

    public void testCopyFolder() throws Exception {
            File src = new File("testSrcDir");
            File dest = new File("testDestDir");
            try {
                src.mkdirs();
                File child1 = new File(src.getAbsoluteFile() + "/child1");
                File child2 = new File(src.getAbsoluteFile() + "/child2");
                child1.createNewFile();
                child2.createNewFile();
                BIFileUtils.copyFolder(src, dest);
                assertTrue(dest.exists());
                assertTrue(dest.list().length == src.list().length);
            } catch (IOException e) {
                assertTrue(false);
            } finally {
                if (src.exists()) {
                    BIFileUtils.deleteFiles(src);
                }
                if (dest.exists()) {
                    BIFileUtils.deleteFiles(dest);
                }
            }
    }

//    public void testCmdExec() throws Exception {
//        try {
//            String cmds = "ls";
//            Process ps = Runtime.getRuntime().exec(cmds);
//            System.out.print(loadStream(ps.getInputStream()));
//            System.err.print(loadStream(ps.getErrorStream()));
//            File src = new File("testSrcDir");
//            File dest = new File("testDestDir");
//            src.mkdirs();
//            File child1 = new File(src.getAbsoluteFile() + "/child1");
//            File child2 = new File(src.getAbsoluteFile() + "/child2");
//            child1.createNewFile();
//            child2.createNewFile();
//            cmds="cp -r "+src.getAbsolutePath()+" "+dest.getAbsolutePath();
//            ps = Runtime.getRuntime().exec(cmds);
//            System.out.print(loadStream(ps.getInputStream()));
//            System.err.print(loadStream(ps.getErrorStream()));
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        }
//    }

}
