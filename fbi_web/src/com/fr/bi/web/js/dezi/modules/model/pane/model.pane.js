BIDezi.PaneModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.PaneModel.superclass._defaultConfig.apply(this), {
            layoutType: BI.Arrangement.LAYOUT_TYPE.FREE,
            widgets: {}
        });
    },

    _static: function () {
        var self = this;
        return {
            getOperatorIndex: function () {
                return self.operatorIndex;
            },
            isUndoRedoSet: function () {
                return self.isUndoRedoSet;
            },
            setUndoRedoSet: function (v) {
                self.isUndoRedoSet = v;
            }
        }
    },

    _init: function () {
        BIDezi.PaneModel.superclass._init.apply(this, arguments);
        var self = this;
        this.operatorIndex = 0;
        this.saveDebounce = BI.debounce(function (widgets, dims, layoutType) {
            var records = Data.SharingPool.get("records") || [];
            records.splice(self.operatorIndex + 1);
            records.push({
                dimensions: dims,
                widgets: widgets,
                layoutType: layoutType
            });
            Data.SharingPool.put("records", records);
            self.operatorIndex = records.length - 1;
        }, 100);
    },

    _generateWidgetName: function (widgetName) {
        return BI.Func.createDistinctName(this.cat("widgets"), widgetName);
    },

    local: function () {
        var self = this;
        if (this.has("dashboard")) {
            var dashboard = this.get("dashboard");
            var widgets = this.get("widgets");
            var layoutType = dashboard.layoutType;
            var regions = dashboard.regions;
            BI.each(regions, function (i, region) {
                if (BI.isNotNull(widgets[region.id])) {
                    widgets[region.id].bounds = {
                        left: region.left,
                        top: region.top,
                        width: region.width,
                        height: region.height
                    }
                }
            });
            this.set({"widgets": widgets, layoutType: layoutType});
            return true;
        }
        if (this.has("addWidget")) {
            var widget = this.get("addWidget");
            var widgets = this.get("widgets");
            var wId = widget.id;
            var info = widget.info;
            if (!widgets[wId]) {
                widgets[wId] = info;
                widgets[wId].name = self._generateWidgetName(widgets[wId].name);
                widgets[wId].init_time = new Date().getTime();
            }
            this.set({"widgets": widgets});
            return true;
        }
        if (this.has("undo")) {
            this.get("undo");
            this._undoRedoOperator(true);
            return true;
        }
        if (this.has("redo")) {
            this.get("redo");
            this._undoRedoOperator(false);
            return true;
        }
        return false;
    },

    _undoRedoOperator: function (isUndo) {
        isUndo === true ? this.operatorIndex-- : this.operatorIndex++;
        var ob = Data.SharingPool.get("records")[this.operatorIndex];
        this.isUndoRedoSet = true;
        Data.SharingPool.put("dimensions", ob.dimensions);
        Data.SharingPool.put("widgets", ob.widgets);
        Data.SharingPool.put("layoutType", ob.layoutType);
        this.set(ob);
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
        if (key1 === "widgets") {
            BI.Broadcasts.send(BICst.BROADCAST.WIDGETS_PREFIX + key2);
            //全局组件增删事件
            BI.Broadcasts.send(BICst.BROADCAST.WIDGETS_PREFIX);
        }
    },

    similar: function (ob, key1, key2) {
        if (key1 === "widgets") {
            return BI.Utils.getWidgetCopyByID(key2);
        }
    },

    duplicate: function (copy, key1, key2) {
        this.refresh();
        if (key1 === "widgets") {
            BI.Broadcasts.send(BICst.BROADCAST.WIDGETS_PREFIX + key2);
            //全局组件增删事件
            BI.Broadcasts.send(BICst.BROADCAST.WIDGETS_PREFIX);
        }
    },

    change: function (changed, pre) {
        if (this.isUndoRedoSet === true) {
            return;
        }
        this.refresh();
        if (BI.has(changed, "widgets")) {
            if (BI.size(changed.widgets) !== BI.size(pre.widgets)) {
                //全局组件增删事件
                BI.Broadcasts.send(BICst.BROADCAST.WIDGETS_PREFIX);
            }
        }
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

        //用于undo redo
        this.saveDebounce(widgets, dims, this.get("layoutType"));
    }
});