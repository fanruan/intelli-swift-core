package com.fr.bi.web.conf.services.cubetask;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.FRContext;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.data.NetworkHelper;
import com.fr.web.core.utils.ExportUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created by roy on 2016/11/7.
 */
public class BIDownloadFineindexLogAction extends AbstractBIConfigureAction {

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        File logFile = new File(new File((FRContext.getCurrentEnv()).getWebReportPath()).getParentFile().getParentFile(), File.separator + "records" + File.separator + "FineIndex.log");
        NetworkHelper.setCacheSettings(res);
        ExportUtils.setTextContext(res, "FineIndex");
        InputStream is = new FileInputStream(logFile);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        OutputStream os = res.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        try {
            String readLineText;
            while ((readLineText = br.readLine()) != null) {
                osw.write(readLineText);
                osw.write("\r\n");
            }
            osw.flush();
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        } finally {
            is.close();
            isr.close();
            br.close();
            os.close();
            osw.close();
        }


    }

    @Override
    public String getCMD() {
        return "download_fineindex_log";
    }


}
