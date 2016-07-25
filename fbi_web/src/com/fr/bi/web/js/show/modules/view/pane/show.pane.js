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
                el: BI.createWidget(),
                height: 1
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

    _refreshWidgets: function (refresh) {
        var self = this;
        BI.each(this.cat("widgets"), function (id, widget) {
            var type = widget.type;
            switch (type) {
                case BICst.WIDGET.TABLE:
                case BICst.WIDGET.CROSS_TABLE:
                case BICst.WIDGET.COMPLEX_TABLE:
                case BICst.WIDGET.AXIS:
                case BICst.WIDGET.ACCUMULATE_AXIS:
                case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
                case BICst.WIDGET.COMPARE_AXIS:
                case BICst.WIDGET.FALL_AXIS:
                case BICst.WIDGET.BAR:
                case BICst.WIDGET.ACCUMULATE_BAR:
                case BICst.WIDGET.COMPARE_BAR:
                case BICst.WIDGET.LINE:
                case BICst.WIDGET.AREA:
                case BICst.WIDGET.ACCUMULATE_AREA:
                case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
                case BICst.WIDGET.COMPARE_AREA:
                case BICst.WIDGET.RANGE_AREA:
                case BICst.WIDGET.COMBINE_CHART:
                case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                case BICst.WIDGET.PIE:
                case BICst.WIDGET.DONUT:
                case BICst.WIDGET.MAP:
                case BICst.WIDGET.GIS_MAP:
                case BICst.WIDGET.DASHBOARD:
                case BICst.WIDGET.BUBBLE:
                case BICst.WIDGET.FORCE_BUBBLE:
                case BICst.WIDGET.SCATTER:
                case BICst.WIDGET.RADAR:
                case BICst.WIDGET.ACCUMULATE_RADAR:
                case BICst.WIDGET.FUNNEL:
                    type = BICst.WIDGET.TABLE;
            }
            self.skipTo(id + "/" + type, id, "widgets." + id, {}, {
                force: refresh
            });
        });
    },

    change: function (changed) {
        // this.refresh();
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
        var map = [];
        this.dashboard = BI.createWidget({
            type: "bi.fit_4show",
            widgetCreator: function (wId) {
                if (!map[wId]) {
                    map[wId] = BI.createWidget({
                        type: "bi.layout"
                    });
                    self.addSubVessel(wId, map[wId]);
                }
                return map[wId];
            }
        });
        this.dashboard.on(BI.Fit4Show.EVENT_RESIZE, function () {
            var regions = this.getAllRegions();
            var widgets = self.model.get("widgets");
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
            self.model.set({"widgets": widgets});
            self._refreshWidgets(false);
        });
        return this.dashboard;
    },

    refresh: function () {
        var self = this;
        this.dashboard.populate();
        this._refreshWidgets();
    }
});
