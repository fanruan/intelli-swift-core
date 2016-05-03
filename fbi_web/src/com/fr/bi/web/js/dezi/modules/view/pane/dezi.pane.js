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
        var south = this._createSouth();
        BI.createWidget({
            type: "bi.vtape",
            element: vessel,
            items: [{
                el: north,
                height: this._const.toolbarHeight
            }, {
                el: this.dashboard
            }, {
                el: south,
                height: this._const.tabHeight
            }]
        })
    },

    splice: function (old, key1, key2) {
        var self = this;
        if (key1 === "widgets") {
            this.dashboard.deleteRegion(key2);
        }
    },

    duplicate: function (copy, key1, key2) {
        if (key1 === "widgets") {
            this.dashboard.populate();
            this._refreshWidgets();
        }
    },

    local: function () {
        if (this.model.has("dashboard")) {
            var dashboard = this.model.get("dashboard");
            this._refreshWidgets();
            return true;
        }
        return false;
    },

    _refreshWidgets: function () {
        var self = this;
        BI.each(this.cat("widgets"), function (id, widget) {
            var type = widget.type;
            self.skipTo(id + "/" + type, id, "widgets." + id);
        });
    },

    change: function (changed) {

    },

    _createNorth: function () {
        return BI.createWidget({
            type: "bi.layout"
        })
    },


    _createDashBoard: function () {
        var self = this;
        var widgetVessel = {};
        this.dashboard = BI.createWidget({
            type: "bi.fit",
            layoutStyle: this.model.get("layoutStyle"),
            widgetCreator: function (id, info) {
                if (!widgetVessel[id]) {
                    widgetVessel[id] = BI.createWidget();
                    var widgets = self.model.cat("widgets");
                    if (!BI.has(widgets, id)) {
                        self.model.set("addWidget", {
                            id: id,
                            info: info
                        });
                    }
                    self.addSubVessel(id, widgetVessel[id]);
                }
                return widgetVessel[id];
            }
        });
        this.dashboard.on(BI.Fit.EVENT_CHANGE, function () {
            var value = this.getValue();
            self.set("dashboard", {
                layoutStyle: value.layoutStyle,
                regions: value.regions
            })
        });

        return this.dashboard;
    },

    _createSouth: function () {
        var widget = BI.createWidget({
            type: "bi.layout",
            cls: "dashboard-south"
        });
        return widget;
    },

    refresh: function () {
        var self = this;
        this.dashboard.populate();
        this._refreshWidgets();
    }
});
