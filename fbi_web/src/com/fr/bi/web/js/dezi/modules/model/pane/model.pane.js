BIDezi.PaneModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(BIDezi.PaneModel.superclass._defaultConfig.apply(this), {
            layoutType: 0,
            widgets: {}
        });
    },

    _static: function () {
        var self = this;
        return {
            getOperatorIndex: function(){
                return self.operatorIndex;
            },
            isUndoRedoSet: function(){
                return self.isUndoRedoSet;
            },
            setUndoRedoSet: function(v) {
                self.isUndoRedoSet = v;
            }
        }
    },

    _init: function () {
        BIDezi.PaneModel.superclass._init.apply(this, arguments);
        this.operatorIndex = 0;
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
        if(this.has("undo")) {
            this.get("undo");
            this._undoRedoOperator(true);
            return true;
        }
        if(this.has("redo")) {
            this.get("redo");
            this._undoRedoOperator(false);
            return true;
        }
        return false;
    },

    _undoRedoOperator: function(isUndo){
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

    similar: function (ob, key) {
        if (key === "widgets") {
            var obj = {};
            obj.type = ob.type;
            obj.name = this._generateWidgetName(ob.name);
            obj.dimensions = ob.dimensions;
            obj.view = ob.view;
            obj.bounds = {
                height: ob.bounds.height,
                width: ob.bounds.width,
                left: ob.bounds.left + 15,
                top: ob.bounds.top + 15
            };
            obj.settings = ob.settings;
            obj.value = ob.value;
            return obj;
        }
    },

    duplicate: function (copy, key1, key2) {
        this.refresh();
    },

    change: function (changed) {
        if(this.isUndoRedoSet === true) {
            return;
        }
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

        //用于undo redo
        var records = Data.SharingPool.get("records") || [];
        records.splice(this.operatorIndex + 1);
        records.push({
            dimensions: dims,
            widgets: widgets,
            layoutType: this.get("layoutType")
        });
        Data.SharingPool.put("records", records);
        this.operatorIndex = records.length - 1;

    }
});