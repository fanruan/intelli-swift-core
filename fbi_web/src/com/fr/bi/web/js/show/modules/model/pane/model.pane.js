BIShow.PaneModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIShow.PaneModel.superclass._defaultConfig.apply(this), {
            layoutType: 0,
            widgets: {}
        });
    },

    _init: function () {
        BIShow.PaneModel.superclass._init.apply(this, arguments);
    },

    local: function () {
        return false;
    },

    change: function (changed) {
        this.refresh();
    },

    refresh: function () {
        var widgets = this.cat("widgets");
        var dims = {};
        BI.each(widgets, function (id, widget) {
            BI.extend(dims, widget.dimensions);
        });
        Data.SharingPool.put("dimensions", dims);
        Data.SharingPool.put("widgets", widgets);
        Data.SharingPool.put("layoutType", this.get("layoutType"));
    }
});
