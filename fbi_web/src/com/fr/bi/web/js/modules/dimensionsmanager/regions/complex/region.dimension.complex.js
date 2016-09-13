/**
 * Created by Young's on 2016/9/12.
 */
BI.ComplexDimensionRegion = BI.inherit(BI.Widget, {
    constants: {
        TITLE_ICON_HEIGHT: 20,
        TITLE_ICON_WIDTH: 20,
        REGION_HEIGHT_NORMAL: 25,
        REGION_DIMENSION_GAP: 5
    },

    _defaultConfig: function () {
        return BI.extend(BI.ComplexDimensionRegion.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-complex-dimension-region",
            wId: ""
        });
    },

    _init: function () {
        BI.ComplexDimensionRegion.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.store = {};
        this.containers = {};

        this.sinlgeRegion = BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            cls: "dimensions-container",
            scrolly: true,
            hgap: this.constants.REGION_DIMENSION_GAP,
            vgap: this.constants.REGION_DIMENSION_GAP
        });
        this.sinlgeRegion.element.droppable({
            accept: ".select-data-level0-item-button, .select-date-level1-item-button",
            tolerance: "pointer",
            drop: function (event, ui) {
                var helper = ui.helper;
                var data = helper.data("data");
                if (self.options.regionType >= BICst.REGION.TARGET1) {
                    data = BI.filter(data, function (i, dimension) {
                        return dimension.type === BICst.TARGET_TYPE.NUMBER || dimension.type === BICst.TARGET_TYPE.COUNTER || dimension.type === BICst.TARGET_TYPE.FORMULA ||
                            dimension.type === BICst.TARGET_TYPE.YEAR_ON_YEAR_RATE ||
                            dimension.type === BICst.TARGET_TYPE.MONTH_ON_MONTH_RATE ||
                            dimension.type === BICst.TARGET_TYPE.YEAR_ON_YEAR_VALUE ||
                            dimension.type === BICst.TARGET_TYPE.MONTH_ON_MONTH_VALUE ||
                            dimension.type === BICst.TARGET_TYPE.SUM_OF_ABOVE ||
                            dimension.type === BICst.TARGET_TYPE.SUM_OF_ABOVE_IN_GROUP ||
                            dimension.type === BICst.TARGET_TYPE.SUM_OF_ALL ||
                            dimension.type === BICst.TARGET_TYPE.SUM_OF_ALL_IN_GROUP ||
                            dimension.type === BICst.TARGET_TYPE.RANK ||
                            dimension.type === BICst.TARGET_TYPE.RANK_IN_GROUP;
                    });
                }
                if (self.options.regionType < BICst.REGION.TARGET1) {
                    data = BI.filter(data, function (i, dimension) {
                        return dimension.type === BICst.TARGET_TYPE.STRING || dimension.type === BICst.TARGET_TYPE.DATE || dimension.type === BICst.TARGET_TYPE.NUMBER;
                    });
                }
                BI.each(data, function (i, dimension) {
                    self.addDimension(dimension.dId || BI.UUID(), dimension);
                });
                if (data.length > 0) {
                    self.fireEvent(BI.ComplexDimensionRegion.EVENT_CHANGE);
                }
            },
            over: function (event, ui) {

            },
            out: function (event, ui) {

            }
        });
    },

    addDimension: function (dId, options) {
        this.store[dId] = this._createDimension(dId, options);
        this.sinlgeRegion.addItem(this.store[dId]);
    },

    _createDimension: function (dId, options) {
        var self = this, o = this.options;
        options || (options = {});
        var dim = o.dimensionCreator(dId, this.options.regionType, options);
        if (this.containers[dId]) {
            BI.createWidget({
                type: "bi.absolute",
                element: this.containers[dId],
                items: [{
                    el: dim,
                    left: 0,
                    top: 0,
                    right: 0,
                    bottom: 0
                }]
            });
        } else {
            var container = BI.createWidget({
                type: "bi.absolute",
                cls: "dimension-container",
                data: {
                    dId: dId
                },
                height: 25,
                items: [{
                    el: dim,
                    left: 0,
                    top: 0,
                    right: 0,
                    bottom: 0
                }]
            });
            this.containers[dId] = container;
        }
        return this.containers[dId];
    },

    getValue: function () {
        var result = [];
        var dimensions = $(".dimension-container", this.center.element);
        BI.each(dimensions, function (i, dom) {
            result.push($(dom).data("dId"));
        });
        return result;
    },

    populate: function (dimensions) {
        var self = this, o = this.options;
        BI.DOM.hang(this.store);
        var store = this.store;
        this.store = {};
        BI.each(dimensions, function (i, did) {
            self.store[did] = self._createDimension(did);
            self.sinlgeRegion.addItem(self.store[did]);
        })
    }
});
BI.ComplexDimensionRegion.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.complex_dimension_region", BI.ComplexDimensionRegion);
