/**
 * 分页表格
 *
 * Created by GUY on 2016/2/15.
 * @class BI.PageTable
 * @extends BI.Widget
 */
BI.PageTable = BI.inherit(BI.Widget, {

    _const: {
        scrollWidth: 18,
        minScrollWidth: 150
    },

    _defaultConfig: function () {
        return BI.extend(BI.PageTable.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-page-table",
            el: {
                type: "bi.sequence_table"
            },

            hasHNext: BI.emptyFn,
            //横向最大页
            lastHPage: function () {
                return 1;
            },
            pager: {
                pages: false,
                curr: 1,
                hasNext: function (v) {
                    return v < 3;
                },
                hasPrev: function (v) {
                    return v > 1
                },
                firstPage: 1,
                lastPage: function () {
                    return 3;
                }
            },

            itemsCreator: BI.emptyFn,

            isNeedFreeze: false,//是否需要冻结单元格
            freezeCols: [], //冻结的列号,从0开始,isNeedFreeze为true时生效

            isNeedMerge: false,//是否需要合并单元格
            mergeCols: [], //合并的单元格列号
            mergeRule: function (row1, row2) { //合并规则, 默认相等时合并
                return BI.isEqual(row1, row2);
            },

            columnSize: [],
            headerRowSize: 25,
            footerRowSize: 25,
            rowSize: 25,

            regionColumnSize: false,

            header: [],
            footer: false,
            items: [], //二维数组

            //交叉表头
            crossHeader: [],
            crossItems: []
        });
    },

    _init: function () {
        BI.PageTable.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        //记录滚动条滚动到的点
        this.dots = new BI.Queue(10);
        //事件锁
        this.lock = BI.debounce(function () {
            self.dots.clear();
        }, 300);
        this.hpage = 1;

        this.table = BI.createWidget(o.el, {
            type: "bi.sequence_table",
            element: this.element,

            pageSpace: 95,

            isNeedResize: true,
            isResizeAdapt: false,

            isNeedFreeze: o.isNeedFreeze,
            freezeCols: o.freezeCols,

            isNeedMerge: o.isNeedMerge,
            mergeCols: o.mergeCols,
            mergeRule: o.mergeRule,

            columnSize: o.columnSize,
            headerRowSize: o.headerRowSize,
            footerRowSize: o.footerRowSize,
            rowSize: o.rowSize,

            regionColumnSize: o.regionColumnSize,

            header: o.header,
            footer: o.footer,
            items: o.items,
            //交叉表头
            crossHeader: o.crossHeader,
            crossItems: o.crossItems
        });

        this.table.on(BI.CustomScrollTable.EVENT_SCROLL_TO_LEFT, function () {
            var dots = self.dots.toArray();
            BI.delay(function () {
                if (self.element.is(":visible")) {
                    if (dots.length > 1
                        && dots[0] - dots[1] >= 0 && dots[0] - dots[1] < 100
                        && dots[1] - dots[2] >= 0 && dots[1] - dots[2] < 100
                        && dots[2] - dots[3] >= 0 && dots[2] - dots[3] < 100
                        && dots[3] - dots[4] >= 0 && dots[3] - dots[4] < 100
                        && dots[4] - dots[5] >= 0 && dots[4] - dots[5] < 100
                        && dots[5] - dots[6] >= 0 && dots[5] - dots[6] < 100
                        && dots[6] - dots[7] >= 0 && dots[6] - dots[7] < 100
                        && dots[7] - dots[8] >= 0 && dots[7] - dots[8] < 100
                        && dots[8] - dots[9] >= 0 && dots[8] - dots[9] < 100
                    ) {
                        if (self.hpage <= 1) {
                            self.hpage = 1;
                            return;
                        }
                        self.hpage--;
                        self._loading();
                        o.itemsCreator({
                            hpage: self.hpage
                        }, function (items, header, crossItems, crossHeader) {
                            self.populate.apply(self, arguments);
                            self._loaded();
                        });
                    }
                }
            }, 10);
        });
        this.table.on(BI.CustomScrollTable.EVENT_SCROLL_TO_RIGHT, function () {
            var dots = self.dots.toArray();
            //当页面隐藏的时候同时会触发这个事件，要把这种情况去掉
            BI.delay(function () {
                if (self.element.is(":visible")) {
                    if (dots.length > 1
                        && dots[1] - dots[0] >= 0 && dots[1] - dots[0] < 100
                        && dots[2] - dots[1] >= 0 && dots[2] - dots[1] < 100
                        && dots[3] - dots[2] >= 0 && dots[3] - dots[2] < 100
                        && dots[4] - dots[3] >= 0 && dots[4] - dots[3] < 100
                        && dots[5] - dots[4] >= 0 && dots[5] - dots[4] < 100
                        && dots[6] - dots[5] >= 0 && dots[6] - dots[5] < 100
                        && dots[7] - dots[6] >= 0 && dots[7] - dots[6] < 100
                        && dots[8] - dots[7] >= 0 && dots[8] - dots[7] < 100
                        && dots[9] - dots[8] >= 0 && dots[9] - dots[8] < 100
                    ) {
                        var hasNext = false;
                        if (o.hasHNext(self.hpage)) {
                            hasNext = true;
                        } else if (self.hpage < o.lastHPage()) {
                            hasNext = true;
                        }
                        if (hasNext === true) {
                            self.hpage++;
                            self._loading();
                            o.itemsCreator({
                                hpage: self.hpage
                            }, function (items, header, crossItems, crossHeader) {
                                self.populate.apply(self, arguments);
                                self._loaded();
                            });
                        }
                    }
                }
            });
        });

        this.table.on(BI.CustomScrollTable.EVENT_RIGHT_SCROLL, function () {
            var dot = self.table.getRightHorizontalScroll();
            self.dots.push(dot);
            self.lock();
            if (dot < 50 && dot >= 0) {
                //显示页码
                self._showCurrentColumn();
            } else {
                self._hideCurrentColumn();
            }
        });

        this.table.on(BI.Table.EVENT_TABLE_AFTER_INIT, function () {
            self.fireEvent(BI.Table.EVENT_TABLE_AFTER_INIT);
            self.fireEvent(BI.PageTable.EVENT_TABLE_AFTER_INIT);
            self._dealWithPager();
        });
        this.table.on(BI.Table.EVENT_TABLE_RESIZE, function () {
            self.fireEvent(BI.Table.EVENT_TABLE_RESIZE);
            self._dealWithPager();
        });

        this.table.on(BI.Table.EVENT_TABLE_SCROLL, function () {
            self.fireEvent(BI.Table.EVENT_TABLE_SCROLL, arguments);
        });
        this.table.on(BI.Table.EVENT_TABLE_AFTER_REGION_RESIZE, function () {
            self._hideCurrentColumn();
            self._dealWithPager();
            self.fireEvent(BI.Table.EVENT_TABLE_AFTER_REGION_RESIZE);
            self.fireEvent(BI.PageTable.EVENT_TABLE_AFTER_REGION_RESIZE);
        });
        this.table.on(BI.Table.EVENT_TABLE_AFTER_COLUMN_RESIZE, function () {
            self.fireEvent(BI.Table.EVENT_TABLE_AFTER_COLUMN_RESIZE);
            self.fireEvent(BI.PageTable.EVENT_TABLE_AFTER_COLUMN_RESIZE);
        });
    },

    _loading: function () {
        //if (!this.loading) {
        //    this.loading = BI.Maskers.make(this.getName(), this);
        //    BI.createWidget({
        //        type: "bi.absolute",
        //        element: this.loading,
        //        items: [{
        //            el: {
        //                type: "bi.label",
        //                cls: "page-table-loading-text",
        //                text: BI.i18nText("BI-Loading"),
        //                whiteSpace: "normal",
        //                width: this._const.scrollWidth
        //            },
        //            right: 0,
        //            top: 0,
        //            bottom: this._const.scrollWidth
        //        }]
        //    })
        //}
        //BI.Maskers.show(this.getName());
    },

    _loaded: function () {
        //BI.Maskers.hide(this.getName());
        //this._hideCurrentColumn();
    },

    _showCurrentColumn: function () {
        var self = this, o = this.options;
        this._hideCurrentColumn();
        /**
         * 暂时不用显示分页信息
         */
        //this._currentColumn = BI.createWidget({
        //    type: "bi.text_button",
        //    cls: "page-table-current-column",
        //    text: BI.i18nText("BI-Di_A_Col", ((this.hpage - 1) * 20 + 1)),
        //    hgap: 15,
        //    height: 20,
        //    handler: function () {
        //        self._hideCurrentColumn();
        //    }
        //});
        //if (BI.isNotNull(o.isNeedFreeze)) {
        //    var regionSize = this.table.getRegionColumnSize();
        //    BI.createWidget({
        //        type: "bi.absolute",
        //        element: this.element,
        //        items: [{
        //            el: this._currentColumn,
        //            left: regionSize[0] + 2,
        //            bottom: this._const.scrollWidth + 2
        //        }]
        //    })
        //} else {
        //    BI.createWidget({
        //        type: "bi.absolute",
        //        element: this.element,
        //        items: [{
        //            el: this._currentColumn,
        //            left: 2,
        //            bottom: this._const.scrollWidth + 2
        //        }]
        //    })
        //}
    },

    _assertPager: function () {
        var self = this, o = this.options;
        if (!this.pager) {
            this.pager = BI.createWidget(o.pager, {
                type: "bi.number_pager",
                width: 95,
                height: this._const.scrollWidth,
                cls: "page-table-pager"
            });
            this.pager.on(BI.NumberPager.EVENT_CHANGE, function () {
                self._loading();
                var vpage = this.getCurrentPage();
                o.itemsCreator({
                    vpage: vpage
                }, function (items, header, crossItems, crossHeader) {
                    self.setVPage(vpage);
                    self.populate.apply(self, arguments);
                    self._loaded();
                });
            });

            this.tipPager = BI.createWidget({
                type: "bi.label",
                invisible: false,
                cls: "page-table-min-pager",
                width: this._const.scrollWidth,
                height: this._const.scrollWidth
            });

            BI.createWidget({
                type: "bi.absolute",
                element: this.element,
                items: [{
                    el: this.tipPager,
                    right: 0,
                    bottom: 0
                }, {
                    el: this.pager,
                    right: 0,
                    bottom: 0
                }]
            });
        }
    },

    _hideCurrentColumn: function () {
        this._currentColumn && this._currentColumn.destroy();
    },

    _dealWithPager: function () {
        var o = this.options;
        var regionSize = this.table.getCalculateRegionColumnSize();

        var sWidth = o.isNeedFreeze === true ? regionSize[1] : regionSize[0];

        this._assertPager();
        if (sWidth <= this._const.minScrollWidth) {
            this.tipPager.setValue(this.getVPage());
            this.pager.setVisible(false);
            this.tipPager.setVisible(true);
        } else {
            this.tipPager.setVisible(false);
            this.pager.setVisible(true);
        }
    },

    setHPage: function (v) {
        this.hpage = v;
        this.table.setHPage && this.table.setHPage(v);
    },

    setVPage: function (v) {
        this._assertPager();
        this.pager.setValue(v);
        this.table.setVPage && this.table.setVPage(v);
    },

    getHPage: function () {
        return this.hpage;
    },

    getVPage: function () {
        this._assertPager();
        return this.pager.getCurrentPage();
    },

    resize: function () {
        this._assertPager();
        this.table.resize();
        this._dealWithPager();
    },

    setColumnSize: function (size) {
        this.table.setColumnSize(size);
    },

    getColumnSize: function () {
        return this.table.getColumnSize();
    },

    getCalculateColumnSize: function () {
        return this.table.getCalculateColumnSize();
    },

    getCalculateRegionColumnSize: function () {
        return this.table.getCalculateRegionColumnSize();
    },

    getVerticalScroll: function () {
        return this.table.getVerticalScroll();
    },

    attr: function () {
        BI.PageTable.superclass.attr.apply(this, arguments);
        this._hideCurrentColumn();
        this.table.attr.apply(this.table, arguments);
    },

    showSequence: function () {
        this.table.showSequence();
    },

    hideSequence: function () {
        this.table.hideSequence();
    },

    populate: function (items) {
        this.table.populate.apply(this.table, arguments);
        this._assertPager();
        this.pager.populate();
        this._hideCurrentColumn();
        this._dealWithPager();
    },

    destroy: function () {
        this.table.destroy();
        this.pager && this.pager.destroy();
        BI.PageTable.superclass.destroy.apply(this, arguments);
    }
});
BI.PageTable.EVENT_CHANGE = "PageTable.EVENT_CHANGE";
BI.PageTable.EVENT_TABLE_AFTER_INIT = "EVENT_TABLE_AFTER_INIT";
BI.PageTable.EVENT_TABLE_AFTER_COLUMN_RESIZE = "PageTable.EVENT_TABLE_AFTER_COLUMN_RESIZE";
BI.PageTable.EVENT_TABLE_AFTER_REGION_RESIZE = "PageTable.EVENT_TABLE_AFTER_REGION_RESIZE";
$.shortcut('bi.page_table', BI.PageTable);