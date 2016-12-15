/**
 * 维度管理器
 *
 * Created by GUY on 2016/3/16.
 * @class BI.DimensionsManager
 * @extends BI.Widget
 */
BI.DimensionsManager = BI.inherit(BI.Widget, {

    constants: {
        DETAIL_NORTH_HEIGHT: 40,
        DETAIL_TAB_HEIGHT: 40,
        DETAIL_WEST_WIDTH: 280,
        DETAIL_DATA_STYLE_HEIGHT: 320,
        DETAIL_GAP_NORMAL: 10,
        DETAIL_PANE_HORIZONTAL_GAP: 20,
        DETAIL_TAB_WIDTH: 200
    },

    _defaultConfig: function () {
        return BI.extend(BI.DimensionsManager.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimensions-manager",
            dimensionCreator: BI.emptyFn,
            wId: ""
        });
    },

    _init: function () {
        BI.DimensionsManager.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.model = new BI.DimensionsManagerModel({
            wId: o.wId
        });
        this.container = BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [
                {
                    el: {
                        type: "bi.absolute",
                        scrollable: false,
                        items: [{
                            el: this._createChartType(),
                            left: 10,
                            right: 10
                        }]
                    },
                    height: 40
                }, {
                    el: this._createChartRegion()
                }
            ]
        })
    },

    _createChartType: function () {
        var self = this;
        this.chartType = BI.createWidget({
            type: "bi.chart_type"
        });
        this.chartType.on(BI.ChartType.EVENT_CHANGE, function () {
            var val = this.getValue();
            self.model.setType(val.type);
            self.model.setSubType(val.subType);
            self.tab.setSelect(val.type);
            self.fireEvent(BI.DimensionsManager.EVENT_CHANGE, arguments);
        });
        return this.chartType;
    },

    _createChartRegion: function () {
        var self = this, o = this.options;
        return this.tab = BI.createWidget({
            type: "bi.tab",
            defaultShowIndex: false,
            cardCreator: BI.bind(this._createRegionsManager, this)
        })
    },

    _createRegionsManager: function (widgetType) {
        var self = this, o = this.options;
        var type = "bi.table_regions_manager";
        switch (widgetType) {
            case BICst.WIDGET.TABLE:
                type = "bi.table_regions_manager";
                break;
            case BICst.WIDGET.CROSS_TABLE:
                type = "bi.cross_table_regions_manager";
                break;
            case BICst.WIDGET.COMPLEX_TABLE:
                type = "bi.complex_table_regions_manager";
                break;
            case BICst.WIDGET.AXIS:
                type = "bi.axis_regions_manager";
                break;
            case BICst.WIDGET.ACCUMULATE_AXIS:
                type = "bi.accumulate_axis_regions_manager";
                break;
            case BICst.WIDGET.PERCENT_ACCUMULATE_AXIS:
                type = "bi.percent_accumulate_axis_regions_manager";
                break;
            case BICst.WIDGET.COMPARE_AXIS:
                type = "bi.compare_axis_regions_manager";
                break;
            case BICst.WIDGET.FALL_AXIS:
                type = "bi.fall_axis_regions_manager";
                break;
            case BICst.WIDGET.BAR:
                type = "bi.bar_regions_manager";
                break;
            case BICst.WIDGET.ACCUMULATE_BAR:
                type = "bi.accumulate_bar_regions_manager";
                break;
            case BICst.WIDGET.COMPARE_BAR:
                type = "bi.compare_bar_regions_manager";
                break;
            case BICst.WIDGET.PIE:
                type = "bi.pie_regions_manager";
                break;
            case BICst.WIDGET.MULTI_PIE:
                type = "bi.multi_pie_regions_manager";
                break;
            case BICst.WIDGET.MAP:
                type = "bi.map_regions_manager";
                break;
            case BICst.WIDGET.GIS_MAP:
                type = "bi.gis_map_regions_manager";
                break;
            case BICst.WIDGET.DASHBOARD:
                type = "bi.dashboard_regions_manager";
                break;
            case BICst.WIDGET.DONUT:
                type = "bi.donut_regions_manager";
                break;
            case BICst.WIDGET.BUBBLE:
                type = "bi.bubble_regions_manager";
                break;
            case BICst.WIDGET.FORCE_BUBBLE:
                type = "bi.force_bubble_regions_manager";
                break;
            case BICst.WIDGET.SCATTER:
                type = "bi.scatter_regions_manager";
                break;
            case BICst.WIDGET.RADAR:
                type = "bi.radar_regions_manager";
                break;
            case BICst.WIDGET.ACCUMULATE_RADAR:
                type = "bi.accumulate_radar_regions_manager";
                break;
            case BICst.WIDGET.LINE:
                type = "bi.line_regions_manager";
                break;
            case BICst.WIDGET.AREA:
                type = "bi.area_regions_manager";
                break;
            case BICst.WIDGET.ACCUMULATE_AREA:
                type = "bi.accumulate_area_regions_manager";
                break;
            case BICst.WIDGET.PERCENT_ACCUMULATE_AREA:
                type = "bi.percent_accumulate_area_regions_manager";
                break;
            case BICst.WIDGET.COMPARE_AREA:
                type = "bi.compare_area_regions_manager";
                break;
            case BICst.WIDGET.RANGE_AREA:
                type = "bi.range_area_regions_manager";
                break;
            case BICst.WIDGET.COMBINE_CHART:
                type = "bi.combine_chart_regions_manager";
                break;
            case BICst.WIDGET.MULTI_AXIS_COMBINE_CHART:
                type = "bi.multi_axis_combine_chart_regions_manager";
                break;
            case BICst.WIDGET.FUNNEL:
                type = "bi.funnel_regions_manager";
                break;
            default:
                type = "bi.table_regions_manager";
                break;
        }
        var manager = BI.createWidget({
            type: type,
            dimensionCreator: o.dimensionCreator,
            wId: o.wId
        });
        manager.on(BI.RegionsManager.EVENT_CHANGE, function () {
            var val = this.getValue();
            self.model.setViews(val.view);
            self.model.setScopes(val.scopes);
            self.fireEvent(BI.DimensionsManager.EVENT_CHANGE, arguments);
        });
        return manager;
    },

    getValue: function () {
        return this.model.getValue();
    },

    populate: function () {
        var self = this, o = this.options;
        this.model.populate();
        var views = this.model.getViews();
        var widgetType = this.model.getType();
        this.chartType.setValue({type: widgetType, subType: BI.Utils.getWidgetSubTypeByID(o.wId)});
        this.tab.setSelect(widgetType);
        this.tab.populate();
    }
});
BI.DimensionsManager.EVENT_CHANGE = "DimensionsManager.EVENT_CHANGE";
$.shortcut('bi.dimensions_manager', BI.DimensionsManager);
