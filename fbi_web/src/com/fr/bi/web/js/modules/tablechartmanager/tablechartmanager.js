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
            defaultShowIndex: null,
            cardCreator: BI.bind(this._createChartTabs, this)
        });
    },

    _createChartTabs: function (v) {
        switch (v) {
            case BICst.WIDGET.TABLE:
                return this._createGroupTable();
            case BICst.WIDGET.CROSS_TABLE:
                return this._createCrossTable();
            case BICst.WIDGET.COMPLEX_TABLE:
                return this._createComplexTable();
            default:
                //默认创建图表控件，case太多了也没啥意义
                return this._createChart();
        }
    },

    _createChart: function () {
        var self = this, o = this.options;
        var chart = BI.createWidget({
            type: "bi.chart_display",
            wId: o.wId,
            status: o.status
        });
        chart.on(BI.ChartDisplay.EVENT_CHANGE, function () {
            self.fireEvent(BI.TableChartManager.EVENT_CHANGE, arguments);
        });
        return chart;
    },

    _createGroupTable: function () {
        var self = this, o = this.options;
        this.table = BI.createWidget({
            type: "bi.group_table",
            wId: o.wId,
            status: o.status
        });
        this.table.on(BI.GroupTable.EVENT_CHANGE, function (obs) {
            self.fireEvent(BI.TableChartManager.EVENT_CHANGE, obs);
        });
        return this.table;
    },

    _createCrossTable: function() {
        var self = this, o = this.options;
        this.table = BI.createWidget({
            type: "bi.cross_table",
            wId: o.wId,
            status: o.status
        });
        this.table.on(BI.CrossTable.EVENT_CHANGE, function (obs) {
            self.fireEvent(BI.TableChartManager.EVENT_CHANGE, obs);
        });
        return this.table;
    },

    _createComplexTable: function () {
        var self = this, o = this.options;
        this.table = BI.createWidget({
            type: "bi.complex_table",
            wId: o.wId,
            status: o.status
        });
        this.table.on(BI.ComplexTable.EVENT_CHANGE, function (obs) {
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

    magnify: function () {
        this.tableChartTab.getSelectedTab().magnify();
    },

    populate: function () {
        var widgetType = BI.Utils.getWidgetTypeByID(this.options.wId);
        this.tableChartTab.setSelect(widgetType);
        this.tableChartTab.populate();
    }
});
BI.TableChartManager.EVENT_CHANGE = "TableChartManager.EVENT_CHANGE";
$.shortcut('bi.table_chart_manager', BI.TableChartManager);