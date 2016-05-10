/**
 * @class BIShow.NumberDetailView
 * @extends BI.View
 *
 */
BIShow.NumberDetailView = BI.inherit(BI.View, {

    constants: {
        DETAIL_NORTH_HEIGHT: 40,
        DETAIL_TAB_HEIGHT: 40,
        DETAIL_WEST_WIDTH: 280,
        DETAIL_DATA_STYLE_HEIGHT: 320,
        DETAIL_GAP_NORMAL: 10,
        DETAIL_PANE_HORIZONTAL_GAP: 20,
        DETAIL_TITLE_WIDTH: 400,
        DETAIL_TITLE_HEIGHT: 22
    },

    _defaultConfig: function () {
        return BI.extend(BIShow.NumberDetailView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-widget-attribute-setter"
        })
    },

    _init: function () {
        BIShow.NumberDetailView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        this.main = BI.createWidget({
            type: "bi.border",
            element: vessel,
            items: {
                north: {
                    el: this._buildNorth(),
                    height: this.constants.DETAIL_NORTH_HEIGHT
                },
                west: {
                    el: this._buildWest(),
                    width: this.constants.DETAIL_WEST_WIDTH
                },
                center: {
                    el: this._buildCenter()
                }
            }
        });
    },

    _buildNorth: function () {
        var self = this;
        var widgetNameEditor = BI.createWidget({
            type: "bi.text_editor",
            value: this.model.get("name"),
            height: this.constants.DETAIL_TITLE_HEIGHT,
            width: this.constants.DETAIL_TITLE_WIDTH
        });

        widgetNameEditor.on(BI.TextEditor.EVENT_CONFIRM, function () {
            self.model.set("name", widgetNameEditor.getValue());
        });

        var complete = BI.createWidget({
            type: "bi.button",
            height: 25,
            title: BI.i18nText('BI-Return_To_Dashboard'),
            text: BI.i18nText('BI-Detail_Set_Complete')
        });

        complete.on(BI.Button.EVENT_CHANGE, function () {
            self.notifyParentEnd(true);
        });

        return BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "widget-attr-north",
            items: {
                left: [widgetNameEditor],
                right: [complete]
            },
            lhgap: this.constants.DETAIL_PANE_HORIZONTAL_GAP,
            rhgap: this.constants.DETAIL_PANE_HORIZONTAL_GAP
        });
    },

    _buildWest: function () {
        return BI.createWidget({
            type: "bi.absolute",
            items: [{
                el: {
                    type: "bi.select_number",
                    cls: "widget-select-data-pane"
                },
                left: this.constants.DETAIL_PANE_HORIZONTAL_GAP,
                top: this.constants.DETAIL_GAP_NORMAL,
                bottom: this.constants.DETAIL_GAP_NORMAL,
                right: this.constants.DETAIL_PANE_HORIZONTAL_GAP
            }],
            cls: "widget-attr-west"
        });
    },

    _buildCenter: function () {
        var combo = this._createCombo();

        var top = BI.createWidget({
            type: "bi.vtape",
            cls: "widget-top-wrapper",
            items: [{
                el: {
                    type: "bi.label",
                    text: BI.i18nText("BI-Data")
                },
                height: 30
            }, {
                el: this._createRegion()
            }]
        });

        return BI.createWidget({
            type: "bi.absolute",
            cls: "widget-attr-center",
            items: [{
                el: {
                    type: "bi.vtape",
                    cls: "widget-show-data-pane",
                    items: [{
                        el: top,
                        height: this.constants.DETAIL_DATA_STYLE_HEIGHT - this.constants.DETAIL_NORTH_HEIGHT
                    }, {
                        el: {
                            type: "bi.absolute",
                            cls: "widget-attr-chart",
                            items: [{
                                el: combo,
                                left: 10,
                                right: 10,
                                top: 10
                            }]
                        }
                    }],
                    vgap: 10
                },
                left: 0,
                right: this.constants.DETAIL_PANE_HORIZONTAL_GAP,
                top: this.constants.DETAIL_GAP_NORMAL,
                bottom: this.constants.DETAIL_GAP_NORMAL
            }]
        });
    },

    _createRegion: function () {
        var self = this;
        var dimensionsVessel = {};
        this.dimensionsManager = BI.createWidget({
            type: "bi.dimensions_manager_show",
            wId: this.model.get("id"),
            dimensionCreator: function (dId, regionType, op) {
                if (!dimensionsVessel[dId]) {
                    dimensionsVessel[dId] = BI.createWidget({
                        type: "bi.layout"
                    });
                    self.addSubVessel(dId, dimensionsVessel[dId]).skipTo(regionType + "/" + dId, dId, "dimensions." + dId);
                }
                return dimensionsVessel[dId];
            }
        });

        this.dimensionsManager.on(BI.DimensionsManager.EVENT_CHANGE, function () {
            var values = this.getValue();
            self.model.set(values);
            this.populate();
        });
        return this.dimensionsManager;
    },

    _createCombo: function () {
        var self = this;
        this.combo = BI.createWidget({
            type: "bi.numerical_interval"
        });
        this.combo.on(BI.NumericalInterval.EVENT_CHANGE, function () {
            self.model.set("value", this.getValue());
        });
        return this.combo;
    },

    local: function () {
        return false;
    },

    change: function (changed, prev) {
        if (BI.has(changed, "dimensions")) {
        }
    },

    splice: function (old, key1, key2) {
        if (key1 === "dimensions") {
        }
    },

    refresh: function () {
        var self = this;
        this.dimensionsManager.populate();
        var filter = this.model.get("filter_value");
        this.combo.setValue(filter.filter_value);
    }
});
