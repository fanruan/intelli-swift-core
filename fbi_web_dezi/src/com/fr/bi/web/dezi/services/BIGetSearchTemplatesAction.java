package com.fr.bi.web.dezi.services;

import com.fr.bi.stable.utils.CubeBaseUtils;
import com.fr.bi.web.dezi.AbstractBIDeziAction;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.pinyin.PinyinHelper;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * create by guy
 * 14/9
 */
public class BIGetSearchTemplatesAction extends AbstractBIDeziAction {

    @Override
    public String getCMD() {
        return "get_search_templates";
    }

    /**
     * action方法
     *
     * @param req       参数1
     * @param res       参数2
     * @param sessionID 当前session
     * @throws Exception
     */
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res,
                          String sessionID) throws Exception {
        String key = WebUtils.getHTTPRequestParameter(req, "key").toUpperCase();
        String templateString = WebUtils.getHTTPRequestParameter(req, "templates");
        JSONArray templates = new JSONArray(templateString);
        JSONObject matched = new JSONObject();
        JSONObject equaled = new JSONObject();
        JSONObject result = new JSONObject();

        run(result, matched, equaled, templates, key);

        WebUtils.printAsJSON(res, result);
    }

    private void run(JSONObject result, JSONObject matched, JSONObject equaled, JSONArray templates, String key) throws JSONException {

        JSONArray matchedTemplate = new JSONArray();
        JSONArray matchedWidget = new JSONArray();
        JSONArray matchedTarget = new JSONArray();

        JSONArray equaledTemplate = new JSONArray();
        JSONArray equaledWidget = new JSONArray();
        JSONArray equaledTarget = new JSONArray();

        List threadList = new ArrayList();
        for (int i = 0; i < templates.length(); i++) {
            JSONObject template = templates.getJSONObject(i);
            threadList.add(new TemplateSearch(template, key, matchedTemplate, matchedWidget, matchedTarget, equaledTemplate, equaledWidget, equaledTarget));
        }
        try {
            CubeBaseUtils.invokeCalculatorThreads(threadList);
        } catch (InterruptedException e) {
            System.out.println("search template error");
        }
        matched.put("templates", matchedTemplate)
                .put("widgets", matchedWidget)
                .put("targets", matchedTarget);

        equaled.put("templates", equaledTemplate)
                .put("widgets", equaledWidget)
                .put("targets", equaledTarget);

        result.put("equaled", equaled)
                .put("matched", matched);
    }

    private void dealWithTemplate(JSONObject template, JSONArray matchedWidget, JSONArray matchedTarget, JSONArray equaledWidget, JSONArray equaledTarget, String key) throws JSONException {
        JSONObject result = new JSONObject();
        JSONArray resControl = new JSONArray();
        JSONArray resWidget = new JSONArray();
        JSONArray resDetail = new JSONArray();

        JSONObject targetResult = new JSONObject();
        JSONArray targetResultWidget = new JSONArray();

        if (template.has("control")) {
            JSONArray controls = template.getJSONArray("control");
            for (int i = 0; i < controls.length(); i++) {
                JSONObject control = controls.getJSONObject(i);
                String name = control.getString("name");
                String py = PinyinHelper.getShortPinyin(name);
                control.put("py", py);
                if (name.toUpperCase().indexOf(key) >= 0 || py.toUpperCase().indexOf(key) >= 0) {
                    resControl.put(control);
                }
                if (ComparatorUtils.equals(name.toUpperCase(), key) || ComparatorUtils.equals(py.toUpperCase(), key)) {
                    equaledWidget.put(control);
                }
                //dealWithWidget(control,matchedTarget,equaledTarget,key);
            }
        }
        if (template.has("widget")) {

            JSONArray widgets = template.getJSONArray("widget");
            for (int i = 0; i < widgets.length(); i++) {

                JSONObject widget = widgets.getJSONObject(i);
                JSONObject targetResultTarget = new JSONObject();
                String name = widget.getString("name");
                String py = PinyinHelper.getShortPinyin(name);
                widget.put("py", py);
                if (name.toUpperCase().indexOf(key) >= 0 || py.toUpperCase().indexOf(key) >= 0) {
                    resWidget.put(widget);
                }
                if (ComparatorUtils.equals(name.toUpperCase(), key) || ComparatorUtils.equals(py.toUpperCase(), key)) {
                    equaledWidget.put(widget);
                }
                dealWithWidget(widget, targetResultTarget, equaledTarget, key);
                targetResultWidget.put(targetResultTarget);
            }
        }
        if (template.has("detail")) {
            JSONArray details = template.getJSONArray("detail");
            for (int i = 0; i < details.length(); i++) {
                JSONObject detail = details.getJSONObject(i);
                String name = detail.getString("name");
                String py = PinyinHelper.getShortPinyin(name);
                detail.put("py", py);
                if (name.toUpperCase().indexOf(key) >= 0 || py.toUpperCase().indexOf(key) >= 0) {
                    resDetail.put(detail);
                }
                if (ComparatorUtils.equals(name.toUpperCase(), key) || ComparatorUtils.equals(py.toUpperCase(), key)) {
                    equaledWidget.put(detail);
                }
                //dealWithWidget(detail,matchedTarget,equaledTarget,key);
            }
        }
        result.put("control", resControl).put("widget", resWidget).put("detail", resDetail)
                .put("name", template.get("name")).put("py", template.get("py"));
        matchedWidget.put(result);

        targetResult.put("widget", targetResultWidget)
                .put("name", template.get("name")).put("py", template.get("py"));
        ;

        matchedTarget.put(targetResult);
    }

    private JSONArray getAllFields(JSONObject widget, JSONObject except) throws JSONException {
        JSONArray result = new JSONArray();

        if (widget.has("targets")) {
            JSONArray targets = widget.getJSONArray("targets");
            for (int i = 0; i < targets.length(); i++) {
                JSONObject target = targets.getJSONObject(i);
                if (!ComparatorUtils.equals(target.get("name"), except.get("name"))) {
                    result.put(targets.getJSONObject(i));
                }

            }
        }
        return result;
    }

    private void dealWithWidget(JSONObject widget, JSONObject resultTarget, JSONArray equaledTarget, String key) throws JSONException {
        JSONArray matchedDimension = new JSONArray();
        JSONArray matchedTarget = new JSONArray();

        if (widget.has("dimensions")) {
            JSONArray dimensions = widget.getJSONArray("dimensions");
            for (int i = 0; i < dimensions.length(); i++) {
                JSONObject dimension = dimensions.getJSONObject(i);
                String name = dimension.getString("name");
                String py = PinyinHelper.getShortPinyin(name);
                dimension.put("py", py);
                if (name.toUpperCase().indexOf(key) >= 0 || py.toUpperCase().indexOf(key) >= 0) {
                    matchedDimension.put(dimension);
                }
                if (ComparatorUtils.equals(name.toUpperCase(), key) || ComparatorUtils.equals(py.toUpperCase(), key)) {

                    equaledTarget.put(dimension);
                }
            }
        }
        if (widget.has("targets")) {
            JSONArray targets = widget.getJSONArray("targets");
            for (int i = 0; i < targets.length(); i++) {
                JSONObject target = targets.getJSONObject(i);
                String name = target.getString("name");
                String py = PinyinHelper.getShortPinyin(name);
                target.put("py", py);
                if (name.toUpperCase().indexOf(key) >= 0 || py.toUpperCase().indexOf(key) >= 0) {
                    matchedTarget.put(target);
                }
                if (ComparatorUtils.equals(name.toUpperCase(), key) || ComparatorUtils.equals(py.toUpperCase(), key)) {
                    JSONArray all_fields = getAllFields(widget, target);
                    target.put("all_fields", all_fields);
                    equaledTarget.put(target);
                }
            }
        }
        resultTarget.put("dimensions", matchedDimension).put("targets", matchedTarget)
                .put("name", widget.get("name")).put("py", widget.get("py")).put("view", widget.get("view"));
    }

    private class TemplateSearch implements java.util.concurrent.Callable {
        JSONArray matchedTemplate;
        JSONArray matchedWidget;
        JSONArray matchedTarget;

        JSONArray equaledTemplate;
        JSONArray equaledWidget;
        JSONArray equaledTarget;

        JSONObject template;//要搜的模板
        String key;

        public TemplateSearch(JSONObject template,
                              String key,
                              JSONArray matchedTemplate,
                              JSONArray matchedWidget,
                              JSONArray matchedTarget,
                              JSONArray equaledTemplate,
                              JSONArray equaledWidget,
                              JSONArray equaledTarget
        ) {
            this.template = template;
            this.key = key;
            this.matchedTemplate = matchedTemplate;
            this.matchedWidget = matchedWidget;
            this.matchedTarget = matchedTarget;
            this.equaledTemplate = equaledTemplate;
            this.equaledWidget = equaledWidget;
            this.equaledTarget = equaledTarget;
        }

        @Override
        public Object call() throws Exception {
            String name = template.getString("name");
            String py = PinyinHelper.getShortPinyin(name);
            template.put("py", py);
            if (name.toUpperCase().indexOf(key) >= 0 || py.toUpperCase().indexOf(key) >= 0) {
                matchedTemplate.put(template);
            }
            if (ComparatorUtils.equals(name.toUpperCase(), key) || ComparatorUtils.equals(py.toUpperCase(), key)) {
                equaledTemplate.put(template);
            }
            dealWithTemplate(template, matchedWidget, matchedTarget, equaledWidget, equaledTarget, key);
            return null;
        }
    }
}