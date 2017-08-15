package com.fr.fs.mapeditor.server.service;

import com.fr.fs.fun.LoginUIProcessor;
import com.fr.fs.plugin.ExtraPlatformClassManager;
import com.fr.fs.privilege.base.DefaultLoginUIProcessor;
import com.fr.privilege.Authentication;
import com.fr.privilege.authentication.AuthenticationFactory;
import com.fr.stable.BaseConstants;
import com.fr.stable.Constants;
import com.fr.stable.bridge.StableFactory;
import com.fr.stable.fun.Service;
import com.fr.web.Browser;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class MapEditorOpenEntryService implements Service{
    private static final String TEMPLATE_PATH = "/com/fr/fs/mapeditor/server/map.html";

    private static final String UNSUPPORTED = "/com/fr/fs/mapeditor/server/unsupport.html";

    private static final String MAP_EDITOR_JS = "map.editor.js";

    private static final String MAP_EDITOR_CSS = "map.editor.css";

    public String actionOP(){
        return "map";
    }

    static {

        StableFactory.registerJavaScriptFiles(MAP_EDITOR_JS, new String[]{

                "/com/fr/fs/mapeditor/server/js/list.js",
                "/com/fr/fs/mapeditor/server/js/map.mapbox.js",
                "/com/fr/fs/mapeditor/server/js/map.lib.js",
                "/com/fr/fs/mapeditor/server/js/map.site.js",
                "/com/fr/fs/mapeditor/server/js/map.pane.js"

        });

        StableFactory.registerStyleFiles(MAP_EDITOR_CSS, new String[]{
                "/com/fr/fs/mapeditor/server/css/map.base.css",
                "/com/fr/fs/mapeditor/server/css/map.draw.css",
                "/com/fr/fs/mapeditor/server/css/map.mapbox.css",
                "/com/fr/fs/mapeditor/server/css/map.marker.css",
                "/com/fr/fs/mapeditor/server/css/map.site.css",
                "/com/fr/fs/mapeditor/server/css/leaflet.css"
        });

    }

    public void process(HttpServletRequest req, HttpServletResponse res, String op, String sessionID) throws Exception{

        // 是否是超级管理员
        Authentication authentication = AuthenticationFactory.exAuth4CommonAccess(req);
        if (authentication == null || !authentication.isRoot()) {
            LoginUIProcessor processor = ExtraPlatformClassManager.getInstance().getSingle(LoginUIProcessor.XML_TAG);
            if (processor == null) {
                processor = DefaultLoginUIProcessor.getInstance();
            }
            //跳转fs登录页
            String login_url = req.getRequestURI() + "?" + processor.loginFace();
            HttpSession session = req.getSession(true);
            session.setAttribute(Constants.ORIGINAL_URL, WebUtils.getOriginalURL(req));
            session.setAttribute("isTemplate", false);
            session.setAttribute(BaseConstants.Message.FROM, WebUtils.getOriginalURL(req));
            session.removeAttribute(Constants.P.FR_CURRENT_PRIVILEGE_LOADER);
            res.sendRedirect(login_url + "&_=" + System.currentTimeMillis());
            return;
        }


        Map<String, Object> map4Tpl = new HashMap<String, Object>();
        WebUtils.writeOutTemplate(Browser.resolve(req).isLowIEVersion() ? UNSUPPORTED : TEMPLATE_PATH, res, map4Tpl);
    }
}
