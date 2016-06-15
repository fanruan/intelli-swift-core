/**
 * 图表切换管理器
 *
 * Created by GUY on 2016/3/16.
 * @class BI.TableChartManager
 * @extends BI.Widget
 */
BI.TableChartManager = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.TableChartManager.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-table-chart-manager",
            wId: ""
        });
    },

    _init: function () {
        BI.TableChartManager.superclass._init.apply(this, arguments);

        this.tableChartTab = BI.createWidget({
            type: "bi.tab",
            element: this.element,
            cardCreator: BI.bind(this._createChartTabs, this)
        });
    },

    _createChartTabs: function (v) {
        switch (v) {
            case BICst.WIDGET.TABLE:
            case BICst.WIDGET.CROSS_TABLE:
            case BICst.WIDGET.COMPLEX_TABLE:
                return this._createTable();
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
                return this._createChart();
            case BICst.WIDGET.NONE:
                this.tipPane = BI.createWidget({
                    type: "bi.layout"
                });
                return this.tipPane;
        }
    },

    /**
     * 图表不能画出基本逻辑
     * 通用：至少要有分类和指标有一个 条形、柱形图、面积图、折线图、组合图、（饼图以前）
     * 饼图、仪表盘 只要有指标就可以了
     * 气泡、散点 必须每个区域都要有一个
     *
     */
    _isShowChartPane: function () {
        var o = this.options;
        var wId = o.wId, status = o.status;
        var view = BI.Utils.getWidgetViewByID(wId);
        var cls = true;
        var dim1Size = 0, dim2Size = 0, tar1Size = 0, tar2Size = 0, tar3Size = 0;
        BI.each(view, function (vId, v) {
            switch (vId) {
                case BICst.REGION.DIMENSION1:
                    BI.each(v, function (i, dId) {
                        BI.Utils.isDimensionUsable(dId) && dim1Size++;
                    });
                    break;
                case BICst.REGION.DIMENSION2:
                    BI.each(v, function (i, dId) {
                        BI.Utils.isDimensionUsable(dId) && dim2Size++;
                    });
                    break;
                case BICst.REGION.TARGET1:
                    BI.each(v, function (i, dId) {
                        BI.Utils.isDimensionUsable(dId) && tar1Size++;
                    });
                    break;
                case BICst.REGION.TARGET2:
                    BI.each(v, function (i, dId) {
                        BI.Utils.isDimensionUsable(dId) && tar2Size++;
                    });
                    break;
                case BICst.REGION.TARGET3:
                    BI.each(v, function (i, dId) {
                        BI.Utils.isDimensionUsable(dId) && tar3Size++;
                    });
                    break;
            }
        });
        switch (BI.Utils.getWidgetTypeByID(wId)) {
            case BICst.WIDGET.TABLE:
                (dim1Size + dim2Size + tar1Size + tar2Size + tar3Size) === 0 && (cls = status === BICst.WIDGET_STATUS.EDIT ? "" : "");
                break;
            case BICst.WIDGET.CROSS_TABLE:
                (dim1Size + dim2Size + tar1Size + tar2Size + tar3Size) === 0 && (cls = status === BICst.WIDGET_STATUS.EDIT ? "" : "");
                break;
            case BICst.WIDGET.COMPLEX_TABLE:
                (dim1Size + dim2Size + tar1Size + tar2Size + tar3Size) === 0 && (cls = status === BICst.WIDGET_STATUS.EDIT ? "" : "");
                break;
            case BICst.WIDGET.BUBBLE:
                !(dim1Size > 0 && tar1Size > 0 && tar2Size > 0 && tar3Size > 0) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "bubble-text-tip-background" : "bubble-tip-background");
                break;
            case BICst.WIDGET.SCATTER:
                !(dim1Size > 0 && tar1Size > 0 && tar2Size > 0) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "scatter-text-tip-background" : "scatter-tip-background");
                break;
            case BICst.WIDGET.AXIS:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "axis-text-tip-background" : "axis-tip-background");
                break;
            case BICst.WIDGET.LINE:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "" : "");
                break;
            case BICst.WIDGET.AREA:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "" : "");
                break;
            case BICst.WIDGET.ACCUMULATE_AXIS:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "" : "");
                break;
            case BICst.WIDGET.ACCUMULATE_AREA:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "" : "");
                break;
            case BICst.WIDGET.ACCUMULATE_RADAR:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "" : "");
                break;
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "" : "");
                break;
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "" : "");
                break;
            case BICst.WIDGET.COMPARE_AXIS:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "" : "");
                break;
            case BICst.WIDGET.COMPARE_AREA:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "" : "");
                break;
            case BICst.WIDGET.FALL_AXIS:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "" : "");
                break;
            case BICst.WIDGET.RANGE_AREA:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "" : "");
                break;
            case BICst.WIDGET.BAR:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "" : "");
                break;
            case BICst.WIDGET.ACCUMULATE_BAR:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "" : "");
                break;
            case BICst.WIDGET.COMPARE_BAR:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "" : "");
                break;
            case BICst.WIDGET.COMBINE_CHART:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "" : "");
                break;
            case BICst.WIDGET.RADAR:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "radar-text-tip-background" : "radar-tip-background");
                break;
            case BICst.WIDGET.DONUT:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "donut-text-tip-background" : "donut-tip-background");
                break;
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "" : "");
                break;
            case BICst.WIDGET.FORCE_BUBBLE:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "" : "");
                break;
            case BICst.WIDGET.FUNNEL:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "funnel-text-tip-background" : "funnel-tip-background");
                break;
            case BICst.WIDGET.MAP:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "map-text-tip-background" : "map-tip-background");
                break;
            case BICst.WIDGET.GIS_MAP:
                !(dim1Size > 0 && (tar1Size > 0 || tar2Size > 0 || tar3Size > 0)) && (cls = status === BICst.WIDGET_STATUS.EDIT ? "map-gis-text-tip-background" : "map-gis-tip-background");
                break;
            case BICst.WIDGET.PIE:
                tar1Size === 0 && (cls = status === BICst.WIDGET_STATUS.EDIT ? "pie-text-tip-background" : "pie-tip-background");
                break;
            case BICst.WIDGET.DASHBOARD:
                tar1Size === 0 && (cls = status === BICst.WIDGET_STATUS.EDIT ? "dashboard-text-tip-background" : "dashboard-tip-background");
                break;
        }
        return cls;
    },

    _createChart: function () {
        var self = this;
        var chart = BI.createWidget({
            type: "bi.chart_display",
            wId: this.options.wId
        });
        chart.on(BI.ChartDisplay.EVENT_CHANGE, function () {
            self.fireEvent(BI.TableChartManager.EVENT_CLICK_CHART, arguments);
        });
        return chart;
    },

    _createTable: function () {
        var self = this, o = this.options;
        this.table = BI.createWidget({
            type: "bi.summary_table",
            wId: o.wId
        });
        this.table.on(BI.SummaryTable.EVENT_CHANGE, function (obs) {
            self.fireEvent(BI.TableChartManager.EVENT_CHANGE, obs);
        });
        return this.table;
    },

    resize: function () {
        this.tableChartTab.getSelectedTab().resize();
    },

    getValue: function () {
        return this.tableChartTab.getValue();
    },

    populate: function () {
        var widgetType = BI.Utils.getWidgetTypeByID(this.options.wId);
        var cls = this._isShowChartPane();
        if (cls !== true) {
            this.tableChartTab.setSelect(BICst.WIDGET.NONE);
            this.tipPane.element.removeClass().addClass(cls);
            return;
        }
        if (!BI.Utils.isAllFieldsExistByWidgetID(this.options.wId)) {
            this.tableChartTab.setSelect(BICst.WIDGET.NONE);
            this.tipPane.element.removeClass().addClass("data-miss-background");
            return;
        }
        this.tableChartTab.setSelect(widgetType);
        this.tableChartTab.populate();
    }
});
BI.TableChartManager.EVENT_CHANGE = "TableChartManager.EVENT_CHANGE";
BI.TableChartManager.EVENT_CLICK_CHART = "EVENT_CLICK_CHART";
$.shortcut('bi.table_chart_manager', BI.TableChartManager);