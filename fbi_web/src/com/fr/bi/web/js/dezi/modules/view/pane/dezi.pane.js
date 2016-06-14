/**
 * @class BIDezi.PaneView
 * @extends BI.View
 * @type {*|void|Object}
 */
BIDezi.PaneView = BI.inherit(BI.View, {
    _const: {
        tabHeight: 30,
        toolbarHeight: 30,
        toolbarWidth: 90
    },

    _defaultConfig: function () {
        return BI.extend(BIDezi.PaneView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-pane-view"
        })
    },

    _init: function () {
        BIDezi.PaneView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var north = this._createNorth();
        this.dashboard = this._createDashBoard();
        BI.createWidget({
            type: "bi.vtape",
            element: vessel,
            items: [{
                el: north,
                height: this._const.toolbarHeight
            }, {
                el: BI.createWidget(),
                height: 1
            }, {
                el: this.dashboard
            }]
        })
    },

    splice: function (old, key1, key2) {
        var self = this;
        if (key1 === "widgets") {
            this.dashboard.deleteRegion(key2);
        }
        if (BI.Utils.isControlWidgetByWidgetType(old.type)) {
            BI.Utils.broadcastAllWidgets2Refresh();
        }
        this._refreshButtons();
    },

    duplicate: function (copy, key1, key2) {
        if (key1 === "widgets") {
            this.dashboard.copyRegion(key2, copy);
            this._refreshWidgets();
        }
        this._refreshButtons();
    },

    local: function () {
        var self = this;
        if (this.model.has("dashboard")) {
            var dashboard = this.model.get("dashboard");
            //不刷新子组件
            this._refreshWidgets(false);
            return true;
        }
        if (this.model.has("addWidget")) {
            var w = this.model.get("addWidget");
            this._refreshWidgets();
            BI.nextTick(function () {
                var widgets = self.cat("widgets");
                self.skipTo(w.id + "/" + widgets[w.id].type, w.id, {
                    expand: true
                });
            });
            return true;
        }
        if (this.model.has("undo")) {
            this.model.get("undo");
            this.refresh();
            return true;
        }
        if (this.model.has("redo")) {
            this.model.get("redo");
            this.refresh();
            return true;
        }
        return false;
    },

    _refreshWidgets: function (refresh) {
        var self = this;
        BI.each(this.cat("widgets"), function (id, widget) {
            var type = widget.type;
            self.skipTo(id + "/" + type, id, "widgets." + id, {}, {
                force: refresh
            });
        });
    },

    change: function (changed) {
        this._refreshButtons();
        if (this.model.get("isUndoRedoSet")) {
            this.refresh();
            this.model.get("setUndoRedoSet", false);
        }
    },

    _createNorth: function () {
        var self = this;

        this.undoButton = BI.createWidget({
            type: "bi.icon_text_item",
            cls: "toolbar-undo-font",
            text: BI.i18nText("BI-Undo"),
            height: 30,
            width: 60
        });
        this.undoButton.on(BI.IconTextIconItem.EVENT_CHANGE, function () {
            self.model.set("undo", true);
        });
        this.undoButton.setEnable(false);
        this.redoButton = BI.createWidget({
            type: "bi.icon_text_item",
            cls: "toolbar-redo-font",
            text: BI.i18nText("BI-Redo"),
            height: 30,
            width: 60
        });
        this.redoButton.setEnable(false);
        this.redoButton.on(BI.IconTextIconItem.EVENT_CHANGE, function () {
            self.model.set("redo", true);
        });
        var viewChange = BI.createWidget({
            type: "bi.icon_text_item",
            cls: "toolbar-preview-font",
            text: BI.i18nText("BI-Preview_Report"),
            height: 30,
            width: 80
        });
        viewChange.on(BI.IconTextIconItem.EVENT_CHANGE, function () {
            var reportId = Data.SharingPool.get("reportId");
            var createBy = Data.SharingPool.get("createBy");
            window.location.href = FR.servletURL + "?op=fr_bi&cmd=bi_init&id=" + reportId + "&createBy=" + createBy;
        });
        return BI.createWidget({
            type: "bi.absolute",
            cls: "dashboard-toolbar",
            items: [{
                el: this.undoButton,
                top: 0,
                left: 110
            }, {
                el: this.redoButton,
                top: 0,
                left: 190
            }, {
                el: viewChange,
                top: 0,
                left: 270
            }]
        })
    },

    _refreshButtons: function () {
        var operatorIndex = this.model.get("getOperatorIndex");
        var records = Data.SharingPool.get("records") || [];
        //模拟一下change的时候发生的事（坑爹的回调里做的事，没办法这边实时拿到）
        if (!this.model.get("isUndoRedoSet")) {
            records.splice(operatorIndex + 1);
            records.push({});
            operatorIndex = records.length - 1;
        }
        var recordsSize = records.length;
        if (operatorIndex === recordsSize - 1) {
            this.undoButton.setEnable(true);
            this.redoButton.setEnable(false);
        }
        if (operatorIndex < recordsSize - 1) {
            this.undoButton.setEnable(true);
            this.redoButton.setEnable(true);
        }
        if (operatorIndex === 0) {
            this.undoButton.setEnable(false);
        }
    },


    _createDashBoard: function () {
        var self = this;
        var widgetVessel = {};
        this.dashboard = BI.createWidget({
            type: "bi.fit",
            layoutType: this.model.get("layoutType"),
            widgetCreator: function (id, info) {
                if (!widgetVessel[id]) {
                    widgetVessel[id] = BI.createWidget();
                    self.addSubVessel(id, widgetVessel[id]);
                    var widgets = self.model.cat("widgets");
                    if (!BI.has(widgets, id)) {
                        self.model.set("addWidget", {
                            id: id,
                            info: info
                        });
                    }
                }
                return widgetVessel[id];
            }
        });
        this.dashboard.on(BI.Fit.EVENT_CHANGE, function () {
            var value = this.getValue();
            self.set("dashboard", {
                layoutType: value.layoutType,
                regions: value.regions
            })
        });

        return this.dashboard;
    },

    refresh: function () {
        this._refreshButtons();
        this.dashboard.populate();
        this._refreshWidgets();
    }
});
