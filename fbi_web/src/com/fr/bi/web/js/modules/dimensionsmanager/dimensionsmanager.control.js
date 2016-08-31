/**
 * 维度管理器(控件)
 *
 * Created by GUY on 2016/3/16.
 * @class BI.DimensionsManagerControl
 * @extends BI.Widget
 */
BI.DimensionsManagerControl = BI.inherit(BI.Widget, {

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
        return BI.extend(BI.DimensionsManagerControl.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-dimensions-manager",
            dimensionCreator: BI.emptyFn,
            wId: ""
        });
    },

    _init: function () {
        BI.DimensionsManagerControl.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.model = new BI.DimensionsManagerModel({
            wId: o.wId
        });
        this.container = BI.createWidget({
            type: "bi.vtape",
            element: this.element,
            items: [this._createChartRegion()]
        })
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
                    self.fireEvent(BI.DimensionsManagerControl.EVENT_CHANGE, arguments);
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
        this.tab.setSelect(widgetType);
        this.tab.populate(views);
    }
});
BI.DimensionsManagerControl.EVENT_CHANGE = "DimensionsManagerControl.EVENT_CHANGE";
$.shortcut('bi.dimensions_manager_control', BI.DimensionsManagerControl);
