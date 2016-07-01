package com.fr.bi.web.base;

import com.fr.base.FRContext;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.fun.ErrorHandler;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JSONErrorHandler implements ErrorHandler {

    @Override
    public void error(HttpServletRequest req, HttpServletResponse res,
                      Throwable throwable) {
        this.error(req, res, throwable.getMessage());
    }

    @Override
    public void error(HttpServletRequest req, HttpServletResponse res,
                      String message) {
        try {
            WebUtils.printAsJSON(res, new JSONObject().put("__ERROR_MESSAGE__", message));
        } catch (JSONException e) {
            FRContext.getLogger().errorWithServerLevel(e.getMessage(), e);
        } catch (Exception e) {
            FRContext.getLogger().errorWithServerLevel(e.getMessage(), e);
        }
    }

    @Override
    public int currentAPILevel() {
        return CURRENT_LEVEL;
    }

    @Override
    public int layerIndex() {
        return 2;
    }
}
