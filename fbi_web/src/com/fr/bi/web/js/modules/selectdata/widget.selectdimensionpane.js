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
                segment: {

                }
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
                        if (op.node.nodeType === self.constants.FOLDER){
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

    _findChildItemsFromItems: function(pId, layer){
        var self = this;
        var items = BI.filter(this.templateItems, function(idx, item){
            return item.pId === pId + "";
        });
        var templateItems = [], folderItems = [];
        BI.each(items, function (idx, item) {
            if(BI.has(item, "buildUrl")){
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
            }else{
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
        items = BI.filter(items, function(idx, item){
            return BI.Utils.getCurrentTemplateId() !== item.id;
        });
        return items;
    },

    _getTemplateStructure: function(callback){
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
        if(BI.isEmptyArray(this.templateItems)){
            BI.Utils.getAllTemplates(function(res){
                self.templateItems = res;
                self.templateItems.push(createByMe);
                var currentTemplate = BI.find(self.templateItems, function(idx, item){
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
        }else{
            var currentTemplate = BI.find(self.templateItems, function(idx, item){
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

    _getSearchResult: function (type, keyword) {
        var self = this;
        var searchResult = [], matchResult = [];
        var widgetsIDs = BI.Utils.getAllWidgetIDs();
        widgetsIDs = BI.filter(widgetsIDs, function (idx, wId) {
            return wId !== self.options.wId;
        });
        //选择了组件
        if (type & BI.SelectDataSearchSegment.SECTION_TABLE) {
            var result = [];
            //todo 获取所有模板， 暂时做成获取当前模板所有组件
            var items = self._getWidgetStructureByTemplateId();
            result.push(BI.Func.getSearchResult(items, keyword));
            BI.each(result, function (i, sch) {
                searchResult = searchResult.concat(sch.finded);
                matchResult = matchResult.concat(sch.matched);
            })
        } else {
            var result = [], map = {};
            var items = [];
            BI.each(widgetsIDs, function (i, wid) {
                items = items.concat(self._getDimensionStructureByWidgetId(wid));
            });
            result.push(BI.Func.getSearchResult(items, keyword));
            BI.each(result, function (i, sch) {
                BI.each(sch.finded, function (j, finded) {
                    if (!map[finded.pId]) {
                        searchResult.push({
                            id: finded.pId,
                            type: "bi.detail_select_data_level0_node",
                            text: BI.Utils.getWidgetNameByID(finded.pId),
                            title: BI.Utils.getWidgetNameByID(finded.pId),
                            value: finded.pId,
                            isParent: true,
                            open: true
                        });
                        map[finded.pId] = true;
                    }
                });
                searchResult = searchResult.concat(sch.finded);
                matchResult = matchResult.concat(sch.matched);
            })
        }
        return {
            finded: searchResult,
            matched: matchResult
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

        var call = function(widgets){
            var result = BI.map(widgets, function(wId, widget){
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

        if(!self.widgetItems[id]){
            BI.Utils.getWidgetsByTemplateId(id, function(data){
                var widgets = {};
                if(id === BI.Utils.getCurrentTemplateId()){
                    BI.each(data, function(wId, widget){
                        if (wId !== o.wId) {
                            widgets[wId] = widget;
                        }
                    })
                }else{
                    BI.each(data, function(wId, widget){
                        widgets[wId] = widget;
                    })
                }
                self.widgetItems[id] = widgets;
                call(self.widgetItems[id]);
            });
        }else{
            call(self.widgetItems[id]);
        }
    },

    _getAllDimensions: function(widgetId){
        var self = this;
        var dimensions = {}, widget = {};
        BI.find(this.widgetItems, function(pId, wItems){
            return widget = BI.find(wItems, function(wId, widget){
                return wId === widgetId;
           });
        });
        if (BI.isNotNull(widget)) {
            var dims = [], tars = [], calcTars = [];
            var views = widget.view;
            BI.each(views, function (i, dim) {
                if (i >= BI.parseInt(BICst.REGION.DIMENSION1) && i < (BI.parseInt(BICst.REGION.TARGET1))) {
                    BI.each(dim, function (idx, dimId) {
                        dims.push(widget.dimensions[dimId]);
                    })
                } else {
                    BI.each(dim, function (idx, dimId) {
                        if(widget.dimensions[dimId].type === BICst.TARGET_TYPE.NUMBER){
                            widget.dimensions[dimId].dId = dimId;
                            tars.push(widget.dimensions[dimId]);
                        }else{
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

    _createCalcStructure: function(dimensions, targetIds, targetIdMap, calcId){
        var self = this;
        var calc = dimensions[calcId];
        calc._src.expression.cal_target_name = BI.isArray(calc._src.expression.cal_target_name) ? calc._src.expression.cal_target_name : [calc._src.expression.cal_target_name];
        BI.each(calc._src.expression.cal_target_name, function(idx, tId){
            if(BI.contains(targetIds, tId) && !BI.has(targetIdMap, tId)){
                targetIdMap[tId] = BI.UUID();
                dimensions[targetIdMap[tId]] = dimensions[tId];
                dimensions[targetIdMap[tId]].dId = targetIdMap[tId];
            }
            var type = BI.has(targetIdMap, tId) ? dimensions[targetIdMap[tId]].type : dimensions[tId].type;
            if(BI.has(calc._src.expression, "formula_value")){
                calc._src.expression.formula_value = calc._src.expression.formula_value.replace(tId, targetIdMap[tId] || tId);
            }
            calc._src.expression.cal_target_name[idx] = targetIdMap[tId] || tId;
            if(type === BICst.TARGET_TYPE.FORMULA ||
                type === BICst.TARGET_TYPE.YEAR_ON_YEAR_RATE ||
                type === BICst.TARGET_TYPE.MONTH_ON_MONTH_RATE ||
                type === BICst.TARGET_TYPE.YEAR_ON_YEAR_VALUE ||
                type === BICst.TARGET_TYPE.MONTH_ON_MONTH_VALUE ||
                type === BICst.TARGET_TYPE.SUM_OF_ABOVE ||
                type === BICst.TARGET_TYPE.SUM_OF_ABOVE_IN_GROUP ||
                type === BICst.TARGET_TYPE.SUM_OF_ALL ||
                type === BICst.TARGET_TYPE.SUM_OF_ALL_IN_GROUP ||
                type === BICst.TARGET_TYPE.RANK ||
                type === BICst.TARGET_TYPE.RANK_IN_GROUP){
                self._createCalcStructure(dimensions, targetIds, targetIdMap, tId);
            }
        });
    },

    _getDimensionStructureByWidgetId: function (wId, layer) {
        var self = this;
        var dimensionStructure = [];
        var targetStructure = [];
        var dimensions = this._getAllDimensions(wId);
        BI.each(dimensions, function (idx, dimension) {
            var dimensionName = dimension.name;
            if(dimension.type === BICst.TARGET_TYPE.STRING || dimension.type === BICst.TARGET_TYPE.NUMBER || dimension.type === BICst.TARGET_TYPE.STRING){
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
            }else{
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

    _getCalcDimensions: function (dim, dimensions, result) {
        var self = this;
        var checkUniq = function (tar, res) {
            var tmp = BI.find(res, function(idx, item){
                return BI.isEqual(item, tar);
            });
            if(BI.isNull(tmp)){
                result.push(tar);
            }
        };
        var calc = dim._src.expression.cal_target_name;
        calc = BI.isArray(calc) ? calc : [calc];
        checkUniq(dim, result);
        BI.each(calc, function(id, tId){
            var target = BI.find(dimensions, function(i, d){
                return d.dId === tId;
            });
            if(target.type === BICst.TARGET_TYPE.FORMULA ||
                target.type === BICst.TARGET_TYPE.YEAR_ON_YEAR_RATE ||
                target.type === BICst.TARGET_TYPE.MONTH_ON_MONTH_RATE ||
                target.type === BICst.TARGET_TYPE.YEAR_ON_YEAR_VALUE ||
                target.type === BICst.TARGET_TYPE.MONTH_ON_MONTH_VALUE ||
                target.type === BICst.TARGET_TYPE.SUM_OF_ABOVE ||
                target.type === BICst.TARGET_TYPE.SUM_OF_ABOVE_IN_GROUP ||
                target.type === BICst.TARGET_TYPE.SUM_OF_ALL ||
                target.type === BICst.TARGET_TYPE.SUM_OF_ALL_IN_GROUP ||
                target.type === BICst.TARGET_TYPE.RANK ||
                target.type === BICst.TARGET_TYPE.RANK_IN_GROUP){
                self._getCalcDimensions(target, dimensions, result);
            }else{
                checkUniq(target, result);
            }
        });
    },

    _changeCalcTargetIds: function (dimensions) {
        var self = this;
        var targetIdMap = {};
        var targetIds = [];
        var copyDimensions = {};
        BI.each(dimensions, function(idx, dimension){
            copyDimensions[dimension.dId] = dimension;
            targetIds.push(dimension.dId);
        });
        BI.each(copyDimensions, function (idx, dim) {
            if(dim.type === BICst.TARGET_TYPE.FORMULA ||
                dim.type === BICst.TARGET_TYPE.YEAR_ON_YEAR_RATE ||
                dim.type === BICst.TARGET_TYPE.MONTH_ON_MONTH_RATE ||
                dim.type === BICst.TARGET_TYPE.YEAR_ON_YEAR_VALUE ||
                dim.type === BICst.TARGET_TYPE.MONTH_ON_MONTH_VALUE ||
                dim.type === BICst.TARGET_TYPE.SUM_OF_ABOVE ||
                dim.type === BICst.TARGET_TYPE.SUM_OF_ABOVE_IN_GROUP ||
                dim.type === BICst.TARGET_TYPE.SUM_OF_ALL ||
                dim.type === BICst.TARGET_TYPE.SUM_OF_ALL_IN_GROUP ||
                dim.type === BICst.TARGET_TYPE.RANK ||
                dim.type === BICst.TARGET_TYPE.RANK_IN_GROUP){
                self._createCalcStructure(copyDimensions, targetIds, targetIdMap, dim.dId);
                if(!BI.has(targetIdMap, dim.dId)){
                    targetIdMap[dim.dId] = BI.UUID();
                    copyDimensions[targetIdMap[dim.dId]] = copyDimensions[dim.dId];
                }
                copyDimensions[dim.dId].dId = targetIdMap[dim.dId];
            }
        });
        BI.remove(copyDimensions, function(idx){
            return BI.contains(BI.keys(targetIdMap), idx);
        }, this);
        BI.each(copyDimensions, function(idx, dimension){
            if((dimension.type === BICst.TARGET_TYPE.NUMBER || dimension.type === BICst.TARGET_TYPE.COUNTER) && !BI.has(targetIdMap, dimension.dId)){
                dimension.dId = BI.UUID();
            }
        });
        return BI.values(copyDimensions);
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
                BI.each(dims, function(idx, dim){
                    var type = dim.type;
                    var newDimensions = self._changeCalcTargetIds(dimensions);
                    if(type === BICst.TARGET_TYPE.FORMULA ||
                        type === BICst.TARGET_TYPE.YEAR_ON_YEAR_RATE ||
                        type === BICst.TARGET_TYPE.MONTH_ON_MONTH_RATE ||
                        type === BICst.TARGET_TYPE.YEAR_ON_YEAR_VALUE ||
                        type === BICst.TARGET_TYPE.MONTH_ON_MONTH_VALUE ||
                        type === BICst.TARGET_TYPE.SUM_OF_ABOVE ||
                        type === BICst.TARGET_TYPE.SUM_OF_ABOVE_IN_GROUP ||
                        type === BICst.TARGET_TYPE.SUM_OF_ALL ||
                        type === BICst.TARGET_TYPE.SUM_OF_ALL_IN_GROUP ||
                        type === BICst.TARGET_TYPE.RANK ||
                        type === BICst.TARGET_TYPE.RANK_IN_GROUP){
                        self._getCalcDimensions(dim, newDimensions, result);
                    }else{
                        result.push(dim);
                    }
                });
                if (result.length > 1) {
                    text = BI.i18nText("BI-All_Field_Count", result.length);
                }
                var data = BI.map(result, function (id, dim) {
                    var type = dim.type;
                    if(type === BICst.TARGET_TYPE.STRING || type === BICst.TARGET_TYPE.NUMBER || type === BICst.TARGET_TYPE.DATE || type === BICst.TARGET_TYPE.COUNTER){
                        var dimension = {
                            dId: dim.dId,
                            type: dim.type,
                            name: dim.name,
                            _src: dim._src
                        };

                        var sort = dim.sort;
                        var group = dim.group;
                        var filter_value = dim.filter_value;
                        if(BI.isNotNull(sort) && sort.type === BICst.SORT.CUSTOM){
                            dimension.sort = sort;
                        }
                        var groupArray = [BICst.GROUP.CUSTOM_GROUP, BICst.SUMMARY_TYPE.SUM, BICst.SUMMARY_TYPE.MAX
                        , BICst.SUMMARY_TYPE.MIN, BICst.SUMMARY_TYPE.AVG
                        ];
                        if(BI.isNotNull(group) && BI.contains(groupArray, group.type)){
                            dimension.group = group;
                        }
                        if(BI.isNotNull(filter_value)){
                            dimension.filter_value = filter_value;
                        }
                        return dimension;
                    }else{
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

    populate: function(){
        this.searcher.populate();
    }
});
$.shortcut("bi.detail_select_dimension_data_pane", BI.DetailSelectDimensionPane);