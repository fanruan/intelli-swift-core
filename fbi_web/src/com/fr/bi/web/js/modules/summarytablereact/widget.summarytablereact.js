/**
 * 汇总表（分组表、交叉表）
 * @class BI.SummaryTableReact
 * @extends BI.Pane
 */
BI.SummaryTableReact = BI.inherit(BI.Pane, {
    _defaultConfig: function () {
        return BI.extend(BI.SummaryTableReact.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-summary-table-react",
            overlap: false
        })
    },

    _init: function () {
        BI.SummaryTableReact.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.model = new BI.SummaryTableModel({
            wId: o.wId,
            status: o.status
        });
        this._createTable();
        this.errorPane = BI.createWidget({
            type: "bi.table_chart_error_pane",
            invisible: true
        });
        this.errorPane.element.css("z-index", 1);
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.errorPane,
                top: 0,
                left: 0,
                bottom: 0,
                right: 0
            }]
        })
    },

    _createTable: function () {
        var self = this, o = this.options;
        this.vPage = 1;
        this.hPage = 1;

        var columnSizeChange = function (data) {
            var columnSize = BI.clone(data.columnSize);
            var regionColumnSize = BI.clone(data.regionColumnSize);
            self.model.setStoredRegionColumnSize(regionColumnSize[0]);
            self.fireEvent(BI.DetailTableReact.EVENT_CHANGE, {settings: BI.extend(BI.Utils.getWidgetSettingsByID(o.wId), {column_size: columnSize})});
        };
        var pageChange = function (vPage, hPage) {
            var pageOperator = BICst.TABLE_PAGE_OPERATOR.COLUMN_NEXT;
            if (vPage > self.vPage) {
                pageOperator = BICst.TABLE_PAGE_OPERATOR.ROW_NEXT;
            }
            if (vPage < self.vPage) {
                pageOperator = BICst.TABLE_PAGE_OPERATOR.ROW_PRE;
            }
            if (hPage > self.hPage) {
                pageOperator = BICst.TABLE_PAGE_OPERATOR.COLUMN_NEXT;
            }
            if (hPage < self.hPage) {
                pageOperator = BICst.TABLE_PAGE_OPERATOR.COLUMN_PRE;
            }
            self.hPage = hPage || 1;
            self.vPage = vPage || 1;
            self.model.setPageOperator(pageOperator);
            self._onPageChange(function (op) {
                self._refreshTable(op);
            })
        };

        this.table = BI.createWidget({
            type: "bi.summary_table_component",

            onColumnResizeEnd: columnSizeChange,
            onRegionColumnResizeEnd: columnSizeChange,

            vCurr: 1,
            hCurr: 1,
            hasVNext: function (vCurr) {
                return self.model.getPage()[1] === 1;
            },

            hasHNext: function (hCurr) {
                return self.model.getPage()[3] === 1;
            },

            onVPrev: function (vCurr) {
                pageChange(vCurr, self.hPage);
            },

            onVNext: function (vCurr) {
                pageChange(vCurr, self.hPage);
            },

            onHPrev: function (vCurr) {
                pageChange(self.vPage, vCurr);
            },

            onHNext: function (vCurr) {
                pageChange(self.vPage, vCurr);
            }
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.table,
                left: 0,
                right: 0,
                top: 0,
                bottom: 0
            }]
        })
    },

    /**
     * 无维度或指标发生变化时（如：展开节点）
     * @private
     */
    _populateNoDimsChange: function () {
        var self = this, wId = this.options.wId;
        this.loading();
        BI.Utils.getWidgetDataByID(wId, {
            success: function (jsonData) {
                if (BI.isNotNull(jsonData.error)) {
                    self.errorPane.setErrorInfo(jsonData.error);
                    self.errorPane.setVisible(true);
                    return;
                }
                if (BI.isNull(jsonData.data) || BI.isNull(jsonData.page)) {
                    self.errorPane.setErrorInfo("invalid json data!");
                    self.errorPane.setVisible(true);
                    return;
                }
                self.model.setDataAndPage(jsonData);
                var widgetType = BI.Utils.getWidgetTypeByID(wId);
                try {
                    switch (widgetType) {
                        case BICst.WIDGET.TABLE:
                            self._prepareData4GroupTable();
                            break;
                        case BICst.WIDGET.CROSS_TABLE:
                            //如果没有列表头，还是以分组表展示——后台传这样的数据
                            if (BI.isNotNull(self.model.getData().t)) {
                                self._prepareData4CrossTable();
                            } else {
                                self._prepareData4GroupTable();
                            }
                            break;
                        case BICst.WIDGET.COMPLEX_TABLE:
                            self._populateComplexTable();
                            break;
                    }
                    self._refreshTable();
                } catch (e) {
                    self.errorPane.setErrorInfo("error happens during populate table: " + e);
                    self.errorPane.setVisible(true);
                }
            },
            done: function () {
                self.loaded();
            }
        }, this.model.getExtraInfo());
    },

    _onPageChange: function (callback) {
        var self = this, wId = this.options.wId;
        this.loading();
        BI.Utils.getWidgetDataByID(wId, {
            success: function (jsonData) {
                if (BI.isNotNull(jsonData.error)) {
                    self.errorPane.setErrorInfo(jsonData.error);
                    self.errorPane.setVisible(true);
                    return;
                }
                if (BI.isNull(jsonData.data) || BI.isNull(jsonData.page)) {
                    self.errorPane.setErrorInfo("invalid json data!");
                    self.errorPane.setVisible(true);
                    return;
                }
                self.model.setDataAndPage(jsonData);
                var widgetType = BI.Utils.getWidgetTypeByID(wId);
                try {
                    switch (widgetType) {
                        case BICst.WIDGET.TABLE:
                            self.model.createGroupTableAttrs(BI.bind(self._onClickHeaderOperator, self), BI.bind(self._populateNoDimsChange, self), BI.bind(self._onClickBodyCellOperator, self));
                            break;
                        case BICst.WIDGET.CROSS_TABLE:
                            if (BI.isNotNull(self.model.getData().t)) {
                                self.model.createCrossTableAttrs(BI.bind(self._onClickHeaderOperator, self), BI.bind(self._populateNoDimsChange, self), BI.bind(self._onClickBodyCellOperator, self));
                            } else {
                                self.model.createGroupTableAttrs(BI.bind(self._onClickHeaderOperator, self), BI.bind(self._populateNoDimsChange, self), BI.bind(self._onClickBodyCellOperator, self));
                            }
                            break;
                    }
                    callback();
                } catch (e) {
                    self.errorPane.setErrorInfo("error happens during populate for table: " + e);
                    self.errorPane.setVisible(true);
                }
                self.fireEvent(BI.SummaryTableReact.EVENT_CHANGE, {
                    _page_: {
                        h: self.hPage,
                        v: self.vPage
                    }
                });
            },
            done: function () {
                self.loaded();
            }
        }, this.model.getExtraInfo());
    },

    /**
     * 分组表
     * @private
     */
    _prepareData4GroupTable: function () {
        //创建表格的各种属性——回调各种点击事件
        this.model.createGroupTableAttrs(BI.bind(this._onClickHeaderOperator, this), BI.bind(this._populateNoDimsChange, this), BI.bind(this._onClickBodyCellOperator, this));
    },

    /**
     * 交叉表
     * @private
     */
    _prepareData4CrossTable: function () {
        this.model.createCrossTableAttrs(BI.bind(this._onClickHeaderOperator, this), BI.bind(this._populateNoDimsChange, this), BI.bind(this._onClickBodyCellOperator, this));
    },

    /**
     * 表头上的一系列操作（排序、过滤）
     */
    _onClickHeaderOperator: function (v, dId) {
        switch (v) {
            case BICst.SORT.ASC:
            case BICst.SORT.DESC:
            case BICst.SORT.NONE:
                this._onClickHeaderSort(dId, v);
                break;
            default :
                this._onClickHeaderCellFilter(dId);
                break;
        }
    },

    /**
     * 表上的操作（上钻、下钻）
     * @private
     */
    _onClickBodyCellOperator: function (clicked) {
        this.fireEvent(BI.SummaryTableReact.EVENT_CHANGE, {clicked: clicked});
    },

    _onClickHeaderCellFilter: function (dId) {
        var self = this;

        function formatTargetFilter(filter, tId) {
            if (filter.filter_type === BICst.FILTER_TYPE.AND ||
                filter.filter_type === BICst.FILTER_TYPE.OR) {
                BI.each(filter.filter_value, function (i, filter) {
                    formatTargetFilter(filter, tId);
                });
                return;
            }
            filter.target_id = tId;
        }

        BI.Popovers.remove(dId);
        if (BI.Utils.isDimensionByDimensionID(dId)) {
            var popup = BI.createWidget({
                type: "bi.dimension_filter_popup",
                dId: dId
            });
            popup.on(BI.DimensionFilterPopup.EVENT_CHANGE, function (v) {
                var dimensions = BI.Utils.getWidgetDimensionsByID(self.options.wId);
                dimensions[dId].filter_value = v;
                self.fireEvent(BI.SummaryTableReact.EVENT_CHANGE, {dimensions: dimensions});
            });
        } else {
            var popup = BI.createWidget({
                type: "bi.target_summary_filter_popup",
                dId: dId
            });
            popup.on(BI.TargetSummaryFilterPopup.EVENT_CHANGE, function (v) {
                var targetFilter = BI.Utils.getWidgetFilterValueByID(self.options.wId);
                if (BI.isNotNull(v)) {
                    formatTargetFilter(v, dId);
                    targetFilter[dId] = v;
                } else {
                    delete targetFilter[dId];
                }

                self.fireEvent(BI.SummaryTableReact.EVENT_CHANGE, {filter_value: targetFilter});
            });
        }
        BI.Popovers.create(dId, popup).open(dId);
        popup.populate();
    },

    _onClickHeaderSort: function (dId, v) {
        var ob = {};
        if (BI.Utils.isDimensionByDimensionID(dId)) {
            var dimensions = BI.Utils.getWidgetDimensionsByID(this.options.wId);
            dimensions[dId].sort = {sort_target: dId, type: v};
            ob.dimensions = dimensions;
        } else {
            ob.sort = {sort_target: dId, type: v};
        }
        this.fireEvent(BI.SummaryTableReact.EVENT_CHANGE, ob);
    },

    _populateComplexTable: function () {

    },

    _formatItem: function (item, dGroupMap) {
        //日期的需要format
        var text = item.text;
        var dGroup = dGroupMap[item.dId];
        if (BI.isNotNull(dGroup) && BI.isNumeric(text)) {
            switch (dGroup.type) {
                case BICst.GROUP.YMD:
                    var date = new Date(BI.parseInt(text));
                    text = date.print("%Y-%X-%d");
                    break;
                case BICst.GROUP.YMDHMS:
                    var date = new Date(BI.parseInt(text));
                    text = date.print("%Y-%X-%d  %H:%M:%S");
                    break;
                case BICst.GROUP.S:
                    text = BICst.FULL_QUARTER_NAMES[text - 1];
                    break;
                case BICst.GROUP.M:
                    text = BICst.FULL_MONTH_NAMES[text - 1];
                    break;
                case BICst.GROUP.W:
                    text = BICst.FULL_WEEK_NAMES[text - 1];
                    break;
            }
        }
        item.text = text;
        return item;
    },

    _formatLinkageTarget: function (item) {
        var dId = item.dId, clicked = item.clicked;
        if (BI.isNotNull(dId)) {
            var widgetId = BI.Utils.getWidgetIDByDimensionID(dId);
            var linkage = BI.Utils.getWidgetLinkageByID(widgetId);
        }
        var linkedWidgets = [], linkedFrom = [];
        BI.each(linkage, function (i, link) {
            if (link.from === item.dId && BI.isEmpty(link.cids)) {
                linkedWidgets.push(link);
            } else if (link.cids && link.cids[0] === item.dId) {
                linkedFrom.push(link);
            }
        });
        if (!BI.isEmptyArray(linkedWidgets) || !BI.isEmptyArray(linkedFrom)) {
            item.style = {
                textDecoration: 'underline'
            };
            //一般指标或只有一个指标联动的计算指标
            if (!isContainsDiffLinkages(linkedFrom)) {
                item.onClick = function () {
                    //这个clicked应该放到子widget中保存起来
                    BI.each(linkedWidgets.concat(linkedFrom), function (i, linkWid) {
                        BI.Broadcasts.send(BICst.BROADCAST.LINKAGE_PREFIX + linkWid.to, linkWid.from, clicked);
                        send2AllChildLinkWidget(linkWid.to, item);
                    });
                };
                return item;
            }

            //计算指标
            var linkages = [];
            BI.each(linkedFrom, function (idx, linkage) {
                var name = BI.i18nText("BI-An");
                BI.each(linkage.cids, function (i, cid) {
                    name += BI.Utils.getDimensionNameByID(cid) + "-";
                });
                name += BI.Utils.getDimensionNameByID(linkage.from);
                var temp = {
                    text: name,
                    value: name,
                    title: name,
                    from: linkage.from,
                    to: linkage.to,
                    cids: linkage.cids,
                    onClick: clickCalculateTarget
                };
                var containsItem = containsLinkage(linkages, temp);
                if (BI.isEmptyObject(containsItem)) {
                    linkages.push(temp);
                } else {
                    BI.isArray(containsItem.to) ? containsItem.to.push(temp.to) : containsItem.to = [containsItem.to, temp.to];
                }
            });
            item.downList = linkages;
            return item;
        }

        function clickCalculateTarget(link) {
            BI.each(BI.isArray(link.to) ? link.to : [link.to], function (idx, to) {
                BI.Broadcasts.send(BICst.BROADCAST.LINKAGE_PREFIX + to, link.from, clicked);
                send2AllChildLinkWidget(to, {from: clicked});
            });
        }

        function send2AllChildLinkWidget(wid, opt) {
            var dId = opt.dId, clicked = opt.clicked;
            var linkage = BI.Utils.getWidgetLinkageByID(wid);
            BI.each(linkage, function (i, link) {
                BI.Broadcasts.send(BICst.BROADCAST.LINKAGE_PREFIX + link.to, dId, clicked);
                send2AllChildLinkWidget(link.to);
            });
        }

        function containsLinkage(list, item) {
            for (var i = 0; i < list.length; i++) {
                if (list[i].from === item.from && BI.isEqual(list[i].cids, item.cids)) {
                    return list[i];
                }
            }
            return {};
        }

        function isContainsDiffLinkages(linkages) {
            for (var i = 0; i < linkages.length; i++) {
                for (var j = i + 1; j < linkages.length; j++) {
                    if (!(BI.isEqual(linkages[i].from, linkages[j].from) && BI.isEqual(linkages[i].cids, linkages[j].cids))) {
                        return true;
                    }
                }
            }
            return false;
        }
    },

    _formatValue: function (item, styleSettingsMap) {
        if (BI.isNull(item.dId)) {
            return item;
        }
        var styleSettings = styleSettingsMap[item.dId];
        var text = item.text;
        var iconClass = "", color = "";
        var format = styleSettings.format, numLevel = styleSettings.num_level, num_separators = styleSettings.num_separators;
        text = BI.TargetBodyNormalCell.parseNumByLevel(text, numLevel);
        var iconStyle = styleSettings.icon_style, mark = styleSettings.mark;
        iconClass = getIconByStyleAndMark(text, iconStyle, mark);
        var conditions = styleSettings.conditions;
        BI.some(conditions, function (i, co) {
            var range = co.range;
            var min = BI.parseFloat(range.min), max = BI.parseFloat(range.max);
            var minBoolean = true;
            var maxBoolean = true;
            if (BI.isNumeric(min)) {
                minBoolean = (range.closemin === true ? text >= min : text > min);
            }
            if (BI.isNumeric(max)) {
                maxBoolean = (range.closemax === true ? text <= max : text < max);
            }
            if (minBoolean && maxBoolean) {
                color = co.color;
            }
        });
        text = BI.TargetBodyNormalCell.parseFloatByDot(text, format, num_separators);
        if (text === Infinity) {
            text = "N/0";
        } else if (numLevel === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT && BI.isNumeric(text)) {
            text += "%";
        }

        item.iconClass = iconClass;
        item.color = color;
        item.text = text;
        item.title = text;

        this._formatLinkageTarget(item);
        return {
            iconClass: iconClass,
            color: color,
            text: text
        };

        function getIconByStyleAndMark(text, style, mark) {
            var num = BI.parseFloat(text), nMark = BI.parseFloat(mark);
            switch (style) {
                case BICst.TARGET_STYLE.ICON_STYLE.NONE:
                    return "";
                case BICst.TARGET_STYLE.ICON_STYLE.POINT:
                    if (num > nMark) {
                        return "target-style-more-dot-font";
                    } else if (num === nMark) {
                        return "target-style-equal-dot-font"
                    } else {
                        return "target-style-less-dot-font";
                    }
                case BICst.TARGET_STYLE.ICON_STYLE.ARROW:
                    if (num > nMark) {
                        return "target-style-more-arrow-font";
                    } else if (num === nMark) {
                        return "target-style-equal-arrow-font";
                    } else {
                        return "target-style-less-arrow-font";
                    }
            }
            return "";
        }
    },

    _formatItems: function (items) {
        var self = this;

        var dGroupMap = {}, styleSettingsMap = {};
        BI.each(BI.Utils.getAllDimensionIDs(this.options.wId), function (i, dId) {
            dGroupMap[dId] = BI.Utils.getDimensionGroupByID(dId);
            styleSettingsMap[dId] = BI.Utils.getDimensionSettingsByID(dId);
        });

        //children在react中是关键字换成childs
        //标识汇总行
        function track(index, node) {
            if (node.children) {
                node.childs = node.children;
                BI.each(node.children, function (i, child) {
                    track(index + 1, child);
                });
                delete node.children;
                BI.each(node.values, function (i, val) {
                    val.isSum = true;
                    val.isLast = index === -1;
                    self._formatValue(val, styleSettingsMap);
                });
            } else {
                self._formatItem(node, dGroupMap);
                BI.each(node.values, function (i, val) {
                    self._formatValue(val, styleSettingsMap);
                });
            }
            node.title = node.text;
            if(BI.isNull(node.dId)){
                return;
            }
            if(BI.isNotEmptyArray(BI.Utils.getDrillDownDIdsByWidgetId(BI.Utils.getWidgetIDByDimensionID(node.dId))) ||
                BI.isNotNull(BI.Utils.getDrillUpDimensionIdByDimensionId(node.dId))){
                node.iconClass = "table-drill-up-down";
            }
        }

        BI.each(items, function (i, node) {
            track(-1, node);
        });
        return items;
    },

    _formatCrossItems: function (items) {
        var self = this;
        //children在react中是关键字换成childs
        //标识汇总行
        function track(index, node) {
            if (node.children) {
                node.childs = node.children;
                BI.each(node.children, function (i, child) {
                    track(index + 1, child);
                });
                delete node.children;
                BI.each(node.values, function (i, val) {
                    val.isSum = true;
                    val.isLast = index === -1;
                });
            } else {
            }
            node.title = node.text;
            if(BI.isNull(node.dId)){
                return;
            }
            if(BI.isNotEmptyArray(BI.Utils.getDrillDownDIdsByWidgetId(BI.Utils.getWidgetIDByDimensionID(node.dId))) ||
                BI.isNotNull(BI.Utils.getDrillUpDimensionIdByDimensionId(node.dId))){
                node.iconClass = "table-drill-up-down";
            }
        }

        BI.each(items, function (i, node) {
            track(-1, node);
        });
        return items;
    },

    _formatDimensionHeaderCell: function (cell) {
        var sort = BI.Utils.getDimensionSortByID(cell.dId), filter = BI.Utils.getDimensionFilterValueByID(cell.dId);
        if (BI.isEmptyObject(sort)) {
            //默认排序方式
            var sortType = BICst.SORT.ASC;
            var fieldType = BI.Utils.getFieldTypeByDimensionID(cell.dId);
            if (fieldType === BICst.COLUMN.NUMBER) {
                sortType = BICst.SORT.CUSTOM;
            }
            sort = {type: sortType};
        }
        var isNotEmptyFilter = BI.isNotEmptyObject(filter);
        cell.list = [[{
            text: isFirstDimensionBydId(cell.dId) ? BI.i18nText("BI-Ascend") : BI.i18nText("BI-Asc_Group"),
            active: sort.type === BICst.SORT.ASC,
            iconClass: isNotEmptyFilter ? "table-ascending-filter-font" : "table-ascending-no-filter-font",
            value: BICst.SORT.ASC,
            onClick: function (item) {
                cell.sortFilterChange(BICst.SORT.ASC);
            }
        }, {
            text: isFirstDimensionBydId(cell.dId) ? BI.i18nText("BI-Descend") : BI.i18nText("BI-Des_Group"),
            active: sort.type === BICst.SORT.DESC,
            iconClass: isNotEmptyFilter ? "table-descending-filter-font" : "table-descending-no-filter-font",
            value: BICst.SORT.DESC,
            onClick: function (item) {
                cell.sortFilterChange(BICst.SORT.DESC);
            }
        }], [{
            text: BI.i18nText("BI-Filter_Number_Summary"),
            active: isNotEmptyFilter,
            value: -1,
            onClick: function (item) {
                cell.sortFilterChange(-1);
            }
        }]];
        return cell;
        function isFirstDimensionBydId(dId) {
            var wId = BI.Utils.getWidgetIDByDimensionID(dId);
            var dims = BI.Utils.getAllUsableDimDimensionIDs(wId);
            return dims[0] === dId;
        }
    },

    _formatTargetHeaderCell: function (cell) {
        var targetFilters = BI.Utils.getWidgetFilterValueByID(BI.Utils.getWidgetIDByDimensionID(cell.dId));
        var widgetType = BI.Utils.getWidgetTypeByID(BI.Utils.getWidgetIDByDimensionID(cell.dId));
        var sort = BI.Utils.getWidgetSortByID(BI.Utils.getWidgetIDByDimensionID(cell.dId)), filter = targetFilters[cell.dId] || null;
        if (widgetType === BICst.WIDGET.DETAIL) {
            sort = BI.Utils.getDimensionSortByID(cell.dId);
        }
        var sortType = sort.sort_target === cell.dId ? sort.type : BICst.SORT.NONE;
        var isNotEmptyFilter = BI.isNotNull(filter);
        cell.list = [[{
            text: BI.i18nText("BI-Ascend"),
            active: sortType === BICst.SORT.ASC,
            iconClass: isNotEmptyFilter ? "table-ascending-filter-font" : "table-ascending-no-filter-font",
            value: BICst.SORT.ASC,
            onClick: function (item) {
                cell.sortFilterChange(BICst.SORT.ASC);
            }
        }, {
            text: BI.i18nText("BI-Descend"),
            active: sortType === BICst.SORT.DESC,
            iconClass: isNotEmptyFilter ? "table-descending-filter-font" : "table-descending-no-filter-font",
            value: BICst.SORT.DESC,
            onClick: function (item) {
                cell.sortFilterChange(BICst.SORT.DESC);
            }
        }, {
            text: BI.i18nText("BI-Unsorted"),
            active: sortType === BICst.SORT.NONE,
            iconClass: isNotEmptyFilter ? "table-no-sort-filter-font" : "table-no-sort-no-filter-font",
            value: BICst.SORT.NONE,
            onClick: function (item) {
                cell.sortFilterChange(BICst.SORT.NONE);
            }
        }], [{
            text: BI.i18nText("BI-Filter_Number_Summary"),
            active: isNotEmptyFilter,
            value: -1,
            onClick: function (item) {
                cell.sortFilterChange(-1);
            }
        }]];
        return cell;
    },

    _formatHeader: function (header) {
        var self = this;
        BI.each(header, function (i, node) {
            if (BI.isNull(node.dId)) {
                return;
            }
            var styleSettings = BI.Utils.getDimensionSettingsByID(node.dId);
            var st = getNumLevelByLevel(styleSettings.num_level) + (styleSettings.unit || "");
            if (BI.isNotEmptyString(st)) {
                node.text = node.text + "(" + st + ")";
            }
            node.title = node.text;
            if (BI.Utils.isDimensionByDimensionID(node.dId)) {
                self._formatDimensionHeaderCell(node);
            } else {
                self._formatTargetHeaderCell(node);
            }
        });
        return header;
        function getNumLevelByLevel(level) {
            var numLevel = "";
            BI.each(BICst.TARGET_STYLE_LEVEL, function (i, ob) {
                if (ob.value === level && level !== BICst.TARGET_STYLE.NUM_LEVEL.NORMAL) {
                    numLevel = ob.text;
                }
            });
            return numLevel;
        }
    },

    _formatCrossHeader: function (header) {
        var self = this;
        BI.each(header, function (i, node) {
            if (BI.isNull(node.dId)) {
                return;
            }
            node.title = node.text;
            self._formatDimensionHeaderCell(node);
        });
        return header;
    },

    _refreshTable: function (op) {
        this.errorPane.setVisible(false);
        this.table.populate(BI.extend({
            isNeedFreeze: true,
            freezeCols: this.model.getFreezeCols(),
            mergeCols: this.model.getMergeCols() || [],
            columnSize: this.model.getColumnSize(),
            regionColumnSize: this.model.getStoredRegionColumnSize(),
            header: this._formatHeader(this.model.getHeader()),
            items: this._formatItems(this.model.getItems()),
            crossHeader: this._formatCrossHeader(this.model.getCrossHeader()),
            crossItems: this._formatCrossItems(this.model.getCrossItems()),
            showSequence: this.model.isShowNumber(),
            styleType: this.model.getTableStyle(),
            color: this.model.getThemeColor(),
            tableStyle: this.model.getTableForm(),
            vCurr: this.vPage,
            hCurr: this.hPage
        }, op));
    },

    populate: function () {
        var self = this;
        var widgetId = this.options.wId;
        this.vPage = 1;
        this.hPage = 1;
        this.model.setPageOperator(BICst.TABLE_PAGE_OPERATOR.REFRESH);
        this.model.resetETree();
        this.loading();
        BI.Utils.getWidgetDataByID(widgetId, {
            success: function (jsonData) {
                if (BI.isNotNull(jsonData.error)) {
                    self.errorPane.setErrorInfo(jsonData.error);
                    self.errorPane.setVisible(true);
                    return;
                }
                if (BI.isNull(jsonData.data) || BI.isNull(jsonData.page)) {
                    self.errorPane.setErrorInfo("invalid json data!");
                    self.errorPane.setVisible(true);
                    return;
                }
                self.model.setDataAndPage(jsonData);
                var widgetType = BI.Utils.getWidgetTypeByID(widgetId);
                // try {
                switch (widgetType) {
                    case BICst.WIDGET.TABLE:
                        self._prepareData4GroupTable();
                        break;
                    case BICst.WIDGET.CROSS_TABLE:
                        //如果没有列表头，还是以分组表展示——后台传这样的数据
                        if (BI.isNotNull(self.model.getData().t)) {
                            self._prepareData4CrossTable();
                        } else {
                            self._prepareData4GroupTable();
                        }
                        break;
                    case BICst.WIDGET.COMPLEX_TABLE:
                        self._populateComplexTable();
                        break;
                }
                self._refreshTable({
                    vCurr: 1
                });
                // } catch (e) {
                //     self.errorPane.setErrorInfo("error happens during populate table: " + e);
                //     console.error(e);
                //     self.errorPane.setVisible(true);
                // }
            },
            done: function () {
                self.loaded();
            }
        }, this.model.getExtraInfo());
    },

    resize: function () {
        this.table.resize();
    },

    magnify: function () {

    }
});
BI.SummaryTableReact.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.summary_table_react", BI.SummaryTableReact);
