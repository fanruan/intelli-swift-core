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

        BI.each(o.header, function(idx, header){
            BI.each(header, function(id, obj){
                obj.cls = (obj.cls || "") + " drag-header"
            })
        })

        this.table = BI.createWidget(o.el, {
            type: "bi.table_view",
            element: this.element,
            isNeedResize: o.isNeedResize,
            isResizeAdapt: o.isResizeAdapt,

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
            accept: ".drag-header",
            drop: function (e, ui) {
                //e.pageX鼠标距文档左边缘位置，包含滚动条
                //ui.helper.offset().left元素距文档左边缘位置，不包括滚动条
                //ui.position.left 距离drop元素左边缘距离
                //self.table.getRightHorizontalScroll() 表格滚动条水平偏移
                //getCalculateColumnSize获取每个column宽度
                //getCalculateRegionColumnSize获取整个columns宽度
                //getCalculateRegionRowSize获取整个rows高度

                //var absolutePosition = ui.position.left + self.table.getRightHorizontalScroll() + (e.pageX - ui.helper.offset().left);
                var absolutePosition = ui.position.left + self.table.getRightHorizontalScroll();
                var columnSizes = self.table.getCalculateColumnSize()
                var dropPosition = [];
                BI.each(columnSizes, function(idx, columnSize){
                    if(idx === 0){
                        dropPosition.push(0)
                    }else{
                        dropPosition.push(dropPosition[idx - 1] + columnSizes[idx - 1])
                    }
                });
                var insertIndex = self._getNearIndexFromArray(dropPosition, absolutePosition)
                //这个insertIndex是包含原元素的index
                //调整item顺序，重新populate
                self._exchangeItemsAndHeaderPosition(ui.helper.data("index"), insertIndex)
                self.populate(o.items, o.header);
            }
        });
    },

    _initDrag: function(){
        var self = this;
        BI.each(this.table.getColumns().header[0], function(idx, header){
            header.element.draggable({
                axis: "x",      //拖拽路径
                revert: false,
                cursor: BICst.cursorUrl,
                cursorAt: {left: 5, top: 5},
                containment: self.table.element,   //约束拖拽区域
                helper: function () {
                    var RowsSize = self.table.getCalculateRegionRowSize();
                    var columnsSizes = self.table.getCalculateColumnSize();
                    var clone = BI.createWidget({
                        type: "bi.layout",
                        cls: "sortable_table_drag_clone",
                        data: {index: idx},
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

    _exchangeItemsAndHeaderPosition: function (sourceIndex, targetIndex) {
        var o = this.options;
        var header = BI.unzip(o.header);
        var items = BI.unzip(o.items);
        var sourceHeader = header[sourceIndex];
        var sourceitems = items[sourceIndex];
        header[sourceIndex] = header[targetIndex];
        items[sourceIndex] = items[targetIndex];
        header[targetIndex] = sourceHeader;
        items[targetIndex] = sourceitems;
        o.header = BI.unzip(header);
        o.items = BI.unzip(items);
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
    },

    getColumnSize: function () {
        return this.table.getColumnSize();
    },

    getCalculateColumnSize: function () {
        return this.table.getCalculateColumnSize();
    },

    setHeaderColumnSize: function (columnSize) {
        this.table.setHeaderColumnSize(columnSize);
    },

    setRegionColumnSize: function (columnSize) {
        this.table.setRegionColumnSize(columnSize);
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

    populate: function (items, headers) {
        var self = this, o = this.options;
        BI.nextTick(function(){
            o.header = headers;
            o.items = items;
            BI.each(o.header, function(idx, header){
                BI.each(header, function(id, obj){
                    obj.cls = (obj.cls || "") + " drag-header"
                })
            })
            self.table.populate.apply(self.table, arguments);
            self._initDrag();
        });
    },

    destroy: function () {
        this.table.destroy();
        BI.SortableTable.superclass.destroy.apply(this, arguments);
    }
});
$.shortcut('bi.sortable_table', BI.SortableTable);