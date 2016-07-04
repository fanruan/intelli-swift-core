/**
 * Created by GUY on 2015/6/24.
 */
BIDezi.DetailView = BI.inherit(BI.View, {

    constants: {
        DETAIL_NORTH_HEIGHT: 40,
        DETAIL_TAB_HEIGHT: 40,
        DETAIL_WEST_WIDTH: 270,
        DETAIL_DATA_STYLE_HEIGHT: 280,
        DETAIL_GAP_NORMAL: 10,
        DETAIL_PANE_HORIZONTAL_GAP: 10,
        DETAIL_TAB_WIDTH: 200
    },

    _defaultConfig: function () {
        return BI.extend(BIDezi.DetailView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-widget-attribute-setter"
        })
    },

    _init: function () {
        BIDezi.DetailView.superclass._init.apply(this, arguments);
    },

    splice: function (old, key1, key2) {
        if (key1 === "dimensions") {
            this.dimensionsManager.populate();
        }
    },

    duplicate: function (copy, key1, key2) {
        var self = this;
        if (key1 === "dimensions") {
            this.dimensionsManager.populate();
            this._refreshDimensions();
        }
    },

    change: function (changed, prev) {
        if (BI.has(changed, "view") ||
            BI.has(changed, "dimensions") ||
            BI.has(changed, "sort") ||
            BI.has(changed, "filter_value")) {
            this.tableChartPopupulate();
        }
        if (BI.has(changed, "type") || BI.has(changed, "sub_type")) {
            this.tableChartPopupulate();
            this._refreshDimensions();
        }
        if (BI.has(changed, "settings")) {
            var diffs = BI.deepDiff(changed.settings, prev.settings);
            if (diffs.length > 0 && (diffs.length > 1 || diffs[0] !== "column_size")) {
                this.tableChartPopupulate();
            }
        }
        if (BI.has(changed, "clicked")) {
            this.tableChartPopupulate();
        }
        if (BI.has(changed, "dimensions")) {
            this.dimensionsManager.populate();
            this._refreshDimensions();
        }
        if (BI.has(changed, "view")) {
            this.dimensionsManager.populate();
            this._refreshDimensions();
        }
    },

    _render: function (vessel) {
        var mask = BI.createWidget();
        mask.element.__buildZIndexMask__(0);
        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: mask,
                left: 0,
                right: 0,
                top: 0,
                bottom: 0
            }, {
                el: {
                    type: "bi.htape",
                    cls: "widget-attribute-setter-container",
                    items: [{
                        el: this._buildWest(),
                        width: this.constants.DETAIL_WEST_WIDTH
                    }, {
                        type: "bi.vtape",
                        items: [{
                            el: this._buildNorth(), height: this.constants.DETAIL_NORTH_HEIGHT
                        }, {
                            el: this._buildCenter()
                        }]
                    }]
                },
                left: 20,
                right: 20,
                top: 20,
                bottom: 20
            }]
        });
    },

    _buildNorth: function () {
        var self = this;
        var shrink = BI.createWidget({
            type: "bi.button",
            height: 25,
            title: BI.i18nText('BI-Return_To_Dashboard'),
            text: BI.i18nText('BI-Detail_Set_Complete'),
            handler: function () {
                self.notifyParentEnd();
            }
        });
        return BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            items: {
                right: [shrink]
            },
            lhgap: this.constants.DETAIL_PANE_HORIZONTAL_GAP,
            rhgap: this.constants.DETAIL_PANE_HORIZONTAL_GAP
        });
    },

    _buildWest: function () {
        var self = this;
        var tab = BI.createWidget({
            type: "bi.detail_select_data",
            cls: "widget-select-data-pane",
            wId: this.model.get("id")
        });

        return BI.createWidget({
            type: "bi.absolute",
            items: [{
                el: tab,
                left: this.constants.DETAIL_PANE_HORIZONTAL_GAP,
                top: this.constants.DETAIL_GAP_NORMAL,
                bottom: this.constants.DETAIL_GAP_NORMAL,
                right: this.constants.DETAIL_PANE_HORIZONTAL_GAP
            }],
            cls: "widget-attr-west"
        });
    },

    _buildCenter: function () {
        var self = this;
        this.tableChartTab = BI.createWidget({
            type: "bi.table_chart_manager",
            cls: "widget-center-wrapper",
            wId: this.model.get("id"),
            status: BICst.WIDGET_STATUS.DETAIL
        });
        this.tableChartPopupulate = BI.debounce(BI.bind(this.tableChartTab.populate, this.tableChartTab), 0);
        this.tableChartTab.on(BI.TableChartManager.EVENT_CHANGE, function (obs) {
            self.model.set(obs);
        });
        // var checkbox = BI.createWidget({
        //     type: "bi.real_data_checkbox"
        // });

        var data_style_tab = BI.createWidget({
            type: "bi.data_style_tab",
            wId: this.model.get("id"),
            cardCreator: BI.bind(this._createTabs, this),
            cls: "widget-top-wrapper"
        });

        data_style_tab.on(BI.DataStyleTab.EVENT_CHANGE, function () {
            if (this.getSelect() === BICst.DETAIL_TAB_STYLE) {
                self.chartSetting.populate();
            }
        });

        // var top = BI.createWidget({
        //     type: "bi.vtape",
        //     cls: "widget-top-wrapper",
        //     items: [data_style_tab, {
        //         el: checkbox,
        //         height: this.constants.DETAIL_NORTH_HEIGHT
        //     }]
        // });

        return BI.createWidget({
            type: "bi.absolute",
            cls: "widget-attr-center",
            items: [{
                el: {
                    type: "bi.border",
                    items: {
                        north: {
                            el: data_style_tab,
                            height: this.constants.DETAIL_DATA_STYLE_HEIGHT,
                            bottom: this.constants.DETAIL_GAP_NORMAL
                        },
                        center: this.tableChartTab
                    }
                },
                left: 0,
                right: this.constants.DETAIL_PANE_HORIZONTAL_GAP,
                top: this.constants.DETAIL_GAP_NORMAL,
                bottom: this.constants.DETAIL_GAP_NORMAL
            }]
        });
    },

    _createTabs: function (v) {
        switch (v) {
            case BICst.DETAIL_TAB_TYPE_DATA:
                return this._createTypeAndData();
            case BICst.DETAIL_TAB_STYLE:
                return this._createStyle();
        }
    },

    /**
     * 图表样式设置
     * @returns {*}
     * @private
     */
    _createStyle: function () {
        var self = this;
        this.chartSetting = BI.createWidget({
            type: "bi.chart_setting",
            wId: this.model.get("id")
        });
        this.chartSetting.populate();
        this.chartSetting.on(BI.ChartSetting.EVENT_CHANGE, function (v) {
            self.model.set("settings", BI.extend(self.model.get("settings"), v));
        });
        return this.chartSetting;
    },

    _createTypeAndData: function () {
        var self = this;
        var dimensionsVessel = {};
        this.dimensionsManager = BI.createWidget({
            type: "bi.dimensions_manager",
            wId: this.model.get("id"),
            dimensionCreator: function (dId, regionType, op) {
                if (!dimensionsVessel[dId]) {
                    dimensionsVessel[dId] = BI.createWidget({
                        type: "bi.layout"
                    });
                    self.addSubVessel(dId, dimensionsVessel[dId]);
                    var dimensions = self.model.cat("dimensions");
                    if (!BI.has(dimensions, dId)) {
                        self.model.set("addDimension", {
                            dId: dId,
                            regionType: regionType,
                            src: op
                        });
                    }
                }

                //self.addSubVessel(dId, dimensionsVessel[dId]).skipTo(regionType + "/" + dId, dId, "dimensions." + dId);
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

    local: function () {
        if (this.model.has("addDimension")) {
            this.model.get("addDimension");
            return true;
        }
        return false;
    },

    _refreshDimensions: function () {
        var self = this;
        BI.each(self.model.cat("view"), function (regionType, dids) {
            BI.each(dids, function (i, dId) {
                self.skipTo(regionType + "/" + dId, dId, "dimensions." + dId, {}, {force: true});
            });
        });
    },

    refresh: function () {
        var self = this;

        this.dimensionsManager.populate();
        this._refreshDimensions();
        this.tableChartPopupulate();
    }
});