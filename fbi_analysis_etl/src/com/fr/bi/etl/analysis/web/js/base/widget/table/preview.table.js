BI.AnalysisETLPreviewTable = BI.inherit(BI.Widget, {

    _constant: {
        nullCard: "null_card",
        tableCard: "table_card",
        errorCard: "error_card"
    },

    _defaultConfig: function () {
        var conf = BI.AnalysisETLPreviewTable.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: "bi-analysis-etl-preview-table",
            items: [[{text: ""}, {text: ""}, {text: ""}], [{text: ""}, {text: ""}, {text: ""}], [{text: ""}, {text: ""}, {text: ""}], [{text: ""}, {text: ""}, {text: ""}], [{text: ""}, {text: ""}, {text: ""}],
                [{text: ""}, {text: ""}, {text: ""}], [{text: ""}, {text: ""}, {text: ""}], [{text: ""}, {text: ""}, {text: ""}], [{text: ""}, {text: ""}, {text: ""}], [{text: ""}, {text: ""}, {text: ""}]],
            header: [{text: ""}, {text: ""}, {text: ""}],
            rowSize: 25,
            headerRowSize: 30,
            nameValidationChecker: BI.emptyFn(),
            operator: ETLCst.ANALYSIS_ETL_PAGES.SELECT_DATA
        })
    },

    _init: function () {
        BI.AnalysisETLPreviewTable.superclass._init.apply(this, arguments);
        var o = this.options;

        this.table = BI.createWidget({
            type: "bi.sortable_table",
            isNeedResize: false,
            isResizeAdapt: false,
            isNeedFreeze: false,
            freezeCols: [],
            rowSize: o.rowSize,
            headerRowSize: o.headerRowSize,
            header: this._createHeader(),
            items: this._createCell()
        })

        this.table.on(BI.SortableTable.EVENT_CHANGE, function(oldIndex, insertIndex){
            self.fireEvent(BI.AnalysisETLPreviewTable.EVENT_SORT_COLUMN, oldIndex, insertIndex)
        });


        var self = this;
        this.label = BI.createWidget({
            type: "bi.label",
            cls: o.baseCls + "-null-label",
            text: BI.i18nText("BI-Add_Fields_First")
        });
        this.card = BI.createWidget({
            type: "bi.tab",
            element: this.element,
            defaultShowIndex: this._constant.nullCard,
            cardCreator: function (v) {
                switch (v) {
                    case self._constant.nullCard:
                        return BI.createWidget({
                            type: "bi.center_adapt",
                            items: [self.label]
                        });
                    case self._constant.tableCard:
                        return self.table;
                    case self._constant.errorCard:
                        return BI.createWidget({
                            type: "bi.center_adapt",
                            items: [{
                                type: "bi.label",
                                width: 200,
                                cls: o.baseCls + "-null-label warning",
                                text: BI.i18nText("BI-Current_Tab_Error")
                            }]
                        });

                }
            }
        });
        this._showCard()
    },

    _createHeader: function (header) {
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
        return BI.map([header], function (i, items) {
            return BI.map(items, function (j, item) {
                return BI.extend({
                    type: ETLCst.ANALYSIS_TABLE_OPERATOR_PREVIEW_HEADER + widgetType,
                    fieldValuesCreator: function () {
                        if (BI.isNull(o.fieldValuesCreator)) {
                            return [];
                        }
                        return o.fieldValuesCreator.apply(this, arguments);
                    },
                    nameValidationChecker: function (v) {
                        if (BI.isNull(o.nameValidationController)) {
                            return true;
                        }
                        return o.nameValidationController(j, v);
                    }
                }, item);
            });
        })
    },

    _createCell: function (cell) {
        cell = cell || this.options.items;
        return BI.map(cell, function (i, items) {
            return BI.map(items, function (j, item) {
                return BI.extend({
                    type: "bi.analysis_etl_preview_table_cell"
                }, item);
            });
        })
    },

    _getNullText: function () {
        switch (this.options.operator) {
            case ETLCst.ANALYSIS_ETL_PAGES.USE_PART_FIELDS:
                return BI.i18nText("BI-ETL_Please_Select_Field");
            default:
                return BI.i18nText("BI-Add_Fields_First");
        }
    },
    _showCard: function () {
        if (this.options.operator === ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.ERROR) {
            this.card.setSelect(this._constant.errorCard)
        } else if (this.options.header.length === 0) {
            this.label.setText(this._getNullText());
            this.card.setSelect(this._constant.nullCard)
        } else {
            this.card.setSelect(this._constant.tableCard);
            return true;
        }
        return false;
    },

    populate: function (items, header, operator) {
        var self = this;
        self.options.operator = operator
        if (BI.isNull(header)) {
            header = self.options.header;
        }
        if (BI.isNull(items)) {
            items = self.options.items;
        }
        self.options.header = header;
        self.options.items = items;
        var showTable = self._showCard()
        if (showTable === false) {
            return;
        }
        self.table.setSortable(operator === ETLCst.ANALYSIS_ETL_PAGES.SELECT_DATA);
        self.table.populate(self._createCell(), self._createHeader())
        BI.each(self.table.getColumns().header[0], function (idx, item) {
            item.on(BI.AnalysisETLPreviewTable.DELETE_EVENT, function () {
                self.fireEvent(BI.AnalysisETLPreviewTable.DELETE_EVENT, idx)
            })
            item.on(BI.AnalysisETLPreviewTable.EVENT_FILTER, function () {
                self.fireEvent(BI.AnalysisETLPreviewTable.EVENT_FILTER, arguments)
            })
            item.on(BI.AnalysisETLPreviewTable.EVENT_RENAME, function (name) {
                self.fireEvent(BI.AnalysisETLPreviewTable.EVENT_RENAME, idx, name)
            })
        })
    }
})

BI.AnalysisETLPreviewTable.DELETE_EVENT = "EVENT_DELETE";
BI.AnalysisETLPreviewTable.EVENT_RENAME = "EVENT_RENAME";
BI.AnalysisETLPreviewTable.EVENT_FILTER = "EVENT_FILTER";
BI.AnalysisETLPreviewTable.EVENT_SORT_COLUMN = "EVENT_SORT_COLUMN";

$.shortcut("bi.analysis_etl_preview_table", BI.AnalysisETLPreviewTable);