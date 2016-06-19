package com.finebi.cube.gen.arrange;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
public class GetFileSize
{
    public static void main(String[] args) {
        double dirSize = getDirSize(new File("/Users/wuk/Downloads/软件开发规范文档.rar"));
        System.out.println(dirSize);
    }
    private static long getFileSize(File f) throws IOException {
        FileChannel fc= null;
        if (f.exists() && f.isFile()){
            FileInputStream fis= new FileInputStream(f);
            fc= fis.getChannel();
        }
        if (null!=fc){
                fc.close();
        }
        
        return fc.size();
    }

    public static double getDirSize(File file) {
        //判断文件是否存在     
        if (file.exists()) {
            //如果是目录则递归计算其内容的总大小    
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                double size = 0;
                for (File f : children)
                    size += getDirSize(f);
                return size;
            } else {//如果是文件则直接返回其大小,以“兆”为单位   
                
                double size = (double) file.length() / 1024 / 1024;
                return size;
            }
        } else {
            System.out.println("文件或者文件夹不存在，请检查路径是否正确！");
            return 0.0;
        }
    }

}
