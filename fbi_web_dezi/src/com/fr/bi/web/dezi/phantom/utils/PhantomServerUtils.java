package com.fr.bi.web.dezi.phantom.utils;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.general.ComparatorUtils;
import com.fr.general.IOUtils;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.OperatingSystem;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by AstronautOO7 on 2016/12/22.
 */
public class PhantomServerUtils {
    //ip,port of phantom server
    private static final String IP = "127.0.0.1";
    private static final int PORT = 8089;

    public static final String CLOSE = "{\"status\": \"close\"}";
    public static final String LIVE = "{\"status\": \"live\"}";

    private static final int LINUX32BIT = 32;
    private static final int LINUX64BIT = 64;

    private static final int CONNECTTIME = 5000;
    private static final int READTIME = 5000;

    public static boolean checkServer(String ip, int port){
        try {
            String res = PhantomServerUtils.postMessage(ip, port, LIVE);
            return isServerReady(res);
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isServerReady(String res){
        try {
            String status = getResParam(res, "status");
            if (ComparatorUtils.equals(status, "OK")){
                return true;
            }
        } catch (JSONException e) {
            return false;
        }
        return false;
    }

    public static boolean isServerInjectSuccess(String res){
        try {
            String status = getResParam(res, "status");
            if (ComparatorUtils.equals(status, "success")){
                return true;
            }
        } catch (JSONException e) {
            return false;
        }
        return false;
    }

    public static String getResParam(String res, String key) throws JSONException {
        JSONObject json = new JSONObject(res);
        return json.getString(key);
    }

    /**
     * @param ip
     * @param port
     */
    public static void shutDownPhantomServer(String ip, int port) {
        try {
            String res = PhantomServerUtils.postMessage(ip, port, CLOSE);
        } catch (Exception e) {
            BILoggerFactory.getLogger().info(e.getMessage());
        }
    }

    /**
     * @param ip
     * @param port
     * @param message
     * @return
     * @throws IOException
     */
    public static String postMessage(String ip, int port, String message) throws IOException {
        URL url = new URL("http://" + ip + ":" + port + "/");
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        connection.setConnectTimeout(CONNECTTIME);
        connection.setReadTimeout(READTIME);

        OutputStream out = connection.getOutputStream();
        out.write(message.getBytes("utf-8"));
        out.close();

        //get base64 picture
        InputStream in = connection.getInputStream();
        String response = IOUtils.inputStream2String(in);
        in.close();

        return response;
    }

    public static String getExe(String path) {
        String exe = path + "/phantomjs";
        //if it is not windows, set authority
        if (isLinux32() || isLinux64()){
            PhantomServerUtils.setAuthority(exe);
        }else if (OperatingSystem.isMacOS()){
            PhantomServerUtils.setAuthority(exe);
        }else if (isUnix32() || isUnix64()){
            PhantomServerUtils.setAuthority(exe);
        }
        return exe;
    }

    public static String setAuthority(String exe) {
        return "chmod +x " + exe;
    }

    public static boolean isLinux32(){
        int bit = linuxBit();
        if (bit == LINUX32BIT){
            return true;
        }
        return false;
    }

    public static boolean isUnix32(){
        return isLinux32();
    }

    public static boolean isLinux64(){
        int bit = linuxBit();
        if (bit == LINUX64BIT){
            return true;
        }
        return false;
    }
    public static boolean isUnix64(){
        return isLinux64();
    }

    private static int linuxBit(){
        String systemType = System.getProperty("os.name");
        if (systemType.equalsIgnoreCase("linux")) {
            try {
                Process process = Runtime.getRuntime().exec("getconf LONG_BIT");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String s = bufferedReader.readLine();
                process.destroy();
                if (s.contains("64")) {//64位
                    return LINUX32BIT;
                } else {//32位
                    return LINUX64BIT;
                }
            } catch (IOException e) {
               BILoggerFactory.getLogger().info(e.getMessage());
            }
        }
        return -1;
    }

    public static BufferedImage base64Decoder(String base64){
        BASE64Decoder decoder = new BASE64Decoder();
        BufferedImage img = null;
        try {
            // Base64解码
            byte[] bytes = decoder.decodeBuffer(base64);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {// 调整异常数据
                    bytes[i] += 256;
                }
            }
            InputStream buffin = new ByteArrayInputStream(bytes, 0, bytes.length);

            img = ImageIO.read(buffin);
        } catch (Exception e) {
            return null;
        }
        return img;
    }
}
