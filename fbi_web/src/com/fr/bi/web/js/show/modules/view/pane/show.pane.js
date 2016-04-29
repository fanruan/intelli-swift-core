/**
 * @class BIShow.PaneView
 * @extends BI.View
 * @type {*|void|Object}
 */
BIShow.PaneView = BI.inherit(BI.View, {
    _const: {
        tabHeight: 30,
        toolbarHeight: 30,
        toolbarWidth: 90
    },

    _defaultConfig: function () {
        return BI.extend(BIShow.PaneView.superclass._defaultConfig.apply(this, arguments), {})
    },

    _init: function () {
        BIShow.PaneView.superclass._init.apply(this, arguments);
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
        var items = [];

        BI.each(this.model.cat("widgets"), function (wid, widget) {
            var vessel = BI.createWidget({
                type: "bi.layout",
                id: wid
            });
            self.addSubVessel(wid, vessel);
            items.push({
                el: vessel,
                left: widget.bounds.left,
                top: widget.bounds.top,
                width: widget.bounds.width,
                height: widget.bounds.height
            });
        });
        var dashboard = BI.createWidget({
            type: "bi.adaptive_arrangement",
            layoutType: this.model.get("layoutStyle"),
            resizable: false
        });
        dashboard.populate(items);
        return dashboard;
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
        this._refreshWidgets();
    }
});
