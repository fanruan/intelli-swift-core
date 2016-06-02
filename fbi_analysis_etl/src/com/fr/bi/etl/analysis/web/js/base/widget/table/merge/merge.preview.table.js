



BI.AnalysisETLMergePreviewTable = BI.inherit(BI.Widget, {

    _defaultConfig: function() {
        var conf = BI.AnalysisETLMergePreviewTable.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls : "bi-analysis-etl-merge-preview-table",
            items:[[{text:"a"},{text:"b"},{text:"c"}],[{text:"a"},{text:"b"},{text:"c"}],[{text:"a"},{text:"b"},{text:"c"}],[{text:""},{text:""},{text:""}],[{text:""},{text:""},{text:""}],
                [{text:"d"},{text:"e"},{text:"f"}],[{text:""},{text:""},{text:""}],[{text:""},{text:""},{text:""}],[{text:""},{text:""},{text:""}],[{text:""},{text:""},{text:""}]],
            header :[{text:"g"},{text:"d"},{text:"a"}],
            rowSize: 40,
            headerRowSize: 40,
            leftColumns:[1],
            mergeColumns:[{index:2,mergeBy:["fieldA","fieldB"]}],
            rename:true,
            nameValidationChecker : BI.emptyFn
        })
    },

    _init : function() {
        BI.AnalysisETLMergePreviewTable.superclass._init.apply(this, arguments);
        var o = this.options;

        this.table = BI.createWidget({
            type:"bi.table_view",
            isNeedResize: false,
            isResizeAdapt: false,
            element:this.element,
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
    },

    _createHeader : function (header) {
        var self = this, o = this.options
        header = header || this.options.header;
        return  BI.map([header], function (i, items) {
            return BI.map(items, function (j, item) {
                return BI.extend({
                    type:"bi.analysis_etl_merge_p_t_h_cell",
                    merge : self._getMergeInfo(j),
                    rename:o.rename,
                    validationChecker : function (v) {
                        if (BI.isNull(o.nameValidationController)){
                            return true;
                        }
                        return o.nameValidationController(j, v);
                    }
                }, item);
            });
        })
    },

    _getMergeInfo : function (column) {
        var o = this.options;
        var mergeInfo = BI.find(o.mergeColumns, function (idx, item) {
            return item.index === column;
        })
        return BI.isNotNull(mergeInfo) ? mergeInfo.mergeBy : []
    },

    _getColumnCls : function (column) {
        var o = this.options;
        if(BI.indexOf(o.leftColumns, column) > -1) {
            return "left";
        }
        if(BI.isNotNull(BI.find(o.mergeColumns, function (idx, item) {
            return item.index === column;
        }))){
            return "merge";
        }
        return "right";
    },

    _createCell : function (cell) {
        cell = cell || this.options.items;
        var self = this;
        return BI.map(cell, function (i, items) {
            return BI.map(items, function (j, item) {
                return BI.extend({
                    type: "bi.analysis_etl_preview_table_cell",
                    cls: self._getColumnCls(j)
                }, item);
            });
        })
    },

    _adjustColumns : function () {
        this.table.setRegionColumnSize(["100%"]);
    },

    populate: function(items, header, leftColumns, mergeColumns) {
        var self = this;
        BI.nextTick(function(e){
            self.options.leftColumns = leftColumns
            self.options.mergeColumns = mergeColumns
            if(BI.isNull(header)) {
                header = self.options.header;
            }
            if(BI.isNull(items)) {
                items = self.options.items;
            }
            self.options.header = header;
            self.options.items = items;
            self.table.populate(self._createCell(), self._createHeader())
            BI.each(self.table.headerItems[0], function(idx, item){
                item.on(BI.AnalysisETLMergePreviewTable.EVENT_RENAME, function(name){
                    self.fireEvent(BI.AnalysisETLMergePreviewTable.EVENT_RENAME, idx, name)
                })
            })
        })
    }
})

BI.AnalysisETLMergePreviewTable.EVENT_RENAME = "event_rename";

$.shortcut("bi.analysis_etl_merge_preview_table", BI.AnalysisETLMergePreviewTable);