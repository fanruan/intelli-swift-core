package com.fr.fs.mapeditor.server.service;

import com.fr.stable.fun.Service;
import com.fr.stable.web.RequestCMDReceiver;
import com.fr.web.core.WebActionsDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MapEditorEntryService implements Service{
    RequestCMDReceiver[] actions = new RequestCMDReceiver[]{
            new MapEditorAddEntryAction(),
            new MapEditorDeleteEntryAction(),
            new MapEditorEditEntryAction(),
            new MapEditorSaveImageAction(),
            new MapEditorSaveJSONDataAction(),
            new MapEditorImportExcelDataAction(),
            new MapEditorGetJSONAction(),
            new MapEditorGetFolderAction(),
            new MapEditorAddLayerEntryAction(),
            new MapEditorDeleteLayerEntryAction(),
            new MapEditorEditLayerEntryAction(),
            new MapEditorSaveLayerEntryAction(),
            new MapEditorUpdateWMSLayersAction()};

    public MapEditorEntryService() {
    }

    public String actionOP() {
        return "map_entry";
    }

    public void process(HttpServletRequest var1, HttpServletResponse var2, String var3, String var4) throws Exception {
        WebActionsDispatcher.dealForActionCMD(var1, var2, var4, this.actions);
    }
}
