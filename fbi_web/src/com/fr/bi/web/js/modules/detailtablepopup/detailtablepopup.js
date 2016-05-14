/**
 * 指标弹出明细表
 *
 * Created by GUY on 2016/5/13.
 * @class BI.DetailTablePopup
 * @extends BI.Widget
 */
BI.DetailTablePopup = BI.inherit(BI.Widget, {

    constants: {
        DETAIL_NORTH_HEIGHT: 40,
        DETAIL_TAB_HEIGHT: 40,
        DETAIL_WEST_WIDTH: 280,
        DETAIL_DATA_STYLE_HEIGHT: 240,
        DETAIL_GAP_NORMAL: 10,
        DETAIL_PANE_HORIZONTAL_GAP: 20
    },

    _defaultConfig: function () {
        return BI.extend(BI.DetailTablePopup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-table-popup",
            dId: ""
        });
    },

    _init: function () {
        BI.DetailTablePopup.superclass._init.apply(this, arguments);
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
            self.fireEvent(BI.DetailTablePopup.EVENT_CHANGE);
        });
        return BI.createWidget({
            type: "bi.right_vertical_adapt",
            items: [shrink],
            hgap: this.constants.DETAIL_PANE_HORIZONTAL_GAP
        });
    },

    _buildWest: function () {
        return BI.createWidget({
            type: "bi.absolute",
            items: [{
                el: {
                    type: BI.Utils.isRealTime() ? "bi.detail_select_data_4_realtime" : "bi.detail_detail_table_select_data",
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
        var self = this;
        var table = this._createTable();
        this.center = BI.createWidget({
            type: "bi.default",
            cls: "widget-center-wrapper"
        });

        var tab = BI.createWidget({
            type: "bi.data_style_tab",
            cardCreator: BI.bind(self._createTabs, this)
        });

        return BI.createWidget({
            type: "bi.absolute",
            cls: "widget-attr-center",
            items: [{
                el: {
                    type: "bi.border",
                    cls: "widget-show-data-pane",
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
            type: "bi.dimensions_manager",
            wId: this.model.get("id"),
            dimensionCreator: function (dId, regionType, op) {
                var relationItem = op.relationItem;
                if (BI.isNotNull(relationItem)) {
                    self.model.set("setRelation", {
                        dId: dId,
                        relationItem: op.relationItem
                    });
                }

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
                self.addSubVessel(dId, dimensionsVessel[dId]).skipTo(regionType + "/" + dId, dId, "dimensions." + dId);
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

    _createStyle: function () {
        return BI.createWidget({
            type: "bi.label",
            text: "Style"
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

    _createTable: function () {
        var self = this;
        this.table = BI.createWidget({
            type: "bi.detail_table",
            cls: "widget-center-wrapper",
            wId: this.model.get("id")
        });
        this.table.on(BI.DetailTable.EVENT_CHANGE, function (ob) {

        });
        return this.table;
    },

    populate: function () {
        var self = this, o = this.options;
    }
});

BI.DetailTablePopup.EVENT_CHANGE = "DetailTablePopup.EVENT_CHANGE";
$.shortcut('bi.detail_table_popup', BI.DetailTablePopup);