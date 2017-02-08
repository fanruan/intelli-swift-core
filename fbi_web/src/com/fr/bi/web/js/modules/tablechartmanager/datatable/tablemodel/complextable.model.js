/**
 * Created by Young's on 2017/1/20.
 */
BI.ComplexTableModel = BI.inherit(BI.CrossTableModel, {

    constant: {
        OUTER_SUM: "__outer_sum__"
    },

    _init: function () {
        BI.ComplexTableModel.superclass._init.apply(this, arguments);
    },

    _refreshDimsInfo: function () {
        var self = this;
        this.dimIds = [];
        this.crossDimIds = [];
        var view = BI.Utils.getWidgetViewByID(this.wId);
        var regionIds = BI.keys(view);
        var sortedRegions = BI.sortBy(regionIds);
        //行表头、列表头都只需要第一个分组中使用中的维度
        BI.some(sortedRegions, function (i, sRegion) {
            if (BI.Utils.isDimensionRegion1ByRegionType(sRegion) &&
                view[sRegion].length > 0) {
                BI.each(view[sRegion], function (j, dId) {
                    BI.Utils.isDimensionUsable(dId) && (self.dimIds.push(dId));
                });
                return self.dimIds.length > 0;
            }
        });
        BI.some(sortedRegions, function (i, sRegion) {
            if (BI.Utils.isDimensionRegion2ByRegionType(sRegion) &&
                view[sRegion].length > 0) {
                BI.each(view[sRegion], function (j, dId) {
                    BI.Utils.isDimensionUsable(dId) && (self.crossDimIds.push(dId));
                });
                return self.crossDimIds.length > 0;
            }
        });
        //使用中的指标
        this.targetIds = [];
        BI.each(view[BICst.REGION.TARGET1], function (i, dId) {
            BI.Utils.isDimensionUsable(dId) && (self.targetIds.push(dId));
        });
    },

    getExtraInfo: function () {
        var op = {};
        var crossExpanderNodes = this._formatExpanderTree(this.crossETree.toJSONWithNode());
        var expanderNodes = this._formatExpanderTree(this.eTree.toJSONWithNode());
        var crossEValues = [], eValues = [];
        var rowRegions = this._getRowRegions(), colRegions = this._getColRegions();

        function searchIndexOfRegion(dId, regions) {
            var index = 0;
            var regionIds = BI.sortBy(BI.keys(regions));
            BI.some(regions, function (id, region) {
                if (region.contains(dId)) {
                    index = regionIds.indexOf(id);
                    return true;
                }
            });
            return index;
        }

        BI.each(rowRegions, function (i, rRegion) {
            eValues.push([]);
        });
        BI.each(colRegions, function (i, cRegion) {
            crossEValues.push([]);
        });
        BI.each(crossExpanderNodes, function (i, node) {
            crossEValues[searchIndexOfRegion(node.dId, colRegions)].push(node);
        });
        BI.each(expanderNodes, function (i, node) {
            eValues[searchIndexOfRegion(node.dId, rowRegions)].push(node);
        });

        op.expander = {
            x: {
                type: BI.Utils.getWSOpenColNodeByID(this.wId),
                value: crossEValues
            },
            y: {
                type: BI.Utils.getWSOpenRowNodeByID(this.wId),
                value: eValues
            }
        };
        op.clickvalue = this.clickValue;
        op.page = this.pageOperator;
        op.status = this.status;
        op.real_data = true;
        if (this.status === BICst.WIDGET_STATUS.DETAIL) {
            op.real_data = BI.Utils.isShowWidgetRealDataByID(this.wId) || false;
        }
        return op;
    },

    //获取有效的行表头区域
    _getRowRegions: function () {
        var rowRegions = {};
        var view = BI.Utils.getWidgetViewByID(this.wId);
        BI.each(view, function (regionId, dIds) {
            if (BI.Utils.isDimensionRegion1ByRegionType(regionId) &&
                dIds.length > 0) {
                BI.each(dIds, function (i, dId) {
                    if (BI.Utils.isDimensionUsable(dId)) {
                        if (BI.isNull(rowRegions[regionId])) {
                            rowRegions[regionId] = [];
                        }
                        rowRegions[regionId].push(dId);
                    }
                });
            }
        });
        return rowRegions;
    },

    //获取有效的列表头区域
    _getColRegions: function () {
        var colRegions = {};
        var view = BI.Utils.getWidgetViewByID(this.wId);
        BI.each(view, function (regionId, dIds) {
            if (BI.Utils.isDimensionRegion2ByRegionType(regionId) &&
                dIds.length > 0) {
                BI.each(dIds, function (i, dId) {
                    if (BI.Utils.isDimensionUsable(dId)) {
                        if (BI.isNull(colRegions[regionId])) {
                            colRegions[regionId] = [];
                        }
                        colRegions[regionId].push(dId);
                    }
                });
            }
        });
        return colRegions;
    },

    //行表头是否存在
    _isRowRegionExist: function () {
        return BI.size(this._getRowRegions()) > 0;
    },

    //列表头是否存在
    _isColRegionExist: function () {
        return BI.size(this._getColRegions()) > 0;
    },

    _getLargestLengthOfRowRegions: function () {
        var regions = this._getRowRegions();
        var length = 0;
        BI.each(regions, function (i, region) {
            region.length > length && (length = region.length);
        });
        return length;
    },

    _getLargestLengthOfColRegions: function () {
        var regions = this._getColRegions();
        var length = 0;
        BI.each(regions, function (i, region) {
            region.length > length && (length = region.length);
        });
        return length;
    },

    /**
     * 基本的复杂表结构
     * 有几个维度的分组表示就有几个表
     * view: {10000: [a, b], 10001: [c, d]}, 20000: [e, f], 20001: [g, h], 20002: [i, j], 30000: [k]}
     * 表示横向（类似与交叉表）会有三个表，纵向会有两个表
     */
    _createComplexTableItems: function () {
        var self = this;
        var tempItems = [], tempCrossItems = [];
        // 如果行表头和列表头都只有一个region构造一个二维的数组
        if (BI.isNotNull(this.data.l) && BI.isNotNull(this.data.t)) {
            this.data = [[this.data]];
        }
        BI.each(this.data, function (i, rowTables) {
            self.rowValues = {};
            BI.each(rowTables, function (j, tableData) {
                //parse一个表结构
                var singleTable = self._createSingleCrossTableItems(tableData, self._getDimsByDataPos(i, j));
                self._parseRowTableItems(singleTable.item);
                if (j === 0) {
                    tempItems.push(singleTable.item);
                }
                if (BI.parseInt(i) === 0) {
                    tempCrossItems.push(singleTable.crossItem);
                }
            });
        });
        this._parseColTableItems(tempItems);
        this._parseRowTableCrossItems(tempCrossItems);
    },

    /**
     * 交叉表 items and crossItems
     */
    _createSingleCrossTableItems: function (data, dims) {
        var self = this;
        var top = data.t, left = data.l;

        //根据所在的层，汇总情况——是否含有汇总
        var singleCrossItemsSums = [];
        singleCrossItemsSums[0] = [];
        if (BI.isNotNull(left.s)) {
            singleCrossItemsSums[0].push(true);
        }
        this._initCrossItemsSum(0, left.c, singleCrossItemsSums);

        //交叉表items
        var crossItem = {
            children: this._createCrossPartItems(top.c, 0, null, dims)
        };
        if (this.showColTotal === true) {
            BI.each(this.targetIds, function (i, tId) {
                crossItem.children.push({
                    type: "bi.normal_header_cell",
                    dId: tId,
                    text: BI.i18nText("BI-Summary_Values"),
                    tag: BI.UUID(),
                    styles: BI.SummaryTableHelper.getHeaderStyles(self.getThemeColor(), self.getTableStyle()),
                    sortFilterChange: function (v) {
                        self.resetETree();
                        self.pageOperator = BICst.TABLE_PAGE_OPERATOR.REFRESH;
                        self.headerOperatorCallback(v, tId);
                    },
                    isSum: true
                });
            });
        }
        var crossItems = [crossItem];

        //用cross parent value来对应到联动的时候的列表头值
        var crossPV = [];

        function parseCrossItem2Array(crossItems, pValues, pv) {
            BI.each(crossItems, function (i, crossItem) {
                if (BI.isNotNull(crossItem.children)) {
                    var tempPV = [];
                    if (BI.isNotNull(crossItem.dId)) {
                        if (BI.isNotEmptyArray(crossItem.values)) {
                            BI.each(crossItem.values, function (j, v) {
                                tempPV = pv.concat([{
                                    dId: crossItem.dId,
                                    value: [BI.Utils.getClickedValue4Group(crossItem.text, crossItem.dId)]
                                }]);
                            });
                            //显示列汇总的时候需要构造汇总
                        } else {
                            tempPV = pv.concat([{
                                dId: crossItem.dId,
                                value: [BI.Utils.getClickedValue4Group(crossItem.text, crossItem.dId)]
                            }]);
                        }
                    }
                    parseCrossItem2Array(crossItem.children, pValues, tempPV);
                    //汇总
                    if (BI.isNotEmptyArray(crossItem.values)) {
                        BI.each(crossItem.values, function (j, v) {
                            pValues.push([{
                                dId: crossItem.dId,
                                value: [BI.Utils.getClickedValue4Group(crossItem.text, crossItem.dId)]
                            }]);
                        });
                    }
                } else if (BI.isNotNull(crossItem.dId)) {
                    if (BI.isNotEmptyArray(crossItem.values)) {
                        BI.each(crossItem.values, function (j, v) {
                            pValues.push(pv.concat([{
                                dId: crossItem.dId,
                                value: [BI.Utils.getClickedValue4Group(crossItem.text, crossItem.dId)]
                            }]));
                        });
                    } else {
                        pValues.push([]);
                    }
                } else if (BI.isNotNull(crossItem.isSum)) {
                    pValues.push(pv);
                }
            });
        }

        parseCrossItem2Array(crossItems, crossPV, []);

        var item = {
            children: this._createCommonTableItems(left.c, 0, null, dims.dimIds, crossPV)
        };
        if (this.showRowTotal === true) {
            //汇总值
            var sums = [], ob = {index: 0};
            if (BI.isNotNull(left.s.c) && BI.isNotNull(left.s.s)) {
                this._createTableSumItems(left.s.c, sums, [], ob, true, null, crossPV);
            } else {
                BI.isArray(left.s) && this._createTableSumItems(left.s, sums, [], ob, true, null, crossPV);
            }
            if (this.showColTotal === true) {
                var outerValues = [];
                BI.each(left.s.s, function (i, v) {
                    if (self.targetIds.length > 0) {
                        var tId = self.targetIds[i];
                        outerValues.push({
                            type: "bi.target_body_normal_cell",
                            text: v,
                            tag: BI.UUID(),
                            dId: tId,
                            cls: "summary-cell last",
                            clicked: [{}],
                            styles: BI.SummaryTableHelper.getLastSummaryStyles(self.getThemeColor(), self.getTableStyle())
                        });
                    }
                });
                BI.each(sums, function (i, sum) {
                    sums[i].cls = "summary-cell last"
                });
                sums = sums.concat(outerValues);
            }
            item.values = sums;
        }
        return {
            crossItem: crossItem,
            item: item
        }
    },

    _createComplexTableHeader: function () {
        this._createCrossTableHeader();
        //补齐header的长度
        var count = 0,
            rowLength = this._getLargestLengthOfRowRegions(),
            colLength = this._getLargestLengthOfColRegions();
        var lastDimHeader = this.header[this.dimIds.length - 1],
            lastCrossDimHeader = this.crossHeader[this.crossDimIds.length - 1];
        while (count < rowLength - this.dimIds.length) {
            // this.header.splice(this.dimIds.length + count - 1, 0, {
            //     type: "bi.page_table_cell",
            //     tag: BI.UUID(),
            //     styles: BI.SummaryTableHelper.getHeaderStyles(this.getThemeColor(), this.getTableStyle())
            // });
            this.header.splice(this.dimIds.length + count, 0, lastDimHeader);
            count++;
        }
        count = 0;
        while (count < colLength - this.crossDimIds.length) {
            count++;
            this.crossHeader.splice(this.crossDimIds.length + count, 0, lastCrossDimHeader);
        }
    },

    /**
     * 交叉表——crossItems
     */
    _createCrossPartItems: function (c, currentLayer, parent, dims) {
        var self = this, crossHeaderItems = [];
        currentLayer++;
        BI.each(c, function (i, child) {
            if (BI.isNull(child.c) && (self.targetIds.contains(child.n) || dims.crossDimIds.contains(child.n))) {
                return;
            }
            var cId = BI.isEmptyString(child.n) ? self.EMPTY_VALUE : child.n;
            var currDid = dims.crossDimIds[currentLayer - 1], currValue = child.n;
            var nodeId = BI.isNotNull(parent) ? parent.get("id") + cId : cId;
            var node = new BI.Node(nodeId);
            node.set("name", child.n);
            node.set("dId", currDid);
            self.crossTree.addNode(parent, node);
            var pValues = [];
            var tempLayer = currentLayer, tempNodeId = nodeId;
            while (tempLayer > 0) {
                var dId = dims.crossDimIds[tempLayer - 1];
                pValues.push({
                    value: [BI.Utils.getClickedValue4Group(self.crossTree.search(tempNodeId).get("name"), dId)],
                    dId: dims.crossDimIds[tempLayer - 1]
                });
                tempNodeId = self.crossTree.search(tempNodeId).getParent().get("id");
                tempLayer--;
            }
            var item = {
                type: "bi.normal_expander_cell",
                text: currValue,
                tag: BI.UUID(),
                dId: currDid,
                isCross: true,
                styles: BI.SummaryTableHelper.getHeaderStyles(self.themeColor, self.tableStyle),
                expandCallback: function () {
                    var clickNode = self.crossETree.search(nodeId);
                    //全部展开再收起——纵向
                    if (self.openColNode === true) {
                        self._addNode2crossETree4OpenColNode(nodeId);
                    } else {
                        if (BI.isNull(clickNode)) {
                            self.crossETree.addNode(self.crossETree.search(BI.isNull(parent) ? self.crossTree.getRoot().get("id") : parent.get("id")), BI.deepClone(node));
                        } else {
                            clickNode.getParent().removeChild(nodeId);
                        }
                    }
                    self.pageOperator = BICst.TABLE_PAGE_OPERATOR.EXPAND;
                    self.clickValue = child.n;
                    self.expanderCallback();
                }
            };
            if (currentLayer < dims.crossDimIds.length) {
                item.needExpand = true;
                item.isExpanded = false;
            }
            if (BI.isNotNull(child.c)) {
                var children = self._createCrossPartItems(child.c, currentLayer, node, dims);
                if (BI.isNotEmptyArray(children)) {
                    item.children = self._createCrossPartItems(child.c, currentLayer, node, dims);
                    item.isExpanded = true;
                }
            }
            var hasSum = false;
            if (BI.isNotNull(self.crossItemsSums) &&
                BI.isNotNull(self.crossItemsSums[currentLayer]) &&
                self.crossItemsSums[currentLayer][i] === true) {
                hasSum = true;
            }
            if (hasSum === true && self.showColTotal === true && BI.isNotEmptyArray(item.children)) {
                BI.each(self.targetIds, function (k, tId) {
                    item.values = [];
                    BI.each(self.targetIds, function (k, tarId) {
                        item.values.push("");
                    });
                });
            }
            if (self.showColTotal === true || BI.isNull(item.children)) {
                item.values = BI.makeArray(self.targetIds.length, "");
            }
            crossHeaderItems.push(item);
        });
        return crossHeaderItems;
    },

    /**
     * 初始化 crossItemsSum
     */
    _initCrossItemsSum: function (currentLayer, sums, singleCrossItemsSums) {
        var self = this;
        currentLayer++;
        BI.each(sums, function (i, v) {
            if (BI.isNotNull(v) && BI.isNotNull(v.c)) {
                self._initCrossItemsSum(currentLayer, v.c, singleCrossItemsSums);
            }
            BI.isNull(singleCrossItemsSums[currentLayer]) && (singleCrossItemsSums[currentLayer] = []);
            singleCrossItemsSums[currentLayer].push(BI.isNotNull(v.s));
        });
    },

    _getDimsByDataPos: function (row, col) {
        var rowRegions = this._getRowRegions();
        var colRegions = this._getColRegions();
        var sortedRowRIds = BI.sortBy(BI.keys(rowRegions));
        var sortedColRIds = BI.sortBy(BI.keys(colRegions));
        return {
            dimIds: sortedRowRIds.length > 0 ? rowRegions[sortedRowRIds[row]] : [],
            crossDimIds: sortedColRIds.length > 0 ? colRegions[sortedColRIds[col]] : []
        };
    },

    //对于首层，可以以行号作为key，其他层以dId + text作为key
    //一般只处理有values（同时有children的表示合计、否则表示相应的值）
    _parseRowTableItems: function (data) {
        var self = this;
        //最外层的合计 可以通过data中是否包含dId来确定
        if (BI.isNotNull(data.children) && BI.isNotNull(data.values) && BI.isNull(data.dId)) {
            if (BI.isNull(this.rowValues[this.constant.OUTER_SUM])) {
                this.rowValues[this.constant.OUTER_SUM] = data.values;
            } else {
                BI.each(data.values, function (i, v) {
                    self.rowValues[self.constant.OUTER_SUM].push(v);
                });
            }
        }
        if (BI.isNotNull(data.values) && BI.isNotNull(data.dId)) {
            var itemId = data.dId + data.text;
            if (BI.isNull(this.rowValues[itemId])) {
                this.rowValues[itemId] = data.values;
            } else {
                BI.each(data.values, function (i, v) {
                    self.rowValues[itemId].push(v);
                });
            }
        }
        if (BI.isNotNull(data.children) && data.children.length > 0) {
            BI.each(data.children, function (i, child) {
                self._parseRowTableItems(child);
            });
        }
    },

    _parseRowTableCrossItems: function (tempCrossItems) {
        this.crossItems = [];
        var children = [];
        BI.each(tempCrossItems, function (i, tCrossItem) {
            children = children.concat(tCrossItem.children);
        });
        this.crossItems = [{
            children: children
        }];
    },

    // 处理列 针对于children
    // 要将所有的最外层的values处理成children
    _parseColTableItems: function (tempItems) {
        var self = this;
        this.items = [];
        var children = [];
        BI.each(tempItems, function (i, tItem) {
            children = children.concat(tItem.children);
            if (self.showRowTotal === true &&
                (self._isColRegionExist() || self._isRowRegionExist())) {
                children.push({
                    type: "bi.page_table_cell",
                    text: BI.i18nText("BI-Summary_Values"),
                    tag: BI.UUID(),
                    values: tItem.values,
                    cls: "summary-cell last",
                    styles: BI.SummaryTableHelper.getLastSummaryStyles(self.getThemeColor(), self.getTableStyle())
                });
            }
        });
        this.items = [{
            children: children
        }];
    },

    _setOtherComplexAttrs: function () {
        this._setOtherCrossAttrs();
        var largestRowDims = this._getLargestLengthOfRowRegions();
        var count = 0;
        while (count < largestRowDims - this.dimIds.length) {
            count++;
            this.mergeCols.push(this.mergeCols.length);
            this.freezeCols.push(this.mergeCols.length);
        }
    },

    createTableAttrs: function () {
        this.headerOperatorCallback = arguments[0];
        this.expanderCallback = arguments[1];

        this._resetPartAttrs();
        this._refreshDimsInfo();

        //正常复杂表
        if (this._isColRegionExist() && this._isRowRegionExist()) {
            this._createComplexTableItems();
            this._createComplexTableHeader();
            this._setOtherComplexAttrs();
            return;
        }
        //仅有列表头的时候（无指标）
        if (this._isColRegionExist() && !this._isRowRegionExist() &&
            this.targetIds.length === 0) {
            this._createCrossHeader4OnlyCross();
            this._createCrossItems4OnlyCross();
            this._setOtherAttrs4OnlyCross();
            return;
        }

        //无列表头
        this._createTableHeader();
        this._createTableItems();
        this._setOtherAttrs();

    }
});