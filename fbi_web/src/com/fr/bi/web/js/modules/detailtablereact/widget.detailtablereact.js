/**
 * @class BI.DetailTableReact
 * @extends BI.Pane
 * 明细表的表格
 */
BI.DetailTableReact = BI.inherit(BI.Pane, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailTableReact.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-detail-table-react",
            overlap: false,
            wId: ""
        })
    },

    _init: function () {
        BI.DetailTableReact.superclass._init.apply(this, arguments);
        var self = this;
        var o = this.options;
        this.table = BI.createWidget({
            type: "bi.table_react",
            regionColumnSize: this.getStoredRegionColumnSize() || [],
            onColumnResizeEnd: function (data) {
                var columnSize = BI.clone(data.columnSize);
                var regionColumnSize = BI.clone(data.regionColumnSize);
                self.setStoredRegionColumnSize(regionColumnSize[0]);
                self.fireEvent(BI.DetailTableReact.EVENT_CHANGE, {settings: BI.extend(BI.Utils.getWidgetSettingsByID(o.wId), {column_size: columnSize})});
            },
            onRegionColumnResizeEnd: function (data) {
                var columnSize = BI.clone(data.columnSize);
                var regionColumnSize = BI.clone(data.regionColumnSize);
                self.setStoredRegionColumnSize(regionColumnSize[0]);
                self.fireEvent(BI.DetailTableReact.EVENT_CHANGE, {settings: BI.extend(BI.Utils.getWidgetSettingsByID(o.wId), {column_size: columnSize})});
            },

            onPrev: function (curr) {
                self._onPageChange(curr, function (opt) {
                    self.table.populate(opt);
                })
            },

            onNext: function (curr) {
                self._onPageChange(curr, function (opt) {
                    self.table.populate(opt);
                })
            }
        });

        this.errorPane = BI.createWidget({
            type: "bi.table_chart_error_pane",
            invisible: true
        });
        this.errorPane.element.css("z-index", 1);
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.table,
                top: 0,
                bottom: 0,
                left: 0,
                right: 0
            }, {
                el: this.errorPane,
                top: 0,
                left: 0,
                bottom: 0,
                right: 0
            }]
        })
    },

    _onPageChange: function (vPage, callback) {
        var self = this;
        var widgetId = this.options.wId;
        this.loading();
        this.errorPane.setVisible(false);
        this.data = [];
        var dimensions = BI.Utils.getAllDimensionIDs(widgetId);
        if (BI.isEmpty(dimensions)) {
            this.loaded();
            callback({
                pages: 1,
                curr: 1
            });
            return;

        }
        this.pageOperator = vPage || BICst.TABLE_PAGE_OPERATOR.REFRESH;

        var ob = {};
        ob.page = this.pageOperator;
        BI.Utils.getWidgetDataByID(widgetId, {
            success: function (jsonData) {
                if (BI.isNotNull(jsonData.error)) {
                    self.errorPane.setErrorInfo(jsonData.error);
                    self.errorPane.setVisible(true);
                    return;
                }
                try {
                    var json = jsonData.data, row = jsonData.row, size = jsonData.size;
                    if (BI.isNull(json) || BI.isNull(row)) {
                        callback({});
                        return;
                    }
                    var header = self._createHeader();
                    var items = self._createTableItems(json.value);

                    callback({
                        columnSize: self._getColumnSize(header).slice(0, BI.Utils.getAllDimensionIDs(widgetId).length),
                        regionColumnSize: self.getStoredRegionColumnSize(),
                        items: items,
                        header: [header],
                        pages: Math.ceil(row / size),
                        curr: vPage,
                        total: row,
                        styleType: BI.Utils.getWSTableStyleByID(widgetId),
                        color: BI.Utils.getWSThemeColorByID(widgetId),
                        showSequence: BI.Utils.getWSShowNumberByID(widgetId)
                    });
                } catch (e) {
                    self.errorPane.setErrorInfo("error happens during populate chart: " + e);
                    self.errorPane.setVisible(true);
                }
            },
            done: function () {
                self.loaded();
            }
        }, ob);
    },

    _getNumLevelByLevel: function (level) {
        var numLevel = "";
        BI.each(BICst.TARGET_STYLE_LEVEL, function (i, ob) {
            if (ob.value === level && level !== BICst.TARGET_STYLE.NUM_LEVEL.NORMAL) {
                numLevel = ob.text;
            }
        });
        return numLevel;
    },

    _createHeader: function () {
        var self = this, o = this.options;
        var header = [], view = BI.Utils.getWidgetViewByID(o.wId);
        BI.each(view[BICst.REGION.DIMENSION1], function (i, dId) {
            if (BI.Utils.isDimensionUsable(dId)) {
                var sort = BI.Utils.getDimensionSortByID(dId);
                var styleSettings = BI.Utils.getDimensionSettingsByID(dId);
                var name = BI.Utils.getDimensionNameByID(dId);
                var st = self._getNumLevelByLevel(styleSettings.num_level) + (styleSettings.unit || "");
                if (BI.isNotEmptyString(st)) {
                    name = name + "(" + st + ")";
                }
                var sortType = sort.sort_target === dId ? sort.type : BICst.SORT.NONE;
                header.push({
                    dId: dId,
                    text: name,
                    title: name,
                    list: !BI.Utils.isCalculateTargetByDimensionID(dId) ? [[{
                        text: BI.i18nText("BI-Ascend"),
                        active: sortType === BICst.SORT.ASC,
                        iconClass: "table-ascending-no-filter-font",
                        value: BICst.SORT.ASC,
                        onClick: function () {
                            self._onClickHeaderSort(dId, BICst.SORT.ASC);
                        }
                    }, {
                        text: BI.i18nText("BI-Descend"),
                        active: sortType === BICst.SORT.DESC,
                        iconClass: "table-descending-no-filter-font",
                        value: BICst.SORT.DESC,
                        onClick: function () {
                            self._onClickHeaderSort(dId, BICst.SORT.DESC);
                        }
                    }, {
                        text: BI.i18nText("BI-Unsorted"),
                        active: sortType === BICst.SORT.NONE,
                        iconClass: "table-no-sort-no-filter-font",
                        value: BICst.SORT.NONE,
                        onClick: function () {
                            self._onClickHeaderSort(dId, BICst.SORT.NONE);
                        }
                    }], [{
                        text: BI.i18nText("BI-Filter_Number_Summary"),
                        value: 0,
                        onClick: function () {
                            self.pageOperator = BICst.TABLE_PAGE_OPERATOR.REFRESH;
                            self._onClickHeaderCellFilter(dId);
                        }
                    }]] : []
                });
            }
        });
        return header;
    },

    _createTableItems: function (values) {
        var self = this, o = this.options;
        var tableItems = [];
        var dimensionIds = BI.Utils.getWidgetViewByID(o.wId)[BICst.REGION.DIMENSION1];
        var obs = [], used = {}, styleSettings = [], dimensionTypes = [], dimensionGroups = [];
        BI.each(dimensionIds, function (i, dId) {
            if (BI.Utils.isDimensionUsable(dId)) {
                used[dId] = true;
                var styleSetting = BI.Utils.getDimensionSettingsByID(dId);
                var dimensionType = BI.Utils.getDimensionTypeByID(dId);
                var dimensionGroup = BI.Utils.getDimensionGroupByID(dId);
                var ob = {
                    dId: dimensionIds[i]
                };
                if (self._isUseHyperLink(dId)) {
                    var hyperlink = BI.Utils.getDimensionHyperLinkByID(dId);
                    var expression = BI.Func.formatAddress(hyperlink.expression);
                    ob.style = {
                        textDecoration: 'underline'
                    };
                    ob.onClick = function (item) {
                        window.open(expression.replaceAll("\\$\\{.*\\}", item.text));
                    };
                }
                obs[i] = ob;
                styleSettings[i] = styleSetting;
                dimensionTypes[i] = dimensionType;
                dimensionGroups[i] = dimensionGroup;
            }
        });
        BI.each(values, function (i, row) {
            var rowItems = [];
            BI.each(row, function (i, rowValue) {
                if (used[dimensionIds[i]] === true) {
                    var val = self._createItemWithStyle(rowValue, styleSettings[i], dimensionTypes[i], dimensionGroups[i]);
                    rowItems.push(BI.extend({
                        text: val.text,
                        iconCls: val.iconCls,
                        style: {
                            color: val.fontColor
                        }
                    }, obs[i]));
                }
            });
            tableItems.push(rowItems);
        });
        return tableItems
    },

    _createItemWithStyle: function (text, styleSettings, dimensionType, dimensionGroup) {
        var o = this.options;
        var iconCls = "", color = "";
        var type = dimensionType;

        var format = styleSettings.format, numLevel = styleSettings.num_level,
            iconStyle = styleSettings.icon_style, mark = styleSettings.mark,
            num_separators = styleSettings.num_separators;

        text = BI.isNull(text) ? "" : text;
        text = BI.TargetBodyNormalCell.parseNumByLevel(text, numLevel);

        iconCls = this._getIconByStyleAndMark(text, iconStyle, mark);
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

        if (type === BICst.TARGET_TYPE.NUMBER || type === BICst.TARGET_TYPE.FORMULA) {
            text = BI.TargetBodyNormalCell.parseFloatByDot(text, format, num_separators);
        }

        if (text === Infinity) {
            text = "N/0";
        } else if (styleSettings.num_level === BICst.TARGET_STYLE.NUM_LEVEL.PERCENT && BI.isNumeric(text)) {
            text += "%";
        }

        //日期的需要format
        if (BI.isNotNull(dimensionGroup) && BI.isNumeric(text)) {
            switch (dimensionGroup.type) {
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
                    text = BICst.FULL_MONTH_NAMES[text];
                    break;
                case BICst.GROUP.W:
                    text = BICst.FULL_WEEK_NAMES[text - 1];
                    break;
            }
        }

        return {
            text: text,
            iconCls: iconCls,
            fontColor: color
        };
    },

    _getIconByStyleAndMark: function (text, style, mark) {
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
    },

    _isUseHyperLink: function (dId) {
        var hyperlink = BI.Utils.getDimensionHyperLinkByID(dId);
        return hyperlink.used || false
    },

    _getColumnSize: function (header) {
        var columnSize = BI.Utils.getWidgetSettingsByID(this.options.wId).column_size;
        if (BI.isNull(columnSize) || columnSize.length !== header.length) {
            columnSize = BI.makeArray(header.length, "");
        }
        return columnSize;
    },


    _onClickHeaderSort: function (dId, v) {
        var ob = {};
        var dimensions = BI.Utils.getWidgetDimensionsByID(this.options.wId);
        dimensions[dId].sort = {sort_target: dId, type: v};
        ob.dimensions = dimensions;
        var sortSequence = BI.Utils.getWidgetSortSequenceByID(this.options.wId);
        switch (v) {
            case BICst.SORT.ASC:
            case BICst.SORT.DESC:
                BI.remove(sortSequence, dId);
                sortSequence.push(dId);
                break;
            case BICst.SORT.NONE:
                BI.remove(sortSequence, dId);
        }
        ob.sort_sequence = sortSequence;
        this.fireEvent(BI.DetailTableReact.EVENT_CHANGE, ob);
    },

    _onClickHeaderCellFilter: function (dId) {
        var self = this;
        BI.Popovers.remove(dId);
        var popup = BI.createWidget({
            type: "bi.detail_table_filter_popup",
            dId: dId
        });
        popup.on(BI.DetailTableFilterPopup.EVENT_CHANGE, function (v) {
            var filterValue = BI.Utils.getWidgetFilterValueByID(self.options.wId);
            if (BI.isNotNull(v)) {
                filterValue[dId] = v;
            } else {
                delete filterValue[dId];
            }
            self.fireEvent(BI.DetailTableReact.EVENT_CHANGE, {filter_value: filterValue});
        });
        BI.Popovers.create(dId, popup).open(dId);
        popup.populate();
    },

    _getFreezeCols: function () {
        var wId = this.options.wId;
        return BI.Utils.getWSFreezeFirstColumnById(wId) ? [0] : [];
    },

    getStoredRegionColumnSize: function () {
        var columnSize = BI.Cache.getItem(BICst.CACHE.REGION_COLUMN_SIZE_PREFIX + this.options.wId);
        if (BI.isKey(columnSize)) {
            return [BI.parseInt(columnSize), ""];
        }
        return false;
    },

    setStoredRegionColumnSize: function (columnSize) {
        if (BI.isKey(columnSize)) {
            BI.Cache.setItem(BICst.CACHE.REGION_COLUMN_SIZE_PREFIX + this.options.wId, columnSize);
        }
    },

    populate: function () {
        var self = this;
        this._onPageChange(BICst.TABLE_PAGE_OPERATOR.REFRESH, function (opt) {
            opt || (opt = {});
            opt.curr = 1;
            opt.isNeedFreeze = true;
            opt.freezeCols = self._getFreezeCols();
            self.table.populate(opt);
        });
    },

    resize: function () {
        this.table.resize();
    }

});
BI.DetailTableReact.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.detail_table_react", BI.DetailTableReact);