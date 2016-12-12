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

    _createNorth: function () {
        var self = this;

        var globalExport = this._createGlobalExport();

        var zclip = BI.createWidget({
            type: "bi.copy_link_item"
        });
        //undo
        this.undoButton = BI.createWidget({
            type: "bi.icon_text_item",
            cls: "toolbar-undo-font",
            text: BI.i18nText("BI-Undo"),
            height: 30,
            width: 60
        });
        this.undoButton.setEnable(false);
        this.undoButton.on(BI.IconTextIconItem.EVENT_CHANGE, function () {
            self.model.set("undo", true);
        });

        //redo
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

        //预览
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

        //全局样式
        this.globalStyle = BI.createWidget({
            type: "bi.global_style"
        });
        this.globalStyle.on(BI.GlobalStyle.EVENT_SET, function (v) {
            self.model.set("globalStyle", v);
            BI.Broadcasts.send(BICst.BROADCAST.GLOBAL_STYLE_PREFIX, v);
        });

        return BI.createWidget({
            type: "bi.absolute",
            invisible: !!Data.SharingPool.get("hideTop"),
            cls: "dashboard-toolbar",
            items: [{
                el: globalExport,
                top: 0,
                left: 110,
            }, {
                el: zclip,
                top: 0,
                left: 210
            }, {
                el: this.undoButton,
                top: 0,
                left: 310
            }, {
                el: this.redoButton,
                top: 0,
                left: 390
            }, {
                el: viewChange,
                top: 0,
                left: 470
            }, {
                el: this.globalStyle,
                top: 0,
                left: 570
            }]
        })
    },

    _createGlobalExport: function () {
        var trigger = BI.createWidget({
            type: "bi.icon_text_icon_item",
            iconCls1: "toolbar-global-export",
            iconCls2: "pull-down-font",
            text: BI.i18nText("BI-Export"),
            readonly: true,
            height: 30,
            width: 80,
        });

        return BI.createWidget({
            type: "bi.static_combo",
            el: trigger,
            textAlign: 'center',
            items: [{
                text: BI.i18nText("BI-Excel_Export"),
                value: BICst.EXPORT.EXCEL,
            }, {
                text: BI.i18nText("BI-PDF_Export"),
                value: BICst.EXPORT.PDF,
            }]
        })
    },

    _createDashBoard: function () {
        var self = this;
        var widgetVessel = {};
        var dashboard = BI.createWidget({
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
        dashboard.on(BI.Fit.EVENT_CHANGE, function () {
            var value = this.getValue();
            self.set("dashboard", value)
        });

        return dashboard;
    },

    _render: function (vessel) {
        var north = this._createNorth();
        this.dashboard = this._createDashBoard();
        BI.createWidget({
            type: "bi.vtape",
            element: vessel,
            items: [{
                el: north,
                height: Data.SharingPool.get("hideTop") ? 0 : this._const.toolbarHeight
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
                var type = self.model.get("childType", widgets[w.id].type);
                self.skipTo(w.id + "/" + type, w.id, {
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
        if (this.model.has("undoRedoSet")) {
            this.model.get("undoRedoSet");
            return true;
        }
        return false;
    },

    change: function (changed) {
        this._refreshButtons();
        if (this.model.get("undoRedoSet")) {
            this.refresh();
            this.model.set("undoRedoSet", false);
        }
    },

    _refreshWidgets: function (refresh) {
        var self = this;
        BI.each(this.cat("widgets"), function (id, widget) {
            var type = self.model.get("childType", widget.type);
            self.skipTo(id + "/" + type, id, "widgets." + id, {}, {
                force: refresh
            });
        });
    },

    _refreshButtons: function () {
        var operatorIndex = this.model.get("operatorIndex");
        var records = Data.SharingPool.cat("records") || new BI.Queue(10);
        //模拟一下change的时候发生的事（坑爹的回调里做的事，没办法这边实时拿到）
        //避免改对象
        var imitationRecords = BI.makeArray(records.size(), "");
        if (!this.model.get("undoRedoSet")) {
            imitationRecords.splice(operatorIndex + 1);
            imitationRecords.push("");
            operatorIndex = imitationRecords.length - 1;
        }
        var recordsSize = imitationRecords.length;
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

    refresh: function () {
        this.globalStyle.populate();
        this._refreshButtons();
        this.dashboard.populate();
        this._refreshWidgets();
    }
});
