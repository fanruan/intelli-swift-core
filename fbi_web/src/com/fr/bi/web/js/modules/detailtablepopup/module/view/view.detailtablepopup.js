/**
 * 指标弹出明细表
 *
 * Created by GUY on 2016/5/16.
 * @class BI.DetailTablePopupView
 * @extends BI.Widget
 */
BI.DetailTablePopupView = BI.inherit(BI.View, {

    constants: {
        DETAIL_NORTH_HEIGHT: 40,
        DETAIL_TAB_HEIGHT: 40,
        DETAIL_WEST_WIDTH: 280,
        DETAIL_DATA_STYLE_HEIGHT: 200,
        DETAIL_GAP_NORMAL: 10,
        DETAIL_PANE_HORIZONTAL_GAP: 20
    },

    _defaultConfig: function () {
        return BI.extend(BI.DetailTablePopupView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-table-popup-view"
        });
    },

    _init: function () {
        BI.DetailTablePopupView.superclass._init.apply(this, arguments);

    },

    _render: function () {
        var self = this, o = this.options;
        BI.createWidget({
            type: "bi.border",
            element: this.element,
            items: {
                north: {el: this._buildNorth(), height: this.constants.DETAIL_NORTH_HEIGHT},
                west: {el: this._buildWest(), width: this.constants.DETAIL_WEST_WIDTH},
                center: {el: this._buildCenter()}
            }
        });
    },

    _buildNorth: function () {
        var self = this;
        var shrink = BI.createWidget({
            type: "bi.button",
            height: 25,
            title: BI.i18nText('BI-Return_To_Dashboard'),
            text: BI.i18nText('BI-Detail_Set_Complete')
        });
        shrink.on(BI.Button.EVENT_CHANGE, function () {
            self.notifyParentEnd();
        });
        return BI.createWidget({
            type: "bi.right_vertical_adapt",
            items: [shrink],
            hgap: this.constants.DETAIL_PANE_HORIZONTAL_GAP
        });
    },

    _buildWest: function () {
        //TODO 实时报表
        return BI.createWidget({
            type: "bi.absolute",
            cls: "widget-attr-west",
            items: [{
                el: {
                    type: "bi.detail_table_popup_select_data",
                    cls: "widget-select-data-pane"
                },
                left: this.constants.DETAIL_PANE_HORIZONTAL_GAP,
                top: this.constants.DETAIL_GAP_NORMAL,
                bottom: this.constants.DETAIL_GAP_NORMAL,
                right: this.constants.DETAIL_PANE_HORIZONTAL_GAP
            }]
        });
    },

    _buildCenter: function () {
        var self = this;
        var table = this._createTable();
        var tab = this._createTypeAndData();

        return BI.createWidget({
            type: "bi.absolute",
            cls: "widget-attr-center",
            items: [{
                el: {
                    type: "bi.border",
                    items: {
                        north: {
                            el: tab,
                            height: this.constants.DETAIL_DATA_STYLE_HEIGHT,
                            bottom: this.constants.DETAIL_GAP_NORMAL
                        },
                        center: table
                    }
                },
                left: 0,
                right: this.constants.DETAIL_PANE_HORIZONTAL_GAP,
                top: this.constants.DETAIL_GAP_NORMAL,
                bottom: this.constants.DETAIL_GAP_NORMAL
            }]
        });
    },


    _createTypeAndData: function () {
        var self = this;
        var dimensionsVessel = {};
        this.dimensionsManager = BI.createWidget({
            type: "bi.detail_table_popup_dimensions_manager",
            cls: "widget-top-wrapper",
            dimensionCreator: function (dId, regionType, op) {

                if (!dimensionsVessel[dId]) {
                    dimensionsVessel[dId] = BI.createWidget({
                        type: "bi.layout"
                    });
                    var dimensions = self.model.cat("dimensions");
                    if (!BI.has(dimensions, dId)) {
                        self.model.set("addDimension", {
                            dId: dId,
                            regionType: regionType,
                            src: op
                        });
                    }
                }
                self.addSubVessel(dId, dimensionsVessel[dId]).skipTo("detailtablepopup/" + dId, dId, "dimensions." + dId);
                return dimensionsVessel[dId];

            }
        });

        this.dimensionsManager.on(BI.DetailTablePopupDimensionsManager.EVENT_CHANGE, function () {
            var values = this.getValue();
            self.model.set(values);
            this.populate();
        });


        return this.dimensionsManager;
    },

    _createTable: function () {
        var self = this;
        this.table = BI.createWidget({
            type: "bi.detail_table_popup_detail_table",
            cls: "widget-center-wrapper"
        });
        this.table.on(BI.DetailTablePopupDetailTable.EVENT_CHANGE, function (ob) {

        });
        return this.table;
    },

    splice: function (old, key1, key2) {
        if (key1 === "dimensions") {
            this.dimensionsManager.populate();
        }
    },

    change: function (changed, prev) {
        if (BI.has(changed, "dimensions") ||
            BI.has(changed, "sort_sequence") ||
            BI.has(changed, "view") ||
            BI.has(changed, "filter_value") ||
            (BI.has(changed, "target_relation"))) {
            this.table.populate();
        }
        if (BI.has(changed, "settings")) {
            this.table.populate();
        }
    },


    local: function () {
        if (this.model.has("addDimension")) {
            this.model.get("addDimension");
            return true;
        }
        if (this.model.has("addCalculate")) {
            this.model.get("addCalculate");
            return true;
        }
        if (this.model.has("sorted")) {
            this.model.get("sorted");
            return true;
        }
        return false;
    },

    refresh: function () {
        var self = this;
        this.dimensionsManager.populate();
        this.table.populate();
    }
});