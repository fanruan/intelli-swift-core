/**
 * 可以交换列位置的表格
 *
 * @class BI.SortableTable
 * @extends BI.Widget
 */
BI.SortableTable = BI.inherit(BI.Widget, {

    _const: {
        perColumnSize: 100,
        dragButtonWidth: 24,
        dragButtonHeight: 24,
        lineCount: 6
    },

    _defaultConfig: function () {
        return BI.extend(BI.SortableTable.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-sortable-table",

            headerRowSize: 30,
            footerRowSize: 25,
            rowSize: 25,

            header: [],
            items: [] //二维数组
        });
    },

    _init: function () {
        BI.SortableTable.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.insertLine = [];
        BI.each(o.header, function(idx, header){
            BI.each(header, function(id, obj){
                obj.cls = (obj.cls || "") + " drag-header"
            })
        })

        this.table = BI.createWidget({
            type: "bi.table_view",
            element: this.element,
            isNeedResize: false,
            isResizeAdapt: false,
            columnSize: [],
            headerRowSize: o.headerRowSize,
            footerRowSize: o.footerRowSize,
            rowSize: o.rowSize,
            regionColumnSize: false,
            header: o.header,
            items: o.items
        });
        this.table.on(BI.Table.EVENT_TABLE_AFTER_INIT, function () {
            self._createInsertLine();
            self.fireEvent(BI.Table.EVENT_TABLE_AFTER_INIT, arguments);
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

                var absolutePosition = ui.position.left + self.table.getRightHorizontalScroll() + (e.pageX - ui.helper.offset().left);
                var dropPosition = self._getColumnsLeftBorderDistance();
                var insertIndex = self._getNearIndexFromArray(dropPosition, absolutePosition)
                //这个insertIndex是包含原元素的index
                //调整item顺序，重新populate
                var flag = self._exchangeItemsAndHeaderPosition(ui.helper.data("index"), insertIndex)
                if(flag === true){
                    BI.nextTick(function(){
                        self.populate(o.items, o.header);
                    });
                }
            }
        });
    },

    /**
     * 插入到对应列的辅助线
     * @private
     */
    _createInsertLine: function(){
        var self = this;
        var dropPosition = this._getColumnsLeftBorderDistance();
        var lineObj = {
            type: "bi.layout",
            cls: "insert-help-line",
            invisible: true,
            width: 3
        };
        this.insertLine = [BI.createWidget(lineObj)];
        var hearders = this.table.getColumns().header[0];
        BI.each(hearders, function(idx, header){
            var line = BI.createWidget(BI.extend(lineObj));
            self.insertLine.push(line);
        });
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: BI.map(self.insertLine, function(idx, line){
                if(idx === self.insertLine.length - 1){
                    return {
                        el: line,
                        top: 0,
                        width: 3,
                        bottom: 0,
                        right: 0
                    }
                }
                return {
                    el: line,
                    top: 0,
                    bottom: 0,
                    width: 3,
                    left: dropPosition[idx]
                }
            })
        })
    },

    _initDrag: function(){
        var self = this, c = this._const;
        BI.each(this.table.getColumns().header[0], function(idx, header){
            var dragButton = BI.createWidget({
                type: "bi.absolute",
                element: header,
                items: [{
                    el: {
                        type: "bi.triangle_drag_button",
                        cls: "drag-header",
                        width: c.dragButtonWidth,
                        height: c.dragButtonHeight,
                        lineCount: c.lineCount,
                    },
                    top: 0,
                    left: 0
                }]
            })

            dragButton.element.draggable({
                axis: "x",      //拖拽路径
                revert: false,
                cursor: BICst.cursorUrl,
                cursorAt: {left: 5, top: 5},
                containment: self.element,   //约束拖拽区域
                drag: function(e, ui){
                    self._showInsertHelpLine(e, ui);
                },
                stop: function(){
                    BI.each(self.insertLine, function(idx, line){
                        line.setVisible(false);
                    })
                },
                helper: function () {
                    var RowsSize = self.table.getCalculateRegionRowSize();
                    var columnsSizes = self.table.getCalculateColumnSize();
                    var clone = BI.createWidget({
                        type: "bi.layout",
                        cls: "sortable_table_drag_clone",
                        data: {index: BI.parseInt(idx)},
                        width: columnsSizes[idx],
                        height: RowsSize[0]
                    })
                    clone.element.appendTo(self.element);
                    return clone.element;
                }
            })
        });
    },

    _getColumnsLeftBorderDistance: function(){
        var dropPosition = [];
        var columnSizes = this.table.getCalculateColumnSize();
        BI.each(columnSizes, function(idx, columnSize){
            if(idx === 0){
                dropPosition.push(0)
            }else{
                dropPosition.push(dropPosition[idx - 1] + columnSizes[idx - 1])
            }
        });
        return dropPosition;
    },

    _showInsertHelpLine: function(e, ui){
        var absolutePosition =  ui.position.left + this.table.getRightHorizontalScroll() + (e.pageX - ui.helper.offset().left);
        var dropPosition = this._getColumnsLeftBorderDistance();
        var insertIndex = this._getNearIndexFromArray(dropPosition, absolutePosition);
        BI.each(this.insertLine, function(idx, line){
            line.setVisible(insertIndex === idx);
        })
    },

    _exchangeItemsAndHeaderPosition: function (sourceIndex, targetIndex) {
        var o = this.options;
        if(sourceIndex === targetIndex){
            return false;
        }
        var header = BI.unzip(o.header);
        var items = BI.unzip(o.items);
        var sourceHeader = header[sourceIndex];
        var sourceitems = items[sourceIndex];
        header.splice(targetIndex, 0, sourceHeader);
        items.splice(targetIndex, 0, sourceitems);
        var deleteIndex = (sourceIndex < targetIndex) ? sourceIndex : sourceIndex + 1;
        BI.removeAt(header, deleteIndex);
        BI.removeAt(items, deleteIndex);
        o.header = BI.unzip(header);
        o.items = BI.unzip(items);
        return true;
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

    populate: function (items, headers) {
        var self = this, o = this.options;
        o.header = headers;
        o.items = items;
        self.table.populate(o.items, o.header);
        this._createInsertLine();
        self._initDrag();
    },

    destroy: function () {
        this.table.destroy();
        BI.SortableTable.superclass.destroy.apply(this, arguments);
    }
});
$.shortcut('bi.sortable_table', BI.SortableTable);