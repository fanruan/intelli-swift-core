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
        return BI.extend(BIShow.PaneView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-pane-view-show"
        })
    },

    _init: function () {
        BIShow.PaneView.superclass._init.apply(this, arguments);
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
                el: this.dashboard
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
        var viewChange = BI.createWidget({
            type: "bi.icon_text_item",
            cls: "toolbar-edit-font",
            text: BI.i18nText("BI-Edit_Report"),
            height: 30,
            width: 80
        });
        viewChange.on(BI.IconTextItem.EVENT_CHANGE, function () {
            var reportId = Data.SharingPool.get("reportId");
            var createBy = Data.SharingPool.get("createBy");
            window.location.href = FR.servletURL + "?op=fr_bi&cmd=bi_init&id=" + reportId + "&createBy=" + createBy + "&edit=_bi_edit_";
        });
        return BI.createWidget({
            type: "bi.absolute",
            cls: "dashboard-toolbar",
            items: [{
                el: Data.SharingPool.get("show") ? BI.createWidget() : viewChange,
                top: 0,
                left: 110
            }]
        })
    },


    _createDashBoard: function () {
        var self = this;
        var items = [];

        BI.each(this.model.cat("widgets"), function (wid, widget) {
            var vessel = BI.createWidget({
                type: "bi.layout"
            });
            self.addSubVessel(wid, vessel);
            var container = BI.createWidget({
                type: "bi.absolute",
                id: wid,
                items: [{
                    el: vessel,
                    left: 5,
                    right: 5,
                    top: 5,
                    bottom: 5
                }]
            });
            items.push({
                el: container,
                left: widget.bounds.left,
                top: widget.bounds.top,
                width: widget.bounds.width,
                height: widget.bounds.height
            });
        });
        var dashboard = BI.createWidget({
            type: "bi.adaptive_arrangement",
            layoutType: this.model.get("layoutType"),
            resizable: false,
            isNeedReLayout: false
        });
        dashboard.populate(items);
        return dashboard;
    },

    refresh: function () {
        var self = this;
        this._refreshWidgets();
    }
});
