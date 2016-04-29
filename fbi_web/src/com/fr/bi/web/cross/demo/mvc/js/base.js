FR.BIDezi = {};
Data.SharingPool.put("_createby", -999);
Data.SharingPool.put("sessionID", sessionID);

if (popConfig) {
    var widgets = popConfig.widgets;
    var _dims = {};
    BI.each(popConfig.widgets, function (id, widget) {
        BI.extend(_dims, widget.dimensions);
    });
    Data.SharingPool.put("dimensions", _dims);
    Data.SharingPool.put("widgets", popConfig.widgets);
}
BI.Cache.setUsername(Data.SharingPool.get("_createby"));