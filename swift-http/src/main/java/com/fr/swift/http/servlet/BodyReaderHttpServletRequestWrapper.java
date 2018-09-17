package com.fr.swift.http.servlet;

import com.fr.stable.ArrayUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/6/14
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class BodyReaderHttpServletRequestWrapper extends
        HttpServletRequestWrapper {

    private byte[] body;

    public void setBody(byte[] body) {
        this.body = body;
    }

    public byte[] getBody() {
        return this.body;
    }

    public BodyReaderHttpServletRequestWrapper(HttpServletRequest request)
            throws IOException {
        super(request);
        int size = request.getContentLength();
        if (size > 0) {
            body = getRequestBodyStream(request, size);
        } else {
            body = new byte[0];
        }
    }

    @Override
    public int getContentLength() {
        if (ArrayUtils.isEmpty(body)) {
            return 0;
        }

        return body.length;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new WapperedInputStream();
    }

    private class WapperedInputStream extends ServletInputStream {
        final ByteArrayInputStream inputSteam = new ByteArrayInputStream(body);

        @Override
        public int read() throws IOException {
            return inputSteam.read();
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }
    }

    private byte[] getRequestBodyStream(HttpServletRequest request, int size) throws IOException {
        List<Byte> dateList = new ArrayList<Byte>();

//        int readBytes = 0;
//        while (true) {
        byte[] data = new byte[size];
        int read = request.getInputStream().read(data, 0, size);
        for (int i = 0; i < read; i++) {
            if (data != null) {
                dateList.add(data[i]);
            }
        }
//            if (read == -1) {
//                break;
//            }
//            readBytes += read;
//        }
        byte[] bytes = new byte[dateList.size()];
        for (int i = 0; i < dateList.size(); i++) {
            bytes[i] = dateList.get(i).byteValue();
        }
        return bytes;
    }
}
