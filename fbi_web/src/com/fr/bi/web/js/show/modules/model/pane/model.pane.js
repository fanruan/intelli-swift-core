BIShow.PaneModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIShow.PaneModel.superclass._defaultConfig.apply(this), {
            layoutType: 0,
            widgets: {}
        });
    },

    _static: function () {
        return {}
    },

    _init: function () {
        BIShow.PaneModel.superclass._init.apply(this, arguments);
    },

    _generateWidgetName: function () {
        return BI.Func.createDistinctName(this.cat("widgets"), BI.i18nText("BI-Statistical_Component"));
    },


    local: function () {
        var self = this;
        if (this.has("dashboard")) {
            var dashboard = this.get("dashboard");
            var widgets = this.get("widgets");
            var layoutType = dashboard.layoutType;
            var widgetTypes = dashboard.widgetTypes;
            var regions = dashboard.regions;
            BI.each(regions, function (i, region) {
                if (BI.isNotNull(widgets[region.id])) {
                    widgets[region.id].bounds = {
                        left: region.left,
                        top: region.top,
                        width: region.width,
                        height: region.height
                    }
                } else {
                    widgets[region.id] = {
                        bounds: {
                            left: region.left,
                            top: region.top,
                            width: region.width,
                            height: region.height
                        },
                        name: self._generateWidgetName(),
                        type: widgetTypes[region.id]
                    }
                }
            });
            this.set({"widgets": widgets, layoutType: layoutType});
            return true;
        }
        return false;
    },

    splice: function (old, key1, key2) {
        if (key1 === "widgets") {
            var widgets = this.get("widgets");
            var wids = BI.keys(widgets);
            BI.each(widgets, function (i, widget) {
                BI.remove(widget.linkages, function (j, linkage) {
                    return !wids.contains(linkage.to);
                });
            });
            this.set("widgets", widgets);
        }
        this.refresh();
    },

    similar: function (ob, key) {
        if (key === "widgets") {
            ob.name = this._generateWidgetName();
            ob.linkages = [];
            ob.bounds = {
                height: ob.bounds.height,
                width: ob.bounds.width,
                left: ob.bounds.left + 15,
                top: ob.bounds.top + 15
            };
            return ob;
        }
    },

    duplicate: function (copy, key1, key2) {
        this.refresh();
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
