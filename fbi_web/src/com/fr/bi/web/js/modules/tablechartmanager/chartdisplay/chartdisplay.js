/**
 * 图表控件
 * @class BI.ChartDisplay
 * @extends BI.Widget
 */
BI.ChartDisplay = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.ChartDisplay.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart-display",
            wId: ""
        })
    },

    _init: function () {
        BI.ChartDisplay.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.tab = BI.createWidget({
            type: "bi.tab",
            element: this.element,
            cardCreator: BI.bind(this._createTabs, this)
        });
    },

    _createTabs: function(v){
        var self = this;
        switch (v) {
            case BICst.WIDGET.BAR:
                return BI.createWidget({
                    type: "bi.bar_chart"
                });
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
                return BI.createWidget({
                    type: "bi.percent_accumulate_area_chart"
                });
            case BICst.WIDGET.BUBBLE:
                return BI.createWidget({
                    type: "bi.bubble_chart"
                });
            case BICst.WIDGET.SCATTER:
                return BI.createWidget({
                    type: "bi.scatter_chart"
                });
            case BICst.WIDGET.AXIS:
                return BI.createWidget({
                    type: "bi.axis_chart"
                });
            case BICst.WIDGET.ACCUMULATE_AXIS:
                return BI.createWidget({
                    type: "bi.accumulate_axis_chart"
                });
            case BICst.WIDGET.COMPARE_BAR:
            case BICst.WIDGET.COMPARE_AXIS:
            case BICst.WIDGET.COMPARE_AREA:
            case BICst.WIDGET.RANGE_AREA:
            case BICst.WIDGET.LINE:
                return BI.createWidget({
                    type: "bi.line_chart"
                });
            case BICst.WIDGET.AREA:
                return BI.createWidget({
                    type: "bi.area_chart"
                });
            case BICst.WIDGET.ACCUMULATE_AREA:
                return BI.createWidget({
                    type: "bi.accumulate_area_chart"
                });
            case BICst.WIDGET.COMBINE_CHART:
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
                return BI.createWidget({
                    type: "bi.percent_accumulate_axis_chart"
                });
            case BICst.WIDGET.ACCUMULATE_BAR:
                return BI.createWidget({
                    type: "bi.accumulate_bar_chart"
                });
            case BICst.WIDGET.FALL_AXIS:
            case BICst.WIDGET.DONUT:
                return BI.createWidget({
                    type: "bi.donut_chart"
                });
            case BICst.WIDGET.RADAR:
                return BI.createWidget({
                    type: "bi.radar_chart"
                });
            case BICst.WIDGET.ACCUMULATE_RADAR:
                return BI.createWidget({
                    type: "bi.accumulate_radar_chart"
                });
            case BICst.WIDGET.PIE:
                return BI.createWidget({
                    type: "bi.pie_chart"
                });
            case BICst.WIDGET.DASHBOARD:
                return BI.createWidget({
                    type: "bi.dashboard_chart"
                });
            case BICst.WIDGET.FORCE_BUBBLE:
                return BI.createWidget({
                    type: "bi.force_bubble_chart"
                });
            case BICst.WIDGET.FUNNEL:
                return BI.createWidget({
                    type: "bi.funnel_chart"
                });
            case BICst.WIDGET.MAP:
            case BICst.WIDGET.GIS_MAP:
                return BI.createWidget();
        }
    },

    _getShowTarget: function () {
        var self = this, o = this.options;
        var view = BI.Utils.getWidgetViewByID(o.wId);
        this.seriesDid = BI.find(view[BICst.REGION.DIMENSION1], function (idx, did) {
            return BI.Utils.isDimensionUsable(did);
        });
        this.cataDid = BI.find(view[BICst.REGION.DIMENSION2], function (idx, did) {
            return BI.Utils.isDimensionUsable(did);
        });
        this.targetIds = [];
        BI.each(view, function (regionType, arr) {
            if (regionType >= BICst.REGION.TARGET1) {
                self.targetIds = BI.concat(self.targetIds, arr);
            }
        });
        return this.targetIds = BI.filter(this.targetIds, function (idx, tId) {
            return BI.Utils.isDimensionUsable(tId);
        });

    },

    populate: function () {
        var self = this, o = this.options;
        var type = BI.Utils.getWidgetTypeByID(o.wId);
        this.tab.setSelect(type);
        var selectedTab = this.tab.getSelectedTab();
        selectedTab.loading();
        BI.Utils.getWidgetDataByID(o.wId, function (jsonData) {
            selectedTab.populate(jsonData.data);
            selectedTab.loaded();
        });
    },

    resize: function () {
        this.tab.getSelectedTab().resize();
    },

    _send2AllChildLinkWidget: function (wid, dId, clicked) {
        var self = this;
        var linkage = BI.Utils.getWidgetLinkageByID(wid);
        BI.each(linkage, function (i, link) {
            BI.Broadcasts.send(BICst.BROADCAST.LINKAGE_PREFIX + link.to, dId, clicked);
            self._send2AllChildLinkWidget(link.to);
        });
    }
});
$.shortcut('bi.chart_display', BI.ChartDisplay);