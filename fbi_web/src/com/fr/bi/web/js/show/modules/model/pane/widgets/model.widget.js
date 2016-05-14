BIShow.WidgetModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIShow.WidgetModel.superclass._defaultConfig.apply(this), {
            name: "",
            bounds: {},
            linkages: [],
            type: BICst.WIDGET.TABLE,
            dimensions: {},
            view: {},
            settings: BICst.DEFAULT_CHART_SETTING
        })
    },

    _static: function () {

    },

    change: function (changed) {
        if (BI.has(changed, "detail")) {
            this.set(this.get("detail"));
        }
        //维度或指标改变时需要调节联动设置
        if (BI.has(changed, "dimensions")) {
            var dimensions = this.cat("dimensions");
            var dids = BI.keys(dimensions);
            var linkages = this.get("linkages");
            BI.remove(linkages, function (i, linkage) {
                return !dids.contains(linkage.from);
            });
            this.set("linkages", linkages);
        }
    },

    refresh: function () {
        this.tmp({
            detail: {
                name: this.get("name"),
                dimensions: this.get("dimensions"),
                view: this.get("view"),
                type: this.get("type"),
                settings: this.get("settings")
            }
        }, {
            silent: true
        });
    },

    local: function () {
        return false;
    },

    _init: function () {
        BIShow.WidgetModel.superclass._init.apply(this, arguments);
    }
});
