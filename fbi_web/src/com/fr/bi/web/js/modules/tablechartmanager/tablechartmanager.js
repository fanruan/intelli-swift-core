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
        var self = this, o = this.options;

        this.tableChartTab = BI.createWidget({
            type: "bi.tab",
            element: this.element,
            cardCreator: BI.bind(this._createChartTabs, this)
        });
    },

    _createChartTabs: function (v) {
        switch (v) {
            case BICst.Widget.TABLE:
            case BICst.Widget.CROSS_TABLE:
            case BICst.Widget.COMPLEX_TABLE:
                return this._createTable();
            case BICst.Widget.AXIS:
            case BICst.Widget.BAR:
            case BICst.Widget.PIE:
            case BICst.Widget.RADAR:
            case BICst.Widget.ACCUMULATE_BAR:
            case BICst.Widget.MAP:
            case BICst.Widget.DASHBOARD:
            case BICst.Widget.DOUGHNUT:
            case BICst.Widget.BUBBLE:
            case BICst.Widget.SCATTER:
                return this._createChart();
        }
    },

    _createChart: function () {
        return BI.createWidget({
            type: "bi.chart_display",
            wId: this.options.wId
        });
    },

    _createTable: function () {
        var self = this, o =  this.options;
        this.table = BI.createWidget({
            type: "bi.summary_table",
            cls: "widget-attr-chart",
            wId: o.wId
        });
        this.table.on(BI.SummaryTable.EVENT_CHANGE, function(obs){
            self.fireEvent(BI.TableChartManager.EVENT_CHANGE, obs);
        });
        return this.table;
    },

    getValue: function () {
        return this.tableChartTab.getValue();
    },

    populate: function () {
        this.tableChartTab.setSelect(BI.Utils.getWidgetTypeByID(this.options.wId));
        this.tableChartTab.populate();
    }
});
BI.TableChartManager.EVENT_CHANGE = "TableChartManager.EVENT_CHANGE";
$.shortcut('bi.table_chart_manager', BI.TableChartManager);