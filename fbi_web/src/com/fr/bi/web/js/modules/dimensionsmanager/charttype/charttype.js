/**
 * @class BI.ChartType
 * @extend BI.Widget
 * 选择图表类型组
 */
BI.ChartType = BI.inherit(BI.Widget, {

    constants: {
        sort: {
            TABLE: 0,
            AXIS: 1,
            BAR: 2,
            LINE: 7,
            AREA: 3,
            COMBINE: 4,
            PIE: 8,
            DONUT: 9,
            MAP: 10,
            GIS_MAP: 11,
            DASHBOARD: 12,
            BUBBLE: 5,
            SCATTER: 13,
            RADAR: 6,
            FUNNEL: 14
        }
    },

    _defaultConfig: function () {
        return BI.extend(BI.ChartType.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-chart-type"
        })
    },

    _init: function () {
        BI.ChartType.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.multiTypeChart = BI.map([
            BICst.TABLE_TYPE,
            BICst.AXIS_CHART_TYPE,
            BICst.BAR_CHART_TYPE,
            BICst.AREA_CHART_TYPE,
            BICst.COMBINE_CHART_TYPE,
            BICst.BUBBLE_CHART_TYPE,
            BICst.RADAR_CHART_TYPE
        ], function(idx, item){
            return BI.createWidget({
                type: "bi.table_chart_combo",
                width: 40,
                height: 34,
                items: item
            });
        });

        var singleTypeChart = BI.createWidgets(BICst.SINGLE_TYPE_CHART, {
            type: "bi.icon_button",
            width: 40,
            height: 34,
            iconHeight: 25,
            iconWidth: 25,
            extraCls: "chart-type-icon"
        });

        this.singleGroup = BI.createWidget({
            type: "bi.button_group",
            items: singleTypeChart,
            layouts: [{
                type: "bi.default"
            }]
        });

        this.singleGroup.on(BI.ButtonGroup.EVENT_CHANGE, function (v) {
            self.setValue(v);
            self.fireEvent(BI.ChartType.EVENT_CHANGE, arguments);
        });

        BI.each(this.multiTypeChart, function(idx, widget){
            widget.on(BI.TableChartCombo.EVENT_CHANGE, function(v){
                self.setValue(v);
                self.fireEvent(BI.ChartType.EVENT_CHANGE, arguments);
            });
        });

        var sorted = [];
        var all = BI.concat(this.multiTypeChart, singleTypeChart);
        BI.each(this.constants.sort, function(idx, v){
            sorted[sorted.length] = all[v];
        });

        BI.createWidget({
            type: "bi.horizontal",
            element: this.element,
            scrollx: false,
            scrollable: false,
            items: sorted,
            vgap: 3,
            hgap: 3
        })
    },

    getValue: function () {
        var value = null;
        BI.any(this.multiTypeChart, function(idx, chart){
            if(chart.isSelected()){
                value = chart.getValue();
            }
        });
        if(BI.isNull(value)){
            value = this.singleGroup.getValue()[0];
        }
        return value;
    },

    setValue: function (v) {
        BI.each(this.multiTypeChart, function(idx, chart){
            chart.setValue(v);
        });
        this.singleGroup.setValue(v);
    }
});
BI.ChartType.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.chart_type", BI.ChartType);
