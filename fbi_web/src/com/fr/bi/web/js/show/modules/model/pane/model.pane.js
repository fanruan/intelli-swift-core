BIShow.PaneModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIShow.PaneModel.superclass._defaultConfig.apply(this), {
            layoutType: BI.Arrangement.LAYOUT_TYPE.FREE,
            layoutRatio: {},
            widgets: {}
        });
    },

    _init: function () {
        BIShow.PaneModel.superclass._init.apply(this, arguments);
        //放一份原始数据 非必要情况不要改里面的值
        Data.SharingPool.put("original_show_widgets", this.cat("widgets"));
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
