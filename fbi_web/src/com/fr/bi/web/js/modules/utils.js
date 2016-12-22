!(function () {
    //运行时用到的工具类
    BI.Utils = {};
    BI.extend(BI.Utils, {
        /**
         * lic相关
         */
        hasLicence: function () {
            return Data.SharingPool.get("reg", "hasLic");
        },

        supportBasic: function () {
            return Data.SharingPool.get("reg", "supportBasic");
        },

        supportBigData: function () {
            return Data.SharingPool.get("reg", "supportBigData");
        },

        supportCalculateTarget: function () {
            return Data.SharingPool.get("reg", "supportCalculateTarget");
        },

        supportDatabaseUnion: function () {
            return Data.SharingPool.get("reg", "supportDatabaseUnion");
        },

        supportExcelView: function () {
            return Data.SharingPool.get("reg", "supportExcelView");
        },

        supportGeneralControl: function () {
            return Data.SharingPool.get("reg", "supportGeneralControl");
        },

        supportIncrementUpdate: function () {
            return Data.SharingPool.get("reg", "supportIncrementUpdate");
        },

        supportMobileClient: function () {
            return Data.SharingPool.get("reg", "supportMobileClient");
        },

        supportMultiStatisticsWidget: function () {
            return Data.SharingPool.get("reg", "supportMultiStatisticsWidget");
        },

        supportOLAPTable: function () {
            return Data.SharingPool.get("reg", "supportOLAPTable");
        },

        supportReportShare: function () {
            return Data.SharingPool.get("reg", "supportReportShare");
        },

        supportSimpleControl: function () {
            return Data.SharingPool.get("reg", "supportSimpleControl");
        },

        getDefaultChartConfig: function () {
            return Data.SharingPool.cat("plateConfig");
        },

        getAllGroupedPackagesTreeJSON: function () {
            var groupMap = Pool.groups, packages = Pool.packages;
            var packStructure = [], groupedPacks = [];
            var groups = BI.sortBy(groupMap, function (id, item) {
                return item.init_time;
            });
            BI.each(groups, function (i, group) {
                packStructure.push({
                    id: group.id,
                    text: group.name,
                    isParent: true
                });
                BI.each(group.children, function (i, item) {
                    packStructure.push({
                        id: item.id,
                        text: packages[item.id].name,
                        value: item.id,
                        pId: group.id
                    });
                    groupedPacks.push(item.id);
                })
            });
            BI.each(packages, function (id, pack) {
                var isGrouped = false;
                BI.any(groupedPacks, function (i, pId) {
                    if (pId === id) {
                        isGrouped = true;
                        return false;
                    }
                });
                if (!isGrouped) {
                    packStructure.push({
                        id: pack.id,
                        text: pack.name,
                        value: pack.id
                    })
                }
            });
            return packStructure;
        },

        /**
         * 模板相关
         */
        getCurrentTemplateId: function () {
            return Data.SharingPool.get("reportId");
        },

        getLayoutType: function () {
            var layoutType = Data.SharingPool.get("layoutType");
            return BI.isNull(layoutType) ? BICst.DASHBOARD_LAYOUT_GRID : layoutType;
        },

        getLayoutRatio: function () {
            return Data.SharingPool.get("layoutRatio");
        },

        getWidgetsByTemplateId: function (tId, callback) {
            if (tId === this.getCurrentTemplateId()) {
                callback(Data.SharingPool.cat("widgets"));
            } else {
                Data.BufferPool.getWidgetsByTemplateId(tId, callback);
            }
        },

        getAllTemplates: function (callback) {
            Data.BufferPool.getAllTemplates(callback);
        },

        getAllReportsData: function (callback) {
            Data.Req.reqAllReportsData(function (data) {
                callback(data);
            });
        },

        //global style ---- start ----
        getGlobalStyle: function () {
            var self = this;
            var globalStyle = Data.SharingPool.get("globalStyle") || {};
            if (BI.isEmptyObject(globalStyle)) {
                return checkLackProperty();
            }
            return globalStyle;

            function checkLackProperty() {
                var defaultChartConfig = self.getDefaultChartConfig();
                var type = defaultChartConfig.defaultColor;
                if (!BI.has(globalStyle, "chartColor")) {
                    if (BI.isKey(type)) {
                        var finded = BI.find(defaultChartConfig.styleList, function (i, style) {
                            return style.value === type;
                        });
                        if (finded) {
                            globalStyle.chartColor = finded.colors;
                        }
                    }
                    if (defaultChartConfig.styleList.length > 0) {
                        globalStyle.chartColor = defaultChartConfig.styleList[0].colors;
                    }
                }
                return BI.extend(globalStyle, defaultChartConfig);
            }
        },

        getGSMainBackground: function () {
            var gs = this.getGlobalStyle();
            return gs.mainBackground || this.getDefaultChartConfig().mainBackground;
        },

        getGSWidgetBackground: function () {
            var gs = this.getGlobalStyle();
            return gs.widgetBackground || this.getDefaultChartConfig().widgetBackground;
        },

        getGSChartFont: function () {
            var gs = this.getGlobalStyle();
            return BI.extend({}, this.getDefaultChartConfig().chartFont, gs.chartFont, {
                "fontFamily": "Microsoft YaHei",
                "fontSize": 12
            });
        },

        getGSTitleBackground: function () {
            var gs = this.getGlobalStyle();
            return gs.titleBackground || this.getDefaultChartConfig().titleBackground;
        },

        getGSTitleFont: function () {
            var gs = this.getGlobalStyle();
            return BI.extend({}, this.getDefaultChartConfig().titleFont, gs.titleFont);
        },

        getGSNamePos: function () {
            var titleFont = this.getGSTitleFont() || this.getDefaultChartConfig().titleFont;
            if (BI.isNotNull(titleFont)) {
                if (titleFont.textAlign === "left") {
                    return BICst.DASHBOARD_WIDGET_NAME_POS_LEFT
                }
                if (titleFont.textAlign === "center") {
                    return BICst.DASHBOARD_WIDGET_NAME_POS_CENTER
                }
            }
            return BICst.DASHBOARD_WIDGET_NAME_POS_LEFT
        },

        //global style ---- end ----

        /**
         * 业务包相关
         */
        getAllPackageIDs: function () {
            return BI.keys(Pool.packages);
        },

        getCurrentSelectPackageID: function () {
            var id = BI.Cache.getItem(BICst.CACHE.PACKAGE_PREFIX + this.getCurrentTemplateId());
            var ids = this.getAllPackageIDs();
            if (ids.contains(id)) {
                return id;
            }
            return ids[0];
        },

        setCurrentSelectPackageID: function (pId) {
            var key = BICst.CACHE.PACKAGE_PREFIX + this.getCurrentTemplateId();
            BI.Cache.setItem(key, pId);
        },

        getPackageNameByID: function (packageId) {
            if (BI.isNotNull(Pool.packages[packageId])) {
                return Pool.packages[packageId].name;
            }
        },

        getTableIDsOfPackageID: function (packageId) {
            if (BI.isNotNull(Pool.packages[packageId])) {
                return BI.pluck(Pool.packages[packageId].tables, "id");
            }
        },

        getPackageIDByTableID: function (tableId) {
            var packageId;
            BI.find(Pool.packages, function (pId, obj) {
                var ids = BI.pluck(obj.tables, "id");
                if (BI.contains(ids, tableId)) {
                    packageId = pId;
                    return true;
                }
            });
            return packageId;
        },

        /**
         * 表相关
         */
        getTableNameByID: function (tableId) {
            var translations = Pool.translations;
            return translations[tableId];
        },

        getFieldIDsOfTableID: function (tableId) {
            if (BI.isNotNull(Pool.tables[tableId])) {
                var fields = Pool.tables[tableId].fields;
                return BI.pluck(fields[0].concat(fields[1]).concat(fields[2]), "id");
            }
        },

        getStringFieldIDsOfTableID: function (tableId) {
            var self = this;
            if (BI.isNotNull(Pool.tables[tableId])) {
                var fields = Pool.tables[tableId].fields;
                return BI.filter(BI.pluck(fields[0], "id"), function (idx, id) {
                    return self.getFieldTypeByID(id) === BICst.COLUMN.STRING;
                });
            }
            return [];
        },

        getNumberFieldIDsOfTableID: function (tableId) {
            var self = this;
            if (BI.isNotNull(Pool.tables[tableId])) {
                var fields = Pool.tables[tableId].fields;
                return BI.filter(BI.pluck(fields[0], "id"), function (idx, id) {
                    return self.getFieldTypeByID(id) === BICst.COLUMN.NUMBER;
                });
            }
            return [];
        },

        getDateFieldIDsOfTableID: function (tableId) {
            var self = this;
            if (BI.isNotNull(Pool.tables[tableId])) {
                var fields = Pool.tables[tableId].fields;
                return BI.filter(BI.pluck(fields[0], "id"), function (idx, id) {
                    return self.getFieldTypeByID(id) === BICst.COLUMN.DATE;
                });
            }
            return [];
        },

        getCountFieldIDsOfTableID: function (tableId) {
            if (BI.isNotNull(Pool.tables[tableId])) {
                var fields = Pool.tables[tableId].fields;
                return BI.pluck(fields[3], "id");
            }
            return [];
        },

        getSortedFieldIdsOfOneTableByTableId: function (tableId) {
            var translations = Pool.translations;
            var fieldIds = this.getFieldIDsOfTableID(tableId);
            var transIds = [];
            BI.each(fieldIds, function (i, fId) {
                if (BI.isNotNull(translations[fId])) {
                    transIds.push(fId);
                }
            });
            var filterFiledIds = [];
            BI.each(fieldIds, function (i, fId) {
                if (BI.isNull(translations[fId])) {
                    filterFiledIds.push(fId);
                }
            });
            var tNum = [], tString = [], tDate = [], fNum = [], fString = [], fDate = [];
            BI.each(transIds, function (i, id) {
                switch (BI.Utils.getFieldTypeByID(id)) {
                    case BICst.COLUMN.NUMBER:
                        tNum.push(id);
                        break;
                    case BICst.COLUMN.STRING:
                        tString.push(id);
                        break;
                    case BICst.COLUMN.DATE:
                        tDate.push(id);
                        break;
                }
            });
            BI.each(filterFiledIds, function (i, id) {
                switch (BI.Utils.getFieldTypeByID(id)) {
                    case BICst.COLUMN.NUMBER:
                        fNum.push(id);
                        break;
                    case BICst.COLUMN.STRING:
                        fString.push(id);
                        break;
                    case BICst.COLUMN.DATE:
                        fDate.push(id);
                        break;
                }
            });
            return tNum.concat(tString).concat(tDate).concat(fNum).concat(fString).concat(fDate);
        },

        getExcelViewByTableId: function (tableId) {
            var views = Pool.excel_views || {};
            return views[tableId];
        },

        isSelfCircleTableByTableId: function (tableId) {
            var paths = BI.Utils.getPathsFromTableAToTableB(tableId, tableId);
            if (paths.length === 0) {
                return false;
            }
            return !BI.find(paths, function (idx, path) {
                return path.length > 1;
            });
        },

        /**
         * 字段相关
         */
        getFieldNameByID: function (fieldId) {
            var translations = Pool.translations;
            var field = Pool.fields[fieldId];
            var fieldName = translations[fieldId];
            if (BI.isNull(fieldName) && BI.isNotNull(field)) {
                fieldName = field.field_name;
                if (field.field_type === BICst.COLUMN.COUNTER) {
                    //记录数 表名+"记录数"fbi_Records
                    var tableName = this.getTableNameByID(field.table_id);
                    fieldName = tableName + BI.i18nText("BI-Records");
                }
            }
            return fieldName;
        },

        getOriginalFieldNameByID: function (fieldId) {
            var field = Pool.fields[fieldId];
            if (BI.isNotNull(field)) {
                return field.field_name;
            }
        },

        getFieldTypeByID: function (fieldId) {
            if (BI.isNotNull(Pool.fields[fieldId])) {
                return Pool.fields[fieldId].field_type;
            }
        },

        getFieldIsUsableByID: function (fieldId) {
            if (BI.isNotNull(Pool.fields[fieldId])) {
                return Pool.fields[fieldId].is_usable;
            }
        },

        getTableIdByFieldID: function (fieldId) {
            if (BI.isNotNull(Pool.fields[fieldId])) {
                return Pool.fields[fieldId].table_id;
            }
        },

        getAllFieldIDs: function () {
            return BI.keys(Pool.fields);
        },


        /**
         * 组件相关
         */
        getAllWidgetIDs: function () {
            return BI.keys(Data.SharingPool.get("widgets"));
        },

        getWidgetBoundsByID: function (wid) {
            return Data.SharingPool.get("widgets", wid, "bounds") || {};
        },

        getWidgetLinkageByID: function (wid) {
            return Data.SharingPool.get("widgets", wid, "linkages") || [];
        },

        getWidgetViewByID: function (wid) {
            return Data.SharingPool.get("widgets", wid, "view") || {};
        },

        getWidgetViewClassificationByID: function (wid) {
            var views = this.getWidgetViewByID(wid);
            var result = {};
            BI.each(views, function (viewType, view) {
                viewType = BI.parseInt(viewType);
                if (BI.Utils.isDimensionRegion1ByRegionType(viewType)) {
                    result[BICst.REGION.DIMENSION1] = result[BICst.REGION.DIMENSION1] || [];
                    result[BICst.REGION.DIMENSION1].push(viewType + "");
                    return;
                }
                if (BI.Utils.isDimensionRegion2ByRegionType(viewType)) {
                    result[BICst.REGION.DIMENSION2] = result[BICst.REGION.DIMENSION2] || [];
                    result[BICst.REGION.DIMENSION2].push(viewType + "");
                    return;
                }
                if (BI.Utils.isTargetRegion1ByRegionType(viewType)) {
                    result[BICst.REGION.TARGET1] = result[BICst.REGION.TARGET1] || [];
                    result[BICst.REGION.TARGET1].push(viewType + "");
                    return;
                }
                if (BI.Utils.isTargetRegion2ByRegionType(viewType)) {
                    result[BICst.REGION.TARGET2] = result[BICst.REGION.TARGET2] || [];
                    result[BICst.REGION.TARGET2].push(viewType + "");
                    return;
                }
                result[BICst.REGION.TARGET3] = result[BICst.REGION.TARGET3] || [];
                result[BICst.REGION.TARGET3].push(viewType + "");
            });
            return result;
        },

        getWidgetTypeByID: function (wid) {
            return Data.SharingPool.get("widgets", wid, "type");
        },

        getWidgetSubTypeByID: function (wid) {
            var type = this.getWidgetTypeByID(wid);
            var subType = Data.SharingPool.get("widgets", wid, "sub_type");
            if (type === BICst.WIDGET.MAP && BI.isNull(subType)) {
                return BI.findKey(MapConst.INNER_MAP_INFO.MAP_LAYER, function (path, layer) {
                    if (layer === 0) {
                        return true;
                    }
                });
            }
        },

        getWidgetNameByID: function (wid) {
            var widget = Data.SharingPool.cat("widgets", wid);
            if (BI.isNotNull(widget)) {
                return Data.SharingPool.get("widgets", wid, "name");
            }
        },

        getWidgetValueByID: function (wid) {
            var widget = Data.SharingPool.cat("widgets", wid);
            if (BI.isNotNull(widget)) {
                return Data.SharingPool.get("widgets", wid, "value");
            }
        },

        getAllLinkageFromIdsByID: function (wid) {
            var self = this, fromIds = [];
            var linkages = this.getWidgetLinkageByID(wid);
            BI.each(linkages, function (i, link) {
                fromIds.push(link.from);
                fromIds = fromIds.concat(self.getAllLinkageFromIdsByID(link.to));
            });
            return fromIds;
        },

        checkWidgetNameByID: function (name, wId) {
            var allWIds = this.getAllWidgetIDs();
            var self = this, isValid = true;
            BI.some(allWIds, function (i, id) {
                if (wId !== id && self.getWidgetNameByID(id) === name) {
                    isValid = false;
                    return true;
                }
            });
            return isValid;
        },

        isControlWidgetByWidgetId: function (wid) {
            var widgetType = this.getWidgetTypeByID(wid);
            return this.isControlWidgetByWidgetType(widgetType);
        },

        isControlWidgetByWidgetType: function (widgetType) {
            return widgetType === BICst.WIDGET.STRING ||
                widgetType === BICst.WIDGET.STRING_LIST ||
                widgetType === BICst.WIDGET.NUMBER ||
                widgetType === BICst.WIDGET.SINGLE_SLIDER ||
                widgetType === BICst.WIDGET.INTERVAL_SLIDER ||
                widgetType === BICst.WIDGET.DATE ||
                widgetType === BICst.WIDGET.MONTH ||
                widgetType === BICst.WIDGET.QUARTER ||
                widgetType === BICst.WIDGET.TREE ||
                widgetType === BICst.WIDGET.TREE_LIST ||
                widgetType === BICst.WIDGET.LIST_LABEL ||
                widgetType === BICst.WIDGET.TREE_LABEL ||
                widgetType === BICst.WIDGET.YEAR ||
                widgetType === BICst.WIDGET.YMD ||
                widgetType === BICst.WIDGET.GENERAL_QUERY;
        },

        isInstantControlWidgetByWidgetId: function (wid) {
            var widgetType = this.getWidgetTypeByID(wid);
            return this.isInstantControlWidgetByWidgetType(widgetType);
        },

        isInstantControlWidgetByWidgetType: function (widgetType) {
            return widgetType === BICst.WIDGET.LIST_LABEL ||
                widgetType === BICst.WIDGET.TREE_LABEL ||
                widgetType === BICst.WIDGET.TREE_LIST ||
                widgetType === BICst.WIDGET.STRING_LIST ||
                widgetType === BICst.WIDGET.SINGLE_SLIDER ||
                widgetType === BICst.WIDGET.INTERVAL_SLIDER;
        },

        isQueryControlExist: function () {
            var self = this, isQueryExist = false;
            BI.some(this.getAllWidgetIDs(), function (i, wId) {
                if (self.getWidgetTypeByID(wId) === BICst.WIDGET.QUERY) {
                    return isQueryExist = true;
                }
            });
            return isQueryExist;
        },

        getWidgetDimensionsByID: function (wid) {
            return Data.SharingPool.get("widgets", wid, "dimensions") || {};
        },

        getWidgetSortByID: function (wid) {
            return Data.SharingPool.get("widgets", wid, "sort") || {};
        },

        getWidgetSortSequenceByID: function (wid) {
            return Data.SharingPool.get("widgets", wid, "sort_sequence") || [];
        },

        isShowWidgetRealDataByID: function (wid) {
            return Data.SharingPool.get("widgets", wid, "real_data");
        },

        //获取指定widget的拷贝,拷贝信息只包含widget的自身信息，如维度指标及其相关属性
        //不包含widge间的信息,如widget间的联动什么的
        getWidgetCopyByID: function (wid) {
            var self = this;
            var widget = Data.SharingPool.get("widgets", wid);
            if (BI.isNotNull(widget)) {
                var obj = {};
                obj.type = widget.type;
                obj.name = BI.Func.createDistinctName(Data.SharingPool.get("widgets"), widget.name);
                var dimTarIdMap = {};
                var dimensions = {};
                var view = {};
                BI.each(widget.dimensions, function (idx) {
                    var copy = createDimensionsAndTargets(idx);
                    dimensions[copy.id] = copy.dimension;
                });
                BI.each(widget.view, function (region, dimIds) {
                    view[region] = [];
                    BI.each(dimIds, function (idx, dId) {
                        view[region].push(dimTarIdMap[dId]);
                    });
                });
                obj.dimensions = dimensions;
                obj.view = view;
                obj.bounds = {
                    height: widget.bounds.height,
                    width: widget.bounds.width,
                    left: widget.bounds.left,
                    top: widget.bounds.top
                };
                obj.settings = widget.settings;
                obj.value = widget.value;
                //组件表头上指标的排序和过滤
                if (BI.has(widget, "sort") && BI.isNotNull(widget.sort)) {
                    obj.sort = BI.extend({}, widget.sort, {
                        sort_target: createDimensionsAndTargets(widget.sort.sort_target).id
                    })
                }

                if (BI.has(widget, "sort_sequence") && BI.isNotNull(widget.sort_sequence)) {
                    obj.sort_sequence = [];
                    BI.each(widget.sort_sequence, function (idx, dId) {
                        obj.sort_sequence.push(createDimensionsAndTargets(dId).id);
                    })
                }

                if (BI.has(widget, "filter_value") && BI.isNotNull(widget.filter_value)) {
                    var filterValue = {};
                    BI.each(widget.filter_value, function (target_id, filter_value) {
                        var newId = createDimensionsAndTargets(target_id).id;
                        filterValue[newId] = checkFilter(filter_value, target_id, newId);
                    });
                    obj.filter_value = filterValue;
                }

                return obj;
            }

            function checkFilter(oldFilter, dId, newId) {
                var filter = {};
                var filterType = oldFilter.filter_type, filterValue = oldFilter.filter_value;
                filter.filter_type = oldFilter.filter_type;
                if (filterType === BICst.FILTER_TYPE.AND || filterType === BICst.FILTER_TYPE.OR) {
                    filter.filter_value = [];
                    BI.each(filterValue, function (i, value) {
                        filter.filter_value.push(checkFilter(value, dId, newId));
                    });
                } else {
                    BI.extend(filter, oldFilter);
                    //防止死循环
                    if (BI.has(oldFilter, "target_id")) {
                        if (oldFilter.target_id !== dId) {
                            var result = createDimensionsAndTargets(oldFilter.target_id);
                            filter.target_id = result.id;
                        } else {
                            filter.target_id = newId;
                        }
                    }
                    //维度公式过滤所用到的指标ID也要替换掉
                    if (BI.has(oldFilter, "formula_ids")) {
                        var ids = oldFilter.formula_ids || [];
                        if (BI.isNotEmptyArray(ids) && BI.isNull(BI.Utils.getFieldTypeByID(ids[0]))) {
                            BI.each(ids, function (id, tId) {
                                var result = createDimensionsAndTargets(tId);
                                filter.filter_value = filter.filter_value.replaceAll(tId, result.id);
                                filter.formula_ids[id] = result.id;
                            });
                        }
                    }
                }
                return filter;
            }

            function createDimensionsAndTargets(idx) {
                var newId = dimTarIdMap[idx] || BI.UUID();
                var dimension = BI.deepClone(widget.dimensions[idx]);
                if (BI.has(dimTarIdMap, idx) && BI.has(dimensions, [dimTarIdMap[idx]])) {
                    return {id: dimTarIdMap[idx], dimension: dimensions[dimTarIdMap[idx]]};
                }
                switch (widget.dimensions[idx].type) {
                    case BICst.TARGET_TYPE.STRING:
                    case BICst.TARGET_TYPE.NUMBER:
                    case BICst.TARGET_TYPE.DATE:
                        if (BI.has(widget.dimensions[idx], "dimension_map")) {
                            dimension.dimension_map = {};
                            BI.each(widget.dimensions[idx].dimension_map, function (id, map) {
                                //明细表dimensionMap存的key是tableId，与汇总表区分
                                if (self.isDimensionExist(id)) {
                                    var result = createDimensionsAndTargets(id);
                                    dimension.dimension_map[result.id] = map;
                                } else {
                                    dimension.dimension_map[id] = map;
                                }
                            });
                        }
                        if (BI.has(widget.dimensions[idx], "filter_value") && BI.isNotNull(widget.dimensions[idx].filter_value)) {
                            dimension.filter_value = checkFilter(widget.dimensions[idx].filter_value, dimTarIdMap[idx] || idx, newId);
                        }
                        if (BI.has(widget.dimensions[idx], "sort")) {
                            dimension.sort = BI.deepClone(widget.dimensions[idx].sort);
                            if (BI.has(dimension.sort, "sort_target")) {
                                if (dimension.sort.sort_target === idx) {
                                    dimension.sort.sort_target = newId;
                                } else {
                                    var result = createDimensionsAndTargets(dimension.sort.sort_target);
                                    dimension.sort.sort_target = result.id;
                                }
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
                            var result = createDimensionsAndTargets(tId);
                            if (BI.has(expression, "formula_value")) {
                                expression.formula_value = expression.formula_value.replaceAll(tId, result.id);
                            }
                            expression.ids[id] = result.id;
                        });
                        break;
                }
                dimension.dId = newId;
                dimTarIdMap[idx] = newId;
                return {id: newId, dimension: dimension};
            }
        },

        getCalculateValue: function (did) {
            return Data.SharingPool.get("calculateValue", did) || []
        },

        //settings  ---- start ----
        getWSTitleDetailSettingByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.widgetNameStyle) ? ws.widgetNameStyle :
            {};
        },

        getWSWidgetBGByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            var wbg = this.getGSWidgetBackground(wid);
            if (BI.isNull(ws.widgetBG)) {
                return wbg ? wbg : {}
            }
            return ws.widgetBG
        },

        getWSTableFormByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.tableFormGroup) ? ws.tableFormGroup :
                BICst.DEFAULT_CHART_SETTING.tableFormGroup;
        },

        getWSThemeColorByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.themeColor) ? ws.themeColor :
                BICst.DEFAULT_CHART_SETTING.themeColor;
        },

        getWSTableStyleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.tableStyleGroup) ? ws.tableStyleGroup :
                BICst.DEFAULT_CHART_SETTING.tableStyleGroup;
        },

        getWSIsCustomTableStyleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.isCustomTableStyle) ? ws.isCustomTableStyle :
                BICst.DEFAULT_CHART_SETTING.isCustomTableStyle;
        },
        //表格样式
        getWSCustomTableStyleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.customTableStyle) ? ws.customTableStyle :
            {};
        },

        getWSTableNameStyleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            if (ws.customTableStyle) {
                return BI.isNotNull(ws.customTableStyle.tableNameStyle) ? ws.customTableStyle.tableNameStyle :
                {};
            }
            return {}
        },

        getWSTableValueStyleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            if (ws.customTableStyle) {
                return BI.isNotNull(ws.customTableStyle.tableValueStyle) ? ws.customTableStyle.tableValueStyle :
                {};
            }
            return {}
        },

        getWSShowNumberByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.showNumber) ? ws.showNumber :
                BICst.DEFAULT_CHART_SETTING.showNumber;
        },

        getWSShowRowTotalByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.showRowTotal) ? ws.showRowTotal :
                BICst.DEFAULT_CHART_SETTING.showRowTotal;
        },

        getWSShowColTotalByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.showColTotal) ? ws.showColTotal :
                BICst.DEFAULT_CHART_SETTING.showColTotal;
        },

        getWSOpenRowNodeByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.openRowNode) ? ws.openRowNode :
                BICst.DEFAULT_CHART_SETTING.openRowNode;
        },

        getWSOpenColNodeByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.openColNode) ? ws.openColNode :
                BICst.DEFAULT_CHART_SETTING.openColNode;
        },

        getWSMaxRowByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.maxRow) ? ws.maxRow :
                BICst.DEFAULT_CHART_SETTING.maxRow;
        },

        getWSMaxColByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.maxCol) ? ws.maxCol :
                BICst.DEFAULT_CHART_SETTING.maxCol;
        },

        getWSRowHeightByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.rowHeight) ? ws.rowHeight :
                BICst.DEFAULT_CHART_SETTING.rowHeight;
        },

        getWSFreezeDimByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.freezeDim) ? ws.freezeDim :
                BICst.DEFAULT_CHART_SETTING.freezeDim;
        },

        getWSFreezeFirstColumnById: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.freezeFirstColumn) ? ws.freezeFirstColumn :
                BICst.DEFAULT_CHART_SETTING.freezeFirstColumn;
        },

        getWSChartDisplayRulesByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.displayRules) ? ws.displayRules :
                BICst.DEFAULT_CHART_SETTING.displayRules;
        },

        getWSChartBubbleFixedStyleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.fixedStyle) ? ws.fixedStyle :
                BICst.DASHBOARD_STYLE_CONDITIONS
        },

        getWSChartBubbleGradientStyleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.gradientStyle) ? ws.gradientStyle :
                BICst.BUBBLE_GRADIENT_COLOR
        },

        getWSChartBubbleStyleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.bubbleStyle) ? ws.bubbleStyle :
                BICst.DEFAULT_CHART_SETTING.bubbleStyle;
        },

        getWSTransferFilterByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.transferFilter) ? ws.transferFilter :
                BICst.DEFAULT_CHART_SETTING.transferFilter;
        },

        getWSShowNameByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.showName) ? ws.showName :
                BICst.DEFAULT_CHART_SETTING.showName;
        },

        getWSNamePosByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            var gsNamePos = this.getGSNamePos();
            return ws.name_pos
                || gsNamePos
                || BICst.DEFAULT_CHART_SETTING.name_pos;
        },

        getWSColumnSizeByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.column_size) ? ws.column_size : [];
        },

        getWSChartColorByID: function (wid) {
            var self = this;

            function getDefaultColor() {
                var defaultChartConfig = self.getDefaultChartConfig();
                var type = defaultChartConfig.defaultColor;
                if (BI.isKey(type)) {
                    var finded = BI.find(defaultChartConfig.styleList, function (i, style) {
                        return style.value === type;
                    });
                    if (finded) {
                        return finded.colors;
                    }
                }
                if (defaultChartConfig.styleList.length > 0) {
                    return defaultChartConfig.styleList[0].colors;
                }
            }

            var gs = this.getGlobalStyle();
            var ws = this.getWidgetSettingsByID(wid);
            return ws.chartColor
                || gs.chartColor
                || getDefaultColor()
                || BICst.DEFAULT_CHART_SETTING.chartColor;
        },

        getWSChartStyleByID: function (wid) {
            var self = this;

            function getChartStyle() {
                var defaultChartConfig = self.getDefaultChartConfig();
                return defaultChartConfig.chartStyle;
            }

            var ws = this.getWidgetSettingsByID(wid);
            var chartStyle;
            if (BI.isNotNull(ws.chartStyle)) {
                return ws.chartStyle;
            }
            var gs = this.getGlobalStyle();
            if (BI.isNotNull(gs.chartStyle)) {
                return gs.chartStyle
            }
            if (BI.isNotNull(chartStyle = getChartStyle())) {
                return chartStyle;
            }
            return BICst.DEFAULT_CHART_SETTING.chartStyle;
        },

        getWSLineAreaChartTypeByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.lineAreaChartType) ? ws.lineAreaChartType :
                BICst.DEFAULT_CHART_SETTING.lineAreaChartType;
        },

        getWSPieChartTypeByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.pieChartType) ? ws.pieChartType :
                BICst.DEFAULT_CHART_SETTING.pieChartType;
        },

        getWSRadarChartTypeByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.radarChartType) ? ws.radarChartType :
                BICst.DEFAULT_CHART_SETTING.radarChartType;
        },

        getWSDashboardChartTypeByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.dashboardChartType) ? ws.dashboardChartType :
                BICst.DEFAULT_CHART_SETTING.dashboardChartType;
        },

        getWSChartTotalAngleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.totalAngle) ? ws.totalAngle :
                BICst.DEFAULT_CHART_SETTING.totalAngle;
        },

        getWSChartInnerRadiusByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.innerRadius) ? ws.innerRadius :
                BICst.DEFAULT_CHART_SETTING.innerRadius;
        },

        getWSChartLeftYNumberFormatByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.leftYNumberFormat) ? ws.leftYNumberFormat :
                BICst.DEFAULT_CHART_SETTING.leftYNumberFormat;
        },

        getWSChartRightYNumberFormatByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.rightYNumberFormat) ? ws.rightYNumberFormat :
                BICst.DEFAULT_CHART_SETTING.rightYNumberFormat;
        },

        getWSChartRightY2NumberFormatByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.rightY2NumberFormat) ? ws.rightY2NumberFormat :
                BICst.DEFAULT_CHART_SETTING.rightY2NumberFormat;
        },

        getWSChartRightYNumberLevelByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.rightYNumberLevel) ? ws.rightYNumberLevel :
                BICst.DEFAULT_CHART_SETTING.rightYNumberLevel;
        },

        getWSChartRightY2NumberLevelByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.rightY2NumberLevel) ? ws.rightY2NumberLevel :
                BICst.DEFAULT_CHART_SETTING.rightY2NumberLevel;
        },

        getWSChartLeftYNumberLevelByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.leftYNumberLevel) ? ws.leftYNumberLevel :
                BICst.DEFAULT_CHART_SETTING.leftYNumberLevel;
        },

        getWSChartDashboardPointerByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.dashboardPointer) ? ws.dashboardPointer :
                BICst.POINTER.ONE;
        },

        getWSChartStyleRadioByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.styleRadio) ? ws.styleRadio :
                BICst.SCALE_SETTING.AUTO
        },

        getWSChartDashboardStylesByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.dashboardStyles) ? ws.dashboardStyles :
                BICst.dashboardStyles
        },

        getWSChartMapStylesByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.mapStyles) ? ws.mapStyles :
                BICst.MAP_STYLE_CONDITIONS
        },

        getWSLeftYAxisUnitByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.leftYUnit) ? ws.leftYUnit :
                BICst.DEFAULT_CHART_SETTING.leftYUnit;
        },

        getWSDashboardUnitByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.dashboard_unit) ? ws.dashboard_unit :
                BICst.DEFAULT_CHART_SETTING.dashboard_unit;
        },

        getWSChartMinScaleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.minScale) ? ws.minScale :
                BICst.DEFAULT_CHART_SETTING.minScale
        },

        getWSChartMaxScaleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.maxScale) ? ws.maxScale :
                BICst.DEFAULT_CHART_SETTING.maxScale
        },

        getWSChartShowPercentageByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.show_percentage) ? ws.show_percentage :
                BICst.DEFAULT_CHART_SETTING.percentage_not_show
        },

        getWSChartRightYUnitByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.rightYUnit) ? ws.rightYUnit :
                BICst.DEFAULT_CHART_SETTING.rightYUnit;
        },

        getWSChartRightYAxis2UnitByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.rightY2Unit) ? ws.rightY2Unit :
                BICst.DEFAULT_CHART_SETTING.rightY2Unit;
        },

        getWSChartLeftYShowTitleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.leftYShowTitle) ? ws.leftYShowTitle :
                BICst.DEFAULT_CHART_SETTING.leftYShowTitle;
        },

        getWSChartRightYShowTitleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.rightYShowTitle) ? ws.rightYShowTitle :
                BICst.DEFAULT_CHART_SETTING.rightYShowTitle;
        },

        getWSChartRightY2ShowTitleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.rightYShowTitle) ? ws.rightYShowTitle :
                BICst.DEFAULT_CHART_SETTING.rightYShowTitle;
        },

        getWSChartLeftYTitleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.leftYTitle) ? ws.leftYTitle :
                BICst.DEFAULT_CHART_SETTING.leftYTitle;
        },

        getWSChartRightYTitleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.rightYTitle) ? ws.rightYTitle :
                BICst.DEFAULT_CHART_SETTING.rightYTitle;
        },

        getWSChartRightY2TitleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.rightY2Title) ? ws.rightY2Title :
                BICst.DEFAULT_CHART_SETTING.rightY2Title;
        },

        getWSChartCatShowTitleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.catShowTitle) ? ws.catShowTitle :
                BICst.DEFAULT_CHART_SETTING.catShowTitle;
        },

        getWSChartCatTitleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.catTitle) ? ws.catTitle :
                BICst.DEFAULT_CHART_SETTING.catTitle;
        },

        getWSChartLeftYReverseByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.leftYReverse) ? ws.leftYReverse :
                BICst.DEFAULT_CHART_SETTING.leftYReverse;
        },

        getWSChartRightYReverseByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.rightYReverse) ? ws.rightYReverse :
                BICst.DEFAULT_CHART_SETTING.rightYReverse;
        },

        getWSChartRightY2ReverseByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.rightY2Reverse) ? ws.rightY2Reverse :
                BICst.DEFAULT_CHART_SETTING.rightY2Reverse;
        },

        getWSChartLegendByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.legend) ? ws.legend :
                BICst.DEFAULT_CHART_SETTING.legend;
        },

        getWSChartShowDataLabelByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.showDataLabel) ? ws.showDataLabel :
                BICst.DEFAULT_CHART_SETTING.showDataLabel;
        },

        getWSChartDataLabelSettingByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            var chartFont = this.getGSChartFont();
            var dataLabelSetting = ws.dataLabelSetting || BICst.DEFAULT_CHART_SETTING.DataLabelSetting;
            dataLabelSetting.textStyle = BI.extend(chartFont, dataLabelSetting.textStyle);
            return dataLabelSetting;
        },

        getWSChartShowDataTableByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.showDataTable) ? ws.showDataTable :
                BICst.DEFAULT_CHART_SETTING.showDataTable;
        },

        getWSChartBubbleSizeFromByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.bubbleSizeFrom) ? ws.bubbleSizeFrom :
                BICst.DEFAULT_CHART_SETTING.bubbleSizeFrom
        },

        getWSChartBubbleSizeToByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.bubbleSizeTo) ? ws.bubbleSizeTo :
                BICst.DEFAULT_CHART_SETTING.bubbleSizeTo
        },

        getWSLeftYNumberSeparatorByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.leftYSeparator) ? ws.leftYSeparator :
                BICst.DEFAULT_CHART_SETTING.leftYSeparator;
        },

        getWSRightYNumberSeparatorByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.rightYSeparator) ? ws.rightYSeparator :
                BICst.DEFAULT_CHART_SETTING.rightYSeparator;
        },

        getWSRightY2NumberSeparatorByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.rightY2Separator) ? ws.rightY2Separator :
                BICst.DEFAULT_CHART_SETTING.rightY2Separator;
        },

        getWSChartLeftYShowLabelByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.leftYShowLabel) ? ws.leftYShowLabel :
                BICst.DEFAULT_CHART_SETTING.leftYShowLabel
        },

        getWSChartLeftYLabelStyleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            var chartFont = this.getGSChartFont();
            var labelSetting = ws.leftYLabelStyle || {};
            labelSetting.textStyle = BI.extend(chartFont, labelSetting.textStyle);
            labelSetting.textDirection = labelSetting.textDirection || 0;
            return labelSetting;
        },

        getWSChartLeftYLineColorByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.leftYLineColor) ? ws.leftYLineColor :
                BICst.DEFAULT_CHART_SETTING.lineColor
        },

        getWSRightYShowLabelByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.rightYShowLabel) ? ws.rightYShowLabel :
                BICst.DEFAULT_CHART_SETTING.rightYShowLabel
        },

        getWSRightYLabelStyleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            var chartFont = this.getGSChartFont();
            var labelSetting = ws.rightYLabelStyle || {};
            labelSetting.textStyle = BI.extend(chartFont, labelSetting.textStyle);
            labelSetting.textDirection = labelSetting.textDirection || 0;
            return labelSetting;
        },

        getWSRightYLineColorByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.rightYLineColor) ? ws.rightYLineColor :
                BICst.DEFAULT_CHART_SETTING.lineColor
        },

        getWSRightY2ShowLabelByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.right2YShowLabel) ? ws.right2YShowLabel :
                BICst.DEFAULT_CHART_SETTING.right2YShowLabel
        },

        getWSRightY2LabelStyleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            var chartFont = this.getGSChartFont();
            var labelSetting = ws.rightY2LabelStyle || {};
            labelSetting.textStyle = BI.extend(chartFont, labelSetting.textStyle);
            labelSetting.textDirection = labelSetting.textDirection || 0;
            return labelSetting;
        },

        getWSRightY2LineColorByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.rightY2LineColor) ? ws.rightY2LineColor :
                BICst.DEFAULT_CHART_SETTING.lineColor
        },

        getWSChartCatShowLabelByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.catShowLabel) ? ws.catShowLabel :
                BICst.DEFAULT_CHART_SETTING.catShowLabel
        },

        getWSChartCatLabelStyleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            var chartFont = this.getGSChartFont();
            var labelSetting = ws.catLabelStyle || {};
            labelSetting.textStyle = BI.extend(chartFont, labelSetting.textStyle);
            labelSetting.textDirection = labelSetting.textDirection || 0;
            return labelSetting;

        },

        getWSChartCatLineColorByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.catLineColor) ? ws.catLineColor :
                BICst.DEFAULT_CHART_SETTING.lineColor
        },

        getWSChartLegendStyleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            var chartFont = this.getGSChartFont();
            var legendStyle = ws.legendStyle || {};
            legendStyle = BI.extend(chartFont, legendStyle);
            return legendStyle;
        },

        getWSChartLeftYTitleStyleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            var chartFont = this.getGSChartFont();
            var titleStyle = ws.leftYTitleStyle || {};
            titleStyle = BI.extend(chartFont, titleStyle);
            return titleStyle;
        },

        getWSChartRightYTitleStyleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            var chartFont = this.getGSChartFont();
            var titleStyle = ws.rightYTitleStyle || {};
            titleStyle = BI.extend(chartFont, titleStyle);
            return titleStyle;
        },

        getWSChartRightY2TitleStyleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            var chartFont = this.getGSChartFont();
            var titleStyle = ws.rightY2TitleStyle || {};
            titleStyle = BI.extend(chartFont, titleStyle);
            return titleStyle;
        },

        getWSChartCatTitleStyleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            var chartFont = this.getGSChartFont();
            var titleStyle = ws.catTitleStyle || {};
            titleStyle = BI.extend(chartFont, titleStyle);
            return titleStyle;
        },

        getWSChartHShowGridLineByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.hShowGridLine) ? ws.hShowGridLine :
                BICst.DEFAULT_CHART_SETTING.hShowGridLine
        },

        getWSChartHGridLineColorByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.hGridLineColor) ? ws.hGridLineColor :
                BICst.DEFAULT_CHART_SETTING.lineColor
        },

        getWSChartVShowGridLineByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.vShowGridLine) ? ws.vShowGridLine :
                BICst.DEFAULT_CHART_SETTING.vShowGridLine
        },

        getWSChartVGridLineColorByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.vGridLineColor) ? ws.vGridLineColor :
                BICst.DEFAULT_CHART_SETTING.lineColor
        },

        getWSChartToolTipStyleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.tooltipStyle) ? ws.tooltipStyle :
            {}
        },

        getWSLinkageSelectionByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.linkageSelection) ? ws.linkageSelection :
                BICst.DEFAULT_CHART_SETTING.linkageSelection
        },

        getWSMinimalistByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.miniModel) ? ws.miniModel :
                BICst.DEFAULT_CHART_SETTING.miniModel
        },

        getWSChartBigDataModeByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.bigDataMode) ? ws.bigDataMode :
                BICst.DEFAULT_CHART_SETTING.bigDataMode
        },

        getWSChartRightYShowCustomScaleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.rightYShowCustomScale) ? ws.rightYShowCustomScale :
                BICst.DEFAULT_CHART_SETTING.rightYShowCustomScale;
        },

        getWSChartRightYCustomScaleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.rightYCustomScale) ? ws.rightYCustomScale :
                BICst.DEFAULT_CHART_SETTING.customScale
        },

        getWSChartLeftYShowCustomScaleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.leftYShowCustomScale) ? ws.leftYShowCustomScale :
                BICst.DEFAULT_CHART_SETTING.leftYShowCustomScale;
        },

        getWSChartLeftYCustomScaleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.leftYCustomScale) ? ws.leftYCustomScale :
                BICst.DEFAULT_CHART_SETTING.customScale
        },

        getWSChartRightY2CustomScaleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.rightY2CustomScale) ? ws.rightY2CustomScale :
                BICst.DEFAULT_CHART_SETTING.customScale;
        },

        getWSChartRightY2ShowCustomScaleByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.rightY2ShowCustomScale) ? ws.rightY2ShowCustomScale :
                BICst.DEFAULT_CHART_SETTING.rightY2ShowCustomScale
        },

        getWSChartShowZoomByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.showZoom) ? ws.showZoom :
                BICst.DEFAULT_CHART_SETTING.showZoom;
        },

        getWSShowBackgroundByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.isShowBackgroundLayer) ? ws.isShowBackgroundLayer :
                BICst.DEFAULT_CHART_SETTING.isShowBackgroundLayer;
        },

        getWSChartMapBackgroundLayerInfoByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.backgroundLayerInfo) ? ws.backgroundLayerInfo :
                BICst.DEFAULT_CHART_SETTING.backgroundLayerInfo;
        },

        getWSNullContinuityByID: function (wid) {
            var ws = this.getWidgetSettingsByID(wid);
            return BI.isNotNull(ws.nullContinuity) ? ws.nullContinuity :
                BICst.DEFAULT_CHART_SETTING.nullContinuity;
        },

        //settings  ---- end ----

        getWidgetSettingsByID: function (wid) {
            return Data.SharingPool.get("widgets", wid, "settings") || {};
        },

        getWidgetInitTimeByID: function (wid) {
            return Data.SharingPool.get("widgets", wid, "init_time") || new Date().getTime();
        },

        getClickedByID: function (wid) {
            return Data.SharingPool.get("widgets", wid, "clicked") || {};
        },

        getDrillByID: function (wid) {
            var self = this;
            var clicked = this.getClickedByID(wid);
            var drills = {};
            BI.each(clicked, function (dId, value) {
                if (self.isDimensionExist(dId) && self.isDimensionByDimensionID(dId)) {
                    drills[dId] = value;
                }
            });
            return drills;
        },

        getLinkageValuesByID: function (wid) {
            var self = this;
            var clicked = this.getClickedByID(wid);
            var drills = {};
            BI.each(clicked, function (dId, value) {
                if (self.isDimensionExist(dId) && !self.isDimensionByDimensionID(dId)) {
                    drills[dId] = value;
                }
            });
            return drills;
        },

        //根据text dId 获取clicked 处理分组的情况
        getClickedValue4Group: function (v, dId) {
            var group = this.getDimensionGroupByID(dId);
            var fieldType = this.getFieldTypeByDimensionID(dId);
            var clicked = v;

            if (BI.isNotNull(group)) {
                if (fieldType === BICst.COLUMN.STRING) {
                    var details = group.details,
                        ungroup2Other = group.ungroup2Other,
                        ungroup2OtherName = group.ungroup2OtherName;
                    if (ungroup2Other === BICst.CUSTOM_GROUP.UNGROUP2OTHER.SELECTED &&
                        ungroup2OtherName === v) {
                        clicked = BICst.UNGROUP_TO_OTHER;
                    }
                    BI.some(details, function (i, detail) {
                        if (detail.value === v) {
                            clicked = detail.id;
                            return true;
                        }
                    });
                } else if (fieldType === BICst.COLUMN.NUMBER) {
                    var groupValue = group.group_value, groupType = group.type;
                    if (groupType === BICst.GROUP.CUSTOM_NUMBER_GROUP) {
                        var groupNodes = groupValue.group_nodes, useOther = groupValue.use_other;
                        if (useOther === v) {
                            clicked = BICst.UNGROUP_TO_OTHER;
                        }
                        BI.some(groupNodes, function (i, node) {
                            if (node.group_name === v) {
                                clicked = node.id;
                                return true;
                            }
                        });
                    }
                }
            }
            return clicked;
        },

        //获取组件中所有维度的钻取链A->B->C
        getDrillList: function (wid) {
            var drillMap = BI.Utils.getDrillByID(wid);
            var map = {};
            BI.each(drillMap, function (drId, ds) {
                map[drId] = [];
                BI.each(ds, function (idx, obj) {
                    map[drId].push(obj.dId)
                });
            });
            return map;
        },

        getWidgetFilterValueByID: function (wid) {
            if (this.isWidgetExistByID(wid)) {
                return Data.SharingPool.get("widgets", wid, "filter_value") || {};
            }
            return {};
        },

        getAllDimensionIDs: function (wid) {
            if (!wid) {
                return BI.keys(Data.SharingPool.cat("dimensions"))
            }
            if (this.isWidgetExistByID(wid)) {
                return BI.keys(Data.SharingPool.cat("widgets", wid, "dimensions"));
            }
            return [];
        },

        getAllUsedFieldIds: function () {
            var allDIds = this.getAllDimensionIDs();
            var fields = [];
            BI.each(allDIds, function (i, dId) {
                fields.push(BI.Utils.getFieldIDByDimensionID(dId));
            });
            return fields;
        },

        isWidgetExistByID: function (wid) {
            return this.getAllWidgetIDs().contains(wid);
        },

        //是否所有数据存在（配置部分将数据修改的情况）
        isAllFieldsExistByWidgetID: function (wid) {
            var self = this;
            var allDimIds = this.getAllDimensionIDs(wid);
            return !BI.some(allDimIds, function (i, dId) {
                return checkDimension(dId)
            });

            function checkDimension(dId) {
                var dType = BI.Utils.getDimensionTypeByID(dId);
                if (dType === BICst.TARGET_TYPE.STRING ||
                    dType === BICst.TARGET_TYPE.NUMBER ||
                    dType === BICst.TARGET_TYPE.DATE ||
                    dType === BICst.TARGET_TYPE.COUNTER) {
                    var fieldId = BI.Utils.getFieldIDByDimensionID(dId);
                    if (BI.isNull(Pool.fields[fieldId])) {
                        return true;
                    }
                } else {
                    //计算指标
                    var expression = BI.Utils.getExpressionByDimensionID(dId);
                    var fIds = expression.ids;
                    return BI.some(fIds, function (j, fId) {
                        var dId = fId;
                        var id = BI.Utils.getFieldIDByDimensionID(dId);
                        if (BI.isNotNull(self.getDimensionTypeByID(dId))) {
                            checkDimension(dId)
                        } else if (BI.isNull(Pool.fields[id])) {
                            return false;
                        }

                    });
                }
            }
        },

        //获取某组件下所有的维度
        getAllDimDimensionIDs: function (wid) {
            var result = [];
            var views = Data.SharingPool.get("widgets", wid, "view");
            BI.each(views, function (i, dim) {
                if (BI.Utils.isDimensionRegionByRegionType(i)) {
                    result = result.concat(dim);
                }
            });
            return result;
        },
        //获取某组件下所有的指标
        getAllTargetDimensionIDs: function (wid) {
            var result = [];
            var views = Data.SharingPool.get("widgets", wid, "view");
            BI.each(views, function (i, tar) {
                if (BI.Utils.isTargetRegionByRegionType(i)) {
                    result = result.concat(tar);
                }
            });
            return result;
        },

        getAllBaseDimensionIDs: function (wid) {
            var self = this;
            var result = [];
            var ids = this.getAllDimensionIDs(wid);
            var _set = [BICst.TARGET_TYPE.STRING,
                BICst.TARGET_TYPE.NUMBER,
                BICst.TARGET_TYPE.DATE, BICst.TARGET_TYPE.COUNTER];
            BI.each(ids, function (i, id) {
                var type = self.getDimensionTypeByID(id);
                if (_set.contains(type)) {
                    result.push(id);
                }
            });
            return result;
        },

        getAllUsableDimensionIDs: function (wid) {
            var self = this, dimIds = [], usableIds = [];
            if (!wid) {
                dimIds = BI.keys(Data.SharingPool.get("dimensions"));
            } else {
                dimIds = BI.keys(Data.SharingPool.get("widgets", wid, "dimensions"));
            }
            BI.each(dimIds, function (i, dId) {
                self.isDimensionUsable(dId) && (usableIds.push(dId));
            });
            return usableIds;
        },

        //获取某组件下所有可用的维度
        getAllUsableDimDimensionIDs: function (wid) {
            var self = this, usableDims = [];
            var allDims = this.getAllDimDimensionIDs(wid);
            BI.each(allDims, function (i, dId) {
                self.isDimensionUsable(dId) && (usableDims.push(dId));
            });
            return usableDims;
        },

        //获取某组件下所有可用的指标
        getAllUsableTargetDimensionIDs: function (wid) {
            var self = this, usableDims = [];
            var allDims = this.getAllTargetDimensionIDs(wid);
            BI.each(allDims, function (i, dId) {
                self.isDimensionUsable(dId) && (usableDims.push(dId));
            });
            return usableDims;
        },

        getImagesByWidgetID: function (wid) {
            var settings = this.getWidgetSettingsByID(wid);
            return settings.images || [];
        },

        getDatalabelByWidgetID: function (wid) {
            var settings = this.getWidgetSettingsByID(wid);
            return settings.data_label || {};
        },

        /**
         * 维度相关
         */
        getWidgetIDByDimensionID: function (dId) {
            var self = this;
            if (!this._dimension2WidgetMap) {
                this._dimension2WidgetMap = {};
            }
            if (BI.isNotNull(this._dimension2WidgetMap[dId])) {
                return this._dimension2WidgetMap[dId];
            }
            var widgets = this.getAllWidgetIDs();
            var wid = BI.find(widgets, function (i, wid) {
                var dims = self.getAllDimensionIDs(wid);
                return BI.find(dims, function (j, id) {
                    return dId == id;
                })
            });
            this._dimension2WidgetMap[dId] = wid;
            return wid;
        },

        getDimensionNameByID: function (did) {
            if (BI.isNotNull(Data.SharingPool.cat("dimensions", did))) {
                return Data.SharingPool.get("dimensions", did, "name");
            }

        },

        isDimensionUsable: function (did) {
            if (BI.isNotNull(Data.SharingPool.cat("dimensions", did))) {
                return Data.SharingPool.get("dimensions", did, "used");
            }
        },

        isDimensionExist: function (did) {
            return this.getAllDimensionIDs().contains(did);
        },

        getDimensionSortByID: function (did) {
            if (BI.isNotNull(Data.SharingPool.cat("dimensions", did))) {
                return Data.SharingPool.get("dimensions", did, "sort") || {};
            }
            return {};
        },

        getDimensionSrcByID: function (did) {
            if (BI.isNotNull(Data.SharingPool.cat("dimensions", did))) {
                return Data.SharingPool.get("dimensions", did, "_src") || {};
            }
            return {};
        },

        getDimensionGroupByID: function (did) {
            if (BI.isNotNull(Data.SharingPool.cat("dimensions", did))) {
                return Data.SharingPool.get("dimensions", did, "group") || {};
            }
            return {};

        },

        getDimensionTypeByID: function (did) {
            if (BI.isNotNull(Data.SharingPool.cat("dimensions", did))) {
                return Data.SharingPool.get("dimensions", did, "type");
            }

        },

        getDimensionFilterValueByID: function (did) {
            if (BI.isNotNull(Data.SharingPool.cat("dimensions", did))) {
                return Data.SharingPool.get("dimensions", did, "filter_value") || {};
            }
            return {};
        },

        getDimensionSettingsByID: function (did) {
            if (BI.isNotNull(Data.SharingPool.cat("dimensions", did))) {
                return Data.SharingPool.get("dimensions", did, "settings") || {
                        format: BICst.TARGET_STYLE.FORMAT.NORMAL,
                        numLevel: BICst.TARGET_STYLE.NUM_LEVEL.NORMAL,
                        unit: "",
                        iconStyle: BICst.TARGET_STYLE.ICON_STYLE.NONE,
                        mark: 0,
                        conditions: [],
                        numSeparators: true,
                    };
            }
            return {};
        },

        getDimensionHyperLinkByID: function (did) {
            if (BI.isNotNull(Data.SharingPool.cat("dimensions", did))) {
                return Data.SharingPool.get("dimensions", did, "hyperlink") || {};
            }
            return {};
        },

        getFieldTypeByDimensionID: function (did) {
            if (BI.isNotNull(Data.SharingPool.cat("dimensions", did))) {
                var fieldId = this.getFieldIDByDimensionID(did);
                if (BI.isKey(fieldId)) {
                    return this.getFieldTypeByID(fieldId);
                }
                return BICst.COLUMN.NUMBER;
            }
        },


        getDimensionStyleOfChartByID: function (did) {
            if (BI.isNotNull(Data.SharingPool.cat("dimensions", did))) {
                return Data.SharingPool.get("dimensions", did, "style_of_chart");
            }

        },

        getDimensionCordonByID: function (did) {
            if (BI.isNotNull(Data.SharingPool.cat("dimensions", did))) {
                return Data.SharingPool.get("dimensions", did, "cordon");
            }

        },

        getDimensionPositionByID: function (did) {
            if (BI.isNotNull(Data.SharingPool.cat("dimensions", did))) {
                return Data.SharingPool.get("dimensions", did, "position");
            }

        },

        //获取维度或指标所对应的字段id
        getFieldIDByDimensionID: function (did) {
            if (BI.isNotNull(Data.SharingPool.cat("dimensions", did))) {
                return Data.SharingPool.get("dimensions", did, "_src", "field_id");
            }

        },

        //获得计算指标对应的公式内容
        getExpressionByDimensionID: function (did) {
            if (BI.isNotNull(Data.SharingPool.cat("dimensions", did))) {
                return Data.SharingPool.get("dimensions", did, "_src", "expression");
            }
        },

        //获取计算指标用到的指标IDs
        getExpressionValuesByDimensionID: function (dId) {
            var expression = this.getExpressionByDimensionID(dId);
            if (BI.isNotNull(expression)) {
                return expression.ids || [];
            }
            return [];
        },

        //获取维度或指标所对应的表id
        getTableIDByDimensionID: function (did) {
            if (BI.isNotNull(Data.SharingPool.cat("dimensions", did))) {
                return BI.Utils.getTableIdByFieldID(BI.Utils.getFieldIDByDimensionID(did));
            }

        },

        getDimensionMapByDimensionID: function (did) {
            if (BI.isNotNull(Data.SharingPool.cat("dimensions", did))) {
                return Data.SharingPool.get("dimensions", did, "dimension_map") || {};
            }
            return {};
        },

        getDatalabelByID: function (did) {
            if (BI.isNotNull(Data.SharingPool.cat("dimensions", did))) {
                return Data.SharingPool.get("dimensions", did, "data_label") || {};
            }
            return {};
        },

        getDataimageByID: function (did) {
            if (BI.isNotNull(Data.SharingPool.cat("dimensions", did))) {
                return Data.SharingPool.get("dimensions", did, "data_image") || {};
            }
            return {};
        },

        getSeriesAccumulationByID: function (did) {
            if (BI.isNotNull(Data.SharingPool.cat("dimensions", did))) {
                return Data.SharingPool.get("dimensions", did, "seriesAccumulation") || {};
            }
            return {};
        },

        isDimensionByDimensionID: function (dId) {
            var wId = this.getWidgetIDByDimensionID(dId);
            var views = this.getWidgetViewByID(wId);
            var region = 0;
            BI.some(views, function (reg, view) {
                if (view.contains(dId)) {
                    region = reg;
                    return true;
                }
            });
            return BI.Utils.isDimensionRegionByRegionType(region);
        },

        isDimensionRegionByRegionType: function (regionType) {
            return BI.parseInt(regionType) < BI.parseInt(BICst.REGION.TARGET1);
        },

        isDimensionRegion1ByRegionType: function (regionType) {
            return BI.parseInt(regionType) >= BI.parseInt(BICst.REGION.DIMENSION1)
                && BI.parseInt(regionType) < BI.parseInt(BICst.REGION.DIMENSION2);
        },

        isDimensionRegion2ByRegionType: function (regionType) {
            return BI.parseInt(regionType) >= BI.parseInt(BICst.REGION.DIMENSION2)
                && BI.parseInt(regionType) < BI.parseInt(BICst.REGION.TARGET1);
        },

        isTargetByDimensionID: function (dId) {
            var wId = this.getWidgetIDByDimensionID(dId);
            var views = this.getWidgetViewByID(wId);
            var type = this.getDimensionTypeByID(dId);
            var _set = [BICst.TARGET_TYPE.STRING,
                BICst.TARGET_TYPE.NUMBER,
                BICst.TARGET_TYPE.DATE];
            var region = 0;
            BI.some(views, function (reg, view) {
                if (view.contains(dId)) {
                    region = reg;
                    return true;
                }
            });
            return BI.Utils.isTargetRegionByRegionType(region) && _set.contains(type);
        },

        isTargetRegionByRegionType: function (regionType) {
            return BI.parseInt(regionType) >= BI.parseInt(BICst.REGION.TARGET1);
        },

        isTargetRegion1ByRegionType: function (regionType) {
            return BI.parseInt(regionType) >= BI.parseInt(BICst.REGION.TARGET1)
                && BI.parseInt(regionType) < BI.parseInt(BICst.REGION.TARGET2);
        },

        isTargetRegion2ByRegionType: function (regionType) {
            return BI.parseInt(regionType) >= BI.parseInt(BICst.REGION.TARGET2)
                && BI.parseInt(regionType) < BI.parseInt(BICst.REGION.TARGET3);
        },

        isTargetRegion3ByRegionType: function (regionType) {
            return BI.parseInt(regionType) >= BI.parseInt(BICst.REGION.TARGET3);
        },

        isDimensionType: function (type) {
            return type === BICst.TARGET_TYPE.STRING || type === BICst.TARGET_TYPE.DATE || type === BICst.TARGET_TYPE.NUMBER;
        },

        isTargetType: function (type) {
            return type === BICst.TARGET_TYPE.NUMBER
                || this.isCounterType(type)
                || this.isCalculateTargetType(type);
        },

        isCounterType: function (type) {
            return type === BICst.TARGET_TYPE.COUNTER;
        },

        isCalculateTargetType: function (type) {
            var _set = [BICst.TARGET_TYPE.FORMULA,
                BICst.TARGET_TYPE.MONTH_ON_MONTH_RATE,
                BICst.TARGET_TYPE.MONTH_ON_MONTH_VALUE,
                BICst.TARGET_TYPE.RANK,
                BICst.TARGET_TYPE.RANK_IN_GROUP,
                BICst.TARGET_TYPE.SUM_OF_ABOVE,
                BICst.TARGET_TYPE.SUM_OF_ABOVE_IN_GROUP,
                BICst.TARGET_TYPE.SUM_OF_ALL,
                BICst.TARGET_TYPE.SUM_OF_ALL_IN_GROUP,
                BICst.TARGET_TYPE.YEAR_ON_YEAR_RATE,
                BICst.TARGET_TYPE.YEAR_ON_YEAR_VALUE
            ];
            return _set.contains(type);
        },

        isCalculateTargetByDimensionID: function (dId) {
            var type = this.getDimensionTypeByID(dId);
            return this.isCalculateTargetType(type);
        },

        isCounterTargetByDimensionID: function (dId) {
            var wId = this.getWidgetIDByDimensionID(dId);
            var views = this.getWidgetViewByID(wId);
            var type = this.getDimensionTypeByID(dId);
            var _set = [BICst.TARGET_TYPE.COUNTER
            ];
            var region = 0;
            BI.some(views, function (reg, view) {
                if (view.contains(dId)) {
                    region = reg;
                    return true;
                }
            });
            return BI.Utils.isTargetRegionByRegionType(region) && _set.contains(type);
        },

        isSrcUsedBySrcID: function (srcId) {
            var result = BI.find(this.getAllDimensionIDs(), function (i, dId) {
                var src = Data.SharingPool.get("dimensions", dId, "_src");
                return src.id === srcId;
            });
            return BI.isNotNull(result);
        },

        getTargetRelationByDimensionID: function (dId, targetID) {
            var dimensionMap = BI.Utils.getDimensionMapByDimensionID(dId);
            var target_relation;
            if (BI.isNull(targetID)) {
                target_relation = BI.firstObject(dimensionMap).target_relation;
            } else {
                target_relation = dimensionMap[targetID].target_relation;
            }
            return target_relation;
        },

        getDimensionTypeByFieldID: function (fieldId) {
            var fieldType = BI.Utils.getFieldTypeByID(fieldId);
            switch (fieldType) {
                case BICst.COLUMN.STRING:
                    return BICst.TARGET_TYPE.STRING;
                case BICst.COLUMN.COUNTER:
                    return BICst.TARGET_TYPE.COUNTER;
                case BICst.COLUMN.NUMBER:
                    return BICst.TARGET_TYPE.NUMBER;
                case BICst.COLUMN.DATE:
                    return BICst.TARGET_TYPE.DATE;
                default:
                    return BICst.TARGET_TYPE.NUMBER;
            }
        },

        getRegionDimensionIdsByDimensionID: function (dId) {
            var wId = BI.Utils.getWidgetIDByDimensionID(dId);
            var view = BI.Utils.getWidgetViewByID(wId);
            var regionType = BI.findKey(view, function (regionType, dIds) {
                if (BI.contains(dIds, dId)) {
                    return true
                }
            });
            return view[regionType];
        },

        getRegionTypeByDimensionID: function (dId) {
            var wId = BI.Utils.getWidgetIDByDimensionID(dId);
            var view = BI.Utils.getWidgetViewByID(wId);
            return BI.findKey(view, function (regionType, dIds) {
                if (BI.contains(dIds, dId)) {
                    return true
                }
            });
        },

        getWidgetIconClsByWidgetId: function (wId) {
            var widgetType = BI.Utils.getWidgetTypeByID(wId);

        },


        //获取某维度或指标是否被其他维度或指标（计算指标）使用的指标
        getDimensionUsedByOtherDimensionsByDimensionID: function (dId) {
            var self = this;
            if (this.isDimensionByDimensionID(dId)) {
                return [];
            }
            var wId = this.getWidgetIDByDimensionID(dId);
            var ids = this.getAllTargetDimensionIDs(wId);
            var result = [];
            BI.each(ids, function (i, id) {
                var tids = self.getExpressionValuesByDimensionID(id);
                if (tids.contains(dId)) {
                    result.push(id);
                }
            });
            return result;
        },

        //dimension是否合法:
        // 1.与各个指标间是否设置了路径
        // 2.设置的路径是否还存在，不存在的话剩余路径是否可以替换
        isDimensionValidByDimensionID: function (dId) {
            var dimensionMap = this.getDimensionMapByDimensionID(dId);
            var tIds = this.getAllTargetDimensionIDs(this.getWidgetIDByDimensionID(dId));
            var res = BI.find(tIds, function (idx, tId) {
                return !BI.Utils.isCalculateTargetByDimensionID(tId) && !checkDimAndTarRelationValidInCurrentPaths(tId);
            });
            return BI.isNull(res);

            function checkDimAndTarRelationValidInCurrentPaths(tId) {
                var valid = true;
                if (BI.has(dimensionMap, tId)) {
                    var targetRelation = dimensionMap[tId].target_relation;
                    BI.any(targetRelation, function (id, path) {
                        var pId = BI.Utils.getFirstRelationPrimaryIdFromRelations(path);
                        var fId = BI.Utils.getLastRelationForeignIdFromRelations(path);
                        var paths = BI.Utils.getPathsFromFieldAToFieldB(pId, fId);
                        if (!BI.deepContains(paths, path)) {
                            if (paths.length === 1) {
                            } else {
                                valid = false;
                                return true;
                            }
                        }
                    })
                } else {
                    var paths = BI.Utils.getPathsFromFieldAToFieldB(BI.Utils.getFieldIDByDimensionID(dId), BI.Utils.getFieldIDByDimensionID(tId))
                    valid = paths.length === 1;
                }
                return valid
            }
        },


        /**
         * 关联相关
         */
        getFirstRelationPrimaryIdFromRelations: function (path) {
            var relation = BI.first(path);
            var primaryId = "";
            if (BI.isNotNull(relation)) {
                primaryId = relation.primaryKey.field_id;
            }
            return primaryId;
        },

        getLastRelationForeignIdFromRelations: function (path) {
            var foreignId;
            var relation = BI.last(path);
            BI.isNotNull(relation) && (foreignId = relation.foreignKey.field_id);
            return foreignId;
        },

        getPrimaryIdFromRelation: function (relation) {
            var primaryId;
            BI.isNotNull(relation.primaryKey) && (primaryId = relation.primaryKey.field_id);
            return primaryId;
        },

        getForeignIdFromRelation: function (relation) {
            var foreignId;
            BI.isNotNull(relation.foreignKey) && (foreignId = relation.foreignKey.field_id);
            return foreignId;
        },

        getPrimaryRelationTablesByTableID: function (tableId) {
            var primaryTables = [];
            BI.each(Pool.foreignRelations[tableId], function (tId, relations) {
                if (relations.length > 0) {
                    primaryTables.push(tId);
                }
            });
            return BI.uniq(primaryTables);
        },

        getForeignRelationTablesByTableID: function (tableId) {
            var foreignTables = [];
            BI.each(Pool.relations[tableId], function (tId, relations) {
                if (relations.length > 0) {
                    foreignTables.push(tId);
                }
            });
            return BI.uniq(foreignTables);
        },

        getPathsFromTableAToTableB: function (from, to) {
            var relations = Pool.relations;
            if (BI.isNull(from) || BI.isNull(to)) {
                return [];
            }
            if (BI.isNull(relations[from])) {
                return [];
            }
            if (BI.isNull(relations[from][to])) {
                return [];
            }
            return removeCircleInPath();

            function removeCircleInPath() {
                var relationOrder = [];
                return BI.filter(relations[from][to], function (idx, path) {
                    var orders = [];
                    var hasCircle = BI.any(path, function (id, relation) {
                        var prev = BI.Utils.getTableIdByFieldID(BI.Utils.getPrimaryIdFromRelation(relation));
                        var last = BI.Utils.getTableIdByFieldID(BI.Utils.getForeignIdFromRelation(relation));
                        var result = BI.find(relationOrder, function (i, order) {
                            if (order[0] === last && order[1] === prev) {
                                return true;
                            }
                        });
                        orders.push([prev, last]);
                        return BI.isNotNull(result);
                    });
                    if (hasCircle === false) {
                        relationOrder = BI.concat(relationOrder, orders);
                    }
                    return hasCircle === false;
                });
            }

        },

        getPathsFromFieldAToFieldB: function (from, to) {
            var self = this;
            if (BI.isNull(from) || BI.isNull(to)) {
                return [];
            }
            var tableA = BI.Utils.getTableIdByFieldID(from);
            var tableB = BI.Utils.getTableIdByFieldID(to);
            var path = this.getPathsFromTableAToTableB(tableA, tableB);
            if (tableA === tableB) {        //同一张表
                return [[{
                    primaryKey: {field_id: from, table_id: self.getTableIdByFieldID(from)},
                    foreignKey: {field_id: to, table_id: self.getTableIdByFieldID(to)}
                }]]
            }
            return path;

            //获取自循环生成的层级所在的关联
            function getRelationOfselfCircle(from, to, paths) {
                return BI.find(paths, function (idx, path) {
                    return BI.find(path, function (id, relation) {
                        var foreignId = self.getForeignIdFromRelation(relation);
                        return foreignId === from || foreignId === to;
                    });
                })
            }

            //对自循环表检测路径合法依据：路径中的a个关联中是否存在外键为primKey
            function checkPathAvailable(paths, primKey, foreign) {
                var result = BI.find(paths, function (idx, path) {
                    return BI.find(path, function (id, relation) {
                        var foreignKey = self.getForeignIdFromRelation(relation);
                        return foreignKey === primKey || foreignKey === foreign;
                    });
                });
                return BI.isNull(result);
            }
        },

        getCommonPrimaryTablesByTableIDs: function (tableIds) {
            var self = this;
            var commonTables = [];
            var primaryTableMap = {};
            BI.each(tableIds, function (i, tableId) {
                primaryTableMap[tableId] = self.getPrimaryRelationTablesByTableID(tableId);
                primaryTableMap[tableId].splice(0, 0, tableId);
            });
            BI.each(primaryTableMap, function (id, primaryTables) {
                var primaryTableMapCp = BI.deepClone(primaryTableMap);
                primaryTables.push(id);
                BI.each(primaryTables, function (i, primaryTableId) {
                    var isCommonTable = true;
                    BI.findKey(primaryTableMapCp, function (id, tables) {
                        if (!BI.contains(tables, primaryTableId)) {
                            isCommonTable = false;
                            return true
                        }
                    });
                    if (isCommonTable === true) {
                        (!BI.contains(commonTables, primaryTableId)) && commonTables.push(primaryTableId);
                    }
                });
            });
            return commonTables;
        },

        getCommonForeignTablesByTableIDs: function (tableIds) {
            var self = this;
            var commonTables = [];
            var foreignTableMap = {};
            BI.each(tableIds, function (i, tableId) {
                foreignTableMap[tableId] = self.getForeignRelationTablesByTableID(tableId);
                foreignTableMap[tableId].splice(0, 0, tableId);
            });
            BI.each(foreignTableMap, function (id, foreignTables) {
                var foreignTableMapCp = BI.deepClone(foreignTableMap);
                foreignTables.push(id);
                BI.each(foreignTables, function (i, foreignTableId) {
                    var isCommonTable = true;
                    BI.findKey(foreignTableMapCp, function (id, tables) {
                        if (!BI.contains(tables, foreignTableId)) {
                            isCommonTable = false;
                            return true
                        }
                    });
                    if (isCommonTable === true) {
                        (!BI.contains(commonTables, foreignTableId)) && commonTables.push(foreignTableId);
                    }
                });
            });
            return commonTables;
        },

        //表是否属于相关子表和主表
        isTableInRelativeTables: function (tableIds, target) {
            var self = this;
            var commonIds = BI.Utils.getCommonForeignTablesByTableIDs(tableIds);
            return BI.some(commonIds, function (i, tId) {
                if (tId === target) {
                    return true;
                }
                var ids = self.getPrimaryRelationTablesByTableID(tId);
                if (ids.contains(target)) {
                    return true;
                }
            });
        },

        //获取相关的主表和子表，包括tableIds的公共子表以及这些子表的所有主表
        getRelativePrimaryAndForeignTableIDs: function (tableIds) {
            var self = this;
            var result = [];
            var commonIds = this.getCommonForeignTablesByTableIDs(tableIds);
            result = result.concat(commonIds);
            BI.each(commonIds, function (i, tId) {
                var ids = self.getPrimaryRelationTablesByTableID(tId);
                result = result.concat(ids);
            });
            return result;
        },

        getAllPrimaryKeyByTableIds: function (tableIds) {
            var self = this;
            var relations = Pool.relations;
            return BI.flatten(BI.map(tableIds, function (i, tableId) {
                if (BI.isNull(tableId)) {
                    return [];
                }
                if (BI.isNull(relations[tableId])) {
                    return [];
                }
                var tPaths = relations[tableId];
                return BI.map(tPaths, function (idx, paths) {
                    return BI.map(paths, function (id, path) {
                        return self.getFirstRelationPrimaryIdFromRelations(path);
                    });
                });
            }));
        },

        /**
         * 数据相关
         */

        getPreviewTableDataByTableId: function (tableId, callback) {
            //构造一个明细表
            var self = this;
            var fields = this.getSortedFieldIdsOfOneTableByTableId(tableId);
            var dimensions = {}, view = {10000: []};
            BI.each(fields, function (i, fieldId) {
                var id = BI.UUID();
                var dimensionMap = {}, group = {};
                dimensionMap[tableId] = {
                    target_relation: []
                };
                if (self.getFieldTypeByID(fieldId) === BICst.COLUMN.DATE) {
                    group.type = BICst.GROUP.YMDHMS;
                }
                var dType = BICst.TARGET_TYPE.STRING;
                switch (self.getFieldTypeByID(fieldId)) {
                    case BICst.COLUMN.DATE:
                        dType = BICst.TARGET_TYPE.DATE;
                        break;
                    case BICst.COLUMN.NUMBER:
                        dType = BICst.TARGET_TYPE.NUMBER;
                }
                dimensions[id] = {
                    name: id,
                    _src: {
                        field_id: fieldId,
                        table_id: tableId
                    },
                    type: dType,
                    used: true,
                    dimension_map: dimensionMap,
                    group: group
                };
                view[10000].push(id);
            });
            var widget = {
                type: BICst.WIDGET.DETAIL,
                bounds: {
                    height: 0,
                    width: 0,
                    left: 0,
                    top: 0
                },
                name: "__StatisticWidget__" + BI.UUID(),
                page: 0,
                dimensions: dimensions,
                view: view
            };
            Data.Req.reqWidgetSettingByData({widget: widget}, function (res) {
                callback(res.data);
            });
        },

        getDataByFieldID: function (fid, callback) {
            var d = {
                type: BICst.WIDGET.TABLE,
                bounds: {
                    height: 0,
                    width: 0,
                    left: 0,
                    top: 0
                },
                name: "__StatisticWidget__" + BI.UUID(),
                page: -1,
                dimensions: {
                    1234567: {
                        name: "__Dimension__",
                        _src: {
                            field_id: fid
                        },
                        type: BICst.COLUMN.STRING,
                        used: true
                    }
                },
                view: {
                    10000: [1234567]
                }
            };
            Data.Req.reqWidgetSettingByData({widget: d}, function (data) {
                callback(BI.pluck(data.data.c, "n"));
            });
        },

        getNoGroupedDataByDimensionID: function (dId, callback) {
            var dimension = Data.SharingPool.get("dimensions", dId);
            dimension.group = {type: BICst.GROUP.ID_GROUP};
            dimension.filter_value = {};
            dimension.used = true;
            var dimensions = {};
            dimensions[dId] = dimension;
            var view = {};
            view[BICst.REGION.DIMENSION1] = [dId];

            var targetIds = this.getAllTargetDimensionIDs(this.getWidgetIDByDimensionID(dId));
            BI.each(targetIds, function (idx, targetId) {
                dimensions[targetId] = Data.SharingPool.get("dimensions", targetId);
                if (!BI.has(view, BICst.REGION.TARGET1)) {
                    view[BICst.REGION.TARGET1] = [];
                }
                view[BICst.REGION.TARGET1].push(targetId);
            });

            this.getWidgetDataByWidgetInfo(dimensions, view, function (data) {
                callback(BI.pluck(data.data.c, "n"));
            }, {page: BICst.TABLE_PAGE_OPERATOR.ALL_PAGE});

        },

        getDataByDimensionID: function (dId, callback) {
            var wid = this.getWidgetIDByDimensionID(dId);
            var dimension = Data.SharingPool.get("dimensions", dId);
            dimension.filter_value = {};
            dimension.used = true;
            var widget = Data.SharingPool.get("widgets", wid);
            widget.page = -1;
            widget.real_data = true;
            widget.dimensions = {};
            widget.dimensions[dId] = dimension;
            widget.view = {};
            widget.view[BICst.REGION.DIMENSION1] = [dId];

            var targetIds = this.getAllTargetDimensionIDs(this.getWidgetIDByDimensionID(dId));
            BI.each(targetIds, function (idx, targetId) {
                widget.dimensions[targetId] = Data.SharingPool.get("dimensions", targetId);
                if (!BI.has(widget.view, BICst.REGION.TARGET1)) {
                    widget.view[BICst.REGION.TARGET1] = [];
                }
                widget.view[BICst.REGION.TARGET1].push(targetId);
            });

            Data.Req.reqWidgetSettingByData({widget: widget}, function (data) {
                callback(BI.pluck(data.data.c, "n"));
            });
        },

        getWidgetDataByDimensionInfo: function (src, options) {
            var name = "__StatisticWidget__" + BI.UUID();
            var data = {
                type: BICst.WIDGET.STRING,
                bounds: {
                    height: 0,
                    width: 0,
                    left: 0,
                    top: 0
                },
                name: name,
                dimensions: {
                    "1234567": BI.extend({
                        name: "__Dimension__",
                        _src: src,
                        type: BICst.TARGET_TYPE.STRING
                    }, options)
                },
                view: {
                    10000: ["1234567"]
                }
            };

            var options = {};

            return {
                setDimensionType: function (type) {
                    data.dimensions["1234567"].type = type;
                },

                setSrc: function (src) {
                    data.dimensions["1234567"]._src = src;
                },

                setFilterValue: function (filter_value) {
                    data.dimensions["1234567"].filter_value = filter_value;
                },

                setSort: function (sort) {
                    data.dimensions["1234567"].sort = sort;
                },

                setOptions: function (opt) {
                    options = opt;
                },

                first: function (call) {
                    var copy = BI.deepClone(data);
                    copy.page = BICst.TABLE_PAGE_OPERATOR.REFRESH;
                    Data.Req.reqWidgetSettingByData({widget: BI.extend(copy, {text_options: options})}, function (res) {
                        call(res);
                    });
                },

                prev: function (call) {
                    var copy = BI.deepClone(data);
                    copy.page = BICst.TABLE_PAGE_OPERATOR.ROW_PRE;
                    Data.Req.reqWidgetSettingByData({widget: BI.extend(copy, {text_options: options})}, function (res) {
                        call(res);
                    });
                },

                next: function (call) {
                    var copy = BI.deepClone(data);
                    copy.page = BICst.TABLE_PAGE_OPERATOR.ROW_NEXT;
                    Data.Req.reqWidgetSettingByData({widget: BI.extend(copy, {text_options: options})}, function (res) {
                        call(res);
                    });
                },

                all: function (call) {
                    var copy = BI.deepClone(data);
                    copy.page = BICst.TABLE_PAGE_OPERATOR.ALL_PAGE;
                    Data.Req.reqWidgetSettingByData({widget: BI.extend(copy, {text_options: options})}, function (res) {
                        call(res);
                    });
                }
            };
        },

        getWidgetDataByWidgetInfo: function (dimensions, view, callback, options) {
            var self = this;
            options || (options = {});
            var data = {
                bounds: {
                    left: 0,
                    top: 0,
                    width: 0,
                    height: 0
                },
                name: "__StatisticWidget__" + BI.UUID(),
                dimensions: dimensions,
                filter: {
                    filter_type: BICst.FILTER_TYPE.AND,
                    filter_value: self.getControlCalculations(options.id)
                },
                view: view
            };
            Data.Req.reqWidgetSettingByData({widget: BI.extend(data, options)}, function (res) {
                callback(res);
            })
        },

        getControlCalculations: function (notcontain) {
            var self = this, filterValues = [];
            //控件
            var widgetIds = this.getAllWidgetIDs();
            BI.each(widgetIds, function (i, id) {
                if (!self.isControlWidgetByWidgetId(id)) {
                    return;
                }
                if (id === notcontain) {
                    return;
                }
                //去掉自身和在自身之后创建的控件
                if (BI.isNotNull(notcontain) && self.isControlWidgetByWidgetId(notcontain)
                    && self.getWidgetInitTimeByID(id) > self.getWidgetInitTimeByID(notcontain)) {
                    return;
                }
                var value = self.getWidgetValueByID(id);
                if (BI.isNotNull(value)) {
                    var dimensionIds = self.getAllDimensionIDs(id);
                    BI.each(dimensionIds, function (i, dimId) {
                        var fValue = value, fType = "";
                        if (BI.isNull(fValue) || BI.isEmptyString(value) || BI.isEmptyObject(value)) {
                            return;
                        }
                        var filter = null;
                        switch (self.getWidgetTypeByID(id)) {
                            case BICst.WIDGET.STRING:
                            case BICst.WIDGET.STRING_LIST:
                            case BICst.WIDGET.LIST_LABEL:
                                fType = BICst.TARGET_FILTER_STRING.BELONG_VALUE;
                                filter = {
                                    filter_type: fType,
                                    filter_value: fValue,
                                    _src: {field_id: self.getFieldIDByDimensionID(dimId)}
                                };
                                break;
                            case BICst.WIDGET.SINGLE_SLIDER:
                            case BICst.WIDGET.INTERVAL_SLIDER:
                            case BICst.WIDGET.NUMBER:
                                fType = BICst.TARGET_FILTER_NUMBER.BELONG_VALUE;
                                filter = {
                                    filter_type: fType,
                                    filter_value: fValue,
                                    _src: {field_id: self.getFieldIDByDimensionID(dimId)}
                                };
                                break;
                            case BICst.WIDGET.DATE:
                                fType = BICst.FILTER_DATE.BELONG_DATE_RANGE;
                                var start = fValue.start, end = fValue.end;
                                fValue = {};
                                if (BI.isNotNull(start)) {
                                    start = parseComplexDate(start);
                                    fValue.start = start;
                                }
                                if (BI.isNotNull(end)) {
                                    end = parseComplexDate(end);
                                    fValue.end = end;
                                }
                                filter = {
                                    filter_type: fType,
                                    filter_value: fValue,
                                    _src: {field_id: self.getFieldIDByDimensionID(dimId)}
                                };
                                break;
                            case BICst.WIDGET.MONTH:
                                fType = BICst.FILTER_DATE.EQUAL_TO;
                                var year = fValue.year, month = fValue.month;
                                if (BI.isNumeric(year)) {
                                    filterValues.push({
                                        filter_type: BICst.FILTER_DATE.EQUAL_TO,
                                        filter_value: {group: BICst.GROUP.Y, values: year},
                                        _src: {field_id: self.getFieldIDByDimensionID(dimId)}
                                    });
                                }
                                if (!BI.isNumeric(month)) {
                                    return;
                                }
                                fValue = {group: BICst.GROUP.M, values: month};
                                filter = {
                                    filter_type: fType,
                                    filter_value: fValue,
                                    _src: {field_id: self.getFieldIDByDimensionID(dimId)}
                                };
                                break;
                            case BICst.WIDGET.QUARTER:
                                fType = BICst.FILTER_DATE.EQUAL_TO;
                                var quarter = fValue.quarter, year = fValue.year;
                                if (BI.isNumeric(year)) {
                                    filterValues.push({
                                        filter_type: BICst.FILTER_DATE.EQUAL_TO,
                                        filter_value: {group: BICst.GROUP.Y, values: year},
                                        _src: {field_id: self.getFieldIDByDimensionID(dimId)}
                                    });
                                }
                                if (!BI.isNumeric(quarter)) {
                                    return;
                                }
                                fValue = {group: BICst.GROUP.S, values: quarter};
                                filter = {
                                    filter_type: fType,
                                    filter_value: fValue,
                                    _src: {field_id: self.getFieldIDByDimensionID(dimId)}
                                };
                                break;
                            case BICst.WIDGET.YEAR:
                                fType = BICst.FILTER_DATE.EQUAL_TO;
                                fValue = {group: BICst.GROUP.Y, values: fValue};
                                filter = {
                                    filter_type: fType,
                                    filter_value: fValue,
                                    _src: {field_id: self.getFieldIDByDimensionID(dimId)}
                                };
                                break;
                            case BICst.WIDGET.YMD:
                                fType = BICst.FILTER_DATE.EQUAL_TO;
                                fValue = {group: BICst.GROUP.YMD, values: parseComplexDate(fValue)};
                                filter = {
                                    filter_type: fType,
                                    filter_value: fValue,
                                    _src: {field_id: self.getFieldIDByDimensionID(dimId)}
                                };
                                break;
                        }
                        BI.isNotNull(filter) && filterValues.push(filter);
                    });

                    //树控件过滤条件设置,不能对每个纬度单独设置过滤条件
                    if (self.getWidgetTypeByID(id) === BICst.WIDGET.TREE || self.getWidgetTypeByID(id) === BICst.WIDGET.TREE_LIST) {
                        var viewDimensionIds = self.getWidgetViewByID(id)[BICst.REGION.DIMENSION1];
                        var treeValue = [];
                        createTreeFilterValue(treeValue, value, 0, viewDimensionIds);
                        filter = {
                            filter_type: BICst.FILTER_TYPE.OR,
                            filter_value: treeValue
                        };
                        filterValues.push(filter);
                    }

                    if (self.getWidgetTypeByID(id) === BICst.WIDGET.TREE_LABEL) {
                        var viewDimensionIds = self.getWidgetViewByID(id)[BICst.REGION.DIMENSION1];
                        var treeValue = [];
                        createTreeLabelFilterValue(treeValue, value, 0, viewDimensionIds);
                        filter = {
                            filter_type: BICst.FILTER_TYPE.OR,
                            filter_value: treeValue
                        };
                        filterValues.push(filter);
                    }

                    if (value.length === 1) {
                        var filter = value[0];
                        parseFilter(filter);
                        filterValues.push(filter);
                    }
                }
            });
            return filterValues;

            function createTreeFilterValue(result, v, floor, dimensionIds, fatherFilterValue) {
                BI.each(v, function (value, child) {
                        var leafFilterObj = [{
                            filter_type: BICst.TARGET_FILTER_STRING.BELONG_VALUE,
                            filter_value: {
                                type: BI.Selection.Multi,
                                value: [value]
                            },
                            // _src: {field_id: self.getFieldIDByDimensionID(dimensionIds[floor])}
                            _src: self.getDimensionSrcByID(dimensionIds[floor])
                        }];
                        if (BI.isEmptyObject(child)) {
                            var filterObj = {
                                filter_type: BICst.FILTER_TYPE.AND,
                                filter_value: []
                            };
                            filterObj.filter_value = BI.concat(filterObj.filter_value, leafFilterObj);
                            BI.isNotNull(fatherFilterValue) && (filterObj.filter_value = BI.concat(filterObj.filter_value, fatherFilterValue));
                            result.push(filterObj);
                        } else {
                            if (fatherFilterValue) {
                                leafFilterObj = BI.concat(leafFilterObj, fatherFilterValue);
                            }
                            createTreeFilterValue(result, child, floor + 1, dimensionIds, leafFilterObj);
                        }
                    }
                );
            }

            function createTreeLabelFilterValue(result, v, floor, dimensionIds, fatherFilterValue) {
                BI.each(v, function (value, child) {
                        var leafFilterObj = [{
                            filter_type: BICst.TARGET_FILTER_STRING.BELONG_VALUE,
                            filter_value: {
                                type: value === BICst.LIST_LABEL_TYPE.ALL ? BI.Selection.All : BI.Selection.Multi,
                                value: [value === BICst.LIST_LABEL_TYPE.ALL ? "" : value]
                            },
                            // _src: {field_id: self.getFieldIDByDimensionID(dimensionIds[floor])}
                            _src: self.getDimensionSrcByID(dimensionIds[floor])
                        }];
                        if (BI.isEmptyObject(child) === true) {
                            var filterObj = {
                                filter_type: BICst.FILTER_TYPE.AND,
                                filter_value: []
                            };
                            filterObj.filter_value = BI.concat(filterObj.filter_value, leafFilterObj);
                            BI.isNotNull(fatherFilterValue) && (filterObj.filter_value = BI.concat(filterObj.filter_value, fatherFilterValue));
                            result.push(filterObj);
                        } else {
                            if (leafFilterObj[0].filter_value.type === BI.Selection.All) {
                                leafFilterObj = [];
                            }
                            if (fatherFilterValue) {
                                leafFilterObj = BI.concat(leafFilterObj, fatherFilterValue);
                            }
                            createTreeLabelFilterValue(result, child, floor + 1, dimensionIds, leafFilterObj);
                        }
                    }
                );
            }
        },

        getWidgetCalculationByID: function (wid) {
            var self = this;
            var widget = Data.SharingPool.get("widgets", wid);
            var filterValues = [];

            //对于维度的条件，很有可能是一个什么属于分组 这边处理 （没放到构造的地方处理是因为“其他”）
            function parseStringFilter4Group(dId, value) {
                var group = BI.Utils.getDimensionGroupByID(dId);
                var details = group.details;
                var groupMap = {};
                BI.each(details, function (i, detail) {
                    groupMap[detail.id] = [];
                    BI.each(detail.content, function (j, content) {
                        groupMap[detail.id].push(content.value);
                    });
                });
                var groupNames = BI.keys(groupMap), ungroupName = group.ungroup2OtherName;
                if (group.ungroup2Other === BICst.CUSTOM_GROUP.UNGROUP2OTHER.SELECTED) {
                    // groupNames.push(ungroupName);
                    groupNames.push(BICst.UNGROUP_TO_OTHER);
                }
                // 对于drill和link 一般value的数组里只有一个值
                var v = value[0];
                if (groupNames.contains(v)) {
                    if (v === ungroupName) {
                        var vs = [];
                        BI.each(groupMap, function (gk, gv) {
                            gk !== v && (vs = vs.concat(gv));
                        });
                        return {
                            filter_type: BICst.TARGET_FILTER_STRING.NOT_BELONG_VALUE,
                            filter_value: {type: BI.Selection.Multi, value: vs},
                            _src: {field_id: BI.Utils.getFieldIDByDimensionID(dId)}
                        }
                    }
                    return {
                        filter_type: BICst.TARGET_FILTER_STRING.BELONG_VALUE,
                        filter_value: {type: BI.Selection.Multi, value: groupMap[v]},
                        _src: {field_id: BI.Utils.getFieldIDByDimensionID(dId)}
                    }
                }
                return {
                    filter_type: BICst.TARGET_FILTER_STRING.BELONG_VALUE,
                    filter_value: {type: BI.Selection.Multi, value: value},
                    _src: {field_id: BI.Utils.getFieldIDByDimensionID(dId)}
                }
            }

            function parseNumberFilter4Group(dId, v) {
                var value = v[0];
                var group = BI.Utils.getDimensionGroupByID(dId);
                var groupValue = group.group_value, groupType = group.type;
                var groupMap = {};
                if (BI.isNull(groupValue) && BI.isNull(groupType)) {
                    //没有分组为自动分组 但是这个时候维度中无相关分组信息，暂时截取来做
                    var sIndex = value.indexOf("-");
                    var min = value.slice(0, sIndex), max = value.slice(sIndex + 1);
                    return {
                        filter_type: BICst.TARGET_FILTER_NUMBER.BELONG_VALUE,
                        filter_value: {
                            min: min,
                            max: max,
                            closemin: true,
                            closemax: false
                        },
                        _src: {field_id: BI.Utils.getFieldIDByDimensionID(dId)}
                    }
                }
                if (groupType === BICst.GROUP.AUTO_GROUP) {
                    //坑爹，要自己算分组名称出来
                    var groupInterval = groupValue.group_interval, max = groupValue.max, min = groupValue.min;
                    while (min < max) {
                        var newMin = BI.parseInt(min) + BI.parseInt(groupInterval);
                        groupMap[min + "-" + newMin] = {
                            min: min,
                            max: newMin,
                            closemin: true,
                            closemax: newMin >= max
                        };
                        min = newMin;
                    }
                    return {
                        filter_type: BICst.TARGET_FILTER_NUMBER.BELONG_VALUE,
                        filter_value: groupMap[value],
                        _src: {field_id: BI.Utils.getFieldIDByDimensionID(dId)}
                    };
                }
                if (groupType === BICst.GROUP.ID_GROUP) {
                    return {
                        filter_type: BICst.TARGET_FILTER_NUMBER.BELONG_VALUE,
                        filter_value: {
                            min: BI.parseFloat(value),
                            max: BI.parseFloat(value),
                            closemin: true,
                            closemax: true
                        },
                        _src: {field_id: BI.Utils.getFieldIDByDimensionID(dId)}
                    }
                }
                var groupNodes = groupValue.group_nodes, useOther = groupValue.use_other;
                var oMin, oMax;
                BI.each(groupNodes, function (i, node) {
                    i === 0 && (oMin = node.min);
                    i === groupNodes.length - 1 && (oMax = node.max);
                    groupMap[node.id] = {
                        min: node.min,
                        max: node.max,
                        closemin: node.closemin,
                        closemax: node.closemax
                    }
                });
                if (BI.isNotNull(groupMap[value])) {
                    return {
                        filter_type: BICst.TARGET_FILTER_NUMBER.BELONG_VALUE,
                        filter_value: groupMap[value],
                        _src: {field_id: BI.Utils.getFieldIDByDimensionID(dId)}
                    };
                } else if (value === BICst.UNGROUP_TO_OTHER) {
                    return {
                        filter_type: BICst.TARGET_FILTER_NUMBER.NOT_BELONG_VALUE,
                        filter_value: {
                            min: oMin,
                            max: oMax,
                            closemin: true,
                            closemax: true
                        },
                        _src: {field_id: BI.Utils.getFieldIDByDimensionID(dId)}
                    };
                }
            }

            function parseSimpleFilter(v) {
                var dId = v.dId;
                var dType = self.getDimensionTypeByID(dId);
                switch (dType) {
                    case BICst.TARGET_TYPE.STRING:
                        return parseStringFilter4Group(dId, v.value);
                    case BICst.TARGET_TYPE.NUMBER:
                        return parseNumberFilter4Group(dId, v.value);
                    case BICst.TARGET_TYPE.DATE:
                        var dGroup = self.getDimensionGroupByID(dId);
                        var groupType = dGroup.type;
                        return {
                            filter_type: BICst.FILTER_DATE.EQUAL_TO,
                            filter_value: {values: v.value[0], group: groupType},
                            _src: {field_id: BI.Utils.getFieldIDByDimensionID(v.dId)}
                        };
                }
            }

            //钻取条件  对于交叉表，要考虑的不仅仅是used，还有行表头与列表头之间的钻取问题
            var drill = this.getDrillByID(wid);
            if (BI.isNotNull(drill) && widget.type !== BICst.WIDGET.MAP) {
                BI.each(drill, function (drId, drArray) {
                    if (drArray.length === 0) {
                        return;
                    }
                    BI.isNotNull(widget.dimensions[drId]) && (widget.dimensions[drId].used = false);
                    BI.each(drArray, function (i, drill) {
                        if (BI.isNotNull(widget.dimensions[drill.dId])) {
                            widget.dimensions[drill.dId].used = (i === drArray.length - 1);
                            var drillRegionType = self.getRegionTypeByDimensionID(drId);
                            //从原来的region中pop出来
                            var tempRegionType = self.getRegionTypeByDimensionID(drill.dId);
                            var dIndex = widget.view[drillRegionType].indexOf(drId);
                            BI.remove(widget.view[tempRegionType], drill.dId);
                            // if (drillRegionType === tempRegionType) {
                            widget.view[drillRegionType].splice(dIndex, 0, drill.dId);
                            // } else {
                            //     widget.view[drillRegionType].push(drill.dId);
                            // }
                        }
                        BI.each(drArray[i].values, function (i, v) {
                            var filterValue = parseSimpleFilter(v);
                            if (BI.isNotNull(filterValue)) {
                                filterValues.push(filterValue);
                            }
                        });
                    });
                });
            }

            //所有控件过滤条件（考虑有查询按钮的情况）
            filterValues = filterValues.concat(
                this.isQueryControlExist() && !this.isControlWidgetByWidgetId(wid) ?
                    Data.SharingPool.get("control_filters") : this.getControlCalculations(wid));

            //联动 由于这个clicked现在放到了自己的属性里，直接拿就好了
            var linkages = this.getLinkageValuesByID(wid);
            BI.each(linkages, function (cId, linkValue) {
                BI.each(linkValue, function (i, v) {
                    var filterValue = parseSimpleFilter(v);
                    if (BI.isNotNull(filterValue)) {
                        filterValues.push(filterValue);
                    }
                });
                var transferFilter = BI.Utils.getWSTransferFilterByID(BI.Utils.getWidgetIDByDimensionID(cId));
                if (transferFilter === true) {
                    var tarFilter = BI.Utils.getDimensionFilterValueByID(cId);
                    if (BI.isNotNull(tarFilter)) {
                        parseFilter(tarFilter);
                        if (BI.isNotNull(tarFilter) && BI.isNotEmptyObject(tarFilter)) {
                            filterValues.push(tarFilter);
                        }
                    }
                }
            });

            //联动传递过滤条件  找到联动链上的所有的组件，获取当前点击的指标的过滤条件  感觉有点浮夸的功能
            var allLinksWIds = [];

            function getLinkedIds(wid, links) {
                var allWIds = BI.Utils.getAllWidgetIDs();
                BI.each(allWIds, function (i, aWid) {
                    var linkages = BI.Utils.getWidgetLinkageByID(aWid);
                    BI.each(linkages, function (i, link) {
                        if (link.to === wid) {
                            links.push(BI.Utils.getWidgetIDByDimensionID(link.from));
                            getLinkedIds(BI.Utils.getWidgetIDByDimensionID(link.from), links);
                        }
                    });
                });
            }

            getLinkedIds(wid, allLinksWIds);
            BI.each(allLinksWIds, function (i, lId) {
                // 并不是拿到所有的指标的过滤条件
                // if (self.getWSTransferFilterByID(lId) === true) {
                //     var tarIds = BI.Utils.getAllTargetDimensionIDs(lId);
                //     BI.each(tarIds, function (i, tarId) {
                //         var tarFilter = BI.Utils.getDimensionFilterValueByID(tarId);
                //         if (BI.isNotEmptyObject(tarFilter)) {
                //             parseFilter(tarFilter);
                //             filterValues.push(tarFilter);
                //         }
                //     })
                // }

                var lLinkages = BI.Utils.getLinkageValuesByID(lId);
                BI.each(lLinkages, function (cId, linkValue) {
                    var lTransferFilter = BI.Utils.getWSTransferFilterByID(BI.Utils.getWidgetIDByDimensionID(cId));
                    if (lTransferFilter === true) {
                        var lTarFilter = BI.Utils.getDimensionFilterValueByID(cId);
                        if (BI.isNotNull(lTarFilter)) {
                            parseFilter(lTarFilter);
                            filterValues.push(lTarFilter);
                        }
                    }
                });

                //还应该拿到所有的联动过来的组件的钻取条件 也是给跪了
                //联动过来的组件的联动条件被删除，忽略钻取条件
                var linkDrill = self.getDrillByID(lId);
                var notIgnore = BI.some(linkages, function (ldid, link) {
                    return lId === self.getWidgetIDByDimensionID(ldid);
                });
                if (notIgnore && BI.isNotNull(linkDrill) && BI.isNotEmptyObject(linkDrill)) {
                    BI.each(linkDrill, function (drId, drArray) {
                        if (drArray.length === 0) {
                            return;
                        }
                        BI.each(drArray, function (i, drill) {
                            BI.each(drArray[i].values, function (i, v) {
                                var filterValue = parseSimpleFilter(v);
                                if (BI.isNotNull(filterValue)) {
                                    filterValues.push(filterValue);
                                }
                            });
                        });
                    });
                }
            });

            //联动过来的维度的过滤条件
            BI.each(linkages, function (lTId, link) {
                filterValues = filterValues.concat(self.getDimensionsFilterByTargetId(lTId));
            });

            //日期类型的过滤条件
            var dimensions = widget.dimensions;
            BI.each(dimensions, function (dId, dimension) {
                var filterValue = dimension.filter_value || {};
                parseFilter(filterValue);
            });

            //考虑表头上指标过滤条件的日期类型
            var target_filter = widget.filter_value;
            BI.each(target_filter, function (tId, filter) {
                parseFilter(filter)
            });

            //标红维度的处理
            var dIds = this.getAllDimDimensionIDs(wid);
            BI.each(dIds, function (idx, dId) {
                var dimensionMap = self.getDimensionMapByDimensionID(dId);
                var valid = true;
                //树控件和明细表
                if (widget.type === BICst.WIDGET.DETAIL || widget.type === BICst.WIDGET.TREE || widget.type === BICst.WIDGET.TREE_LIST) {
                    BI.each(dimensionMap, function (tableId, obj) {
                        var targetRelation = obj.target_relation;
                        var pId = self.getFirstRelationPrimaryIdFromRelations(targetRelation);
                        var fId = self.getLastRelationForeignIdFromRelations(targetRelation);
                        var paths = self.getPathsFromFieldAToFieldB(pId, fId);
                        if (!BI.deepContains(paths, targetRelation)) {
                            //维度和某个指标之间设置了路径但是路径在配置处被删了
                            if (paths.length >= 1) {
                                widget.dimensions[dId].dimension_map[tableId].target_relation = paths[0];
                            }
                        }
                    })
                } else {
                    var tIds = self.getAllTargetDimensionIDs(wid);
                    BI.any(tIds, function (idx, tId) {
                        if (!self.isCalculateTargetByDimensionID(tId)) {
                            //维度和某个指标之间没有设置路径
                            if (!BI.has(dimensionMap, tId)) {
                                var fieldId = BI.Utils.getFieldIDByDimensionID(dId);
                                var paths = BI.Utils.getPathsFromFieldAToFieldB(fieldId, BI.Utils.getFieldIDByDimensionID(tId))
                                if (paths.length === 1) {
                                    widget.dimensions[dId].dimension_map[tId] = {
                                        _src: {field_id: fieldId},
                                        target_relation: paths
                                    };
                                } else {
                                    valid = false;
                                    return true;
                                }
                            } else {
                                var targetRelation = dimensionMap[tId].target_relation;
                                BI.any(targetRelation, function (id, path) {
                                    var pId = self.getFirstRelationPrimaryIdFromRelations(path);
                                    var fId = self.getLastRelationForeignIdFromRelations(path);
                                    var paths = self.getPathsFromFieldAToFieldB(pId, fId);
                                    if (!BI.deepContains(paths, path)) {
                                        //维度和某个指标之间设置了路径但是路径在配置处被删了
                                        if (paths.length === 1) {
                                            widget.dimensions[dId].dimension_map[tId].target_relation.length = id;
                                            widget.dimensions[dId].dimension_map[tId].target_relation.push(paths[0]);
                                        } else {
                                            valid = false;
                                            return true;
                                        }
                                    }
                                })
                            }
                        }
                    });
                }
                if (valid === false) {
                    widget.dimensions[dId].used = false;
                }
            });

            widget.filter = {filter_type: BICst.FILTER_TYPE.AND, filter_value: filterValues};
            widget.real_data = true;

            return widget;
        },

        getWidgetDataByID: (function () {
            var cache = {};
            return function (wid, callbacks, options) {
                options || (options = {});
                var key = BI.UUID();
                if (!BI.Utils.isControlWidgetByWidgetId(wid)) {
                    key = wid;
                }
                cache[key] = callbacks;
                Data.Req.reqWidgetSettingByData({widget: BI.extend(this.getWidgetCalculationByID(wid), options)}, function (data) {
                    if (cache[key] === callbacks) {
                        callbacks.success(data);
                        delete cache[key];
                    } else {
                        callbacks.error && callbacks.error(data);
                    }
                    callbacks.done && callbacks.done(data);
                });
            }
        })(),

        /**
         * 组件与表的关系
         */
        broadcastAllWidgets2Refresh: function (force, wId) {
            var self = this;
            var allWidgetIds = this.getAllWidgetIDs();
            if (force === true || this.isQueryControlExist() === false) {
                BI.each(allWidgetIds, function (i, widgetId) {
                    if (!self.isControlWidgetByWidgetId(widgetId) || self.isInstantControlWidgetByWidgetId(widgetId)) {
                        if (BI.isNull(wId) || wId !== widgetId) {
                            BI.Broadcasts.send(BICst.BROADCAST.REFRESH_PREFIX + widgetId);
                        }
                    }
                });
            }
        },

        isTableUsableByWidgetID: function (tableId, wId) {
            var self = this;
            var dIds = this.getAllDimensionIDs(wId);
            var noneCalculateTargetIds = [];
            BI.each(dIds, function (i, dId) {
                var dimensionType = self.getDimensionTypeByID(dId);
                switch (dimensionType) {
                    case BICst.TARGET_TYPE.DATE:
                    case BICst.TARGET_TYPE.STRING:
                    case BICst.TARGET_TYPE.NUMBER:
                        noneCalculateTargetIds.push(dId);
                }
            });
            if (noneCalculateTargetIds.length < 1) {
                return true;
            }
            var tIds = [];
            BI.each(noneCalculateTargetIds, function (id, dId) {
                tIds.push(self.getTableIDByDimensionID(dId));
            });
            return this.isTableInRelativeTables(tIds, tableId);
        },

        getDimensionsFilterByTargetId: function (tId) {
            var self = this;
            var dimensionIds = this.getAllDimDimensionIDs(this.getWidgetIDByDimensionID(tId));
            var dFilters = [];
            BI.each(dimensionIds, function (i, dId) {
                var dimensionMap = self.getDimensionMapByDimensionID(dId);
                if (BI.isNotNull(dimensionMap[tId])) {
                    var dFilterValue = self.getDimensionFilterValueByID(dId);
                    if (BI.isNotEmptyObject(dFilterValue)) {
                        parseDimensionFilter4Linkage(dFilterValue, dimensionMap[tId]._src, dId);
                        dFilters.push(dFilterValue);
                    }
                }
            });
            return dFilters;

            function parseDimensionFilter4Linkage(dFilter, src, dId) {
                if (dFilter.filter_type === BICst.FILTER_TYPE.AND ||
                    dFilter.filter_type === BICst.FILTER_TYPE.OR) {
                    BI.each(dFilter.filter_value, function (i, fValue) {
                        parseDimensionFilter4Linkage(fValue, src, dId);
                    });
                } else {
                    if (dFilter.target_id === dId) {
                        dFilter._src = src;
                    } else {
                        dFilter.filter_type = BICst.FILTER_TYPE.EMPTY_CONDITION;
                    }
                }
            }
        }

    });

    //获取复杂日期的值
    function parseComplexDate(v) {
        if (v.type === BICst.MULTI_DATE_PARAM) {
            return parseComplexDateForParam(v.value);
        } else {
            return parseComplexDateCommon(v);
        }
        function parseComplexDateForParam(value) {
            var widgetInfo = value.widgetInfo, offset = value.offset;
            if (BI.isNull(widgetInfo) || BI.isNull(offset)) {
                return;
            }
            var paramdate;
            var wWid = widgetInfo.wId, se = widgetInfo.startOrEnd;
            if (BI.isNotNull(wWid) && BI.isNotNull(se)) {
                var wWValue = BI.Utils.getWidgetValueByID(wWid);
                if (BI.isNull(wWValue) || BI.isEmptyObject(wWValue)) {
                    return;
                }
                if (se === BI.MultiDateParamPane.start && BI.isNotNull(wWValue.start)) {
                    paramdate = parseComplexDateCommon(wWValue.start);
                }
                if (se === BI.MultiDateParamPane.end && BI.isNotNull(wWValue.end)) {
                    paramdate = parseComplexDateCommon(wWValue.end);
                }
            } else {
                if (BI.isNull(widgetInfo.wId) || BI.isNull(BI.Utils.getWidgetValueByID(widgetInfo.wId))) {
                    return;
                }
                paramdate = parseComplexDateCommon(BI.Utils.getWidgetValueByID(widgetInfo.wId));
            }
            if (BI.isNotNull(paramdate)) {
                return parseComplexDateCommon(offset, new Date(paramdate));
            }
        }

        function parseComplexDateCommon(v, consultedDate) {
            var type = v.type, value = v.value;
            var date = BI.isNull(consultedDate) ? new Date() : consultedDate;
            var currY = date.getFullYear(), currM = date.getMonth(), currD = date.getDate();
            date = new Date(date.getFullYear(), date.getMonth(), date.getDate());
            if (BI.isNull(type) && isValidDate(v)) {
                return new Date(v.year, v.month, v.day).getTime();
            }
            switch (type) {
                case BICst.MULTI_DATE_YEAR_PREV:
                    return new Date(currY - 1 * value, currM, currD).getTime();
                case BICst.MULTI_DATE_YEAR_AFTER:
                    return new Date(currY + 1 * value, currM, currD).getTime();
                case BICst.MULTI_DATE_YEAR_BEGIN:
                    return new Date(currY, 0, 1).getTime();
                case BICst.MULTI_DATE_YEAR_END:
                    return new Date(currY, 11, 31).getTime();

                case BICst.MULTI_DATE_MONTH_PREV:
                    return new Date().getBeforeMultiMonth(value).getTime();
                case BICst.MULTI_DATE_MONTH_AFTER:
                    return new Date().getAfterMultiMonth(value).getTime();
                case BICst.MULTI_DATE_MONTH_BEGIN:
                    return new Date(currY, currM, 1).getTime();
                case BICst.MULTI_DATE_MONTH_END:
                    return new Date(currY, currM, (date.getLastDateOfMonth()).getDate()).getTime();

                case BICst.MULTI_DATE_QUARTER_PREV:
                    return new Date().getBeforeMulQuarter(value).getTime();
                case BICst.MULTI_DATE_QUARTER_AFTER:
                    return new Date().getAfterMulQuarter(value).getTime();
                case BICst.MULTI_DATE_QUARTER_BEGIN:
                    return new Date().getQuarterStartDate().getTime();
                case BICst.MULTI_DATE_QUARTER_END:
                    return new Date().getQuarterEndDate().getTime();

                case BICst.MULTI_DATE_WEEK_PREV:
                    return date.getOffsetDate(-7 * value).getTime();
                case BICst.MULTI_DATE_WEEK_AFTER:
                    return date.getOffsetDate(7 * value).getTime();

                case BICst.MULTI_DATE_DAY_PREV:
                    return date.getOffsetDate(-1 * value).getTime();
                case BICst.MULTI_DATE_DAY_AFTER:
                    return date.getOffsetDate(1 * value).getTime();
                case BICst.MULTI_DATE_DAY_TODAY:
                    return date.getTime();
                case BICst.MULTI_DATE_CALENDAR:
                    return new Date(value.year, value.month, value.day).getTime();

            }
        }

        function isValidDate(v) {
            return BI.isNotNull(v.year) && BI.isNotNull(v.month) && BI.isNotNull(v.day);
        }
    }

    //format date type filter
    function parseFilter(filter) {
        var filterType = filter.filter_type, filterValue = filter.filter_value;
        if (filterType === BICst.FILTER_TYPE.AND || filterType === BICst.FILTER_TYPE.OR) {
            BI.each(filterValue, function (i, value) {
                parseFilter(value);
            });
        }
        if (BI.isNull(filterValue)) {
            return;
        }
        if (filterType === BICst.FILTER_DATE.BELONG_DATE_RANGE || filterType === BICst.FILTER_DATE.NOT_BELONG_DATE_RANGE) {
            var start = filterValue.start, end = filterValue.end;
            if (BI.isNotNull(start)) {
                filterValue.start = parseComplexDate(start);
            }
            if (BI.isNotNull(end)) {
                var endTime = parseComplexDate(end);
                if (BI.isNotNull(endTime)) {
                    filterValue.end = new Date(endTime).getOffsetDate(1).getTime() - 1
                } else {
                    delete filterValue.end;
                }
            }
        }
        if (filterType === BICst.FILTER_DATE.BELONG_WIDGET_VALUE || filterType === BICst.FILTER_DATE.NOT_BELONG_WIDGET_VALUE) {
            var filterWId = filterValue.wId, filterValueType = filterValue.filter_value.type;
            var wValue = BI.Utils.getWidgetValueByID(filterWId);
            if (!BI.Utils.isWidgetExistByID(filterWId) || BI.isNull(wValue)) {
                return;
            }
            switch (filterValueType) {
                case BICst.SAME_PERIOD:
                    if (BI.isNotNull(wValue.start)) {
                        filterValue.start = parseComplexDate(wValue.start);
                    }
                    if (BI.isNotNull(wValue.end)) {
                        var endTime = parseComplexDate(wValue.end);
                        if (BI.isNotNull(endTime)) {
                            filterValue.end = new Date(endTime).getOffsetDate(1).getTime() - 1;
                        } else {
                            delete filterValue.end;
                        }
                    }
                    break;
                case BICst.LAST_SAME_PERIOD:
                    if (BI.isNotNull(wValue.start) && BI.isNotNull(wValue.end)) {
                        var s = parseComplexDate(wValue.start);
                        var e = parseComplexDate(wValue.end);
                        if (BI.isNotNull(s) && BI.isNotNull(e)) {
                            filterValue.start = new Date(2 * s - e).getOffsetDate(-1).getTime();
                        } else {
                            delete filterValue.start
                        }
                        if (BI.isNotNull(s)) {
                            filterValue.end = new Date(s).getTime() - 1;
                        } else {
                            delete filterValue.end;
                        }
                    } else if (BI.isNotNull(wValue.start) && BI.isNotNull(wValue.start.year)) {
                        filterValue.start = parseComplexDate(wValue.start);
                    } else if (BI.isNotNull(wValue.end) && BI.isNotNull(wValue.end.year)) {
                        filterValue.end = parseComplexDate(wValue.end);
                    }
                    break;
                case BICst.YEAR_QUARTER:
                case BICst.YEAR_MONTH:
                case BICst.YEAR_WEEK:
                case BICst.YEAR_DAY:
                case BICst.MONTH_WEEK:
                case BICst.MONTH_DAY:
                case BICst.YEAR:
                    var date = getDateControlValue(filterWId);
                    if (BI.isNotNull(date)) {
                        var value = getOffSetDateByDateAndValue(date, filterValue.filter_value);
                        filterValue.start = value.start;
                        if (BI.isNotNull(value.end)) {
                            filterValue.end = new Date(value.end).getOffsetDate(1).getTime() - 1;
                        }
                    }
                    break;
            }
        }
        if (filterType === BICst.FILTER_DATE.EARLY_THAN) {
            var date = getDateControlValue(filterValue.wId);
            if (BI.isNotNull(date)) {
                var value = getOffSetDateByDateAndValue(date, filterValue.filter_value);
                if (BI.isNotNull(value.start)) {
                    filterValue.end = new Date(value.start).getTime() - 1;
                }
            }
        }
        if (filterType === BICst.FILTER_DATE.LATER_THAN) {
            var date = getDateControlValue(filterValue.wId);
            if (BI.isNotNull(date)) {
                var value = getOffSetDateByDateAndValue(date, filterValue.filter_value);
                if (BI.isNotNull(value.start)) {
                    filterValue.start = new Date(value.start).getTime();
                }
            }
        }
        if (filterType === BICst.FILTER_DATE.EQUAL_TO || filterType === BICst.FILTER_DATE.NOT_EQUAL_TO) {
            if (BI.isNull(filterValue)) {
                filterValue = {};
            } else {
                filterValue.values = parseComplexDate(filterValue);
                filterValue.group = BICst.GROUP.YMD;
            }
        }
        return filter;
        //日期偏移值
        function getOffSetDateByDateAndValue(date, value) {
            var tool = new BI.ParamPopupView();
            var type = value.type, value = value.value;
            var fPrevOrAfter = value.foffset === 0 ? -1 : 1;
            var sPrevOrAfter = value.soffset === 0 ? -1 : 1;
            var start, end;
            start = end = date;
            var ydate = new Date((date.getFullYear() + fPrevOrAfter * value.fvalue), date.getMonth(), date.getDate());
            switch (type) {
                case BICst.YEAR:
                    start = new Date((date.getFullYear() + fPrevOrAfter * value.fvalue), 0, 1);
                    end = new Date(start.getFullYear(), 11, 31);
                    break;
                case BICst.YEAR_QUARTER:
                    ydate = new Date().getOffsetQuarter(ydate, sPrevOrAfter * value.svalue);
                    start = new Date().getQuarterStartDate(ydate);
                    end = new Date().getQuarterEndDate(ydate);
                    break;
                case BICst.YEAR_MONTH:
                    ydate = new Date().getOffsetMonth(ydate, sPrevOrAfter * value.svalue);
                    start = new Date(ydate.getFullYear(), ydate.getMonth(), 1);
                    end = new Date(ydate.getFullYear(), ydate.getMonth(), (ydate.getLastDateOfMonth()).getDate());
                    break;
                case BICst.YEAR_WEEK:
                    start = ydate.getOffsetDate(sPrevOrAfter * 7 * value.svalue);
                    end = start.getOffsetDate(6);
                    break;
                case BICst.YEAR_DAY:
                    start = ydate.getOffsetDate(sPrevOrAfter * value.svalue);
                    end = start;
                    break;
                case BICst.MONTH_WEEK:
                    var mdate = new Date().getOffsetMonth(date, fPrevOrAfter * value.fvalue);
                    start = mdate.getOffsetDate(sPrevOrAfter * 7 * value.svalue);
                    end = start.getOffsetDate(6);
                    break;
                case BICst.MONTH_DAY:
                    var mdate = new Date().getOffsetMonth(date, fPrevOrAfter * value.fvalue);
                    start = mdate.getOffsetDate(sPrevOrAfter * value.svalue);
                    end = start;
                    break;
            }
            return {
                start: start.getTime(),
                end: end.getTime()
            }
        }

        //获取日期控件的值
        function getDateControlValue(wid) {
            if (!BI.Utils.isWidgetExistByID(wid)) {
                return null;
            }
            var widgetType = BI.Utils.getWidgetTypeByID(wid);
            var wValue = BI.Utils.getWidgetValueByID(wid);
            var date = null;
            switch (widgetType) {
                case BICst.WIDGET.YEAR:
                    if (BI.isNumeric(wValue)) {
                        date = new Date(wValue, 0, 1);
                    }
                    break;
                case BICst.WIDGET.MONTH:
                    if (BI.isNotNull(wValue) && BI.isNumeric(wValue.year)) {
                        date = new Date(wValue.year, BI.isNumeric(wValue.month) ? wValue.month : 0, 1);
                    }
                    break;
                case BICst.WIDGET.QUARTER:
                    if (BI.isNotNull(wValue) && BI.isNumeric(wValue.year)) {
                        var quarter = wValue.quarter;
                        date = new Date(wValue.year, BI.isNumeric(quarter) ? (quarter * 3 - 1) : 0, 1);
                    }
                    break;
                case BICst.WIDGET.YMD:
                    if (BI.isNotNull(wValue)) {
                        var v = parseComplexDate(wValue);
                        if (BI.isNotNull(v)) {
                            date = new Date(v);
                        }
                    }
                    break;
            }
            return date;
        }
    }
})();
