package com.fr.bi.web.dezi.phantom.utils;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.general.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by AstronautOO7 on 2016/12/22.
 */
public class ServerUtils {
    //ip,port of phantom server
    private static final String IP = "127.0.0.1";
    private static final int PORT = 8089;

    public static final String CLOSE = "{\"status\": \"close\"}";

    /**
     *
     * @param ip
     * @param port
     * @param message
     * @return
     * @throws IOException
     */
    public static String postMessage(String ip, int port, String message) throws IOException {
        int connectTime = 5000;
        int readTime = 5000;
        URL url = new URL("http://" + ip + ":" + port + "/");
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        connection.setConnectTimeout(connectTime);
        connection.setReadTimeout(readTime);

        OutputStream out = connection.getOutputStream();
        out.write(message.getBytes("utf-8"));
        out.close();

        //get base64 picture
        InputStream in = connection.getInputStream();
        String response = IOUtils.inputStream2String(in);
        in.close();

        return response;
    }

    public static void shutDownPhantomServer(String ip, String port) {
        try {

        } catch (Exception e) {
            BILoggerFactory.getLogger().info(e.getMessage());
        }
    }
}
