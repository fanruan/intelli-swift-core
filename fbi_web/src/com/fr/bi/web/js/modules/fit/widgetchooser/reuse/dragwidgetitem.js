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
            case BICst.WIDGET.TABLE:
                return "chart-table-font";
            case BICst.WIDGET.BAR:
                return "chart-bar-font";
            case BICst.WIDGET.ACCUMULATE_BAR:
                return "chart-accumulate-bar-font";
            case BICst.WIDGET.PIE:
                return "chart-pie-font";
            case BICst.WIDGET.AXIS:
                return "chart-axis-font";
            case BICst.WIDGET.DASHBOARD:
                return "chart-dashboard-font";
            case BICst.WIDGET.MAP:
                return "chart-map-font";
            case BICst.WIDGET.DETAIL:
                return "chart-detail-font";
            case BICst.WIDGET.BUBBLE:
                return "chart-bubble-font";
            case BICst.WIDGET.SCATTER:
                return "chart-scatter-font";
            case BICst.WIDGET.RADAR:
                return "chart-radar-font";
            case BICst.WIDGET.STRING:
                return "chart-string-font";
            case BICst.WIDGET.NUMBER:
                return "chart-number-font";
            case BICst.WIDGET.DATE:
                return "chart-date-font";
            case BICst.WIDGET.YEAR:
                return "chart-year-font";
            case BICst.WIDGET.QUARTER:
                return "chart-quarter-font";
            case BICst.WIDGET.MONTH:
                return "chart-month-font";
            case BICst.WIDGET.TREE:
                return "chart-tree-font";
            case BICst.WIDGET.GENERAL_QUERY:
                return "chart-general-query-font";
            case BICst.WIDGET.QUERY:
                return "chart-query-font";
            case BICst.WIDGET.RESET:
                return "chart-reset-font";
            case BICst.WIDGET.CROSS_TABLE:
                return "chart-table-font";
            case BICst.WIDGET.COMPLEX_TABLE:
                return "chart-table-font";
            case BICst.WIDGET.CONTENT:
                return "chart-content-font";
            case BICst.WIDGET.IMAGE:
                return "chart-image-font";
            case BICst.WIDGET.YMD:
                return "chart-ymd-font";
            case BICst.WIDGET.WEB:
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
                o.drag.apply(self, [widget.bounds, ui.position, widget]);
            },
            stop: function (e, ui) {
                var dimensionsAndView = self._createDimensionsAndView(widget);
                result.dimensions = dimensionsAndView.dimensions;
                result.view = dimensionsAndView.view;
                o.stop.apply(self, [widget.bounds, ui.position, widget]);
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
        });
        BI.each(widget.view, function(region, dimIds){
            self.view[region] = [];
            BI.each(dimIds, function(idx, dId){
                self.view[region].push(self.dimTarIdMap[dId]);
            });
        });
        return {
            dimensions: this.dimensions,
            view: this.view
        }
    },

    _checkFilter: function(oldFilter, dId){
        var filter = {};
        var filterType = oldFilter.filter_type, filterValue = oldFilter.filter_value;
        filter.filter_type = oldFilter.filter_type;
        if (filterType === BICst.FILTER_TYPE.AND || filterType === BICst.FILTER_TYPE.OR) {
            filter.filter_value = [];
            BI.each(filterValue, function (i, value) {
                filter.filter_value.push(this._checkFilter(value));
            });
        }else{
            filter.filter_value = oldFilter.filter_value;
            if(BI.has(oldFilter, "target_id") && oldFilter.target_id !== dId){
                var result = this._createDimensionsAndTargets(oldFilter.target_id);
                filter.target_id = result.id;
            }
        }
        return filter;
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
                        //明细表dimensionmap存的key是tableId，与汇总表区分
                        if(BI.Utils.isDimensionExist(id)){
                            var result = self._createDimensionsAndTargets(id);
                            dimension.dimension_map[result.id] = map;
                        }else{
                            dimension.dimension_map[id] = map;
                        }
                    });
                }
                if(BI.has(self.oldDimensions[idx], "filter_value")){
                    dimension.filter_value = this._checkFilter(self.oldDimensions[idx].filter_value, self.dimTarIdMap[idx] || idx);
                }
                if(BI.has(self.oldDimensions[idx], "sort")){
                    dimension.sort = BI.deepClone(self.oldDimensions[idx].sort);
                    if(BI.has(dimension.sort, "sort_target")){
                        var result = self._createDimensionsAndTargets(dimension.sort.sort_target);
                        dimension.sort.sort_target = result.id;
                    }
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