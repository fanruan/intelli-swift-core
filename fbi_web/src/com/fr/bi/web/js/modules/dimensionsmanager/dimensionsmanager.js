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
            self.tab.setSelect(this.getValue());
            self.model.setType(this.getValue());
            self.fireEvent(BI.DimensionsManager.EVENT_CHANGE, arguments);
        });
        return this.chartType;
    },

    _createChartRegion: function () {
        var self = this, o = this.options;
        return this.tab = BI.createWidget({
            type: "bi.tab",
            defaultShowIndex: false,
            cardCreator: function (v) {
                var manager = BI.createWidget({
                    type: "bi.regions_manager",
                    dimensionCreator: o.dimensionCreator,
                    regionType: v,
                    wId: o.wId
                });
                manager.on(BI.RegionsManager.EVENT_CHANGE, function () {
                    self.model.setViews(manager.getValue());
                    self.fireEvent(BI.DimensionsManager.EVENT_CHANGE, arguments);
                });
                return manager;
            }
        })
    },

    getValue: function () {
        return this.model.getValue();
    },

    populate: function () {
        var self = this, o = this.options;
        this.model.populate();
        var views = this.model.getViews();
        var widgetType = this.model.getType();
        this.chartType.setValue(widgetType);
        if (BI.isNull(this.chartType.getValue())) {
            this.container.attr("items")[0].height = 0;
            this.container.resize();
        }
        this.tab.setSelect(widgetType);
        this.tab.populate(views);
    }
});
BI.DimensionsManager.EVENT_CHANGE = "DimensionsManager.EVENT_CHANGE";
$.shortcut('bi.dimensions_manager', BI.DimensionsManager);
