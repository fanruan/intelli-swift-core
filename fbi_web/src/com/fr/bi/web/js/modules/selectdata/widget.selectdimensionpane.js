/**
 * @class BI.DetailSelectDimensionPane
 * @extend BI.Widget
 * 复用指标维度面板
 */
BI.DetailSelectDimensionPane = BI.inherit(BI.Widget, {

    constants: {
        WIDGET: 0,
        TEMPLATE: 1,
        FOLDER: 2,
        CREATE_BY_ME_ID: "-1"
    },

    _defaultConfig: function () {
        return BI.extend(BI.DetailSelectDimensionPane.superclass._defaultConfig.apply(this, arguments), {
            wId: ""
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
            case BICst.Widget.Content:
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
        BI.DetailSelectDimensionPane.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.templateItems = [];
        this.widgetItems = {};
        this.searcher = BI.createWidget({
            type: "bi.simple_select_data_searcher",
            element: this.element,
            adapter: {
                el: {
                    el: {
                        chooseType: BI.ButtonGroup.CHOOSE_TYPE_MULTI
                    }
                }
            },
            popup: {
                segment: {}
            },
            itemsCreator: function (op, populate) {
                if (BI.isNotNull(op.keyword) && BI.isNotNull(op.searchType)) {
                    //var result = self._getSearchResult(op.searchType, op.keyword);
                    var result = {finded: [], matched: []};
                    populate(result.finded, result.matched);
                } else {
                    if (!op.node) {//根节点,获取文件夹及其模板信息
                        self._getTemplateStructure(populate);
                        return;
                    }
                    if (BI.isNotNull(op.node.isParent)) {
                        if (op.node.nodeType === self.constants.FOLDER) {
                            populate(self._findChildItemsFromItems(op.node.id, op.node.layer + 1));
                        }
                        if (op.node.nodeType === self.constants.TEMPLATE) {
                            self._getWidgetStructureByTemplateId(op.node.id, op.node.layer + 1, populate);
                        }
                        if (op.node.nodeType === self.constants.WIDGET) {
                            populate(self._getDimensionStructureByWidgetId(op.node.id, op.node.layer + 1));
                        }
                    }
                }
            }
        });
    },

    _findChildItemsFromItems: function (pId, layer) {
        var self = this;
        var items = BI.filter(this.templateItems, function (idx, item) {
            return item.pId === pId + "";
        });
        var templateItems = [], folderItems = [];
        BI.each(items, function (idx, item) {
            if (BI.has(item, "buildUrl")) {
                templateItems.push({
                    id: item.id,
                    pId: item.pId,
                    type: "bi.multilayer_icon_arrow_node",
                    iconCls: "file-font",
                    text: item.text,
                    title: item.text,
                    isParent: true,
                    value: item.id,
                    layer: layer,
                    nodeType: self.constants.TEMPLATE
                });
            } else {
                folderItems.push({
                    id: item.id,
                    pId: item.pId,
                    type: "bi.multilayer_icon_arrow_node",
                    iconCls: "folder-font",
                    text: item.text,
                    title: item.text,
                    isParent: true,
                    value: item.id,
                    layer: layer,
                    nodeType: self.constants.FOLDER
                });
            }
        });
        templateItems = BI.sortBy(templateItems, "text");
        folderItems = BI.sortBy(folderItems, "text");
        items = BI.concat(templateItems, folderItems);
        items = BI.filter(items, function (idx, item) {
            return BI.Utils.getCurrentTemplateId() !== item.id;
        });
        return items;
    },

    _getTemplateStructure: function (callback) {
        var self = this;
        var createByMe = {
            id: self.constants.CREATE_BY_ME_ID,
            pId: BI.UUID(),
            type: "bi.multilayer_icon_arrow_node",
            iconCls: "folder-font",
            text: BI.i18nText("BI-My_Created"),
            title: BI.i18nText("BI-My_Created"),
            isParent: true,
            value: self.constants.CREATE_BY_ME_ID,
            layer: 0,
            nodeType: self.constants.FOLDER
        };
        if (BI.isEmptyArray(this.templateItems)) {
            BI.Utils.getAllTemplates(function (res) {
                self.templateItems = res;
                self.templateItems.push(createByMe);
                var currentTemplate = BI.find(self.templateItems, function (idx, item) {
                    return item.id === BI.Utils.getCurrentTemplateId();
                });
                var currentTemplateItem = {
                    id: currentTemplate.id,
                    pId: BI.UUID(),
                    type: "bi.multilayer_icon_arrow_node",
                    iconCls: "file-font",
                    text: currentTemplate.text,
                    title: currentTemplate.text,
                    isParent: true,
                    value: currentTemplate.id,
                    layer: 0,
                    nodeType: self.constants.TEMPLATE
                };
                callback(BI.concat([currentTemplateItem, createByMe], self._findChildItemsFromItems(-1, 1)));
            });
        } else {
            var currentTemplate = BI.find(self.templateItems, function (idx, item) {
                return item.id === BI.Utils.getCurrentTemplateId();
            });
            var currentTemplateItem = {
                id: currentTemplate.id,
                pId: BI.UUID(),
                type: "bi.multilayer_icon_arrow_node",
                iconCls: "file-font",
                text: currentTemplate.text,
                title: currentTemplate.text,
                isParent: true,
                value: currentTemplate.id,
                layer: 0,
                nodeType: self.constants.TEMPLATE
            };
            callback(BI.concat([currentTemplateItem, createByMe], self._findChildItemsFromItems(-1, 1)));
        }
    },

    /**
     * 模板中的所有组件
     * @param id
     * @param layer
     * @param callback
     * @private
     */
    _getWidgetStructureByTemplateId: function (id, layer, callback) {
        var self = this, o = this.options;

        var call = function (widgets) {
            var result = BI.map(widgets, function (wId, widget) {
                return {
                    id: wId,
                    pId: id,
                    isParent: true,
                    layer: layer,
                    nodeType: self.constants.WIDGET,

                    type: "bi.multilayer_icon_arrow_node",
                    text: widget.name,
                    title: widget.name,
                    value: wId,
                    iconCls: self._getWidgetClass(widget.type)
                }
            });
            callback(BI.sortBy(result, "text"));
        };

        if (!self.widgetItems[id]) {
            BI.Utils.getWidgetsByTemplateId(id, function (data) {
                var widgets = {};
                if (id === BI.Utils.getCurrentTemplateId()) {
                    BI.each(data, function (wId, widget) {
                        if (wId !== o.wId) {
                            widgets[wId] = widget;
                        }
                    })
                } else {
                    BI.each(data, function (wId, widget) {
                        widgets[wId] = widget;
                    })
                }
                self.widgetItems[id] = widgets;
                call(self.widgetItems[id]);
            });
        } else {
            call(self.widgetItems[id]);
        }
    },

    _getAllDimensions: function (widgetId) {
        var self = this;
        var dimensions = {}, widget = {};
        BI.find(this.widgetItems, function (pId, wItems) {
            return widget = BI.find(wItems, function (wId, widget) {
                return wId === widgetId;
            });
        });
        if (BI.isNotNull(widget)) {
            var dims = [], tars = [], calcTars = [];
            var views = widget.view;
            BI.each(views, function (i, dim) {
                if (i >= BI.parseInt(BICst.REGION.DIMENSION1) && i < (BI.parseInt(BICst.REGION.TARGET1))) {
                    BI.each(dim, function (idx, dimId) {
                        widget.dimensions[dimId].dId = dimId;
                        dims.push(widget.dimensions[dimId]);
                    })
                } else {
                    BI.each(dim, function (idx, dimId) {
                        if (widget.dimensions[dimId].type === BICst.TARGET_TYPE.NUMBER || widget.dimensions[dimId].type === BICst.TARGET_TYPE.COUNTER) {
                            widget.dimensions[dimId].dId = dimId;
                            tars.push(widget.dimensions[dimId]);
                        } else {
                            widget.dimensions[dimId].dId = dimId;
                            calcTars.push(widget.dimensions[dimId]);
                        }
                    })
                }
            });
            dimensions = BI.concat(BI.concat(BI.sortBy(dims, "name"), BI.sortBy(tars, "name")), BI.sortBy(calcTars, "name"));
        }
        return dimensions;
    },

    _getDimensionStructureByWidgetId: function (wId, layer) {
        var self = this;
        var dimensionStructure = [];
        var targetStructure = [];
        var dimensions = this._getAllDimensions(wId);
        BI.each(dimensions, function (idx, dimension) {
            var dimensionName = dimension.name;
            if (dimension.type === BICst.TARGET_TYPE.STRING || dimension.type === BICst.TARGET_TYPE.NUMBER || dimension.type === BICst.TARGET_TYPE.DATE
                || dimension.type === BICst.TARGET_TYPE.COUNTER) {
                dimensionStructure.push({
                    id: dimension.dId,
                    pId: wId,
                    type: "bi.detail_select_dimension_level0_item",
                    layer: layer,
                    fieldType: BI.Utils.getFieldTypeByID(dimension._src.field_id),
                    text: dimensionName,
                    title: dimensionName,
                    value: dimension,
                    drag: self._createDrag(dimensionName)
                });
            } else {
                dimensionStructure.push({
                    id: dimension.dId,
                    pId: wId,
                    type: "bi.detail_select_calculation_target_level0_item",
                    layer: layer,
                    text: dimensionName,
                    title: dimensionName,
                    value: dimension,
                    dimensions: dimensions,
                    drag: self._createDrag(dimensionName, dimensions)
                });
            }
        });

        return BI.concat(dimensionStructure, targetStructure);
    },

    /**
     * 复制指标，维度，计算指标
     * @param old
     * @param dimTarIdMap
     * @param dimensions
     * @returns []
     * @private
     */
    _createDimensionsAndTargets: function (old, dimTarIdMap, dimensions) {
        var self = this;
        var result = [];
        var dimension = BI.deepClone(old);
        if (BI.has(dimTarIdMap, old.dId)) {
            var dim = dimensions[dimTarIdMap[old.dId]] || dimension;
            dim.dId = dimTarIdMap[old.dId];
            return [dim];
        }
        switch (old.type) {
            case BICst.TARGET_TYPE.STRING:
            case BICst.TARGET_TYPE.NUMBER:
            case BICst.TARGET_TYPE.DATE:
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
                    var find = BI.find(dimensions, function (idx, d) {
                        return d.dId === tId;
                    });
                    var r = self._createDimensionsAndTargets(find, dimTarIdMap, dimensions);
                    if (BI.has(expression, "formula_value")) {
                        expression.formula_value = expression.formula_value.replace(tId, r[0].dId);
                    }
                    expression.ids[id] = r[0].dId;
                    result = BI.concat(result, r);
                });
                break;
        }
        var id = BI.UUID();
        dimTarIdMap[old.dId] = id;
        dimension.dId = id;
        result.push(dimension);
        return result;
    },

    /**
     *
     * @param dimensionName
     * @param dimensions
     * @returns {{cursor: *, cursorAt: {left: number, top: number}, helper: helper}}
     * @private
     */
    _createDrag: function (dimensionName, dimensions) {
        var self = this;
        return {
            cursor: BICst.cursorUrl,
            cursorAt: {left: 5, top: 5},
            helper: function () {
                var text = dimensionName;
                var result = [];
                var dims = self.searcher.getValue();
                var targetIdMap = {};
                BI.each(dims, function (idx, dim) {
                    var copy = self._createDimensionsAndTargets(dim, targetIdMap, dimensions);
                    BI.each(copy, function (idx, obj) {
                        if (!BI.deepContains(result, obj)) {
                            result.push(obj);
                        }
                    });
                });
                if (result.length > 1) {
                    text = BI.i18nText("BI-All_Field_Count", result.length);
                }
                var data = BI.map(result, function (id, dim) {
                    var type = dim.type;
                    if (type === BICst.TARGET_TYPE.STRING || type === BICst.TARGET_TYPE.NUMBER || type === BICst.TARGET_TYPE.DATE || type === BICst.TARGET_TYPE.COUNTER) {
                        var dimension = {
                            dId: dim.dId,
                            type: dim.type,
                            name: dim.name,
                            _src: dim._src
                        };

                        var sort = dim.sort;
                        var group = dim.group;
                        var filter_value = dim.filter_value;
                        if (BI.isNotNull(sort) && sort.type === BICst.SORT.CUSTOM) {
                            dimension.sort = sort;
                        }
                        var groupArray = [BICst.GROUP.CUSTOM_GROUP, BICst.SUMMARY_TYPE.SUM, BICst.SUMMARY_TYPE.MAX
                            , BICst.SUMMARY_TYPE.MIN, BICst.SUMMARY_TYPE.AVG
                        ];
                        if (BI.isNotNull(group) && BI.contains(groupArray, group.type)) {
                            dimension.group = group;
                        }
                        if (BI.isNotNull(filter_value)) {
                            dimension.filter_value = filter_value;
                        }
                        return dimension;
                    } else {
                        return dim;
                    }
                });
                var help = BI.createWidget({
                    type: "bi.helper",
                    data: {data: data},
                    text: text
                });
                BI.createWidget({
                    type: "bi.absolute",
                    element: "body",
                    items: [{
                        el: help
                    }]
                });
                return help.element;
            }
        }
    },

    populate: function () {
        this.searcher.populate();
    }
});
$.shortcut("bi.detail_select_dimension_data_pane", BI.DetailSelectDimensionPane);