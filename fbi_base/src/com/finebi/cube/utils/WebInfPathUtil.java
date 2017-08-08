package com.finebi.cube.utils;

import com.fr.stable.project.ProjectConstants;

/**
 * Created by Lucifer on 2017-6-23.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class WebInfPathUtil {

    public static String getWebInfUrl(Class pathClass) {
        String url = pathClass.getClassLoader().getResource("").getFile();
        String[] urls = url.split(ProjectConstants.WEBINF_NAME);
        String webInfUrl = urls[0] + ProjectConstants.WEBINF_NAME;
        return webInfUrl;
    }
}
