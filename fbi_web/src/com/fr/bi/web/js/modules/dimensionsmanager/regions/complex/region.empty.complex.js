/**
 * Created by Young's on 2016/9/12.
 */
BI.ComplexEmptyRegion = BI.inherit(BI.Widget, {
    _defaultConfig: function() {
        return BI.extend(BI.ComplexEmptyRegion.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-complex-empty-region"
        });
    },

    _init: function() {
        BI.ComplexEmptyRegion.superclass._init.apply(this, arguments);
        var self = this;
        var emptyRegion = BI.createWidget({
            type: "bi.vertical",
            element: this.element
        });
        emptyRegion.element.droppable({
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
                    self.addDimension(dimension.dId || BI.UUID(), dimension)
                });
                if (data.length > 0) {
                    self.fireEvent(BI.ComplexEmptyRegion.EVENT_CHANGE);
                }
            },
            over: function (event, ui) {

            },
            out: function (event, ui) {

            }
        });
    }
});
BI.ComplexEmptyRegion.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.complex_empty_region", BI.ComplexEmptyRegion);