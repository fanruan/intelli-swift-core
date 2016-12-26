package com.fr.bi.web.dezi.phantom;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.FRContext;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.web.dezi.phantom.utils.ServerUtils;
import com.fr.general.RunTimeErorException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * phantom server
 * Created by AstronautOO7 on 2016/12/21.
 */
public class PhantomServer {

    private static final String IP = "127.0.0.1";
    private static final int PORT = 8089;

    private static Map<String, String> osMap = new HashMap<String, String>();
    private static String PhantomEnv = BIBaseConstant.PHANTOM.PHANTOM_PATH;
    private static String PhantomLib = PhantomEnv + "/lib";
    private static String PhantomResources = FRContext.getCurrentEnv().getPath() + "/classes/com/fr/bi/web/js/third";

    private static final int BUFF_LENGTH = 8192;

    static {
        osMap.put("linux32", "/phantomjs");
        osMap.put("linux64", "/phantomjs");
        osMap.put("macOS", "/phantomjs");
        osMap.put("windows", "/phantomjs.exe");
        osMap.put("unix32", "/phantomjs");
        osMap.put("unix64", "/phantomjs");
    }

    //resources needed in the phantom server
    private static final String[][] SCRIPT_SOURCES = {
            new String[]{PhantomResources + "/d3.js","d3.js"},
            new String[]{PhantomResources + "/vancharts-all.js","vancharts-all.js"},
            new String[]{PhantomResources + "/leaflet.js", "leaflet.js"}
    };

    public void start() throws IOException {
        if (ServerUtils.checkServer(IP, PORT)) {
            return;
        };

        getResources(PhantomLib, SCRIPT_SOURCES);

        String exe = ServerUtils.getExe(PhantomEnv);

        ArrayList<String> commands = new ArrayList<String>();
        commands.add(exe);
        commands.add(PhantomEnv + "/webserver.js");

        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        Process process = processBuilder.start();

        final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        final String readLine = bufferedReader.readLine();
        if(readLine == null && !readLine.contains("ready")) {
            process.destroy();
            throw new RunTimeErorException("Error, PhantomJs can't start!");
        }
    }

    private static void getResources (String libDir, String[][] libFiles) throws FileNotFoundException {
        //delete old lib with dependence resources
        File serverLib = new File(libDir);
        if(serverLib.exists() && serverLib.isDirectory()) {
            deleteDir(serverLib);
        }

        serverLib.mkdirs();

        //build new phantom server lib
        for (int i = 0; i < libFiles.length; i ++) {
            InputStream in = new FileInputStream(libFiles[i][0]);
            File file = new File(libDir + File.separator + libFiles[i][1]);
            try {
                inputStreamToFile(in, file);
            } catch (IOException e) {
                BILoggerFactory.getLogger().error(e.getMessage());
            }
        }
    }

    private static boolean deleteDir (File dir) {
        if(dir.isDirectory()) {
            String[] children = dir.list();
            //recursion to delete sub directory
            for (int i = 0; i < children.length; i++) {
                return deleteDir(new File(children[i]));
            }
        }
        return dir.delete();
    }

    private static void inputStreamToFile(InputStream in, File file) throws IOException {
        OutputStream outputStream = new FileOutputStream(file);
        int bytesRead = 0;
        byte[] buffer = new byte[BUFF_LENGTH];
        while ((bytesRead = in.read(buffer, 0, BUFF_LENGTH)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.close();
        in.close();
    }

}
