package com.fr.bi.stable.utils.file;

import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpProtocolException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * This class created on 2016/6/14.
 *
 * @author Connery
 * @since 4.0
 */
public class FtpBusiness {

    FtpClient ftpClient;

    public void connectServer(String ip, int port, String user, String password, String path) {
        try {
            /* ******连接服务器的两种方法*******/
            ftpClient = FtpClient.create();
            try {
                SocketAddress addr = new InetSocketAddress(ip, port);
                ftpClient.connect(addr);
//                ftpClient.login(user, password.toCharArray());
                System.out.println("login success!");
                if (path.length() != 0) {
                    //把远程系统上的目录切换到参数path所指定的目录
                    ftpClient.changeDirectory(path);
                }
            } catch (FtpProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    public void download(String remoteFile, String localFile) {
        InputStream is = null;
        FileOutputStream os = null;
        try {            //获取远程机器上的文件filename，借助TelnetInputStream把该文件传送到本地。

            try {
                is = ftpClient.getFileStream(remoteFile);
            } catch (FtpProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            File file_in = new File(localFile);
            os = new FileOutputStream(file_in);
            byte[] bytes = new byte[1024];
            int c;
            while ((c = is.read(bytes)) != -1) {
                os.write(bytes, 0, c);
            }
            System.out.println("download success");
        } catch (IOException ex) {
            System.out.println("not download");
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
