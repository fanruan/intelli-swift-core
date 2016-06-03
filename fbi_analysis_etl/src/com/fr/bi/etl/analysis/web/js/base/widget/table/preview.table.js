



BI.AnalysisETLPreviewTable = BI.inherit(BI.Widget, {

    _constant : {
        nullCard : "null_card",
        tableCard : "table_card",
        errorCard : "error_card"
    },

    _defaultConfig: function() {
        var conf = BI.AnalysisETLPreviewTable.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls : "bi-analysis-etl-preview-table",
            items:[[{text:""},{text:""},{text:""}],[{text:""},{text:""},{text:""}],[{text:""},{text:""},{text:""}],[{text:""},{text:""},{text:""}],[{text:""},{text:""},{text:""}],
                [{text:""},{text:""},{text:""}],[{text:""},{text:""},{text:""}],[{text:""},{text:""},{text:""}],[{text:""},{text:""},{text:""}],[{text:""},{text:""},{text:""}]],
            header :[{text:""},{text:""},{text:""}],
            rowSize: 25,
            headerRowSize: 30,
            nameValidationChecker : BI.emptyFn(),
            operator:ETLCst.ANALYSIS_ETL_PAGES.SELECT_DATA
        })
    },

    _init : function() {
        BI.AnalysisETLPreviewTable.superclass._init.apply(this, arguments);
        var o = this.options;

        this.table = BI.createWidget({
            type:"bi.table_view",
            isNeedResize: false,
            isResizeAdapt: false,
            isNeedFreeze: false,
            freezeCols: [],
            rowSize: o.rowSize,
            headerRowSize: o.headerRowSize,
            header: this._createHeader(),
            items: this._createCell()
        })


        var self = this;
        this.table.on(BI.Table.EVENT_TABLE_AFTER_INIT, function () {
            self._adjustColumns();
        });

        this.table.on(BI.Table.EVENT_TABLE_RESIZE, function () {
            self._adjustColumns();
        });

        this._initDrag();

        this.card = BI.createWidget({
            type:"bi.card",
            element:this.element,
            defaultShowName : this._constant.nullCard,
            items :[{
                cardName : this._constant.nullCard,
               el : {
                   type : "bi.center_adapt",
                   items : [{
                       type:"bi.label",
                       cls: o.baseCls +"-null-label",
                       text: BI.i18nText("BI-Add_Fields_First")
                   }]
               }
            }, {
                el :this.table,
                cardName: this._constant.tableCard
            }, {
                cardName : this._constant.errorCard,
                el : {
                    type : "bi.center_adapt",
                    items : [{
                        type:"bi.label",
                        cls: o.baseCls +"-null-label warning",
                        text: BI.i18nText("BI-Current_Tab_Error")
                    }]
                }
            }]
        })
        this._showCard()
    },

    _initDrag : function() {
        var self = this;

        this.dropHelper = BI.createWidget({
            type:"bi.single_line",
            cls:"drop-helper",
            time:10,
            lineWidth:2,
            step:1,
            move:true,
            direction:1,
            container:this.table.element,
            len:0
        });

        this.dropHelper.refreshPosition = function(e, ui) {
            var absolutePosition = ui.position.left + self.table.getRightHorizontalScroll()+ (e.pageX - ui.helper.offset().left);
            var nearPosition= self._getNearPosFromArray(self.dropHelper.dropPosition, absolutePosition);
            self.dropHelper.stroke(self.dragHepler.dragHeight, 0, nearPosition - self.table.getRightHorizontalScroll());
        }

        this.dropHelper.hide();
        self.element.droppable({
            accept:"."+ ETLCst.ANALYSIS_DRAG_CLASS,
            drop : function(e, ui) {
                var absolutePosition = ui.position.left + self.table.getRightHorizontalScroll() + (e.pageX - ui.helper.offset().left);
                var insertIndex= self._getNearIndexFromArray(self.dropHelper.dropPosition, absolutePosition)
                //这个insertIndex是包含原元素的index
                self.dropHelper.scrollLeft = self.table.getRightHorizontalScroll();
                if(BI.isNotNull(self.dragHepler.timeoutTimer)) {
                    clearTimeout(self.dragHepler.timeoutTimer)
                    self.dragHepler.timeoutTimer = null;
                }
                self.fireEvent(BI.AnalysisETLPreviewTable.EVENT_SORT_COLUMN, self.dragHepler.currentColumnIndex, insertIndex);
            }
        })
        this.dragHepler = BI.createWidget({
            type : 'bi.rectangle',
            baseCls :"rect-border",
            time:10,
            lineWidth:2,
            step:1,
            move:true,
            container:this.table.element,
            drag : {
                axis:"x",
                revert: "invalid",
                helper:"clone",
                cursor: BICst.cursorUrl,
                cursorAt: {left:5, top:5},
                containment:self.element,
                start : function(e, ui) {
                    self.table.element.unbind("mouseover")
                    self.dragHepler.dragging = true;
                    self.table.element.addClass('rect-border-dragging')
                    self.dragHepler.setMove(false)
                    self.dragHepler.dragWidth = self.element.width();
                    self.dragHepler.dragHeight = $(".table", self.table.element).height();
                    self.dragHepler.helperWidth = ui.helper.width();
                    self.dropHelper.dropPosition = [];
                    var start = 0;
                    self.dropHelper.dropPosition.push(start)
                    BI.each(self.table.headerTds[0], function(idx, item){
                        start += item.outerWidth()
                        self.dropHelper.dropPosition.push(start)
                    })
                },
                stop : function () {
                    self.dragHepler.dragging = false;
                    self.table.element.removeClass('rect-border-dragging')
                    self.dragHepler.setMove(true);
                    self.dropHelper.hide();
                    if(BI.isNotNull(self.dragHepler.timer)) {
                        clearInterval(self.dragHepler.timer);
                        self.dragHepler.timer = null;
                    }
                },
                drag : function (e, ui) {
                    self.dropHelper.refreshPosition(e, ui);
                    if(ui.position.left === 0) {
                        if(BI.isNull(self.dragHepler.timer)) {
                            if(self.table.getRightHorizontalScroll() > 0) {
                                self.dragHepler.timer = setInterval(function(){
                                    var currentLeft = self.table.getRightHorizontalScroll();
                                    self.table.setRightHorizontalScroll(currentLeft - 10);
                                    var move = currentLeft - self.table.getRightHorizontalScroll();
                                    self.dragHepler.moveLeft(move)
                                    self.dropHelper.refreshPosition(e, ui);
                                    if(self.table.getRightHorizontalScroll() <=0 ){
                                        clearInterval(self.dragHepler.timer);
                                        self.dragHepler.timer = null;
                                    }
                                }, 10);
                            }
                        }
                    } else if(ui.position.left + self.dragHepler.helperWidth  >= self.dragHepler.dragWidth) {
                        if(BI.isNull(self.dragHepler.timer)) {
                            if(self.table.getRightHorizontalScroll() + self.dragHepler.dragWidth < self.table.scrollContainer.element[0].scrollWidth) {
                                self.dragHepler.timer = setInterval(function(){
                                    var currentLeft = self.table.getRightHorizontalScroll();
                                    self.table.setRightHorizontalScroll(currentLeft + 10);
                                    var move = currentLeft - self.table.getRightHorizontalScroll();
                                    self.dragHepler.moveLeft(move)
                                    self.dropHelper.refreshPosition(e, ui);
                                    if(self.table.getRightHorizontalScroll() + self.dragHepler.dragWidth >= self.table.scrollContainer.element[0].scrollWidth ){
                                        clearInterval(self.dragHepler.timer);
                                        self.dragHepler.timer = null;
                                    }
                                }, 10);
                            }
                        }
                    } else {
                        if(BI.isNotNull(self.dragHepler.timer)) {
                            clearInterval(self.dragHepler.timer);
                            self.dragHepler.timer = null;
                        }
                    }
                },
                helper : function () {
                    var column = self.dragHepler.currentColumnIndex;
                    var headerTd = self.table.headerTds[0][column];
                    var header = [self.options.header[column]];
                    var items = [];
                    BI.each(self.options.items, function(idx, item){
                        items.push([item[column]]);
                    })
                    var o = self.options;
                    var clone = BI.createWidget({
                        type:"bi.layout",
                        cls:"bi_preview_table_drag_clone",
                        width:headerTd.outerWidth() + 1,
                        height:$(".table", self.table.element).height()
                    })
                    clone.element.appendTo(self.element);
                    return clone.element;
                }
            }
        })

        self.dragHepler.hide();
        this.table.element.hover(function(e){
            if(self.options.operator === ETLCst.ANALYSIS_ETL_PAGES.SELECT_DATA){
                self.table.element.bind("mouseover", function(e){

                    var fn  = function () {
                        if(self._isDragButton(el)){
                            self.dragHepler.prepareDragging = true;
                            return;
                        }
                        if(self.dragHepler.dragging === true) {
                            return;
                        }
                        self.dragHepler.prepareDragging = false;
                        var value = self._getColumnIndexByElement(el);
                        self.dragHepler.currentColumnIndex = value[0];
                        if(value[0] === -1){
                            self.dragHepler.hide();
                        } else {
                            self.dragHepler.stroke(value[1].outerWidth(), Math.min(self.element.outerHeight(),value[2].outerHeight()), 0, value[1][0].offsetLeft - self.table.getRightHorizontalScroll());
                        }
                        e.stopPropagation();
                    }
                    var el = $(e.target)
                    if(self.dragHepler.prepareDragging === true){
                        //放到DragButton之后所有事件要延迟执行，不然黑没有开始dragging 这边就继续执行了，结果就跑偏了
                        self.dragHepler.timeoutTimer = BI.delay(fn, 100);
                    } else {
                        fn.apply();
                    }
                })
            }
        }, function(e){
            if(self.dragHepler.dragging === true) {
                return;
            }
            self.table.element.unbind("mouseover")
            self.dragHepler.hide();
            self.dropHelper.hide();
        })
    },

    _getNearIndexFromArray : function(array, v) {
        var index = 0;
        BI.some(array, function(idx, item){
            if(idx === array.length - 1) {
                index = idx;
            } else {
                if(v < array[idx + 1]) {
                    var avg = (item + array[idx + 1])/2;

                    index = v < avg ? idx : idx + 1;
                    return true;
                }
            }
        })
        return index;
    },

    _getNearPosFromArray : function(array, v) {
        var pos = 0;
        BI.some(array, function(idx, item){
            if(idx === array.length - 1) {
                pos = item;
            } else {
                if(v < array[idx + 1]) {
                    var avg = (item + array[idx + 1])/2;

                    pos = v < avg ? item : array[idx + 1];
                    return true;
                }
            }
        })
        return pos;
    },

    _isDragButton : function($el) {
        return $el.hasClass(ETLCst.ANALYSIS_DRAG_CLASS)
            || $el.parents("."+ETLCst.ANALYSIS_DRAG_CLASS, this.table.element).length > 0
    },

    _getColumnIndexByElement : function($el) {
        var index = $el[0].cellIndex;
        var $table = $el.parents("table:eq(0)", this.table.element)
        while(BI.isUndefined(index) || !$table.hasClass("table")){
            $el = $el.parents("td:eq(0)", this.table.element);
            if($el.length === 0) {
                index = -1;
                break;
            }
            index = $el[0].cellIndex;
            $table = $el.parents("table:eq(0)", this.table.element)
        }
        return [index, $el, $table]
    },

    _createHeader : function (header) {
        var self = this, o = this.options, operator = this.options.operator
        var widgetType = BI.ANALYSIS_ETL_HEADER.NORMAL
        switch (operator) {
            case  ETLCst.ANALYSIS_ETL_PAGES.SELECT_DATA : {
                widgetType = BI.ANALYSIS_ETL_HEADER.DELETE;
                break;
            }
            case  ETLCst.ANALYSIS_ETL_PAGES.FILTER : {
                widgetType = BI.ANALYSIS_ETL_HEADER.FILTER;
                break;
            }
            default : {
                widgetType = BI.ANALYSIS_ETL_HEADER.NORMAL;
            }

        }
        header = header || this.options.header;
        return  BI.map([header], function (i, items) {
            return BI.map(items, function (j, item) {
                return BI.extend({
                    type: ETLCst.ANALYSIS_TABLE_OPERATOR_PREVIEW_HEADER + widgetType,
                    fieldValuesCreator : function () {
                        if (BI.isNull(o.fieldValuesCreator)){
                            return [];
                        }
                        return o.fieldValuesCreator.apply(this, arguments);
                    },
                    nameValidationChecker : function (v) {
                        if (BI.isNull(o.nameValidationController)){
                            return true;
                        }
                        return o.nameValidationController(j, v);
                    }
                }, item);
            });
        })
    },

    _createCell : function (cell) {
        cell = cell || this.options.items;
        return BI.map(cell, function (i, items) {
            return BI.map(items, function (j, item) {
                return BI.extend({
                    type: "bi.analysis_etl_preview_table_cell"
                }, item);
            });
        })
    },

    _showCard : function () {
        if(this.options.operator === ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.ERROR) {
            this.card.showCardByName(this._constant.errorCard)
        } else if(this.options.header.length === 0){
            this.card.showCardByName(this._constant.nullCard)
        } else {
            this.card.showCardByName(this._constant.tableCard)
            return true;
        }
        return false;
    },

    _adjustColumns : function () {
        this.table.setRegionColumnSize(["100%"]);
    },

    populate: function(items, header, operator) {
        var self = this;
        BI.nextTick(function(e){
            self.options.operator = operator
            if(BI.isNull(header)) {
                header = self.options.header;
            }
            if(BI.isNull(items)) {
                items = self.options.items;
            }
            self.options.header = header;
            self.options.items = items;
            var showTable = self._showCard()
            if(showTable === false) {
                return;
            }
            self.table.populate(self._createCell(), self._createHeader())
            self.dragHepler.reInitDrag()
            if(BI.isNotNull(self.dropHelper.scrollLeft)){
                $(".scroll-table", self.table.element)[0].scrollLeft = self.dropHelper.scrollLeft
            }
            self.dragHepler.hide()
            BI.each(self.table.headerItems[0], function(idx, item){
                item.on(BI.AnalysisETLPreviewTable.DELETE_EVENT, function(){
                    self.fireEvent(BI.AnalysisETLPreviewTable.DELETE_EVENT, idx)
                })
                item.on(BI.AnalysisETLPreviewTable.EVENT_FILTER, function(){
                    self.fireEvent(BI.AnalysisETLPreviewTable.EVENT_FILTER, arguments)
                })
                item.on(BI.AnalysisETLPreviewTable.EVENT_RENAME, function(name){
                    self.fireEvent(BI.AnalysisETLPreviewTable.EVENT_RENAME, idx, name)
                })
            })

        })
    }
})

BI.AnalysisETLPreviewTable.DELETE_EVENT = "event_delete";
BI.AnalysisETLPreviewTable.EVENT_RENAME = "event_rename";
BI.AnalysisETLPreviewTable.EVENT_FILTER = "event_filter";
BI.AnalysisETLPreviewTable.EVENT_SORT_COLUMN = "event_sort_column";

$.shortcut("bi.analysis_etl_preview_table", BI.AnalysisETLPreviewTable);