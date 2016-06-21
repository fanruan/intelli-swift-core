/**
 * 图表控件
 * @class BI.ChartDisplay
 * @extends BI.Widget
 */
BI.ChartDisplay = BI.inherit(BI.Widget, {

    constants: {
        SCATTER_REGION_COUNT: 3,
        BUBBLE_REGION_COUNT: 4
    },

    _defaultConfig: function () {
        return BI.extend(BI.ChartDisplay.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart-display",
            wId: ""
        })
    },

    _init: function () {
        BI.ChartDisplay.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.model = new BI.ChartDisplayModel({
            wId: o.wId
        });
        this.chart = BI.createWidget({
            type: "bi.combine_chart",
            element: this.element
        });
        this.chart.on(BI.CombineChart.EVENT_CHANGE, function(obj){
            self._doChartItemClick(obj);
            self.fireEvent(BI.ChartDisplay.EVENT_CHANGE, arguments);
        });
    },

    _doChartItemClick: function(obj){
        var self = this, o = this.options;
        var linkageInfo = this.model.getLinkageInfo(obj);
        var dId = linkageInfo.dId, clicked = linkageInfo.clicked;
        BI.each(BI.Utils.getWidgetLinkageByID(o.wId), function (i, link) {
            if (BI.contains(dId, link.from)) {
                BI.Broadcasts.send(BICst.BROADCAST.LINKAGE_PREFIX + link.to, link.from, clicked);
                self._send2AllChildLinkWidget(link.to, link.from, clicked);
            }
        });
    },

    _send2AllChildLinkWidget: function (wid, dId, clicked) {
        var self = this;
        var linkage = BI.Utils.getWidgetLinkageByID(wid);
        BI.each(linkage, function (i, link) {
            BI.Broadcasts.send(BICst.BROADCAST.LINKAGE_PREFIX + link.to, dId, clicked);
            self._send2AllChildLinkWidget(link.to, dId, clicked);
        });
    },

    populate: function () {
        var self = this, o = this.options;
        var type = BI.Utils.getWidgetTypeByID(o.wId);
        this.model.getWidgetData(type, function(types, data, options){
            self.chart.setTypes(types);
            self.chart.setOptions(BI.extend(BI.Utils.getWidgetSettingsByID(o.wId), {
                tooltipFormatter: self.model.getToolTip(type),
                cordon: self.model.getCordon(),
                mapType: {type: BI.Utils.getWidgetTypeByID(o.wId), subType: BI.Utils.getWidgetSubTypeByID(o.wId)}
            }));
            self.chart.populate(data, options);
        });
    },

    resize: function () {
        this.chart.resize();
    }
});
BI.ChartDisplay.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut('bi.chart_display', BI.ChartDisplay);