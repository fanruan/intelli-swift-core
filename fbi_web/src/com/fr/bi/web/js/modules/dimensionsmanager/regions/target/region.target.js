/**
 * 一个区域
 *
 * Created by GUY on 2016/3/17.
 * @class BI.TargetRegion
 * @extends BI.AbstractRegion
 */
BI.TargetRegion = BI.inherit(BI.AbstractRegion, {

    _defaultConfig: function () {
        var conf = BI.TargetRegion.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            extraCls: 'bi-target-region',
            regionType: BICst.REGION.TARGET1
        })
    },

    _init: function () {
        BI.TargetRegion.superclass._init.apply(this, arguments);
        this.containers = {};
        // this._createDragTool();
    },

    _createDragTool: function () {
        var dragTool = BI.createWidget({
            type: "bi.layout",
            cls: "complex-region-left-drag-background drag-tool",
            width: 10
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: dragTool,
                left: 0,
                top: 0,
                bottom: 0
            }]
        });
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
                cls: "target-container",
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

    _getRegionConnect: function () {
        return "targets-container";
    },

    _getDragTipContent: function () {
        return BI.i18nText("BI-Drag_Left_Numberic_Field");
    },

    _fieldDragStart: function (fields) {
        this.fields = fields;
        var hasNum = BI.some(fields, function (i, fieldId) {
            var fieldType = BI.Utils.getFieldTypeByID(fieldId);
            return fieldType === BICst.COLUMN.NUMBER || fieldType === BICst.COLUMN.COUNTER;
        });
        if (!hasNum) {
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
        var notNums = 0;
        BI.each(this.fields, function (i, fieldId) {
            var fieldType = BI.Utils.getFieldTypeByID(fieldId);
            if (fieldType !== BICst.COLUMN.COUNTER &&
                fieldType !== BICst.COLUMN.NUMBER) {
                notNums++;
            }
        });
        if (notNums > 0 && notNums !== total) {
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
                        text: total - notNums
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
                        text: notNums
                    }],
                    lgap: 5
                }],
                rgap: 5
            });
        } else if (notNums === total) {
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
        var self = this, o = this.options || {};
        var result = [];
        var dimensions = $(".target-container", this.center.element);
        BI.each(dimensions, function (i, dom) {
            var dId = $(dom).data("dId");
            if (BI.isNull(self.containers[dId])) {
                var dim = o.dimensionCreator(dId, o.regionType, o);
                self.containers[dId] = BI.createWidget({
                    type: "bi.absolute",
                    element: dom,
                    items: [{
                        el: dim,
                        left: 0,
                        top: 0,
                        right: 0,
                        bottom: 0
                    }]
                });
            }
            result.push(dId);
        });
        return result;
    }
});
$.shortcut("bi.target_region", BI.TargetRegion);