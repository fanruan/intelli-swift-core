/**
 *
 */
package com.fr.bi.cal.report;

import com.fr.bi.stable.utils.file.BIPictureUtils;
import com.fr.page.ReportPageProvider;
import com.fr.web.core.ReportPageProviderAction;


public class FBIScreenCaptureReportPageProviderAction implements
        ReportPageProviderAction {

    @Override
    public void actionDealWith(final String bookPath, final int pageNumber, ReportPageProvider reportPage) {
        BIPictureUtils.savePageScreenCapture(bookPath, pageNumber, reportPage);
    }

}