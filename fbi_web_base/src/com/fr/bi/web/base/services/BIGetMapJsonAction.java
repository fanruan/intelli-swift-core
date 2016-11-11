package com.fr.bi.web.base.services;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.FRContext;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.web.base.AbstractBIBaseAction;
import com.fr.stable.CodeUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by User on 2016/8/30.
 */
public class BIGetMapJsonAction extends AbstractBIBaseAction {
    @Override
    public String getCMD() {
        return "get_map_json";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String path = CodeUtils.cjkDecode(WebUtils.getHTTPRequestParameter(req, "file_path"));
        OutputStream os = res.getOutputStream();
        try {
            File parentFile = new File(FRContext.getCurrentEnv().getPath() + BIBaseConstant.MAP_JSON.MAP_PATH);
            File file = new File(parentFile, path);
            byte[] bytes;
            InputStream is = new FileInputStream(file);
            int length = (int) file.length();
            bytes = new byte[length];
            int offset = 0;
            int numRead;
            while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
            is.close();
            os.write(bytes);
            os.flush();
            os.close();
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }

    }
}
