/**
 * 一个区域
 *
 * Created by GUY on 2016/3/17.
 * @class BI.TargetRegion
 * @extends BI.AbstractRegion
 */
BI.TargetRegion = BI.inherit(BI.AbstractRegion, {
    consts: {
        RECEIVE_TYPES: [BICst.TARGET_TYPE.FORMULA,
            BICst.TARGET_TYPE.MONTH_ON_MONTH_RATE,
            BICst.TARGET_TYPE.MONTH_ON_MONTH_VALUE,
            BICst.TARGET_TYPE.RANK,
            BICst.TARGET_TYPE.RANK_IN_GROUP,
            BICst.TARGET_TYPE.SUM_OF_ABOVE,
            BICst.TARGET_TYPE.SUM_OF_ABOVE_IN_GROUP,
            BICst.TARGET_TYPE.SUM_OF_ALL,
            BICst.TARGET_TYPE.SUM_OF_ALL_IN_GROUP,
            BICst.TARGET_TYPE.YEAR_ON_YEAR_RATE,
            BICst.TARGET_TYPE.YEAR_ON_YEAR_VALUE,
            BICst.TARGET_TYPE.NUMBER,
            BICst.TARGET_TYPE.COUNTER
        ]
    },

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
        this._createDragTool();
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
        // this.containers[dId].element.draggable();
        return this.containers[dId];
    },

    _getRegionConnect: function () {
        return "targets-container";
    },

    _getDragTipContent: function () {
        return BI.i18nText("BI-Drag_Left_Numberic_Field");
    },

    _fieldDragStart: function (tars) {
        var self = this;
        this.targets = tars;
        var hasNum = BI.some(tars, function (i, target) {
            return BI.contains(self.consts.RECEIVE_TYPES, target.type);
        });
        if (!hasNum) {
            this._showForbiddenMask();
        }
    },

    _fieldDragStop: function () {
        this.targets = null;
        this._hideForbiddenMask();
    },

    _getFieldDropOverHelper: function () {
        //可以放置的字段 + 不可放置的字段
        var self = this;
        var total = this.targets.length;
        var notNums = 0;
        BI.each(this.targets, function (i, target) {
            if (!BI.contains(self.consts.RECEIVE_TYPES, target.type)) {
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