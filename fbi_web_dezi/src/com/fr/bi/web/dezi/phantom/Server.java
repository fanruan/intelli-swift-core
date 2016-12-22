package com.fr.bi.web.dezi.phantom;

import com.fr.base.FRContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by AstronautOO7 on 2016/12/21.
 */
public class Server {
    public void start() throws IOException {
        String path = FRContext.getCurrentEnv().getPath();

        ArrayList<String> commands = new ArrayList<String>();
        commands.add(path + File.separator + "resources/phantom/phantomjs.exe");
        commands.add(path + File.separator + "resources/phantom/webserver.js");

        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        Process process = processBuilder.start();

        final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));

        System.out.println(bufferedReader.readLine());
    }
}
