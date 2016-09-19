/**
 * Created by Young's on 2016/9/14.
 */
BI.ComplexTableModel = BI.inherit(FR.OB, {
    _init: function () {
        BI.ComplexTableModel.superclass._init.apply(this, arguments);
        var self = this;
        this.wId = this.options.wId;
        this.status = this.options.status;      //一个恶心的属性，来自于详细设置，查看真实数据
        this.EMPTY_VALUE = BI.UUID();
        this._refreshDimsInfo();

        //展开的节点的树结构，需要保存
        this.tree = new BI.Tree();
        this.crossTree = new BI.Tree();

        this.page = [0, 0, 0, 0, 0];
        this.eTree = new BI.Tree();         //展开节点——维度
        this.crossETree = new BI.Tree();    //展开节点——系列，用于交叉表

        this.clickValue = "";               //点击的值
        this.pageOperator = BICst.TABLE_PAGE_OPERATOR.REFRESH;  //翻页操作

        //当当前组件删除的时候删除存储的区域columnSize缓存
        BI.Broadcasts.on(BICst.BROADCAST.WIDGETS_PREFIX + this.wId, function () {
            self.deleteStoredRegionColumnSize();
        });
    },

    getStoredRegionColumnSize: function () {
        var columnSize = BI.Cache.getItem(BICst.CACHE.REGION_COLUMN_SIZE_PREFIX + this.wId);
        if (BI.isKey(columnSize)) {
            return [BI.parseInt(columnSize), ""];
        }
        return false;
    },

    setStoredRegionColumnSize: function (columnSize) {
        if (BI.isKey(columnSize)) {
            BI.Cache.setItem(BICst.CACHE.REGION_COLUMN_SIZE_PREFIX + this.wId, columnSize);
        }
    },

    deleteStoredRegionColumnSize: function () {
        BI.Cache.removeItem(BICst.CACHE.REGION_COLUMN_SIZE_PREFIX + this.wId);
    },

    getWidgetId: function () {
        return this.wId;
    },

    getStatus: function () {
        return this.status;
    },

    isNeed2Freeze: function () {
        if (this.targetIds.length === 0 || (this.dimIds.length + this.crossDimIds.length) === 0) {
            return false;
        }
        return this.freezeDim;
    },

    getFreezeCols: function () {
        return this.isNeed2Freeze() ? this.freezeCols : [];
    },

    getMergeCols: function () {
        return this.mergeCols;
    },

    getColumnSize: function () {
        var columnSize = [];
        BI.each(this.columnSize, function (i, size) {
            if (size < 80) {
                size = 80;
            }
            columnSize.push(size);
        });
        return columnSize;
    },

    getHeader: function () {
        return this.header;
    },

    getCrossHeader: function () {
        return this.crossHeader;
    },

    getItems: function () {
        return this.items;
    },

    getCrossItems: function () {
        return this.crossItems;
    },

    getPage: function () {
        return this.page;
    },

    getData: function () {
        return this.data;
    },

    getPageOperator: function () {
        return this.pageOperator;
    },

    isShowNumber: function () {
        return this.showNumber;
    },

    getThemeColor: function () {
        return this.themeColor;
    },

    getTableForm: function () {
        return this.tableForm;
    },

    getTableStyle: function () {
        return this.tableStyle;
    },

    setPageOperator: function (pageOperator) {
        this.pageOperator = pageOperator;
    },

    getExtraInfo: function () {
        var op = {};
        op.expander = {
            x: {
                type: BI.Utils.getWSOpenColNodeByID(this.wId),
                value: [this._formatExpanderTree(this.crossETree.toJSONWithNode())]
            },
            y: {
                type: BI.Utils.getWSOpenRowNodeByID(this.wId),
                value: [this._formatExpanderTree(this.eTree.toJSONWithNode())]
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

    setDataAndPage: function (data) {
        this.data = data.data;
        this.page = data.page;
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
            if (BI.parseInt(sRegion) < BI.parseInt(BICst.REGION.DIMENSION2)) {
                BI.each(view[sRegion], function (j, dId) {
                    BI.Utils.isDimensionUsable(dId) && (self.dimIds.push(dId));
                });
                return true;
            }
        });
        BI.some(sortedRegions, function (i, sRegion) {
            if (BI.parseInt(BICst.REGION.DIMENSION2) <= BI.parseInt(sRegion) &&
                BI.parseInt(sRegion) < BI.parseInt(BICst.REGION.TARGET1)) {
                BI.each(view[sRegion], function (j, dId) {
                    BI.Utils.isDimensionUsable(dId) && (self.crossDimIds.push(dId));
                });
                return true;
            }
        });
        //使用中的指标
        this.targetIds = [];
        BI.each(view[BICst.REGION.TARGET1], function (i, dId) {
            BI.Utils.isDimensionUsable(dId) && (self.targetIds.push(dId));
        });
    },

    /**
     * 重置部分数据，用于无后台请求
     */
    _resetPartAttrs: function () {
        var wId = this.options.wId;
        this.showNumber = BI.Utils.getWSShowNumberByID(wId);         //显示行号
        this.showRowTotal = BI.Utils.getWSShowRowTotalByID(wId);    //显示行汇总
        this.showColTotal = BI.Utils.getWSShowColTotalByID(wId);    //显示列汇总
        this.openRowNode = BI.Utils.getWSOpenRowNodeByID(wId);      //展开所有行表头节点
        this.openColNode = BI.Utils.getWSOpenColNodeByID(wId);      //展开所有列表头节点
        this.freezeDim = BI.Utils.getWSFreezeDimByID(wId);           //冻结维度
        this.themeColor = BI.Utils.getWSThemeColorByID(wId);         //主题色
        this.tableForm = BI.Utils.getWSTableFormByID(wId);           //表格类型
        this.tableStyle = BI.Utils.getWSTableStyleByID(wId);         //表格风格

        this.header = [];
        this.items = [];
        this.crossHeader = [];
        this.crossItems = [];
        this.mergeCols = [];
        this.columnSize = BI.Utils.getWSColumnSizeByID(wId);

        this.tree = new BI.Tree();
        this.crossTree = new BI.Tree();

    },

    resetETree: function () {
        this.eTree = new BI.Tree();
        this.crossETree = new BI.Tree();
    },

    /**
     * format展开节点树
     */
    _formatExpanderTree: function (eTree) {
        var self = this, result = [];
        BI.each(eTree, function (i, t) {
            var item = {};
            item.name = t.node.name;
            if (BI.isNotNull(t.children)) {
                item.children = self._formatExpanderTree(t.children);
            }
            result.push(item);
        });
        return result;
    },

    /**
     * 基本的复杂表结构
     * 有几个维度的分组表示就有几个表
     * view: {10000: [a, b], 10001: [c, d]}, 20000: [e, f], 20001: [g, h], 20002: [i, j], 30000: [k]}
     * 表示横向（类似与交叉表）会有三个表，纵向会有两个表
     */
    _createComplexTableItems: function() {
        var self = this;
        BI.each(this.data, function(i, rowTables) {
            var singleRow = {};
            BI.each(rowTables, function(j, tableData) {
                //parse一个表结构
                var singleTable = self._createSingleCrossTableItems(tableData);
                if(BI.isNull(singleRow.children))
                singleRow.push(self._createSingleCrossTableItems(tableData));
            });
            //拼每一个children的values

        });
    },

    //合并每个层级的values
    _concatItemValues: function(singleRow) {
        var oneRow = {};
        BI.each(singleRow, function(i, item) {
            if (BI.isNull(oneRow.values) && BI.isNotNull(item.values)) {
                oneRow.values = item.values;
            }
        });
        return oneRow;
    },

    /**
     * 交叉表 items and crossItems
     */
    _createSingleCrossTableItems: function (data) {
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
            children: this._createCrossPartItems(top.c, 0)
        };
        if (this.showColTotal === true) {
            BI.each(this.targetIds, function (i, tId) {
                crossItem.children.push({
                    type: "bi.normal_header_cell",
                    dId: tId,
                    text: BI.i18nText("BI-Summary_Values"),
                    tag: BI.UUID(),
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
                                    value: [self._parseClickedValue4Group(crossItem.text, crossItem.dId)]
                                }]);
                            });
                            //显示列汇总的时候需要构造汇总
                        } else {
                            tempPV = pv.concat([{
                                dId: crossItem.dId,
                                value: [self._parseClickedValue4Group(crossItem.text, crossItem.dId)]
                            }]);
                        }
                    }
                    parseCrossItem2Array(crossItem.children, pValues, tempPV);
                    //汇总
                    if (BI.isNotEmptyArray(crossItem.values)) {
                        BI.each(crossItem.values, function (j, v) {
                            pValues.push([{
                                dId: crossItem.dId,
                                value: [self._parseClickedValue4Group(crossItem.text, crossItem.dId)]
                            }]);
                        });
                    }
                } else if (BI.isNotNull(crossItem.dId)) {
                    if (BI.isNotEmptyArray(crossItem.values)) {
                        BI.each(crossItem.values, function (j, v) {
                            pValues.push(pv.concat([{
                                dId: crossItem.dId,
                                value: [self._parseClickedValue4Group(crossItem.text, crossItem.dId)]
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
            children: this._createTableItems(left.c, 0)
        };
        if (this.showRowTotal === true) {
            //汇总值
            var sums = [], ob = {index: 0};
            if (BI.isNotNull(left.s.c) && BI.isNotNull(left.s.s)) {
                this._createTableSumItems(left.s.c, sums, [], ob, true);
            } else {
                BI.isArray(left.s) && this._createTableSumItems(left.s, sums, [], ob, true);
            }
            if (this.showColTotal === true) {
                var outerValues = [];
                BI.each(left.s.s, function (i, v) {
                    if (self.targetIds.length > 0) {
                        var tId = self.targetIds[i];
                        outerValues.push({
                            type: "bi.target_body_normal_cell",
                            text: v,
                            dId: tId,
                            cls: "summary-cell last",
                            clicked: [{}]
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
            crossItems: [crossItem],
            items: [item],
            crossPV: crossPV
        }
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
            singleCrossItemsSums[currentLayer].push(BI.isNotNull(v.s) ? true : false);
        });
    },

    /**
     * 交叉表——crossItems
     */
    _createCrossPartItems: function (c, currentLayer, parent) {
        var self = this, crossHeaderItems = [];
        currentLayer++;
        BI.each(c, function (i, child) {
            if (BI.isNull(child.c) && (self.targetIds.contains(child.n) || self.crossDimIds.contains(child.n))) {
                return;
            }
            var cId = BI.isEmptyString(child.n) ? self.EMPTY_VALUE : child.n;
            var currDid = self.crossDimIds[currentLayer - 1], currValue = child.n;
            var nodeId = BI.isNotNull(parent) ? parent.get("id") + cId : cId;
            var node = new BI.Node(nodeId);
            node.set("name", child.n);
            self.crossTree.addNode(parent, node);
            var pValues = [];
            var tempLayer = currentLayer, tempNodeId = nodeId;
            while (tempLayer > 0) {
                var dId = self.crossDimIds[tempLayer - 1];
                pValues.push({
                    value: [self._parseClickedValue4Group(self.crossTree.search(tempNodeId).get("name"), dId)],
                    dId: self.crossDimIds[tempLayer - 1]
                });
                tempNodeId = self.crossTree.search(tempNodeId).getParent().get("id");
                tempLayer--;
            }
            var item = {
                type: "bi.normal_expander_cell",
                text: currValue,
                dId: currDid,
                isCross: true,
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
            if (currentLayer < self.crossDimIds.length) {
                item.needExpand = true;
                item.isExpanded = false;
            }
            if (BI.isNotNull(child.c)) {
                var children = self._createCrossPartItems(child.c, currentLayer, node);
                if (BI.isNotEmptyArray(children)) {
                    item.children = self._createCrossPartItems(child.c, currentLayer, node);
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
    }
});