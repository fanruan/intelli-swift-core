/**
 * 一个区域
 *
 * Created by GUY on 2016/3/16.
 * @class BI.DimensionRegion
 * @extends BI.AbstractRegion
 */
BI.DimensionRegion = BI.inherit(BI.AbstractRegion, {

    _defaultConfig: function () {
        var conf = BI.DimensionRegion.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            extraCls: 'bi-dimension-region',
            regionType: BICst.REGION.DIMENSION1
        })
    },

    _init: function () {
        BI.DimensionRegion.superclass._init.apply(this, arguments);
        this.containers = {};
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

    _getDragTipContent: function () {
        return BI.i18nText("BI-Drag_Left_Field");
    },

    _fieldDragStart: function (fields) {
        this.fields = fields;
        var onlyCounter = !BI.some(fields, function (i, fieldId) {
            return BI.Utils.getFieldTypeByID(fieldId) !== BICst.COLUMN.COUNTER && BI.isNotNull(fieldId);
        });
        if (onlyCounter) {
            this._showForbiddenMask();
        }
    },

    _fieldDragStop: function () {
        this.fields = null;
        this._hideForbiddenMask();
    },

    _getFieldDropOverHelper: function () {
        //可以放置的字段 + 不可放置的字段
        var total = this.fields.length;
        var counters = 0;
        BI.each(this.fields, function (i, fieldId) {
            if (BI.Utils.getFieldTypeByID(fieldId) === BICst.COLUMN.COUNTER || BI.isNull(fieldId)) {
                counters++;
            }
        });
        if (counters > 0 && counters !== total) {
            return BI.createWidget({
                type: "bi.left",
                cls: "helper-warning",
                items: [{
                    type: "bi.left",
                    cls: "drag-helper-active-font",
                    items: [{
                        type: "bi.icon",
                        width: 20,
                        height: 20
                    }, {
                        type: "bi.label",
                        text: total - counters
                    }],
                    lgap: 5
                }, {
                    type: "bi.left",
                    cls: "drag-helper-forbidden-font",
                    items: [{
                        type: "bi.icon",
                        width: 20,
                        height: 20
                    }, {
                        type: "bi.label",
                        text: counters
                    }],
                    lgap: 5
                }],
                rgap: 5
            });
        } else if (counters === total) {
            return BI.createWidget({
                type: "bi.left",
                cls: "helper-warning drag-helper-forbidden-font",
                items: [{
                    type: "bi.icon",
                    width: 20,
                    height: 20
                }],
                hgap: 5
            });
        }
    },

    getValue: function () {
        var result = [];
        var dimensions = $(".dimension-container", this.center.element);
        BI.each(dimensions, function (i, dom) {
            result.push($(dom).data("dId"));
        });
        return result;
    }
});
$.shortcut("bi.dimension_region", BI.DimensionRegion);