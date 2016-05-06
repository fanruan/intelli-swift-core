/**
 * @class BI.DragWidgetitem
 * @extends BI.Widget
 * 复用的课拖拽的widget item
 */
BI.DragWidgetitem = BI.inherit(BI.Single, {
    _defaultConfig: function () {
        var conf = BI.DragWidgetitem.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: "bi-drag-widget-item",
            drag: BI.emptyFn,
            stop: BI.emptyFn,
            widget: {}
        })
    },

    _getWidgetClass: function (type) {
        switch (type) {
            case BICst.Widget.TABLE:
                return "chart-table-font";
            case BICst.Widget.BAR:
                return "chart-bar-font";
            case BICst.Widget.ACCUMULATE_BAR:
                return "chart-accumulate-bar-font";
            case BICst.Widget.PIE:
                return "chart-pie-font";
            case BICst.Widget.AXIS:
                return "chart-axis-font";
            case BICst.Widget.DASHBOARD:
                return "chart-dashboard-font";
            case BICst.Widget.MAP:
                return "chart-map-font";
            case BICst.Widget.DETAIL:
                return "chart-detail-font";
            case BICst.Widget.BUBBLE:
                return "chart-bubble-font";
            case BICst.Widget.SCATTER:
                return "chart-scatter-font";
            case BICst.Widget.RADAR:
                return "chart-radar-font";
            case BICst.Widget.STRING:
                return "chart-string-font";
            case BICst.Widget.NUMBER:
                return "chart-number-font";
            case BICst.Widget.DATE:
                return "chart-date-font";
            case BICst.Widget.YEAR:
                return "chart-year-font";
            case BICst.Widget.QUARTER:
                return "chart-quarter-font";
            case BICst.Widget.MONTH:
                return "chart-month-font";
            case BICst.Widget.TREE:
                return "chart-tree-font";
            case BICst.Widget.QUERY:
                return "chart-query-font";
            case BICst.Widget.RESET:
                return "chart-reset-font";
            case BICst.Widget.CROSS_TABLE:
                return "chart-table-font";
            case BICst.Widget.COMPLEX_TABLE:
                return "chart-table-font";
            case BICst.Widget.CONTENT:
                return "chart-content-font";
            case BICst.Widget.IMAGE:
                return "chart-image-font";
            case BICst.Widget.YMD:
                return "chart-ymd-font";
            case BICst.Widget.WEB:
                return "chart-web-font";
        }
    },

    _init: function () {
        BI.DragWidgetitem.superclass._init.apply(this, arguments);
        var o = this.options, self = this;
        this.button = BI.createWidget({
            type: "bi.multilayer_icon_tree_leaf_item",
            layer: o.layer,
            element: this.element,
            text: o.widget.name,
            value: o.widget.name,
            iconCls: this._getWidgetClass(o.widget.type)
        });

        var widget = this.options.widget;
        var result = {
            type: widget.type,
            name: widget.name,
            bounds: widget.bounds,
            settings: widget.settings
        };

        if (BI.has(widget, "value")) {
            result.value = widget.value;
        }

        this.button.element.draggable({
            cursor: BICst.cursorUrl,
            cursorAt: {left: 0, top: 0},
            drag: function (e, ui) {
                o.drag.apply(self, [result, ui.position]);
            },
            stop: function (e, ui) {
                var dimensionsAndView = self._createDimensionsAndView(widget);
                result.dimensions = dimensionsAndView.dimensions;
                result.view = dimensionsAndView.view;
                o.stop.apply(self, [result, ui.position]);
            },
            helper: o.helper
        });
    },

    _createDimensionsAndView: function (widget) {
        var self = this;
        this.oldDimensions = widget.dimensions;
        this.dimensions = {};
        this.view = {};
        this.dimTarIdMap = {};
        BI.each(widget.dimensions, function (idx, dimension) {
            var copy = self._createDimensionsAndTargets(idx);
            self.dimensions[copy.id] = copy.dimension;
            var region = BI.find(BI.keys(widget.view), function (id, regionId) {
                return BI.contains(widget.view[regionId], idx);
            });
            if (!BI.has(self.view, region)) {
                self.view[region] = [];
            }
            self.view[region].push(copy.id);
        });
        return {
            dimensions: this.dimensions,
            view: this.view
        }
    },

    _createDimensionsAndTargets: function (idx) {
        var self = this;
        var dimension = BI.deepClone(self.oldDimensions[idx]);
        if (BI.has(self.dimTarIdMap, idx)) {
            return {id: self.dimTarIdMap[idx], dimension: self.dimensions[self.dimTarIdMap[idx]] || dimension};
        }
        switch (self.oldDimensions[idx].type) {
            case BICst.TARGET_TYPE.STRING:
            case BICst.TARGET_TYPE.NUMBER:
            case BICst.TARGET_TYPE.DATE:
                if (BI.has(self.oldDimensions[idx], "dimension_map")) {
                    dimension.dimension_map = {};
                    BI.each(self.oldDimensions[idx].dimension_map, function (id, map) {
                        var result = self._createDimensionsAndTargets(id);
                        dimension.dimension_map[result.id] = map;
                    });
                }
                break;
            case BICst.TARGET_TYPE.FORMULA:
            case BICst.TARGET_TYPE.YEAR_ON_YEAR_RATE:
            case BICst.TARGET_TYPE.MONTH_ON_MONTH_RATE:
            case BICst.TARGET_TYPE.YEAR_ON_YEAR_VALUE:
            case BICst.TARGET_TYPE.MONTH_ON_MONTH_VALUE:
            case BICst.TARGET_TYPE.SUM_OF_ABOVE:
            case BICst.TARGET_TYPE.SUM_OF_ABOVE_IN_GROUP:
            case BICst.TARGET_TYPE.SUM_OF_ALL:
            case BICst.TARGET_TYPE.SUM_OF_ALL_IN_GROUP:
            case BICst.TARGET_TYPE.RANK:
            case BICst.TARGET_TYPE.RANK_IN_GROUP:
                var expression = dimension._src.expression;
                BI.each(expression.ids, function (id, tId) {
                    var result = self._createDimensionsAndTargets(tId);
                    if (BI.has(expression, "formula_value")) {
                        expression.formula_value = expression.formula_value.replace(tId, result.id);
                    }
                    expression.ids[id] = result.id;
                });
                break;
        }
        var id = BI.UUID();
        self.dimTarIdMap[idx] = id;
        return {id: id, dimension: dimension};
    },

    doRedMark: function () {
        this.button.doRedMark.apply(this.button, arguments);
    },

    unRedMark: function () {
        this.button.unRedMark.apply(this.button, arguments);
    },

    doHighLight: function () {
        this.button.doHighLight.apply(this.button, arguments);
    },

    unHighLight: function () {
        this.button.unHighLight.apply(this.button, arguments);
    }
});
$.shortcut("bi.drag_widget_item", BI.DragWidgetitem);