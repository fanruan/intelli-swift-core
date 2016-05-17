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
            case BICst.WIDGET.SCATTER:
            case BICst.WIDGET.RADAR:
            case BICst.WIDGET.ACCUMULATE_RADAR:
            case BICst.WIDGET.FUNNEL:
                return this._createChart();
            case BICst.WIDGET.NONE:
                return this._createNoDataPane();
        }
    },

    _createNoDataPane: function(){
        return BI.createWidget({
            type: "bi.center_adapt",
            items: [{
                type: "bi.horizontal_auto",
                cls: "dimension-no-data-icon",
                items: [{
                    type: "bi.icon",
                    width: 110,
                    height: 110
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Source_Data_Removed_Can_Not_Display_Here"),
                    cls: "no-data-detail-comment",
                    height: 30
                }, {
                    type: "bi.label",
                    text: BI.i18nText("BI-Please_Contact_Admin"),
                    cls: "contact-admin-comment",
                    height: 30
                }],
                vgap: 5
            }]
        })
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
            wId: o.wId
        });
        this.table.on(BI.SummaryTable.EVENT_CHANGE, function(obs){
            self.fireEvent(BI.TableChartManager.EVENT_CHANGE, obs);
        });
        return this.table;
    },

    resize: function () {
        switch (this.tableChartTab.getSelect()) {
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.PIE:
            case BICst.WIDGET.RADAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.MAP:
            case BICst.WIDGET.DASHBOARD:
            case BICst.WIDGET.DONUT:
            case BICst.WIDGET.BUBBLE:
            case BICst.WIDGET.SCATTER:
                this.tableChartTab.getSelectedTab().resize();
        }
    },

    getValue: function () {
        return this.tableChartTab.getValue();
    },

    populate: function () {
        var widgetType = BI.Utils.getWidgetTypeByID(this.options.wId);
        if(!BI.Utils.isAllFieldsExistByWidgetID(this.options.wId)){
            widgetType = BICst.WIDGET.NONE;
        }
        this.tableChartTab.setSelect(widgetType);
        this.tableChartTab.populate();
    }
});
BI.TableChartManager.EVENT_CHANGE = "TableChartManager.EVENT_CHANGE";
$.shortcut('bi.table_chart_manager', BI.TableChartManager);