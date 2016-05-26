BIDezi.WidgetModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.WidgetModel.superclass._defaultConfig.apply(this), {
            name: "",
            bounds: {},
            linkages: [],
            type: BICst.WIDGET.TABLE,
            dimensions: {},
            view: {}
        })
    },

    change: function (changed, pre) {
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
            this.refresh();
            this.set("linkages", linkages);
        }
        if (BI.has(changed, "linkages")) {
            //找到所有被删除掉的linkages，通知到相关的组件
            BI.each(pre.linkages, function (i, preLink) {
                var found = BI.some(changed.linkages, function (j, link) {
                    if (link.from === preLink.from && link.to === preLink.to) {
                        return true;
                    }
                });
                if (found === false) {
                    BI.Broadcasts.send(BICst.BROADCAST.LINKAGE_PREFIX + preLink.to, preLink.from);
                }
            });
        }
        if (BI.has(changed, "filter_value")) {
            this.refresh();
        }
    },

    refresh: function () {
        this.tmp({
            detail: {
                name: this.get("name"),
                dimensions: this.get("dimensions"),
                view: this.get("view"),
                type: this.get("type"),
                settings: this.get("settings"),
                filter_value: this.get("filter_value")
            }
        }, {
            silent: true
        });
    },

    local: function () {
        return false;
    },

    _init: function () {
        BIDezi.WidgetModel.superclass._init.apply(this, arguments);
    }
});