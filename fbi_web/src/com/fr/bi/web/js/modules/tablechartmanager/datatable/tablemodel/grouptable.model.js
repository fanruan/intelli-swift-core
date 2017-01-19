/**
 * 分组表
 * Created by Young's on 2017/1/17.
 */
BI.GroupTableModel = BI.inherit(FR.OB, {
    _init: function () {
        BI.GroupTableModel.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.wId = o.wId;
        this.status = o.status;
        this.EMPTY_VALUE = BI.UUID();
        this._refreshDimsInfo();

        //展开的节点的树结构，需要保存
        this.tree = new BI.Tree();

        this.page = [0, 0, 0, 0, 0];
        this.eTree = new BI.Tree();         //展开节点——维度

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
        return [];
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

    isNeed2Freeze: function () {
        if (this.targetIds.length === 0 || this.dimIds.length === 0) {
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

    getHeaderRowSize: function () {
        return this.headerRowSize;
    },

    getFooterRowSize: function () {
        return this.footerRowSize;
    },

    getRowSize: function () {
        return this.rowSize;
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

    getPageOperatorByPages: function (nextVPage, nextHPage, currVPage, currHPage) {
        var pageOperator = BICst.TABLE_PAGE_OPERATOR.COLUMN_NEXT;
        if (nextVPage > currVPage) {
            pageOperator = BICst.TABLE_PAGE_OPERATOR.ROW_NEXT;
        }
        if (nextVPage < currVPage) {
            pageOperator = BICst.TABLE_PAGE_OPERATOR.ROW_PRE;
        }
        if (nextHPage > currHPage) {
            pageOperator = BICst.TABLE_PAGE_OPERATOR.COLUMN_NEXT;
        }
        if (nextHPage < currHPage) {
            pageOperator = BICst.TABLE_PAGE_OPERATOR.COLUMN_PRE;
        }
        return pageOperator;
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

    setDataAndPage: function (data) {
        this.data = data.data;
        this.page = data.page;
    },

    resetETree: function () {
        this.eTree = new BI.Tree();
    },

    getExtraInfo: function () {
        var op = {};
        op.expander = {
            x: {
                type: BI.Utils.getWSOpenColNodeByID(this.wId),
                value: []
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

    _refreshDimsInfo: function () {
        //使用中的行表头——考虑钻取
        var self = this;
        this.dimIds = [];
        var view = BI.Utils.getWidgetViewByID(this.wId);
        var drill = BI.Utils.getDrillByID(this.wId);

        BI.each(view[BICst.REGION.DIMENSION1], function (i, dId) {
            BI.Utils.isDimensionUsable(dId) && (self.dimIds.push(dId));
        });
        BI.each(drill, function (drId, drArray) {
            if (drArray.length !== 0) {
                var dIndex = self.dimIds.indexOf(drId);
                BI.remove(self.dimIds, drId);
                BI.each(drArray, function (i, dr) {
                    var tempDrId = dr.dId;
                    if (i === drArray.length - 1) {
                        self.dimIds.splice(dIndex, 0, tempDrId);
                    } else {
                        BI.remove(self.dimIds, tempDrId);
                    }
                });
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

        this.mergeCols = [];
        this.columnSize = BI.Utils.getWSColumnSizeByID(wId);
        this.headerRowSize = BI.Utils.getWSRowHeightByID(wId);
        this.footerRowSize = BI.Utils.getWSRowHeightByID(wId);
        this.rowSize = BI.Utils.getWSRowHeightByID(wId);

        this.header = [];
        this.items = [];

        this.tree = new BI.Tree();
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
     * 通用的创建items方法
     * @param c json结构中的c节点
     * @param currentLayer 当前所在层数
     * @param parent 父节点node
     * @returns {Array}
     * @private
     */
    _createCommonTableItems: function (c, currentLayer, parent) {
        var self = this, items = [];
        currentLayer++;
        BI.each(c, function (i, child) {
            //可以直接使用每一层中的树节点的parent.id + child.n作为id，第一层无需考虑，因为第一层不可能有相同值
            //考虑到空字符串问题
            var cId = BI.isEmptyString(child.n) ? self.EMPTY_VALUE : child.n;
            var nodeId = BI.isNotNull(parent) ? parent.get("id") + cId : cId;
            var node = new BI.Node(nodeId);
            var currDid = self.dimIds[currentLayer - 1], currValue = child.n;
            node.set("name", currValue);
            self.tree.addNode(parent, node);
            var pValues = [];
            var tempLayer = currentLayer, tempNodeId = nodeId;
            while (tempLayer > 0) {
                var pv = self.tree.search(tempNodeId).get("name"), dId = self.dimIds[tempLayer - 1];
                pValues.push({
                    value: [BI.Utils.getClickedValue4Group(pv, dId)],
                    dId: dId
                });
                tempNodeId = self.tree.search(tempNodeId).getParent().get("id");
                tempLayer--;
            }
            var item = {
                type: "bi.normal_expander_cell",
                text: child.n,
                dId: currDid,
                expandCallback: function () {
                    //全部展开再收起——横向
                    var clickNode = self.eTree.search(nodeId);
                    if (self.openRowNode === true) {
                        self._addNode2eTree4OpenRowNode(nodeId);
                    } else {
                        if (BI.isNull(clickNode)) {
                            self.eTree.addNode(self.eTree.search(BI.isNull(parent) ? self.tree.getRoot().get("id") : parent.get("id")), BI.deepClone(node));
                        } else {
                            clickNode.getParent().removeChild(nodeId);
                        }
                    }
                    self.pageOperator = BICst.TABLE_PAGE_OPERATOR.EXPAND;
                    self.clickValue = child.n;
                    self.expanderCallback();
                },
                drillCallback: function () {
                    var regionType = BI.Utils.getRegionTypeByDimensionID(currDid);
                    var obj = {};
                    if (pValues.length > 0) {
                        BI.removeAt(pValues, 0);
                    }
                    if (regionType < BICst.REGION.DIMENSION2) {
                        obj.xValue = child.n;
                        obj.pValues = pValues;
                    } else {
                        obj.zValue = child.n;
                        obj.pValues = pValues;
                    }
                    obj.dimensionIds = [currDid];
                    BI.Broadcasts.send(BICst.BROADCAST.CHART_CLICK_PREFIX + self.wId, obj);
                }
            };
            //展开情况——最后一层没有这个展开按钮
            if (currentLayer < self.dimIds.length) {
                item.needExpand = true;
                item.isExpanded = false;
            }
            //有c->说明有children，构造children，并且需要在children中加入汇总情况（如果有并且需要）
            if (BI.isNotNull(child.c)) {
                item.children = self._createCommonTableItems(child.c, currentLayer, node) || [];
                //在tableForm为 行展开模式 的时候 如果不显示汇总行 只是最后一行不显示汇总
                if (self.showRowTotal === true || self.getTableForm() === BICst.TABLE_FORM.OPEN_COL) {
                    var vs = [];
                    var summary = self._getOneRowSummary(child.s);
                    var tarSize = self.targetIds.length;
                    BI.each(summary, function (i, sum) {
                        vs.push({
                            type: "bi.target_body_normal_cell",
                            text: sum,
                            dId: self.targetIds[i % tarSize],
                            clicked: pValues,
                            cls: "summary-cell"
                        });
                    });
                    item.values = vs;
                }

                item.isExpanded = true;
            } else if (BI.isNotNull(child.s)) {
                var values = [];
                if (BI.isNotNull(child.s.c) || BI.isArray(child.s.s)) {
                    //交叉表，pValue来自于行列表头的结合
                    var ob = {index: 0};
                    self._createTableSumItems(child.s.c, values, pValues, ob);
                    //显示列汇总 有指标
                    if (self.showColTotal === true && self.targetIds.length > 0) {
                        self._createTableSumItems(child.s.s, values, pValues, ob);
                    }
                } else {
                    BI.each(child.s, function (j, sum) {
                        var tId = self.targetIds[j];
                        values.push({
                            type: "bi.target_body_normal_cell",
                            text: sum,
                            dId: tId,
                            clicked: pValues
                        })
                    });
                }
                item.values = values;
            }
            items.push(item);
        });
        return items;
    },

    _getOneRowSummary: function (sums) {
        var self = this;
        var summary = [];
        //对于交叉表的汇总 s: {c: [{s: [200, 300]}, {s: [0, 0]}], s: [100, 500]}
        if (BI.isArray(sums)) {
            BI.each(sums, function (i, sum) {
                if (BI.isObject(sum)) {
                    summary = summary.concat(self._getOneRowSummary(sum));
                    return;
                }
                summary.push(sum);
            });
        } else if (BI.isObject(sums)) {
            var c = sums.c, s = sums.s;
            //是否显示列汇总 并且有指标
            if (BI.isNotNull(c) && BI.isNotNull(s)) {
                summary = summary.concat(self._getOneRowSummary(c));
                if (this.showColTotal === true && self.targetIds.length > 0) {
                    summary = summary.concat(self._getOneRowSummary(s));
                }
            } else if (BI.isNotNull(s)) {
                summary = summary.concat(self._getOneRowSummary(s));
            }
        }
        return summary;
    },

    /**
     * 展开所有节点的情况下的收起    横向
     */
    _addNode2eTree4OpenRowNode: function (nodeId) {
        var self = this;
        var clickNode = self.eTree.search(nodeId);
        if (BI.isNull(clickNode)) {
            //找到原始tree的这个节点的所有父节点，遍历一遍是否存在于eTree中
            //a、存在，向eTree直接添加；b、不存在，把这些父级节点都添加进去
            var pNodes = [];
            while (true) {
                if (BI.isNull(this.eTree.search(nodeId))) {
                    var node = this.tree.search(nodeId);
                    pNodes.push(node);
                    if (node.getParent().get("id") === this.tree.getRoot().get("id")) {
                        break;
                    }
                } else {
                    break;
                }
                nodeId = this.tree.search(nodeId).getParent().get("id");
            }
            pNodes.reverse();
            BI.each(pNodes, function (i, pNode) {
                var epNode = self.eTree.search(pNode.getParent().get("id"));
                pNode.removeAllChilds();
                self.eTree.addNode(BI.isNotNull(epNode) ? epNode : self.eTree.getRoot(), BI.deepClone(pNode));
            });
        } else {
            //如果已经在这个eTree中，看其是否存在兄弟节点，如果没有应该删除当前节点所在的树，有的话， 只删除自身
            function getFinalParent(nodeId) {
                var node = self.eTree.search(nodeId);
                if (node.getParent().get("id") === self.eTree.getRoot().get("id")) {
                    return nodeId;
                } else {
                    return getFinalParent(node.getParent().get("id"));
                }
            }

            if (this.eTree.search(nodeId).getParent().getChildrenLength() > 1) {
                this.eTree.search(nodeId).getParent().removeChild(nodeId);
            } else if (this.eTree.search(nodeId).getChildrenLength() > 0) {
                //此时应该是做收起，把所有的children都remove掉
                this.eTree.search(nodeId).removeAllChilds();
            } else {
                this.eTree.getRoot().removeChild(getFinalParent(nodeId));
            }
        }
    },

    _createTableHeader: function () {
        var self = this;
        BI.each(this.dimIds.concat(this.targetIds), function (i, dId) {
            BI.isNotNull(dId) &&
            self.header.push({
                type: "bi.normal_header_cell",
                dId: dId,
                text: BI.Utils.getDimensionNameByID(dId),
                sortFilterChange: function (v) {
                    self.resetETree();
                    self.pageOperator = BICst.TABLE_PAGE_OPERATOR.REFRESH;
                    self.headerOperatorCallback(v, dId);
                }
            });
        });
    },

    _createTableItems: function () {
        var self = this;
        var currentLayer = 0;
        var item = {
            children: this._createCommonTableItems(this.data.c, currentLayer) || []
        };
        //汇总
        if (this.showRowTotal === true && BI.isNotEmptyArray(this.data.s)) {
            var outerValues = [];
            if (this.dimIds.length > 0) {
                BI.each(this.data.s, function (i, v) {
                    var tId = self.targetIds[i];
                    outerValues.push({
                        type: "bi.target_body_normal_cell",
                        text: v,
                        dId: tId,
                        cls: "summary-cell last",
                        clicked: [{}]
                    });
                });
                item.values = outerValues;
            } else {
                //使用第一个值作为一个维度
                BI.each(this.data.s, function (i, v) {
                    if (i === 0) {
                        return;
                    }
                    var tId = self.targetIds[i];
                    outerValues.push({
                        type: "bi.target_body_normal_cell",
                        text: v,
                        dId: tId,
                        cls: "summary-cell",
                        clicked: [{}]
                    });
                });
                item.children.push({
                    type: "bi.target_body_normal_cell",
                    text: this.data.s[0],
                    dId: self.targetIds[0],
                    cls: "summary-cell",
                    clicked: [{}],
                    tag: BI.UUID(),
                    isSum: true,
                    values: outerValues
                });
                item.values = item;
            }
        }
        this.items = [item];
    },

    _setOtherAttrs: function () {
        var self = this;
        //冻结列
        this.freezeCols = [];
        //合并列，列大小
        this.mergeCols = [];
        BI.each(this.dimIds, function (i, id) {
            self.mergeCols.push(i);
            self.freezeCols.push(i);
        });
        var dtIds = this.dimIds.concat(this.targetIds);
        if (this.columnSize.length !== dtIds.length) {
            //重置列宽
            this.columnSize = [];
            BI.each(dtIds, function (i, id) {
                self.columnSize.push("");
            });
        }
    },

    createTableAttrs: function () {
        this.headerOperatorCallback = arguments[0];
        this.expanderCallback = arguments[1];

        this._resetPartAttrs();
        this._refreshDimsInfo();

        this._createTableHeader();
        this._createTableItems();

        this._setOtherAttrs();
    },
});