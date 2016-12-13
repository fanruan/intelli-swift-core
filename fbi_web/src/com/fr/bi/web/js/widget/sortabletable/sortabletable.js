/**
 * 可以交换列位置的表格
 *
 * @class BI.SortableTable
 * @extends BI.Widget
 */
BI.SortableTable = BI.inherit(BI.Widget, {

    _const: {
        perColumnSize: 100
    },

    _defaultConfig: function () {
        return BI.extend(BI.SortableTable.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-sortable-table",
            el: {
                type: "bi.table_view"
            },
            isNeedResize: true,

            columnSize: [],
            headerRowSize: 30,
            footerRowSize: 25,
            rowSize: 25,

            regionColumnSize: false,

            header: [],
            items: [] //二维数组
        });
    },

    _init: function () {
        BI.SortableTable.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.table = BI.createWidget(o.el, {
            type: "bi.table_view",
            element: this.element,
            isNeedResize: o.isNeedResize,
            isResizeAdapt: false,

            columnSize: o.columnSize,
            headerRowSize: o.headerRowSize,
            footerRowSize: o.footerRowSize,
            rowSize: o.rowSize,

            regionColumnSize: o.regionColumnSize,

            header: o.header,
            items: o.items
        });
        this.table.on(BI.Table.EVENT_TABLE_AFTER_INIT, function () {
            self._adjustColumns();
            self.fireEvent(BI.Table.EVENT_TABLE_AFTER_INIT, arguments);
        });
        this.table.on(BI.Table.EVENT_TABLE_RESIZE, function () {
            self._adjustColumns();
            self.fireEvent(BI.Table.EVENT_TABLE_RESIZE, arguments);
        });
        this.table.on(BI.Table.EVENT_TABLE_SCROLL, function () {
            self.fireEvent(BI.Table.EVENT_TABLE_SCROLL, arguments);
        });
        this.table.on(BI.Table.EVENT_TABLE_BEFORE_REGION_RESIZE, function () {
            self.fireEvent(BI.Table.EVENT_TABLE_BEFORE_REGION_RESIZE, arguments);
        });
        this.table.on(BI.Table.EVENT_TABLE_REGION_RESIZE, function () {
            self.fireEvent(BI.Table.EVENT_TABLE_REGION_RESIZE, arguments);
        });
        this.table.on(BI.Table.EVENT_TABLE_AFTER_REGION_RESIZE, function () {
            self.fireEvent(BI.Table.EVENT_TABLE_AFTER_REGION_RESIZE, arguments);
        });

        this.table.on(BI.Table.EVENT_TABLE_BEFORE_COLUMN_RESIZE, function () {
            self.fireEvent(BI.Table.EVENT_TABLE_BEFORE_COLUMN_RESIZE, arguments);
        });
        this.table.on(BI.Table.EVENT_TABLE_COLUMN_RESIZE, function () {
            self.fireEvent(BI.Table.EVENT_TABLE_COLUMN_RESIZE, arguments);
        });
        this.table.on(BI.Table.EVENT_TABLE_AFTER_COLUMN_RESIZE, function () {
            self.fireEvent(BI.Table.EVENT_TABLE_AFTER_COLUMN_RESIZE, arguments);
        });

        this._initDrag();

        self.element.droppable({
            accept: "." + ETLCst.ANALYSIS_DRAG_CLASS,
            drop: function (e, ui) {
                //e.pageX鼠标距文档左边缘位置，包含滚动条
                // ui.helper.offset().left元素距文档左边缘位置，不包括滚动条
                //ui.position.left 距离drop元素左边缘距离
                //self.table.getRightHorizontalScroll() 表格滚动条水平偏移
                //var absolutePosition = ui.position.left + self.table.getRightHorizontalScroll() + (e.pageX - ui.helper.offset().left);
                var absolutePosition = ui.position.left + self.table.getRightHorizontalScroll();
                var insertIndex = self._getNearIndexFromArray(self.dropHelper.dropPosition, absolutePosition)
                //这个insertIndex是包含原元素的index
                //调整item顺序，重新populate
                //getCalculateColumnSize获取每个column宽度
                //getCalculateRegionColumnSize获取整个columns宽度
                //getCalculateRegionRowSize获取整个rows高度
            }
        });
    },

    _initDrag: function(){
        var self = this;
        var columnsSizes = this.table.getCalculateColumnSize();
        var RowsSize = this.table.getCalculateRegionRowSize();
        BI.each(this.table.getColumns().header, function(idx, header){
            header.element.draggable({
                axis: "x",      //拖拽路径
                revert: "invalid",
                cursor: BICst.cursorUrl,
                cursorAt: {left: 5, top: 5},
                containment: self.table.element,   //约束拖拽区域
                helper: function () {
                    var clone = BI.createWidget({
                        type: "bi.layout",
                        cls: "bi_preview_table_drag_clone",
                        width: columnsSizes[idx],
                        height: RowsSize[0]
                    })
                    clone.element.appendTo(self.element);
                    return clone.element;
                }
            })
        });
    },

    _adjustColumns: function () {
        this.table.setRegionColumnSize(["fill"]);
    },

    _getNearIndexFromArray: function (array, v) {
        var index = 0;
        BI.some(array, function (idx, item) {
            if (idx === array.length - 1) {
                index = idx;
            } else {
                if (v < array[idx + 1]) {
                    var avg = (item + array[idx + 1]) / 2;

                    index = v < avg ? idx : idx + 1;
                    return true;
                }
            }
        })
        return index;
    },

    setColumnSize: function (columnSize) {
        this.table.setColumnSize(columnSize);
        this._adjustRegion();
        this._resizeHeader();
    },

    getColumnSize: function () {
        return this.table.getColumnSize();
    },

    getCalculateColumnSize: function () {
        return this.table.getCalculateColumnSize();
    },

    setHeaderColumnSize: function (columnSize) {
        this.table.setHeaderColumnSize(columnSize);
        this._adjustRegion();
        this._resizeHeader();
    },

    setRegionColumnSize: function (columnSize) {
        this.table.setRegionColumnSize(columnSize);
        this._resizeHeader();
    },

    getRegionColumnSize: function () {
        return this.table.getRegionColumnSize();
    },

    getCalculateRegionColumnSize: function () {
        return this.table.getCalculateRegionColumnSize();
    },

    getCalculateRegionRowSize: function () {
        return this.table.getCalculateRegionRowSize();
    },

    getClientRegionColumnSize: function () {
        return this.table.getClientRegionColumnSize();
    },

    getScrollRegionColumnSize: function () {
        return this.table.getScrollRegionColumnSize();
    },

    getScrollRegionRowSize: function () {
        return this.table.getScrollRegionRowSize();
    },

    hasVerticalScroll: function () {
        return this.table.hasVerticalScroll();
    },

    setVerticalScroll: function (scrollTop) {
        this.table.setVerticalScroll(scrollTop);
    },

    setLeftHorizontalScroll: function (scrollLeft) {
        this.table.setLeftHorizontalScroll(scrollLeft);
    },

    setRightHorizontalScroll: function (scrollLeft) {
        this.table.setRightHorizontalScroll(scrollLeft);
    },

    getVerticalScroll: function () {
        return this.table.getVerticalScroll();
    },

    getLeftHorizontalScroll: function () {
        return this.table.getLeftHorizontalScroll();
    },

    getRightHorizontalScroll: function () {
        return this.table.getRightHorizontalScroll();
    },

    getColumns: function () {
        return this.table.getColumns();
    },

    populate: function (items) {
        var self = this, o = this.options;
        this.table.populate.apply(this.table, arguments);
        this._initDrag();
        if (o.isNeedFreeze === true) {
            BI.nextTick(function () {
                if (self.element.is(":visible")) {
                    self._initRegionSize();
                    self.table.resize();
                    self._resizeHeader();
                }
            });
        }
    },

    destroy: function () {
        this.table.destroy();
        BI.SortableTable.superclass.destroy.apply(this, arguments);
    }
});
$.shortcut('bi.sortable_table', BI.SortableTable);